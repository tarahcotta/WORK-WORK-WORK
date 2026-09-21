package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class VitalRepository(
    private val dao: VitalDao,
    val firestoreSyncManager: FirestoreSyncManager = FirestoreSyncManager()
) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val activeRoutines: Flow<List<WorkoutRoutineEntity>> = dao.getActiveRoutines()
    val allSessions: Flow<List<LoggedWorkoutSessionEntity>> = dao.getAllSessions()
    val allLoggedSets: Flow<List<LoggedSetEntity>> = dao.getAllLoggedSets()

    fun getExercisesForRoutine(routineId: Long): Flow<List<WorkoutExerciseEntity>> {
        return dao.getExercisesForRoutine(routineId)
    }

    suspend fun updateExerciseNotes(exerciseId: Long, notes: String) {
        dao.updateExerciseNotes(exerciseId, notes)
    }


    val bookmarkedExercises: Flow<List<BookmarkedExerciseEntity>> = dao.getBookmarkedExercises()
    val allProgressPhotos: Flow<List<ProgressPhotoEntity>> = dao.getAllProgressPhotos()

    suspend fun addBookmark(exerciseId: String) {
        dao.insertBookmarkedExercise(BookmarkedExerciseEntity(exerciseId))
    }

    suspend fun removeBookmark(exerciseId: String) {
        dao.deleteBookmarkedExercise(exerciseId)
    }

    suspend fun addProgressPhoto(photo: ProgressPhotoEntity): Long {
        return dao.insertProgressPhoto(photo)
    }

    suspend fun deleteProgressPhoto(id: Long, filePath: String) {
        LocalPhotoStorageManager.deleteLocalPhotoFile(filePath)
        dao.deleteProgressPhoto(id)
    }

    fun getSetsForSession(sessionId: Long): Flow<List<LoggedSetEntity>> {
        return dao.getSetsForSession(sessionId)
    }

    suspend fun getSetsForSessionList(sessionId: Long): List<LoggedSetEntity> {
        return dao.getSetsForSession(sessionId).firstOrNull() ?: emptyList()
    }

    fun getMaxWeightForExercise(exerciseName: String): Flow<Float?> {
        return dao.getMaxWeightForExercise(exerciseName)
    }

    suspend fun getLastSetForExercise(exerciseName: String): LoggedSetEntity? {
        return dao.getLastSetForExercise(exerciseName)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity, userId: String? = null) {
        dao.saveUserProfile(profile)
        // Auto-generate fresh custom routines based on updated profile
        generateAndSaveRoutines(profile)

        if (!userId.isNullOrBlank()) {
            firestoreSyncManager.saveUserProfileToCloud(userId, profile)
        }
    }

    suspend fun generateAndSaveRoutines(profile: UserProfileEntity) {
        val routinesWithExercises = RoutineGenerator.generateRoutineForProfile(profile)
        dao.replaceActiveRoutines(routinesWithExercises)
    }

    suspend fun logWorkoutSession(
        session: LoggedWorkoutSessionEntity,
        sets: List<LoggedSetEntity>,
        userId: String? = null
    ): Long {
        val sessionId = dao.insertSession(session)
        val updatedSets = sets.map { it.copy(sessionId = sessionId) }
        dao.insertSets(updatedSets)

        if (!userId.isNullOrBlank()) {
            val updatedSession = session.copy(id = sessionId)
            firestoreSyncManager.saveLoggedSessionToCloud(userId, updatedSession, updatedSets)
        }
        return sessionId
    }

    suspend fun deleteSession(sessionId: Long) {
        dao.deleteSetsForSession(sessionId)
        dao.deleteSession(sessionId)
    }

    suspend fun ensureInitialDataLoaded() = withContext(Dispatchers.IO) {
        try {
            val currentProfile = dao.getUserProfileOnce()
            if (currentProfile == null) {
                val defaultProfile = UserProfileEntity()
                dao.saveUserProfile(defaultProfile)
                generateAndSaveRoutines(defaultProfile)
            }

            if (dao.getSessionCount() == 0) {
                // Seeding disabled to allow starting from 0 workouts
            }
        } catch (e: Exception) {
            Log.e("VitalRepository", "Error ensuring initial data loaded", e)
        }
    }

    suspend fun restoreUserDataFromCloud(userId: String) {
        val cloudProfile = firestoreSyncManager.fetchUserProfileFromCloud(userId)
        if (cloudProfile != null) {
            dao.saveUserProfile(cloudProfile)
            generateAndSaveRoutines(cloudProfile)
        }

        val cloudSessions = firestoreSyncManager.fetchLoggedSessionsFromCloud(userId)
        for ((session, sets) in cloudSessions) {
            logWorkoutSession(session, sets)
        }
    }

    suspend fun backupAllLocalDataToCloud(userId: String) {
        val profile = dao.getUserProfile().firstOrNull()
        val sessions = dao.getAllSessions().firstOrNull() ?: emptyList()
        firestoreSyncManager.backupAllLocalDataToCloud(
            userId = userId,
            profile = profile,
            sessions = sessions,
            fetchSetsForSession = { sessionId -> getSetsForSessionList(sessionId) }
        )
    }
}
