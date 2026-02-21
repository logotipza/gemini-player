package com.gravitymusic.presentation.screens.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gravitymusic.R

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val mediaItem by viewModel.currentMediaItem.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val abState by viewModel.abState.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    
    // Rotating Album Art Animation
    val infiniteTransition = rememberInfiniteTransition(label = "player_rotation_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // A simple Glassmorphism background effect overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .blur(32.dp)
        )

        // Glassmorphism Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Album Cover
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .rotate(if (isPlaying) rotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                val artworkData = mediaItem?.mediaMetadata?.artworkData
                if (artworkData != null) {
                    coil.compose.AsyncImage(
                        model = artworkData,
                        contentDescription = "Album Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    // Placeholder for Cover art
                    Text(
                        text = "🎵",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            val title = mediaItem?.mediaMetadata?.title?.toString() ?: "Unknown Track"
            val artist = mediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Bar
            val sliderPos = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()) else 0f
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formatTime(currentPosition), style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = sliderPos,
                    onValueChange = { percent ->
                        viewModel.seekTo((percent * duration).toLong())
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(formatTime(duration), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls (Glassmorphism card)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed Control
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.decreaseSpeed() }) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease Speed", modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "${String.format("%.1f", playbackSpeed)}x",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(onClick = { viewModel.increaseSpeed() }) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase Speed", modifier = Modifier.size(20.dp))
                            }
                        }

                        // A-B Control
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isASet = abState != com.gravitymusic.media.controller.MusicController.ABState.OFF
                            val isBSet = abState == com.gravitymusic.media.controller.MusicController.ABState.AB_SET

                            TextButton(onClick = { viewModel.setPointA() }, contentPadding = PaddingValues(0.dp)) {
                                Text("A", color = if (isASet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }
                            TextButton(onClick = { viewModel.setPointB() }, contentPadding = PaddingValues(0.dp)) {
                                Text("B", color = if (isBSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }
                            IconButton(onClick = { viewModel.clearABRepeat() }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear A-B", modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.skipToPrevious() }) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = stringResource(R.string.player_prev))
                    }

                    FloatingActionButton(
                        onClick = { viewModel.togglePlayPause() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) stringResource(R.string.player_pause) else stringResource(R.string.player_play)
                        )
                    }

                    IconButton(onClick = { viewModel.skipToNext() }) {
                        Icon(Icons.Filled.SkipNext, contentDescription = stringResource(R.string.player_next))
                    }
                }
              }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
