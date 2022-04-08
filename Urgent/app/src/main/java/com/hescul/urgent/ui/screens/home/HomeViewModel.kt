package com.hescul.urgent.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.hescul.urgent.core.iot.BrokerConfig
import com.hescul.urgent.core.iot.DoctorClient

class HomeViewModel : ViewModel() {
    private lateinit var doctorClient: DoctorClient

    var isConnected by mutableStateOf(false)
        private set

    var patients = mutableStateListOf<Patient>(
        Patient.SamplePatient0,
        Patient.SamplePatient1,
        Patient.SamplePatient2,
    )
        private set

    fun init(userSession: CognitoUserSession) {

    }

    fun addPatient(patient: Patient) {
        doctorClient.subscribe(
            subscribeTopic = "rpm/sos/${patient.deviceId}",
            subscribeQos = BrokerConfig.SUBSCRIBE_QOS,
            onSubscriptionSuccess = {

            },
            onSubscriptionFailure = {

            },
            onMessageCallback = { topic, data ->
                
            }
        )
        patients.add(patient)
    }

    fun onAddPatient() {
        patients.add(0, Patient.SamplePatient3)
    }

    fun onPatientChipClick() {
        patients[0].data[0].value += 1
    }
}