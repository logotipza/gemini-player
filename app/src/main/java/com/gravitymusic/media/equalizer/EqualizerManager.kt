package com.gravitymusic.media.equalizer

import android.media.audiofx.Equalizer

class EqualizerManager {
    private var equalizer: Equalizer? = null

    fun setupEqualizer(audioSessionId: Int) {
        release()
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getNumberOfBands(): Short = equalizer?.numberOfBands ?: 0

    fun getBandLevelRange(): ShortArray = equalizer?.bandLevelRange ?: shortArrayOf(0, 0)

    fun setBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        equalizer?.release()
        equalizer = null
    }
}
