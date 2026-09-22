package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.VitalStrengthTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class BaselineTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun capture_dashboard_screenshot() {
        composeTestRule.setContent { 
            VitalStrengthTheme { 
                DashboardScreen() 
            } 
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard_baseline.png")
    }
}
