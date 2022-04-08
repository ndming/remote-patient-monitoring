package com.hescul.urgent.ui.screens.opening

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class OpeningViewModel : ViewModel() {
    companion object {
        private const val OPENING_DURATION = 1000L  // in ms
    }

    suspend fun showOpening(onDone: () -> Unit) {
        delay(OPENING_DURATION)
        onDone()
    }


}