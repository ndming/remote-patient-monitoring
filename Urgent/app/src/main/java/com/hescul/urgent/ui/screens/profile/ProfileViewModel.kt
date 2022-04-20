package com.hescul.urgent.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.core.mqtt.patient.Patient

class ProfileViewModel : ViewModel() {
    var patient = Patient.UNKNOWN_PATIENT
        private set

    var isProgressing by mutableStateOf(false)
        private set

    fun updatePatient(patient: Patient) {
        this.patient = patient
    }

    fun reset() {
        patient = Patient.UNKNOWN_PATIENT
        isProgressing = false
    }
}