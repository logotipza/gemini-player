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
    val currentMediaItem: StateFlow<MediaItem?> = musicController.currentMediaItem
    val queue: StateFlow<List<MediaItem>> = musicController.queue
    val playbackSpeed: StateFlow<Float> = musicController.playbackSpeed
    val abState: StateFlow<MusicController.ABState> = musicController.abState

    fun moveMediaItem(fromPosition: Int, toPosition: Int) {
        musicController.moveMediaItem(fromPosition, toPosition)
    }

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
        val currentMode = musicController.mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        val nextMode = when (currentMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        musicController.setRepeatMode(nextMode)
    }

    fun cyclePlaybackSpeed() {
        val current = playbackSpeed.value
        val next = when (current) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            1.5f -> 2.0f
            2.0f -> 0.5f
            0.5f -> 0.75f
            0.75f -> 1.0f
            else -> 1.0f
        }
        musicController.setPlaybackSpeed(next)
    }

    // A-B Repeat functionality
    fun handleABClick() {
        when (abState.value) {
            MusicController.ABState.OFF -> musicController.setPointA()
            MusicController.ABState.A_SET -> musicController.setPointB()
            MusicController.ABState.AB_SET -> musicController.clearABRepeat()
        }
    }

    override fun onCleared() {
        musicController.release()
        super.onCleared()
    }
}
