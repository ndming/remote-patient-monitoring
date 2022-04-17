package com.hescul.urgent.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.navigation.HomeScreens

class HomeViewModel : ViewModel() {
    var currentScreen by mutableStateOf(HomeScreens.Patient.route)
        private set

    fun onCurrentScreenChange(route: String) {
        currentScreen = route
    }
}