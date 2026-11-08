package id.heula.presentation.util

import kotlin.JsFun

@JsFun("() => { if (window.heulaZenAudio && window.heulaZenAudio.playZenGong) { window.heulaZenAudio.playZenGong(); } }")
external fun jsPlayZenGong()

actual fun playZenGongSound() {
    try {
        jsPlayZenGong()
    } catch (_: Throwable) {}
}
