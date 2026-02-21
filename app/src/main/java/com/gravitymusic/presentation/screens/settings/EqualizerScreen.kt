package com.gravitymusic.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    navController: NavController,
    viewModel: EqualizerViewModel = hiltViewModel()
) {
    val numberOfBands by viewModel.numberOfBands.collectAsState()
    val bandLevelRange by viewModel.bandLevelRange.collectAsState()
    val bandLevels by viewModel.bandLevels.collectAsState()

    val minLevel = if (bandLevelRange.isNotEmpty()) bandLevelRange[0].toFloat() else -1500f
    val maxLevel = if (bandLevelRange.size > 1) bandLevelRange[1].toFloat() else 1500f

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .blur(32.dp)
        )

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Equalizer", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (numberOfBands <= 0) {
                    Text("Equalizer is not supported or not active yet.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 0 until numberOfBands) {
                            val bandIndex = i.toShort()
                            val currentLvl = bandLevels[bandIndex]?.toFloat() ?: 0f

                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // dB value
                                Text(
                                    text = "${(currentLvl / 100).toInt()} dB",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                // Vertical Slider simulation: in Compose there is no Vertical Slider out-of-the-box in M3
                                // so we rotate a horizontal slider.
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Slider(
                                        value = currentLvl,
                                        onValueChange = { newValue ->
                                            viewModel.setBandLevel(bandIndex, newValue.toInt().toShort())
                                        },
                                        valueRange = minLevel..maxLevel,
                                        modifier = Modifier
                                            .graphicsLayer {
                                                rotationZ = 270f
                                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
                                            }
                                            .width(200.dp) // The height of the slider
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Band ${i + 1}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
