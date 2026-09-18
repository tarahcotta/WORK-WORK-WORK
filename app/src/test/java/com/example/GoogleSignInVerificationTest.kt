package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.FirebaseAuthManager
import com.example.ui.components.GoogleLogoIcon
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GoogleSignInVerificationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun googleLogoIcon_rendersSuccessfully() {
        composeTestRule.setContent {
            MaterialTheme {
                GoogleLogoIcon(modifier = Modifier.size(24.dp).testTag("google_logo_test"))
            }
        }

        composeTestRule
            .onNodeWithTag("google_logo_test")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun firebaseAuthManager_resolvesWebClientId() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val authManager = FirebaseAuthManager(context)
        val webClientId = authManager.getWebClientId()

        assertNotNull("Web Client ID should not be null", webClientId)
        assertTrue("Web Client ID should be non-empty", webClientId.isNotBlank())
        assertTrue(
            "Web Client ID should match Google OAuth client format",
            webClientId.contains(".apps.googleusercontent.com")
        )
    }
}
