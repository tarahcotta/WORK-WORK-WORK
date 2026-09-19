package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.VitalHapticFeedback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Women's Strength", appName)
  }

  @Test
  fun `test MainActivity launches and renders compose`() {
    composeTestRule.waitForIdle()
    val activity = composeTestRule.activity
    assertNotNull(activity)
  }

  @Test
  fun `test VitalHapticFeedback all methods execute without throwing`() {
    val context = composeTestRule.activity
    VitalHapticFeedback.timerButtonTap(context)
    VitalHapticFeedback.timerTick(context)
    VitalHapticFeedback.timerComplete(context)
    VitalHapticFeedback.exerciseComplete(context)
    VitalHapticFeedback.exerciseUnmarked(context)
  }
}

