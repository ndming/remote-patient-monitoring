package com.hescul.urgent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hescul.urgent.navigation.UrgentNavHost
import com.hescul.urgent.navigation.UrgentScreen
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.home.HomeViewModel
import com.hescul.urgent.ui.screens.login.LoginViewModel
import com.hescul.urgent.ui.screens.opening.OpeningViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModels = initViewModels()
        setContent {
            UrgentNavHost(
                startDestination = UrgentScreen.Opening.name,
                viewModels = viewModels,
            )
        }

    }

    private fun initViewModels(): UrgentViewModels {
        val openingViewModel by viewModels<OpeningViewModel>()
        val signUpViewModel by viewModels<SignUpViewModel>()
        val confirmViewModel by viewModels<ConfirmViewModel>()
        val loginViewModel by viewModels<LoginViewModel>()
        val homeViewModel by viewModels<HomeViewModel>()
        return UrgentViewModels(
            openingViewModel = openingViewModel,
            loginViewModel = loginViewModel,
            signUpViewModel = signUpViewModel,
            confirmViewModel = confirmViewModel,
            homeViewModel = homeViewModel
        )
    }
}

