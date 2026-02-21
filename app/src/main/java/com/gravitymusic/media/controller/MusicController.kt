package com.gravitymusic.media.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.gravitymusic.media.service.GravityAudioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicController(private val context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    var mediaController: MediaController? = null
        private set

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem.asStateFlow()

    private val _queue = MutableStateFlow<List<MediaItem>>(emptyList())
    val queue: StateFlow<List<MediaItem>> = _queue.asStateFlow()

    // Скорость воспроизведения
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    // A-B Repeat state
    enum class ABState { OFF, A_SET, AB_SET }
    private val _abState = MutableStateFlow(ABState.OFF)
    val abState: StateFlow<ABState> = _abState.asStateFlow()

    private var pointA: Long? = null
    private var pointB: Long? = null
    private var abLoopJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun initialize() {
        val sessionToken = SessionToken(context, ComponentName(context, GravityAudioService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            setupPlayerListener()
            updateQueue()
        }, MoreExecutors.directExecutor())
    }
    
    private fun updateQueue() {
        val controller = mediaController ?: return
        val count = controller.mediaItemCount
        val list = mutableListOf<MediaItem>()
        for (i in 0 until count) {
            list.add(controller.getMediaItemAt(i))
        }
        _queue.value = list
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                if (isPlaying) startPositionTracking() else stopPositionTracking()
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentMediaItem.value = mediaItem
            }

            override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                updateQueue()
            }
        })
    }

    private fun startPositionTracking() {
        abLoopJob?.cancel()
        abLoopJob = scope.launch {
            while (true) {
                val currentPos = mediaController?.currentPosition ?: 0L
                _currentPosition.value = currentPos
                
                // A-B Repeat Logic check
                pointB?.let { b ->
                    pointA?.let { a ->
                        if (currentPos >= b) {
                            mediaController?.seekTo(a)
                        }
                    }
                }
                delay(100) // Poll every 100ms
            }
        }
    }

    private fun stopPositionTracking() {
        abLoopJob?.cancel()
    }

    fun playMedia(mediaItem: MediaItem) {
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }

    fun play() { mediaController?.play() }
    
    fun pause() { mediaController?.pause() }
    
    fun skipToNext() { mediaController?.seekToNextMediaItem() }
    
    fun skipToPrevious() { mediaController?.seekToPreviousMediaItem() }

    fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        mediaController?.moveMediaItem(currentIndex, newIndex)
    }

    fun setShuffleMode(enabled: Boolean) {
        mediaController?.shuffleModeEnabled = enabled
    }

    fun setRepeatMode(@Player.RepeatMode mode: Int) {
        mediaController?.repeatMode = mode
    }

    fun setPlaybackSpeed(speed: Float) {
        val coercedSpeed = speed.coerceIn(0.5f, 2.0f)
        mediaController?.playbackParameters = PlaybackParameters(coercedSpeed)
        _playbackSpeed.value = coercedSpeed
    }

    // A-B Repeat API
    fun setPointA() {
        pointA = mediaController?.currentPosition
        if (pointA != null) {
            _abState.value = ABState.A_SET
        }
    }

    fun setPointB() {
        val current = mediaController?.currentPosition ?: return
        if (pointA != null && current > pointA!!) {
            pointB = current
            _abState.value = ABState.AB_SET
        }
    }

    fun clearABRepeat() {
        pointA = null
        pointB = null
        _abState.value = ABState.OFF
    }

    fun release() {
        mediaControllerFuture?.let { MediaController.releaseFuture(it) }
        abLoopJob?.cancel()
    }
}
