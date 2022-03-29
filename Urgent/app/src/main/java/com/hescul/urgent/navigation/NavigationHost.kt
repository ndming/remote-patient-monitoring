package com.hescul.urgent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hescul.urgent.model.UrgentViewModel

@Composable
fun UrgentNavHost(
    navController: NavHostController,
    startDestination: String,
    urgentViewModel: UrgentViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        signUpComposable(navController, urgentViewModel, modifier)
        confirmComposable(navController, urgentViewModel, modifier)
    }
}