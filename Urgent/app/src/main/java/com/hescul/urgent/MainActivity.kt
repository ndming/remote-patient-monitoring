package com.hescul.urgent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.hescul.urgent.ui.screens.signup.SignUpScreen
import com.hescul.urgent.ui.screens.signup.SignUpViewModel
import com.hescul.urgent.ui.theme.UrgentTheme

class MainActivity : ComponentActivity() {

    private val signUpViewModel by viewModels<SignUpViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UrgentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SignUpScreen(
                        signUpViewModel = signUpViewModel,
                    )
                }
            }
        }

    }
}