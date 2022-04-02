package com.hescul.urgent.ui.screens.opening

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OpeningViewModel : ViewModel() {
    var isLaunched by mutableStateOf(false)
        private set

    fun onLaunch() {
        isLaunched = true
    }
}