package com.gravitymusic.presentation.screens.cloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gravitymusic.data.sync.CloudSyncWorker
import com.gravitymusic.domain.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CloudSyncViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val workManager: WorkManager
) : ViewModel() {

    val isAuthorized = tokenRepository.getToken()
        .map { !it.isNullOrBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun saveToken(token: String) {
        viewModelScope.launch {
            tokenRepository.saveToken(token)
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
        }
    }

    fun startSync(yandexPath: String) {
        val inputData = Data.Builder()
            .putString("YANDEX_PATH", yandexPath)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<CloudSyncWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(syncRequest)
    }
}
