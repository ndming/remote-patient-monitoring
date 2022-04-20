package com.hescul.urgent

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hescul.urgent.navigation.UrgentNavHost
import com.hescul.urgent.navigation.UrgentScreens
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.home.HomeViewModel
import com.hescul.urgent.ui.screens.home.doctor.DoctorViewModel
import com.hescul.urgent.ui.screens.home.patient.PatientViewModel
import com.hescul.urgent.ui.screens.home.patient.PatientViewModelFactory
import com.hescul.urgent.ui.screens.login.LoginViewModel
import com.hescul.urgent.ui.screens.opening.OpeningViewModel
import com.hescul.urgent.ui.screens.profile.ProfileViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModels = initViewModels(applicationContext)
        setContent {
            UrgentNavHost(
                startDestination = UrgentScreens.Opening.name,
                viewModels = viewModels,
            )
        }

    }

    private fun initViewModels(context: Context): UrgentViewModels {
        val openingViewModel by viewModels<OpeningViewModel>()
        val signUpViewModel by viewModels<SignUpViewModel>()
        val confirmViewModel by viewModels<ConfirmViewModel>()
        val loginViewModel by viewModels<LoginViewModel>()
        val homeViewModel by viewModels<HomeViewModel>()
        val patientViewModel by viewModels<PatientViewModel>(factoryProducer = { PatientViewModelFactory(context) })
        val doctorViewModel by viewModels<DoctorViewModel>()
        val profileViewModel by viewModels<ProfileViewModel>()
        return UrgentViewModels(
            openingViewModel = openingViewModel,
            loginViewModel = loginViewModel,
            signUpViewModel = signUpViewModel,
            confirmViewModel = confirmViewModel,
            homeViewModel = homeViewModel,
            patientViewModel = patientViewModel,
            doctorViewModel = doctorViewModel,
            profileViewModel = profileViewModel
        )
    }
}

