package com.hescul.urgent

import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.home.HomeViewModel
import com.hescul.urgent.ui.screens.home.doctor.DoctorViewModel
import com.hescul.urgent.ui.screens.home.patient.PatientViewModel
import com.hescul.urgent.ui.screens.login.LoginViewModel
import com.hescul.urgent.ui.screens.opening.OpeningViewModel
import com.hescul.urgent.ui.screens.profile.ProfileViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

data class UrgentViewModels(
    val openingViewModel: OpeningViewModel,
    val loginViewModel: LoginViewModel,
    val signUpViewModel: SignUpViewModel,
    val confirmViewModel: ConfirmViewModel,
    val homeViewModel: HomeViewModel,
    val patientViewModel: PatientViewModel,
    val doctorViewModel: DoctorViewModel,
    val profileViewModel: ProfileViewModel
)
