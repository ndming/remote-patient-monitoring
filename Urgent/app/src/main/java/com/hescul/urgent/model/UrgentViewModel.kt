package com.hescul.urgent.model

import androidx.lifecycle.ViewModel
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.hescul.urgent.ui.screens.confirm.ConfirmViewModel
import com.hescul.urgent.ui.screens.signup.SignUpViewModel

class UrgentViewModel(
    private val signUpViewModel: SignUpViewModel,
    private val confirmViewModel: ConfirmViewModel
) : ViewModel() {
    lateinit var cognitoUser: CognitoUser
    lateinit var userAttributes: CognitoUserAttributes
    lateinit var signUpResult: SignUpResult

    fun getSignUpViewModel(): SignUpViewModel = signUpViewModel
    fun getConfirmViewModel(): ConfirmViewModel = confirmViewModel
}