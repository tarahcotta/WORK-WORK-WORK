package com.example

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.components.SmartWarmupDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccessibilityVerificationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun smartWarmupDialog_hasAccessibleControlsAndSemantics() {
        var dismissed = false
        var appliedSets = 0

        composeTestRule.setContent {
            MaterialTheme {
                SmartWarmupDialog(
                    exerciseName = "Reverse Lunge or Step-Up",
                    workingWeightLbs = 100f,
                    onDismiss = { dismissed = true },
                    onApplyWarmupSets = { sets -> appliedSets = sets.size }
                )
            }
        }

        // 1. Verify Dialog Header and Heading semantics
        composeTestRule
            .onNodeWithText("Smart Warm-Up Ladder")
            .assertIsDisplayed()

        // 2. Verify Close button has clear TalkBack content description
        composeTestRule
            .onNodeWithContentDescription("Close Smart Warm-Up Ladder")
            .assertIsDisplayed()
            .assertHasClickAction()

        // 3. Verify Stepper controls have descriptive accessibility labels
        composeTestRule
            .onNodeWithContentDescription("Decrease target weight by 5 pounds")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Increase target weight by 5 pounds")
            .assertIsDisplayed()
            .assertHasClickAction()

        // 4. Verify Target Weight display and rest interval pills
        composeTestRule
            .onNodeWithText("Target Weight")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("100 lbs")
            .assertIsDisplayed()

        // 5. Test interaction with Stepper (+5 lbs)
        composeTestRule
            .onNodeWithContentDescription("Increase target weight by 5 pounds")
            .performClick()

        composeTestRule
            .onNodeWithText("105 lbs")
            .assertIsDisplayed()

        // 6. Verify and interact with action buttons
        composeTestRule
            .onNodeWithText("Close")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Add Warmups")
            .performScrollTo()
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        assert(appliedSets > 0)
    }
}
