package com.hescul.urgent.core.mqtt.doctor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Doctor {
    var name by mutableStateOf("")
    var email by mutableStateOf("")

    companion object {
        val DEFAULT_DOCTOR_PICTURE = Icons.Outlined.AccountCircle
    }
}