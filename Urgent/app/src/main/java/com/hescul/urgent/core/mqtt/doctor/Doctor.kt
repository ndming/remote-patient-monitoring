package com.hescul.urgent.core.mqtt.doctor

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hescul.urgent.R
import com.hescul.urgent.ui.theme.ConnectedColor
import com.hescul.urgent.ui.theme.DisconnectedColor

class Doctor {
    companion object {
        const val DEFAULT_UNKNOWN_VALUE = "--"
        val DEFAULT_DOCTOR_PICTURE = Icons.Outlined.AccountCircle
    }

    enum class Status(val tint: Color) {
        Connected(tint = ConnectedColor),
        Disconnected(tint = DisconnectedColor)
    }

    enum class Attribute(val key: String, @StringRes val translate: Int) {
        Name(key = "name", translate = R.string.ui_doctorScreen_nameKeyTranslate),
        Email(key = "email", translate = R.string.ui_doctorScreen_emailKeyTranslate),
        Sub(key = "sub", translate = R.string.ui_doctorScreen_subKeyTranslate),
        EmailVerified(key = "email_verified", translate = R.string.ui_doctorScreen_emailVerifiedKeyTranslate)
    }

    enum class Option(@StringRes val title: Int, val icon: ImageVector, @StringRes val iconDescription: Int) {
        Account(R.string.ui_doctorScreen_optionAccountTitle, Icons.Default.PersonOutline, R.string.cd_personIcon),
        Settings(R.string.ui_doctorScreen_optionSettingsTitle, Icons.Outlined.Settings, R.string.cd_settingsIcon),
        Help(R.string.ui_doctorScreen_optionHelpTitle, Icons.Default.HelpOutline, R.string.cd_helpIcon),
    }
}