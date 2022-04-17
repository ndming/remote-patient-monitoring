package com.hescul.urgent.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.hescul.urgent.R

/**
 * Screen metadata for Urgent application
 */
enum class UrgentScreens {
    Opening,
    SignUp,
    Confirm,
    Login,
    Home,
    Patient,
    Doctor,
}

sealed class HomeScreens(val route: String, @StringRes val label: Int, val icon: ImageVector, @StringRes val cd: Int) {
    object Patient: HomeScreens(UrgentScreens.Patient.name, R.string.ui_homeScreen_profileScreenLabel, icon = Icons.Outlined.Home, cd = R.string.cd_homeIcon)
    object Doctor: HomeScreens(UrgentScreens.Doctor.name, R.string.ui_homeScreen_doctorScreenLabel, icon = Icons.Outlined.PersonOutline, R.string.cd_personIcon)
}