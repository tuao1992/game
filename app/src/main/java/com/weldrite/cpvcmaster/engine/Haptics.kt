package com.weldrite.cpvcmaster.engine

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Thin wrapper over the platform vibrator with intent-named helpers. */
class Haptics(context: Context) {

    var enabled = true

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            mgr?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) { null }

    private val has = vibrator?.hasVibrator() == true

    fun tick() = buzz(10, 80)
    fun light() = buzz(14, 120)
    fun medium() = buzz(24, 180)
    fun heavy() = buzz(40, 255)

    fun success() = pattern(longArrayOf(0, 18, 50, 30))
    fun error() = pattern(longArrayOf(0, 40, 60, 40))

    private fun buzz(ms: Long, amplitude: Int) {
        if (!enabled || !has) return
        try {
            vibrator?.vibrate(VibrationEffect.createOneShot(ms, amplitude.coerceIn(1, 255)))
        } catch (_: Exception) {}
    }

    private fun pattern(timings: LongArray) {
        if (!enabled || !has) return
        try {
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
        } catch (_: Exception) {}
    }
}
