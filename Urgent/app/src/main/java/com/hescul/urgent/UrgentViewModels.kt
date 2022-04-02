package com.hescul.urgent

import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.login.LoginViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

data class UrgentViewModels(
    val loginViewModel: LoginViewModel,
    val signUpViewModel: SignUpViewModel,
    val confirmViewModel: ConfirmViewModel,
)
