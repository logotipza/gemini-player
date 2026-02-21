package com.gravitymusic.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravitymusic.domain.repository.ThemeConfig
import com.gravitymusic.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val themeConfig: StateFlow<ThemeConfig> = themeRepository.themeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.SYSTEM
        )

    fun setThemeConfig(config: ThemeConfig) {
        viewModelScope.launch {
            themeRepository.setThemeConfig(config)
        }
    }
}
