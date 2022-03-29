package com.hescul.urgent.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

class UrgentViewModelFactory(
    private val signUpViewModel: SignUpViewModel,
    private val confirmViewModel: ConfirmViewModel,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            SignUpViewModel::class.java,
            ConfirmViewModel::class.java
        ).newInstance(signUpViewModel, confirmViewModel)
    }
}