package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import android.media.AudioManager
import android.media.ToneGenerator
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import com.example.ui.components.CustomFlowRow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseLibraryRepository
import com.example.data.LoggedSetEntity
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.components.AnimatedSetCompletionButton
import com.example.ui.components.BaselineCalibrationDialog
import com.example.ui.components.BuiltInIntervalTimerCard
import com.example.ui.components.RestIntervalTimerModalSheetContent
import com.example.ui.components.ExerciseSubstitutionDialog
import com.example.ui.components.ExerciseVideoPlayerBox
import com.example.ui.components.PreWorkoutMobilityCard
import com.example.ui.components.QuickPlateCalculatorDialog
import com.example.ui.components.ProgressiveOverloadTag
import com.example.ui.components.RpeEffortCalibrationDialog
import com.example.ui.components.SmartWarmupDialog
import com.example.ui.components.WarmupSetStep
import com.example.ui.components.WorkoutExitConfirmationDialog
import com.example.ui.components.VitalHapticFeedback
import com.example.ui.theme.TelemetryNumeralStyle
import com.example.ui.theme.TelemetryTimerStyle
import com.example.ui.theme.VitalShapes
import com.example.ui.theme.VitalSpacing

data class ExerciseLogState(
    val exerciseName: String,
    val primaryGoal: String,
    val coachingCues: String,
    val sets: MutableList<SetLogInput> = mutableStateListOf(),
    var formNotes: String = ""
)

data class SetLogInput(
    var setNumber: Int,
    var weightText: String,
    var repsText: String,
    var rpe: Int = 8,
    var jointFeel: String = "Comfortable",
    var isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActiveLoggerScreen(
    routine: WorkoutRoutineEntity?,
    exercises: List<WorkoutExerciseEntity>,
    personalBests: Map<String, Float> = emptyMap(),
    userProfile: com.example.data.UserProfileEntity? = null,
    viewModel: com.example.ui.VitalViewModel,
    onSaveSession: (routineTitle: String, sets: List<LoggedSetEntity>, feel: String, notes: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val isReducedMotion = remember(context) {
        try {
            val animScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            val transScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            )
            animScale == 0f || transScale == 0f
        } catch (_: Throwable) {
            false
        }
    }

    var activeDictationCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                activeDictationCallback?.invoke(spokenText)
            }
        }
    }

    // Routine Title
    val routineTitle = routine?.dayName ?: "Live Longevity Workout"
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Exercise log states
    val exerciseLogs = remember(exercises) { mutableStateListOf<ExerciseLogState>() }

    LaunchedEffect(exercises) {
        exerciseLogs.clear()
        exercises.forEach { ex ->
            val setInputs = mutableStateListOf<SetLogInput>()
            
            // Try to get last weight
            val lastSet = viewModel.getLastSetForExercise(ex.exerciseName)
            val initialWeight = lastSet?.weightLbs?.toInt()?.toString()
                ?: personalBests[ex.exerciseName]?.let { if (it > 0f) "${it.toInt()}" else null }
                ?: run {
                    // Infer baseline load based on strength level
                    when (userProfile?.strengthLevel) {
                        "Beginner" -> "15"
                        "Advanced" -> "45"
                        else -> "25" // Intermediate or null
                    }
                }
            
            for (s in 1..ex.sets) {
                setInputs.add(
                    SetLogInput(
                        setNumber = s,
                        weightText = initialWeight,
                        repsText = ex.repRange.substringBefore("-").filter { it.isDigit() }.ifEmpty { "8" },
                        rpe = 8,
                        jointFeel = "Comfortable"
                    )
                )
            }
            exerciseLogs.add(
                ExerciseLogState(
                    exerciseName = ex.exerciseName,
                    primaryGoal = ex.primaryGoal,
                    coachingCues = ex.coachingCues,
                    sets = setInputs
                )
            )
        }
    }

    var overallFeel by remember { mutableStateOf("Strong & Energized") }
    var notesText by remember { mutableStateOf("") }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showIncompleteSetsWarning by remember { mutableStateOf(false) }
    var showRpeInfoDialog by remember { mutableStateOf(false) }
    var activeFormDemoExercise by remember { mutableStateOf<String?>(null) }
    var rpePickerSet by remember { mutableStateOf<Pair<SetLogInput, String>?>(null) }
    
    // Deeper Redesign & Safety States
    var isHeaderCollapsed by remember { mutableStateOf(false) }
    var swapExerciseDialogTarget by remember { mutableStateOf<String?>(null) }
    var rpeCalibrationDialogSet by remember { mutableStateOf<Pair<SetLogInput, String>?>(null) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    var smartWarmupDialogTarget by remember { mutableStateOf<Pair<String, Float>?>(null) }
    var baselineCalibrationTarget by remember { mutableStateOf<String?>(null) }
    var quickPlateCalcTarget by remember { mutableStateOf<Pair<String, SetLogInput>?>(null) }
    var pendingDeleteSetTarget by remember { mutableStateOf<Triple<Int, Int, SetLogInput>?>(null) }

    // Rest Timer state
    var targetRestSeconds by remember { mutableIntStateOf(90) }
    var timerRemainingSeconds by remember { mutableIntStateOf(90) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var activeTimerExerciseName by remember { mutableStateOf("") }
    var timerAlertMode by remember { mutableStateOf("Sound + Vibrate") }
    var showTimerSettingsSheet by remember { mutableStateOf(false) }

    // Real-time 1-second cadence countdown timer
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerRemainingSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            if (isTimerRunning) {
                if (timerRemainingSeconds > 0) {
                    timerRemainingSeconds -= 1
                }
                if (timerRemainingSeconds <= 0) {
                    timerRemainingSeconds = 0
                    isTimerRunning = false
                }
            }
        }
    }

    // Multi-modal Rest Timer Feedback (Sound + Vibrate, Vibrate Only, Silent)
    LaunchedEffect(timerRemainingSeconds) {
        if (timerRemainingSeconds in 1..3 && isTimerRunning) {
            if (timerAlertMode != "Silent") {
                VitalHapticFeedback.timerTick(context, haptic)
            }
        } else if (timerRemainingSeconds == 0 && targetRestSeconds > 0) {
            if (timerAlertMode != "Silent") {
                VitalHapticFeedback.timerComplete(context, haptic)
            }
            if (timerAlertMode == "Sound + Vibrate") {
                try {
                    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
                    // Release tone generator after beep duration to prevent native audio track leaks
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(400L)
                        try {
                            toneGenerator.release()
                        } catch (_: Throwable) {
                            // Ignored
                        }
                    }
                } catch (_: Throwable) {
                    // Fallback if audio fails or cannot be initialized
                }
            }
        }
    }

    fun startRestTimer(seconds: Int, exerciseName: String = "") {
        targetRestSeconds = seconds
        timerRemainingSeconds = seconds
        if (exerciseName.isNotBlank()) {
            activeTimerExerciseName = exerciseName
        }
        isTimerRunning = true
    }

    val totalCompletedSets = exerciseLogs.sumOf { log -> log.sets.count { it.isCompleted } }
    val totalPrescribedSets = exerciseLogs.sumOf { log -> log.sets.size }
    val isFinishEnabled = totalCompletedSets > 0

    val executeSaveAndComplete = {
        VitalHapticFeedback.exerciseComplete(context, haptic)
        val allLoggedSets = mutableListOf<LoggedSetEntity>()
        exerciseLogs.forEach { log ->
            log.sets.forEach { setInput ->
                val w = setInput.weightText.toFloatOrNull() ?: 0f
                val r = setInput.repsText.toIntOrNull() ?: 0
                allLoggedSets.add(
                    LoggedSetEntity(
                        sessionId = 0,
                        exerciseName = log.exerciseName,
                        setNumber = setInput.setNumber,
                        weightLbs = w,
                        repsCompleted = r,
                        rpeActual = setInput.rpe,
                        jointFeel = setInput.jointFeel
                    )
                )
            }
        }

        val combinedNotes = buildString {
            exerciseLogs.forEach { log ->
                if (log.formNotes.isNotBlank()) {
                    append("[${log.exerciseName}]: ${log.formNotes}\n")
                }
            }
            if (notesText.isNotBlank()) {
                append(notesText)
            }
        }.trim()

        onSaveSession(routineTitle, allLoggedSets, overallFeel, combinedNotes)
        showCompletionDialog = true
    }

    // Back Navigation Guard (WCAG/Heuristic Error Prevention)
    androidx.activity.compose.BackHandler(enabled = true) {
        if (totalCompletedSets > 0) {
            showExitConfirmationDialog = true
        } else {
            onCancel()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    if (isHeaderCollapsed) {
                        // COLLAPSED ULTRA-SLIM STRIP (Max Space Savings)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = routineTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$totalCompletedSets/$totalPrescribedSets Sets",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Expand Header Button
                                IconButton(
                                    onClick = { isHeaderCollapsed = false },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand Header",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { showExitConfirmationDialog = true },
                                    modifier = Modifier.size(32.dp).testTag("cancel_workout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close workout",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // STREAMLINED EXPANDED HEADER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = routineTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (totalCompletedSets > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = "Saved ✓",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "$totalCompletedSets of $totalPrescribedSets sets logged · Target RPE 7–8",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (totalCompletedSets > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Collapse Header Button
                                IconButton(
                                    onClick = { isHeaderCollapsed = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Collapse Header",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { showExitConfirmationDialog = true },
                                    modifier = Modifier.size(32.dp).testTag("cancel_workout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close workout",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // DOCKED PERSISTENT REST TIMER HUD (Ensures Visibility of System Status during long scrolling)
                    AnimatedVisibility(
                        visible = isTimerRunning || timerRemainingSeconds > 0,
                        enter = if (isReducedMotion) fadeIn(tween(0)) else slideInVertically(
                            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                            initialOffsetY = { it }
                        ) + fadeIn(animationSpec = tween(120)),
                        exit = if (isReducedMotion) fadeOut(tween(0)) else slideOutVertically(
                            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing),
                            targetOffsetY = { it }
                        ) + fadeOut(animationSpec = tween(100))
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                            shadowElevation = 4.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                    val isCompactWidth = maxWidth < 380.dp

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1f, fill = false)
                                                .clickable { showTimerSettingsSheet = true },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Timer,
                                                        contentDescription = "Rest Interval Active",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    if (!isCompactWidth) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "REST",
                                                            style = com.example.ui.theme.TelemetryBadgeStyle,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${timerRemainingSeconds / 60}:${"%02d".format(timerRemainingSeconds % 60)}",
                                                style = TelemetryTimerStyle,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                            if (activeTimerExerciseName.isNotBlank()) {
                                                val (timerTitle, _) = parseExerciseName(activeTimerExerciseName)
                                                val cleanTimerTitle = timerTitle.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "· $cleanTimerTitle",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.widthIn(max = if (isCompactWidth) 90.dp else 120.dp)
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { showTimerSettingsSheet = true },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Tune,
                                                    contentDescription = "Configure Rest Presets",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    timerRemainingSeconds = (timerRemainingSeconds + 30)
                                                    VitalHapticFeedback.timerButtonTap(context, haptic)
                                                },
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Text(
                                                    text = "+30S",
                                                    style = com.example.ui.theme.TelemetryBadgeStyle,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    isTimerRunning = !isTimerRunning
                                                    VitalHapticFeedback.timerButtonTap(context, haptic)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = if (isTimerRunning) "Pause Rest" else "Resume Rest",
                                                    tint = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    timerRemainingSeconds = 0
                                                    isTimerRunning = false
                                                    VitalHapticFeedback.timerButtonTap(context, haptic)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Skip Rest",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                if (targetRestSeconds > 0) {
                                    val progress = (timerRemainingSeconds.toFloat() / targetRestSeconds.toFloat()).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        strokeCap = StrokeCap.Square
                                    )
                                }
                            }
                        }
                    }

                    if (isFinishEnabled) {
                        Button(
                            onClick = {
                                val uncompletedCount = exerciseLogs.flatMap { it.sets }.count { !it.isCompleted }
                                if (uncompletedCount > 0) {
                                    showIncompleteSetsWarning = true
                                } else {
                                    executeSaveAndComplete()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("finish_workout_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Finish")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Finish Workout ($totalCompletedSets/$totalPrescribedSets Sets)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        // Distinct informational guidance banner (non-button affordance)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Guidance Info",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log at least 1 set to complete session",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
            // 5-Minute Pre-Workout Dynamic Mobility Routine
            PreWorkoutMobilityCard(
                exercises = exercises,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Exercise Cards Loop
            exerciseLogs.forEachIndexed { exIndex, logState ->
                val (cleanTitle, altName) = parseExerciseName(logState.exerciseName)
                val currentPr = personalBests[logState.exerciseName] ?: 0f

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = VitalSpacing.lg)
                        .testTag("exercise_log_card_$exIndex"),
                    shape = VitalShapes.Medium,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(VitalSpacing.lg)) {
                        // Exercise Card Header (Clean, Monolithic Athletic Voice)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Primary Row: Number + Title on Left, Secondary Quick Tools on Right
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(VitalSpacing.sm)
                                ) {
                                    Surface(
                                        shape = VitalShapes.Micro,
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "${exIndex + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = VitalSpacing.xxs)
                                        )
                                    }
                                    Text(
                                        text = cleanTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.width(VitalSpacing.sm))

                                // Secondary Tools grouped cleanly
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(VitalSpacing.xxs)
                                ) {
                                    IconButton(
                                        onClick = { activeFormDemoExercise = logState.exerciseName },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartDisplay,
                                            contentDescription = "Video Demo for $cleanTitle",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val firstSet = logState.sets.firstOrNull()
                                            val currentWeight = firstSet?.weightText?.toFloatOrNull() ?: currentPr
                                            smartWarmupDialogTarget = Pair(logState.exerciseName, if (currentWeight > 0f) currentWeight else 95f)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = "Warmup and Plates for $cleanTitle",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { swapExerciseDialogTarget = logState.exerciseName },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Swap $cleanTitle",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(VitalSpacing.xs))

                            // Secondary Row: GoalBadge, PR Badge, and altName in adaptive FlowRow to prevent right-side overcrowding
                            CustomFlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalSpacing = VitalSpacing.sm,
                                verticalSpacing = VitalSpacing.xs
                            ) {
                                GoalBadge(
                                    goal = logState.primaryGoal
                                )
                                if (currentPr > 0f) {
                                    Surface(
                                        shape = VitalShapes.Micro,
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = VitalSpacing.xxs),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(VitalSpacing.xs)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                text = "PR: ${currentPr.toInt()} lbs",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                }
                                if (altName != null) {
                                    Text(
                                        text = "• $altName",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Structured Coaching Cue Box
                        if (logState.coachingCues.isNotBlank()) {
                            Spacer(modifier = Modifier.height(VitalSpacing.sm))
                            Surface(
                                shape = VitalShapes.Small,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(VitalSpacing.sm),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Joint Safety Cue",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(VitalSpacing.xs))
                                    Text(
                                        text = logState.coachingCues,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(VitalSpacing.md))

                        // Set Rows (Streamlined Flat Container)
                        logState.sets.forEachIndexed { setIndex, setInput ->
                            val cardBgColor by animateColorAsState(
                                targetValue = if (setInput.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                animationSpec = tween(durationMillis = if (isReducedMotion) 0 else 180, easing = FastOutSlowInEasing),
                                label = "set_card_bg_${exIndex}_$setIndex"
                            )
                            val cardBorderColor by animateColorAsState(
                                targetValue = if (setInput.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                animationSpec = tween(durationMillis = if (isReducedMotion) 0 else 180, easing = FastOutSlowInEasing),
                                label = "set_card_border_${exIndex}_$setIndex"
                            )
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .testTag("set_card_${exIndex}_$setIndex"),
                                shape = RoundedCornerShape(8.dp),
                                color = cardBgColor,
                                border = BorderStroke(1.dp, cardBorderColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    // Tier 1: Set Number Badge & Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (setInput.isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                            border = BorderStroke(1.dp, if (setInput.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Text(
                                                text = "SET ${setInput.setNumber}",
                                                style = com.example.ui.theme.TelemetryBadgeStyle,
                                                color = if (setInput.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Complete Set Button
                                            IconButton(
                                                onClick = {
                                                    if (!setInput.isCompleted) {
                                                        VitalHapticFeedback.exerciseComplete(context, haptic)
                                                        setInput.isCompleted = true
                                                        logState.sets.forEachIndexed { i, s ->
                                                            if (i > setIndex && !s.isCompleted) {
                                                                s.weightText = setInput.weightText
                                                                s.repsText = setInput.repsText
                                                            }
                                                        }
                                                        val calculatedRest = when {
                                                            setInput.rpe >= 9 -> 120
                                                            setInput.rpe == 8 -> 90
                                                            else -> 60
                                                        }
                                                        startRestTimer(calculatedRest, logState.exerciseName)
                                                        val currentWeight = setInput.weightText.toFloatOrNull() ?: 0f
                                                        val currentReps = setInput.repsText.toIntOrNull() ?: 0
                                                        val setVolume = (currentWeight * currentReps).toInt()

                                                        // Propagate weight & reps forward to next uncompleted sets so user doesn't have to re-type
                                                        for (nextIdx in (setIndex + 1) until logState.sets.size) {
                                                            val nextSet = logState.sets[nextIdx]
                                                            if (!nextSet.isCompleted) {
                                                                nextSet.weightText = setInput.weightText
                                                                nextSet.repsText = setInput.repsText
                                                            }
                                                        }

                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                message = "Set ${setInput.setNumber} completed! Great work.",
                                                                duration = androidx.compose.material3.SnackbarDuration.Short
                                                            )
                                                        }
                                                    } else {
                                                        VitalHapticFeedback.exerciseUnmarked(context, haptic)
                                                        setInput.isCompleted = false
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .testTag("set_complete_button_${exIndex}_$setIndex")
                                            ) {
                                                Icon(
                                                    imageVector = if (setInput.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                    contentDescription = if (setInput.isCompleted) "Set Completed" else "Mark Set Complete",
                                                    tint = if (setInput.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

                                            // Delete Set Button
                                            if (logState.sets.size > 1) {
                                                IconButton(
                                                    onClick = {
                                                        if (setInput.isCompleted) {
                                                            pendingDeleteSetTarget = Triple(exIndex, setIndex, setInput)
                                                        } else {
                                                            val removedSet = logState.sets.removeAt(setIndex)
                                                            logState.sets.forEachIndexed { idx, s -> s.setNumber = idx + 1 }
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            coroutineScope.launch {
                                                                val result = snackbarHostState.showSnackbar(
                                                                    message = "${logState.exerciseName} Set ${setIndex + 1} deleted",
                                                                    actionLabel = "Undo",
                                                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                                                )
                                                                if (result == SnackbarResult.ActionPerformed) {
                                                                    logState.sets.add(setIndex.coerceAtMost(logState.sets.size), removedSet)
                                                                    logState.sets.forEachIndexed { idx, s -> s.setNumber = idx + 1 }
                                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                }
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .testTag("delete_set_button_${exIndex}_$setIndex")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Set ${setIndex + 1}",
                                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Tier 2: Weight & Reps Steppers in Equal Weight Columns
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Weight Input Field
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Weight (lbs)",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                // Direct In-Line Plate Calculator helper button
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clickable {
                                                            quickPlateCalcTarget = Pair(logState.exerciseName, setInput)
                                                        }
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.FitnessCenter,
                                                        contentDescription = "Quick Plate Calculator",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "Plates",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            CompactGymStepper(
                                                valueText = setInput.weightText,
                                                onValueChange = { setInput.weightText = it },
                                                label = "Weight",
                                                step = 5f,
                                                modifier = Modifier.fillMaxWidth().testTag("weight_input_${exIndex}_$setIndex")
                                            )
                                        }

                                        // Reps Input Field
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Reps",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                val pb = personalBests[logState.exerciseName]
                                                if (pb != null && pb > 0f) {
                                                    Text(
                                                        text = "Best: ${pb.toInt()} lbs",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            CompactGymStepper(
                                                valueText = setInput.repsText,
                                                onValueChange = { setInput.repsText = it },
                                                label = "Reps",
                                                step = 1f,
                                                modifier = Modifier.fillMaxWidth().testTag("reps_input_${exIndex}_$setIndex")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Descriptive Header for Joint Comfort & Intensity (Plain-Language Usability)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Joint Comfort",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                        val rpeDesc = when (setInput.rpe) {
                                            6 -> "Light (4+ in reserve)"
                                            7 -> "Moderate (3 in reserve)"
                                            8 -> "Challenging (2 in reserve)"
                                            9 -> "Hard (1 in reserve)"
                                            10 -> "Max Effort"
                                            else -> "Effort ${setInput.rpe}/10"
                                        }
                                        Text(
                                            text = "Effort: $rpeDesc",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Compact Secondary Metadata Row (Joint Feel & RPE / RIR)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Joint Feel Selector (Clean Architectural Toggle)
                                        val jointBorder = when (setInput.jointFeel) {
                                            "Comfortable" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                            "Mild Tension" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                            else -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                        }
                                        val jointFg = when (setInput.jointFeel) {
                                            "Comfortable" -> MaterialTheme.colorScheme.primary
                                            "Mild Tension" -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                        val jointIcon = when (setInput.jointFeel) {
                                            "Comfortable" -> Icons.Default.Shield
                                            else -> Icons.Default.Warning
                                        }
                                        val jointLabel = when (setInput.jointFeel) {
                                            "Comfortable" -> "Joints: Good"
                                            "Mild Tension" -> "Joints: Tension"
                                            else -> "Joints: Strain"
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .height(48.dp)
                                                .clickable {
                                                    setInput.jointFeel = when (setInput.jointFeel) {
                                                        "Comfortable" -> "Mild Tension"
                                                        "Mild Tension" -> "Joint Strain"
                                                        else -> "Comfortable"
                                                    }
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, jointBorder)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = jointIcon,
                                                    contentDescription = null,
                                                    tint = jointFg,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = jointLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        // RPE / RIR Selector with 5 Enriched Quick-Tap Segmented Buttons
                                        Row(
                                            modifier = Modifier.weight(1.15f),
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            listOf(6, 7, 8, 9, 10).forEach { valRpe ->
                                                val isSelected = setInput.rpe == valRpe
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                        .clickable {
                                                            setInput.rpe = valRpe
                                                        },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) {
                                                        if (valRpe in 7..8) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                                                    } else MaterialTheme.colorScheme.surfaceVariant,
                                                    border = BorderStroke(
                                                        width = if (isSelected) 1.5.dp else 1.dp,
                                                        color = if (isSelected) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else if (valRpe == 8) {
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                                        } else {
                                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                                        }
                                                    )
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "$valRpe",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                            color = if (isSelected) {
                                                                if (valRpe in 7..8) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                                                            } else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    rpePickerSet = Pair(setInput, logState.exerciseName)
                                                },
                                                modifier = Modifier
                                                    .padding(start = 2.dp)
                                                    .size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = "RPE Guide",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Set Row Actions: Add Set, Repeat Previous Set & Apply Set 1 to All
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(
                                    onClick = {
                                        logState.sets.add(
                                            SetLogInput(
                                                setNumber = logState.sets.size + 1,
                                                weightText = logState.sets.lastOrNull()?.weightText ?: "25",
                                                repsText = logState.sets.lastOrNull()?.repsText ?: "8",
                                                rpe = 8
                                            )
                                        )
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.testTag("add_set_button_$exIndex")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Set", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Set", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                }

                                if (logState.sets.isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            val lastSet = logState.sets.last()
                                            logState.sets.add(
                                                SetLogInput(
                                                    setNumber = logState.sets.size + 1,
                                                    weightText = lastSet.weightText,
                                                    repsText = lastSet.repsText,
                                                    rpe = lastSet.rpe,
                                                    jointFeel = lastSet.jointFeel,
                                                    isCompleted = false
                                                )
                                            )
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        modifier = Modifier.testTag("repeat_last_set_button_$exIndex")
                                    ) {
                                        Icon(imageVector = Icons.Default.Replay, contentDescription = "Repeat Previous Set", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Repeat Set", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            if (logState.sets.size > 1) {
                                Surface(
                                    modifier = Modifier.clickable {
                                        val firstSet = logState.sets.firstOrNull()
                                        if (firstSet != null) {
                                            logState.sets.forEachIndexed { i, s ->
                                                if (i > 0) {
                                                    s.weightText = firstSet.weightText
                                                    s.repsText = firstSet.repsText
                                                }
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Match Sets",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Copy Set 1 to All",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Voice-to-Text Form Notes
                        OutlinedTextField(
                            value = logState.formNotes,
                            onValueChange = { logState.formNotes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_form_notes_$exIndex"),
                            label = { Text("${cleanTitle} Post-Set Notes") },
                            placeholder = { Text("Tap mic to dictate form feedback or observations...") },
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate form notes for $cleanTitle")
                                        }
                                        activeDictationCallback = { text ->
                                            logState.formNotes = if (logState.formNotes.isNotBlank()) "${logState.formNotes} $text" else text
                                        }
                                        try {
                                            speechLauncher.launch(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Voice dictation not supported on this device", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.testTag("dictate_exercise_notes_$exIndex")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Dictate Form Notes",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Overall Session Feel & Notes Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Session Energy & Joint Response",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val feelOptions = listOf("Strong & Energized", "Challenging but Good", "Joint Discomfort / Scaled")
                    feelOptions.forEach { feel ->
                        FilterChip(
                            selected = overallFeel == feel,
                            onClick = { overallFeel = feel },
                            label = { Text(feel, fontWeight = if (overallFeel == feel) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("session_notes_input"),
                        label = { Text("Coaching Notes / Joint Observations") },
                        placeholder = { Text("e.g. Felt great on Goblet Squats, increased weight +5 lbs") },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate workout session coaching notes")
                                    }
                                    activeDictationCallback = { text ->
                                        notesText = if (notesText.isNotBlank()) "$notesText $text" else text
                                    }
                                    try {
                                        speechLauncher.launch(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Voice dictation not supported on this device", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("dictate_session_notes_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Dictate Notes",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(220.dp))
                }
            }
        }

    // Modal Rest Interval Timer Configuration & Presets Sheet
    if (showTimerSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimerSettingsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                RestIntervalTimerModalSheetContent(
                    activeExerciseName = activeTimerExerciseName,
                    targetRestSeconds = targetRestSeconds,
                    isRunning = isTimerRunning,
                    remainingSeconds = timerRemainingSeconds,
                    alertMode = timerAlertMode,
                    onAlertModeChange = { timerAlertMode = it },
                    onTogglePlayPause = {
                        VitalHapticFeedback.timerButtonTap(context, haptic)
                        isTimerRunning = !isTimerRunning
                    },
                    onResetTimer = { newTarget ->
                        VitalHapticFeedback.timerButtonTap(context, haptic)
                        timerRemainingSeconds = newTarget
                        isTimerRunning = true
                    },
                    onAdjustSeconds = { delta ->
                        VitalHapticFeedback.timerButtonTap(context, haptic)
                        timerRemainingSeconds = (timerRemainingSeconds + delta).coerceAtLeast(0)
                    },
                    onPresetSelected = { seconds ->
                        VitalHapticFeedback.timerButtonTap(context, haptic)
                        targetRestSeconds = seconds
                        timerRemainingSeconds = seconds
                        isTimerRunning = true
                    },
                    onClose = { showTimerSettingsSheet = false },
                    showCloseButton = true,
                    showDoneButton = true
                )
            }
        }
    }

    // Modal RPE Selector (Improved UX with Bottom Sheet)
    if (rpePickerSet != null) {
        val (targetSet, exerciseName) = rpePickerSet!!
        ModalBottomSheet(
            onDismissRequest = { rpePickerSet = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Intensity (RPE)",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Set ${targetSet.setNumber} on $exerciseName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showRpeInfoDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "RPE & RIR Guide",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                val rpeOptions = listOf(
                    Triple(6, "RPE 6 · 4+ RIR", "Warmup / speed work. Very low neural fatigue."),
                    Triple(7, "RPE 7 · 3 RIR", "Crisp velocity. Excellent form threshold for beginners."),
                    Triple(8, "RPE 8 · 2 RIR", "Optimal Osteogenic Zone · High axial load without breakdown."),
                    Triple(9, "RPE 9 · 1 RIR", "Near failure. 1 grinding rep left in reserve."),
                    Triple(10, "RPE 10 · 0 RIR", "Absolute max limit. Zero reps remaining.")
                )

                rpeOptions.forEach { (valRpe, title, sub) ->
                    val isSelected = targetSet.rpe == valRpe
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                targetSet.rpe = valRpe
                                rpePickerSet = null
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (valRpe == 8) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (valRpe == 8) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "OPTIMAL",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            softWrap = false,
                                            maxLines = 1,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { rpePickerSet = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Comprehensive RPE & RIR Guide Dialog
    if (showRpeInfoDialog) {
        AlertDialog(
            onDismissRequest = { showRpeInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "RPE Guide",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RPE & RIR Explained", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "RPE (Rate of Perceived Exertion) measures workout intensity. RIR (Reps in Reserve) is how many more clean reps you could complete before failure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = "Why RPE 7–8 for Bone Density?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Clinical research shows that loading bones at 70–85% 1RM (RPE 7–8, 2–3 reps in reserve) creates peak osteogenic mechanotransduction while protecting spinal and joint integrity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "Intensity Scale Quick Reference:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    listOf(
                        "RPE 6 (4+ RIR)" to "Light warmup. Weight moves briskly.",
                        "RPE 7 (3 RIR)" to "Moderate load. Bar speed is crisp.",
                        "RPE 8 (2 RIR)" to "Ideal Working Zone. 2 reps left in reserve.",
                        "RPE 9 (1 RIR)" to "Near maximal effort. 1 grinding rep left.",
                        "RPE 10 (0 RIR)" to "Absolute failure. No more reps possible."
                    ).forEach { (scale, desc) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• $scale: ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRpeInfoDialog = false }) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Incomplete Sets Warning (Improved UX with Bottom Sheet)
    if (showIncompleteSetsWarning) {
        val uncompletedSets = exerciseLogs.flatMap { it.sets }.count { !it.isCompleted }
        ModalBottomSheet(
            onDismissRequest = { showIncompleteSetsWarning = false },
            sheetState = rememberModalBottomSheetState(),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Finish Workout Early?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val uncompletedText = if (uncompletedSets == 1) "1 set" else "$uncompletedSets sets"
                val completedText = if (totalCompletedSets == 1) "1 completed set" else "$totalCompletedSets completed sets"

                Text(
                    text = "You have $uncompletedText left unlogged. Would you like to finish and record your $completedText, or return to logging?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        showIncompleteSetsWarning = false
                        executeSaveAndComplete()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Finish & Save Progress", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = { showIncompleteSetsWarning = false },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Keep Training", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Workout Completion Summary (Improved UX with Bottom Sheet)
    if (showCompletionDialog) {
        ModalBottomSheet(
            onDismissRequest = { 
                showCompletionDialog = false
                onCancel() 
            },
            sheetState = rememberModalBottomSheetState(),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF00C853).copy(alpha = 0.2f),
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00C853),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Session Mastered!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "High-intensity loading confirmed. Osteogenic remodeling stimulated for 48-72 hours.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sets logged", style = MaterialTheme.typography.labelMedium)
                            Text("$totalCompletedSets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Intensity", style = MaterialTheme.typography.labelMedium)
                            Text("8.2 avg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        val summaryText = buildString {
                            appendLine("🏋️ Workout Summary: $routineTitle")
                            appendLine("Total Completed Sets: $totalCompletedSets")
                            appendLine("-------------------")
                            exerciseLogs.forEach { log ->
                                appendLine("• ${log.exerciseName}:")
                                log.sets.forEach { set ->
                                    appendLine("  Set ${set.setNumber}: ${set.weightText} lbs × ${set.repsText} reps (RPE ${set.rpe})")
                                }
                            }
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summaryText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Export Workout Summary")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Workout Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Return to Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // Exercise Video Player Dialog
    if (activeFormDemoExercise != null) {
        Dialog(
            onDismissRequest = { activeFormDemoExercise = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 16.dp)
            ) {
                ExerciseVideoPlayerBox(
                    exerciseName = activeFormDemoExercise ?: "Compound Lift",
                    onDismiss = { activeFormDemoExercise = null }
                )
            }
        }
    }

    // Exercise Substitution / Spine-Sparing Alternatives Dialog
    if (swapExerciseDialogTarget != null) {
        ExerciseSubstitutionDialog(
            currentExerciseName = swapExerciseDialogTarget ?: "",
            onDismiss = { swapExerciseDialogTarget = null },
            onSelectAlternative = { newExerciseName ->
                val targetName = swapExerciseDialogTarget
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetName }
                if (foundIndex != -1) {
                    val currentSets = exerciseLogs[foundIndex].sets
                    val defaultEx = ExerciseLibraryRepository.exercises.firstOrNull { it.name.equals(newExerciseName, ignoreCase = true) }
                    exerciseLogs[foundIndex] = ExerciseLogState(
                        exerciseName = newExerciseName,
                        primaryGoal = defaultEx?.targetBonesAndJoints ?: "Osteogenic Compound",
                        coachingCues = defaultEx?.proFormTips?.joinToString("; ") ?: "Maintain neutral spine; Controlled tempo",
                        sets = currentSets
                    )
                }
                swapExerciseDialogTarget = null
            }
        )
    }

    // Quick In-Workout Plate Calculator Dialog
    if (quickPlateCalcTarget != null) {
        val (exName, targetSet) = quickPlateCalcTarget!!
        val initialWeight = targetSet.weightText.toFloatOrNull() ?: 135f
        QuickPlateCalculatorDialog(
            initialWeight = initialWeight,
            exerciseName = exName,
            onApplyWeight = { newWeight ->
                targetSet.weightText = if (newWeight % 1f == 0f) "${newWeight.toInt()}" else "$newWeight"
                quickPlateCalcTarget = null
            },
            onDismiss = { quickPlateCalcTarget = null }
        )
    }

    // Comprehensive Interactive RPE & RIR Calibration Dialog
    if (rpeCalibrationDialogSet != null) {
        val (targetSet, exName) = rpeCalibrationDialogSet!!
        RpeEffortCalibrationDialog(
            currentRpe = targetSet.rpe,
            exerciseName = exName,
            onDismiss = { rpeCalibrationDialogSet = null },
            onSelectRpe = { selectedRpe ->
                targetSet.rpe = selectedRpe
                rpeCalibrationDialogSet = null
            }
        )
    }

    // Workout Exit Disambiguation Dialog
    if (showExitConfirmationDialog) {
        WorkoutExitConfirmationDialog(
            routineTitle = routineTitle,
            completedSetsCount = totalCompletedSets,
            totalSetsCount = totalPrescribedSets,
            onSavePartial = {
                showExitConfirmationDialog = false
                executeSaveAndComplete()
            },
            onDiscard = {
                showExitConfirmationDialog = false
                onCancel()
            },
            onKeepTraining = {
                showExitConfirmationDialog = false
            },
            onDismiss = {
                showExitConfirmationDialog = false
            }
        )
    }

    // Smart Warm-Up Progression Ladder Dialog
    if (smartWarmupDialogTarget != null) {
        val (targetExName, targetWorkingWeight) = smartWarmupDialogTarget!!
        SmartWarmupDialog(
            exerciseName = targetExName,
            workingWeightLbs = targetWorkingWeight,
            onDismiss = { smartWarmupDialogTarget = null },
            onApplyWarmupSets = { warmupSteps ->
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetExName }
                if (foundIndex != -1) {
                    val exLog = exerciseLogs[foundIndex]
                    // Prepend warmup sets to the existing sets
                    val newSets = mutableStateListOf<SetLogInput>()
                    warmupSteps.forEachIndexed { idx, step ->
                        newSets.add(
                            SetLogInput(
                                setNumber = idx + 1,
                                weightText = step.targetWeightLbs.toInt().toString(),
                                repsText = step.recommendedReps.toString(),
                                rpe = 5 + idx,
                                jointFeel = "Warmup",
                                isCompleted = false
                            )
                        )
                    }
                    // Append remaining working sets
                    exLog.sets.forEachIndexed { sIdx, existingSet ->
                        newSets.add(
                            SetLogInput(
                                setNumber = warmupSteps.size + sIdx + 1,
                                weightText = existingSet.weightText,
                                repsText = existingSet.repsText,
                                rpe = existingSet.rpe,
                                jointFeel = existingSet.jointFeel,
                                isCompleted = existingSet.isCompleted
                            )
                        )
                    }
                    exerciseLogs[foundIndex] = exLog.copy(sets = newSets)
                }
                smartWarmupDialogTarget = null
            }
        )
    }

    // Safe Baseline Strength Calibration Dialog
    if (baselineCalibrationTarget != null) {
        val targetExName = baselineCalibrationTarget!!
        BaselineCalibrationDialog(
            exerciseName = targetExName,
            onDismiss = { baselineCalibrationTarget = null },
            onApplyStartingWeight = { calibratedWeight ->
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetExName }
                if (foundIndex != -1) {
                    val exLog = exerciseLogs[foundIndex]
                    exLog.sets.forEach { setInput ->
                        if (!setInput.isCompleted) {
                            setInput.weightText = "${calibratedWeight.toInt()}"
                        }
                    }
                }
                baselineCalibrationTarget = null
            }
        )
    }

    // Completed Set Deletion Confirmation Dialog
    if (pendingDeleteSetTarget != null) {
        val (exIdx, setIdx, targetSet) = pendingDeleteSetTarget!!
        val exerciseName = exerciseLogs.getOrNull(exIdx)?.exerciseName ?: "Exercise"
        AlertDialog(
            onDismissRequest = { pendingDeleteSetTarget = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Delete Completed Set?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "You are about to delete $exerciseName Set ${setIdx + 1} (${targetSet.weightText} lbs × ${targetSet.repsText} reps), which is marked as completed. This action will remove recorded training data.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val logState = exerciseLogs.getOrNull(exIdx)
                        if (logState != null && logState.sets.size > 1) {
                            val removedSet = logState.sets.removeAt(setIdx)
                            logState.sets.forEachIndexed { idx, s -> s.setNumber = idx + 1 }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "$exerciseName Set ${setIdx + 1} deleted",
                                    actionLabel = "Undo",
                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    logState.sets.add(setIdx.coerceAtMost(logState.sets.size), removedSet)
                                    logState.sets.forEachIndexed { idx, s -> s.setNumber = idx + 1 }
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        }
                        pendingDeleteSetTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSetTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Parses names like "Trap Bar / Dumbbell RDL" into primary title & subtitle alternative.
 */
private fun parseExerciseName(rawName: String): Pair<String, String?> {
    return when {
        rawName.contains(" / ") -> {
            val parts = rawName.split(" / ")
            Pair(parts[0], parts.getOrNull(1))
        }
        rawName.contains(" or ") -> {
            val parts = rawName.split(" or ")
            Pair(parts[0], parts.getOrNull(1))
        }
        else -> Pair(rawName, null)
    }
}

@Composable
fun CompactGymStepper(
    valueText: String,
    onValueChange: (String) -> Unit,
    label: String,
    step: Float = 1f,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var showDirectInputDialog by remember { mutableStateOf(false) }
    var tempInputText by remember { mutableStateOf(valueText) }

    Row(
        modifier = modifier
            .height(54.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val current = valueText.toFloatOrNull() ?: 0f
                val next = (current - step).coerceAtLeast(0f)
                onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease $label",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    tempInputText = valueText
                    showDirectInputDialog = true
                }
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = valueText.ifEmpty { "0" },
                    style = TelemetryNumeralStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label.uppercase(),
                    style = com.example.ui.theme.TelemetryBadgeStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 9.sp
                )
            }
        }
        
        IconButton(
            onClick = {
                val current = valueText.toFloatOrNull() ?: 0f
                val next = current + step
                onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase $label",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showDirectInputDialog) {
        AlertDialog(
            onDismissRequest = { showDirectInputDialog = false },
            title = { Text("Set $label", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter custom $label directly:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempInputText,
                        onValueChange = { tempInputText = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val sanitized = tempInputText.trim()
                    if (sanitized.isNotEmpty()) {
                        onValueChange(sanitized)
                    }
                    showDirectInputDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirectInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
