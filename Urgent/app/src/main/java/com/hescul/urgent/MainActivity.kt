package com.hescul.urgent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hescul.urgent.model.UrgentViewModel
import com.hescul.urgent.model.UrgentViewModelFactory
import com.hescul.urgent.navigation.UrgentNavHost
import com.hescul.urgent.navigation.UrgentScreen
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel
import com.hescul.urgent.ui.theme.UrgentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val urgentViewModel = initViewModels()
        setContent {
            UrgentApp(urgentViewModel)
        }

    }

    private fun initViewModels(): UrgentViewModel {
        val signUpViewModel by viewModels<SignUpViewModel>()
        val confirmViewModel by viewModels<ConfirmViewModel>()
        val factory = UrgentViewModelFactory(
            signUpViewModel = signUpViewModel,
            confirmViewModel = confirmViewModel
        )
        val urgentViewModel by viewModels<UrgentViewModel>(
            factoryProducer = { factory }
        )
        return urgentViewModel
    }
}

@Composable
fun UrgentApp(
    urgentViewModel: UrgentViewModel,
) {
    UrgentTheme {
        //val allScreens = UrgentScreen.values().toList()
        val navController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            UrgentNavHost(
                navController = navController,
                startDestination = UrgentScreen.SignUp.name,
                urgentViewModel = urgentViewModel,
            )
        }
    }
}