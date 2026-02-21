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
    val duration: StateFlow<Long> = musicController.duration

    fun moveMediaItem(fromPosition: Int, toPosition: Int) {
        musicController.moveMediaItem(fromPosition, toPosition)
    }

    fun playMedia(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        musicController.playMedia(mediaItem)
    }

    fun seekTo(position: Long) {
        musicController.seekTo(position)
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
        // Оставили для обратной совместимости, если где-то еще используется
    }

    fun increaseSpeed() {
        val next = (playbackSpeed.value + 0.1f).coerceAtMost(2.0f)
        musicController.setPlaybackSpeed(next)
    }

    fun decreaseSpeed() {
        val next = (playbackSpeed.value - 0.1f).coerceAtLeast(0.5f)
        musicController.setPlaybackSpeed(next)
    }

    // A-B Repeat functionality
    fun handleABClick() {
        // Old cycle click function, can be removed if unused.
    }
    
    fun setPointA() = musicController.setPointA()
    fun setPointB() = musicController.setPointB()
    fun clearABRepeat() = musicController.clearABRepeat()

    override fun onCleared() {
        musicController.release()
        super.onCleared()
    }
}
