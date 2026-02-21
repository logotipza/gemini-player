package com.gravitymusic.presentation.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gravitymusic.presentation.screens.player.PlayerViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel() // Using PlayerViewModel to access queue
) {
    val queue by viewModel.queue.collectAsState()
    
    // We need a local state for smooth UI reordering before committing to the ViewModel
    var localQueue by remember { mutableStateOf(queue) }
    
    LaunchedEffect(queue) {
        localQueue = queue
    }

    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            localQueue = localQueue.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        },
        canDragOver = { draggedOver, _ -> true },
        onDragEnd = { startIndex, endIndex ->
            viewModel.moveMediaItem(startIndex, endIndex)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Playback Queue") }
            )
        }
    ) { padding ->
        if (localQueue.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Queue is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .reorderable(state)
            ) {
                items(localQueue, key = { it.mediaId }) { item ->
                    ReorderableItem(state, key = item.mediaId) { isDragging ->
                        val elevation = if (isDragging) 8.dp else 0.dp
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .shadow(elevation, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val title = item.mediaMetadata.title?.toString() ?: "Unknown Track"
                                    val artist = item.mediaMetadata.artist?.toString() ?: "Unknown Artist"
                                    Text(
                                        text = title, 
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = artist, 
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = "Drag to reorder",
                                    modifier = Modifier.detectReorderAfterLongPress(state),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
