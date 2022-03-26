package com.hescul.urgent.ui.versatile


import androidx.annotation.StringRes
import com.hescul.urgent.R

/**
 * An enumerated type for [InfoTextField] metadata.
 *
 * @param label the label string displayed on the [InfoTextField]
 * @param hint the placeholder text displayed on the [InfoTextField]
 */
enum class InfoFieldType(@StringRes val label: Int, @StringRes val hint: Int) {
    NameField(R.string.ui_signUpScreen_userNameFieldLabel, R.string.ui_signUpScreen_userNameFieldHint),
    EmailField(R.string.ui_signUpScreen_emailFieldLabel, R.string.ui_signUpScreen_emailFieldHint),
    PasswordField(R.string.ui_signUpScreen_passwordFieldLabel, R.string.ui_signUpScreen_passwordFieldHint),
    ConfirmPasswordField(R.string.ui_signUpScreen_confirmPasswordFieldLabel, R.string.ui_signUpScreen_confirmPasswordFieldHint),
}