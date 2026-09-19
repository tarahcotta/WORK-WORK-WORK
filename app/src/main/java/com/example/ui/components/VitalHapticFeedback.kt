package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.core.content.ContextCompat

/**
 * Tactile vibration and haptic feedback helper for Vital Strength.
 * Provides distinct physical sensations for timer ticks, control taps,
 * timer completion, and exercise set completions during workouts.
 * Engineered to be completely fail-safe across all Android OS versions and hardware configurations.
 */
object VitalHapticFeedback {

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            ContextCompat.getSystemService(context, Vibrator::class.java)
                ?: @Suppress("DEPRECATION") (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } catch (_: Throwable) {
            null
        }
    }

    private fun safePerformHaptic(haptic: HapticFeedback?, type: HapticFeedbackType) {
        try {
            haptic?.performHapticFeedback(type)
        } catch (_: Throwable) {
            // Silently absorb any haptic feedback failures
        }
    }

    /**
     * Tactile feedback for timer controls: play/pause, add/subtract seconds, reset, preset select.
     */
    fun timerButtonTap(context: Context, haptic: HapticFeedback? = null) {
        try {
            safePerformHaptic(haptic, HapticFeedbackType.TextHandleMove)
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
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    /**
     * Subtle tactile tick for 3-2-1 countdown warnings.
     */
    fun timerTick(context: Context, haptic: HapticFeedback? = null) {
        try {
            safePerformHaptic(haptic, HapticFeedbackType.TextHandleMove)
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
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    /**
     * Celebratory tactile pulse when a rest interval timer completes (hits 0s).
     */
    fun timerComplete(context: Context, haptic: HapticFeedback? = null) {
        try {
            safePerformHaptic(haptic, HapticFeedbackType.LongPress)
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            val timings = longArrayOf(0, 120, 80, 180)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (vibrator.hasAmplitudeControl()) {
                    val amplitudes = intArrayOf(0, 220, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    /**
     * Punchy, rewarding tactile confirmation when completing an exercise set or finishing a workout.
     */
    fun exerciseComplete(context: Context, haptic: HapticFeedback? = null) {
        try {
            safePerformHaptic(haptic, HapticFeedbackType.LongPress)
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 70, 50, 110)
                if (vibrator.hasAmplitudeControl()) {
                    val amplitudes = intArrayOf(0, 200, 0, 240)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 70, 50, 110), -1)
            }
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    /**
     * Lighter confirmation when unmarking a set.
     */
    fun exerciseUnmarked(context: Context, haptic: HapticFeedback? = null) {
        try {
            safePerformHaptic(haptic, HapticFeedbackType.TextHandleMove)
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(40L, 120))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(40L)
            }
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }
}

