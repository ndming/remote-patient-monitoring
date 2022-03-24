package com.hescul.urgent.ui.versatile

/**
 * An enumerated type for [InfoTextField] metadata.
 *
 * @param label the label string displayed on the [InfoTextField]
 * @param hint the placeholder text displayed on the [InfoTextField]
 */
enum class InfoFieldType(val label: String, val hint: String) {
    EmailField("Email", "Enter your email"),
    PasswordField("Password", "Enter your password"),
    ConfirmPasswordField("Confirm Password", "Re-enter your password"),
}