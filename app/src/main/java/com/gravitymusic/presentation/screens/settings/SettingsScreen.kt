package com.gravitymusic.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gravitymusic.domain.repository.ThemeConfig
import com.gravitymusic.presentation.navigation.Screen
import com.gravitymusic.presentation.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val currentTheme by themeViewModel.themeConfig.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Выбор темы
        Text("Тема оформления", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = currentTheme == ThemeConfig.SYSTEM,
                onClick = { themeViewModel.setThemeConfig(ThemeConfig.SYSTEM) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
            ) {
                Text("System")
            }
            SegmentedButton(
                selected = currentTheme == ThemeConfig.LIGHT,
                onClick = { themeViewModel.setThemeConfig(ThemeConfig.LIGHT) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
            ) {
                Text("Light")
            }
            SegmentedButton(
                selected = currentTheme == ThemeConfig.DARK,
                onClick = { themeViewModel.setThemeConfig(ThemeConfig.DARK) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
            ) {
                Text("Dark")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate(Screen.Equalizer.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Аппаратный Эквалайзер")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("cloud_sync_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Yandex Disk Синхронизация")
        }
    }
}
