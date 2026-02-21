package com.gravitymusic.presentation.screens.player

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.gravitymusic.media.controller.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicController: MusicController
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = musicController.isPlaying
    val currentPosition: StateFlow<Long> = musicController.currentPosition

    fun playMedia(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        musicController.playMedia(mediaItem)
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            musicController.pause()
        } else {
            musicController.play()
        }
    }

    fun skipToNext() = musicController.skipToNext()

    fun skipToPrevious() = musicController.skipToPrevious()

    fun setShuffleMode(enabled: Boolean) = musicController.setShuffleMode(enabled)

    fun toggleRepeatMode() {
        // Toggle between basic OFF, ONE, ALL
        val currentMode = musicController.mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        val nextMode = when (currentMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        musicController.setRepeatMode(nextMode)
    }

    fun setPlaybackSpeed(speed: Float) = musicController.setPlaybackSpeed(speed)

    // A-B Repeat functionality
    fun setPointA() = musicController.setPointA()
    fun setPointB() = musicController.setPointB()
    fun clearABRepeat() = musicController.clearABRepeat()

    override fun onCleared() {
        musicController.release()
        super.onCleared()
    }
}
