package com.gravitymusic.presentation.screens.settings

import androidx.lifecycle.ViewModel
import com.gravitymusic.media.equalizer.EqualizerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val equalizerManager: EqualizerManager
) : ViewModel() {

    private val _numberOfBands = MutableStateFlow<Short>(0)
    val numberOfBands: StateFlow<Short> = _numberOfBands.asStateFlow()

    private val _bandLevelRange = MutableStateFlow(shortArrayOf(0, 0))
    val bandLevelRange: StateFlow<ShortArray> = _bandLevelRange.asStateFlow()

    // Map: Band Index -> Current Level
    private val _bandLevels = MutableStateFlow<Map<Short, Short>>(emptyMap())
    val bandLevels: StateFlow<Map<Short, Short>> = _bandLevels.asStateFlow()

    init {
        loadEqualizerSettings()
    }

    private fun loadEqualizerSettings() {
        val count = equalizerManager.getNumberOfBands()
        val range = equalizerManager.getBandLevelRange()
        
        _numberOfBands.value = count
        _bandLevelRange.value = range

        // Here we could read current levels from Equalizer, but since the AudioFx API 
        // doesn't give getBandLevel easily without a live instance, we'll initialize to 0 or saved.
        // Or if equalizerManager is setup, we can read them. Wait, equalizerManager is instantiated,
        // but Equalizer itself is created inside Service. 
        // For simplicity, we assume we just send levels to it.
        val initialLevels = mutableMapOf<Short, Short>()
        for (i in 0 until count) {
            initialLevels[i.toShort()] = 0
        }
        _bandLevels.value = initialLevels
    }

    fun setBandLevel(band: Short, level: Short) {
        _bandLevels.update { current ->
            current.toMutableMap().apply { this[band] = level }
        }
        equalizerManager.setBandLevel(band, level)
    }
}
