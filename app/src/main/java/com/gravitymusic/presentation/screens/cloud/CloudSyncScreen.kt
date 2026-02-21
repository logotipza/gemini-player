package com.gravitymusic.presentation.screens.cloud

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun CloudSyncScreen(
    navController: NavController,
    viewModel: CloudSyncViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var yandexPath by remember { mutableStateOf("disk:/Music") }
    val isAuthorized by viewModel.isAuthorized.collectAsState()

    val clientId = "<YOUR_CLIENT_ID>" // Replace with actual Yandex Client ID
    val authUrl = "https://oauth.yandex.ru/authorize?response_type=token&client_id=$clientId"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isAuthorized) {
            Text("Yandex Disk Not Connected")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                context.startActivity(intent)
            }) {
                Text("Connect Yandex Disk")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Temporary manual token insertion for testing without handling DeepLinks
            var manualToken by remember { mutableStateOf("") }
            OutlinedTextField(
                value = manualToken,
                onValueChange = { manualToken = it },
                label = { Text("Manual Token Input") }
            )
            Button(onClick = { viewModel.saveToken(manualToken) }) {
                Text("Save Token")
            }
        } else {
            Text("Yandex Disk Connected ✅")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = yandexPath,
                onValueChange = { yandexPath = it },
                label = { Text("Yandex folder path") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.startSync(yandexPath) }) {
                Text("Start Sync")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
        }
    }
}
