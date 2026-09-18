package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val context: Context) {

    private var authInstance: FirebaseAuth? = null

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private var authListener: FirebaseAuth.AuthStateListener? = null

    init {
        initializeAuth()
    }

    fun initializeAuth(): Boolean {
        try {
            var app: FirebaseApp? = if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseApp.getInstance()
            } else {
                null
            }

            if (app == null) {
                try {
                    app = FirebaseApp.initializeApp(context)
                } catch (e: Throwable) {
                    Log.d("FirebaseAuthManager", "Standard google-services.json not found: ${e.message}")
                }
            }

            if (app == null) {
                // Check if API keys/Project ID were provided in BuildConfig
                val apiKey = getBuildConfigValue("FIREBASE_API_KEY")?.takeIf {
                    it.isNotBlank() && it != "DEFAULT_FIREBASE_API_KEY"
                }
                val appId = getBuildConfigValue("FIREBASE_APP_ID")?.takeIf {
                    it.isNotBlank() && it != "DEFAULT_FIREBASE_APP_ID"
                }
                val projectId = getBuildConfigValue("FIREBASE_PROJECT_ID")?.takeIf {
                    it.isNotBlank() && it != "DEFAULT_FIREBASE_PROJECT_ID"
                }

                if (!apiKey.isNullOrBlank() && !appId.isNullOrBlank()) {
                    val builder = FirebaseOptions.Builder()
                        .setApiKey(apiKey)
                        .setApplicationId(appId)
                    if (!projectId.isNullOrBlank()) {
                        builder.setProjectId(projectId)
                    }
                    app = FirebaseApp.initializeApp(context, builder.build())
                }
            }

            if (app != null) {
                val firebaseAuth = FirebaseAuth.getInstance(app)
                authInstance = firebaseAuth
                _currentUser.value = firebaseAuth.currentUser
                _isConfigured.value = true

                authListener?.let { firebaseAuth.removeAuthStateListener(it) }
                val listener = FirebaseAuth.AuthStateListener { fa ->
                    _currentUser.value = fa.currentUser
                }
                firebaseAuth.addAuthStateListener(listener)
                authListener = listener
                return true
            } else {
                authInstance = null
                _isConfigured.value = false
                return false
            }
        } catch (e: Throwable) {
            Log.w("FirebaseAuthManager", "Firebase initialization error: ${e.message}")
            authInstance = null
            _isConfigured.value = false
            return false
        }
    }

    private fun getBuildConfigValue(fieldName: String): String? {
        return try {
            val clazz = Class.forName("com.example.BuildConfig")
            val field = clazz.getField(fieldName)
            field.get(null) as? String
        } catch (_: Throwable) {
            null
        }
    }

    fun refresh(): Boolean {
        return initializeAuth()
    }

    fun clearError() {
        _authError.value = null
    }

    suspend fun signInAnonymously(): FirebaseUser? {
        if (!initializeAuth() || authInstance == null) {
            _authError.value = "Firebase Auth is not set up. Please add google-services.json or Firebase credentials."
            return null
        }
        val currentAuth = authInstance ?: return null
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = currentAuth.signInAnonymously().await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Anonymous sign in failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): FirebaseUser? {
        if (!initializeAuth() || authInstance == null) {
            _authError.value = "Firebase Auth is not set up. Please add google-services.json or Firebase credentials."
            return null
        }
        val currentAuth = authInstance ?: return null
        if (email.isBlank() || pass.isBlank()) {
            _authError.value = "Email and password cannot be empty"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = currentAuth.signInWithEmailAndPassword(email, pass).await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            val rawMsg = e.localizedMessage ?: "Sign in failed"
            _authError.value = when {
                rawMsg.contains("operation is not allowed", ignoreCase = true) ||
                rawMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                    "Email/Password sign-in is not enabled in your Firebase Console. Please enable Email/Password under Authentication > Sign-in method."
                rawMsg.contains("user-not-found", ignoreCase = true) ||
                rawMsg.contains("invalid-credential", ignoreCase = true) ->
                    "Invalid email or password. Please check your credentials or create an account."
                rawMsg.contains("network", ignoreCase = true) ->
                    "Network error. Please verify internet connection."
                else -> rawMsg
            }
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): FirebaseUser? {
        if (!initializeAuth() || authInstance == null) {
            _authError.value = "Firebase Auth is not set up. Please add google-services.json or Firebase credentials."
            return null
        }
        val currentAuth = authInstance ?: return null
        if (email.isBlank() || pass.length < 6) {
            _authError.value = "Password must be at least 6 characters"
            return null
        }
        _isLoading.value = true
        _authError.value = null
        return try {
            val result = currentAuth.createUserWithEmailAndPassword(email, pass).await()
            _currentUser.value = result.user
            result.user
        } catch (e: Exception) {
            val rawMsg = e.localizedMessage ?: "Sign up failed"
            _authError.value = when {
                rawMsg.contains("operation is not allowed", ignoreCase = true) ||
                rawMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                    "Email/Password sign-up is disabled in this Firebase project. To fix: Open Firebase Console > Build > Authentication > Sign-in method, and enable 'Email/Password'."
                rawMsg.contains("email-already-in-use", ignoreCase = true) ->
                    "This email is already registered. Please sign in instead."
                rawMsg.contains("weak-password", ignoreCase = true) ->
                    "Password is too weak. Please use at least 6 characters."
                rawMsg.contains("network", ignoreCase = true) ->
                    "Network error. Please verify your internet connection."
                else -> rawMsg
            }
            null
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signInWithGoogle(webClientId: String): FirebaseUser? {
        if (!initializeAuth() || authInstance == null) {
            _authError.value = "Firebase Auth is not set up. Please add google-services.json or Firebase credentials."
            return null
        }
        val currentAuth = authInstance ?: return null
        _isLoading.value = true
        _authError.value = null
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResult = credentialManager.getCredential(context, request)
            val credential = credentialResult.credential

            if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                val authResult = currentAuth.signInWithCredential(firebaseCredential).await()
                _currentUser.value = authResult.user
                authResult.user
            } else {
                _authError.value = "Invalid credential type received"
                null
            }
        } catch (e: GetCredentialException) {
            _authError.value = "Google Sign-In canceled or unavailable: ${e.localizedMessage}"
            null
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Google Sign-In failed"
            null
        } finally {
            _isLoading.value = false
        }
    }

    fun signOut() {
        authInstance?.signOut()
        _currentUser.value = null
    }
}
