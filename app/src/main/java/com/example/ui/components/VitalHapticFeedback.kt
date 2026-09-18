package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Tactile vibration and haptic feedback helper for Vital Strength.
 * Provides distinct physical sensations for timer ticks, control taps,
 * timer completion, and exercise set completions during workouts.
 */
object VitalHapticFeedback {

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Tactile feedback for timer controls: play/pause, add/subtract seconds, reset, preset select.
     */
    fun timerButtonTap(context: Context, haptic: HapticFeedback? = null) {
        haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35L)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Subtle tactile tick for 3-2-1 countdown warnings.
     */
    fun timerTick(context: Context, haptic: HapticFeedback? = null) {
        haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20L, 100))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20L)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Celebratory double/triple tactile pulse when a rest interval timer completes (hits 0s).
     */
    fun timerComplete(context: Context, haptic: HapticFeedback? = null) {
        haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Waveform: 0ms initial delay, 120ms vibration, 80ms pause, 180ms vibration
                val timings = longArrayOf(0, 120, 80, 180)
                val amplitudes = intArrayOf(0, 220, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 120, 80, 180), -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Punchy, rewarding tactile confirmation when completing an exercise set or finishing a workout.
     */
    fun exerciseComplete(context: Context, haptic: HapticFeedback? = null) {
        haptic?.performHapticFeedback(HapticFeedbackType.LongPress)
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 70, 50, 110)
                val amplitudes = intArrayOf(0, 200, 0, 240)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 70, 50, 110), -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Lighter confirmation when unmarking a set.
     */
    fun exerciseUnmarked(context: Context, haptic: HapticFeedback? = null) {
        haptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40L, 120))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(40L)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
