package id.heula.presentation.util

import android.media.AudioManager
import android.media.ToneGenerator

actual fun playZenGongSound() {
    try {
        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
        tg.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
    } catch (_: Throwable) {}
}
