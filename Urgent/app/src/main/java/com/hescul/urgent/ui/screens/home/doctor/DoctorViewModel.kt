package com.hescul.urgent.ui.screens.home.doctor

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hescul.urgent.R
import com.hescul.urgent.core.cognito.CognitoAuthenticator
import com.hescul.urgent.core.mqtt.doctor.Doctor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    val doctorAttributes = mutableStateMapOf<String, String>(
        Pair("name", "Jessica Johnson")
    )

    var isLaunched by mutableStateOf(false)
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var status by mutableStateOf("")
        private set
    var isStatusError by mutableStateOf(false)



    private fun setStatus(message: String = "", isError: Boolean = false) {
        status = message
        isStatusError = isError
    }

    fun onLaunch(context: Context) {
        isProgressing = true
        val fetchAttributesJob = viewModelScope.launch(Dispatchers.IO) {
            requestUserAttribute(context)
        }
        fetchAttributesJob.invokeOnCompletion { isLaunched = true }
    }

    private fun requestUserAttribute(context: Context) {
        setStatus(FETCH_ATTRIBUTES_MESSAGE)
        val userId = CognitoAuthenticator.getCurrentUserId(context)
        if (userId != null) {
            CognitoAuthenticator.requestUserAttributes(
                context = context,
                userId = userId,
                onGetAttributesSuccess = {
                    updateAttributes(it.attributes.attributes)
                    setStatus()
                    isProgressing = false
                },
                onGetAttributesFailure = { cause ->
                    setStatus(CognitoAuthenticator.reduceFailCause(cause), true)
                    isProgressing = false
                },
                defaultFailCause = context.getString(R.string.ui_doctorScreen_defaultFetchingFailCause)
            )
        }
    }

    private fun updateAttributes(attributes: Map<String, String>) {
        doctorAttributes.clear()
        attributes.forEach { (key, value) ->
            doctorAttributes[key] = value.ifBlank { Doctor.DEFAULT_UNKNOWN_VALUE }
        }
    }

    companion object {
        const val FETCH_ATTRIBUTES_MESSAGE = "Fetching attributes"
    }
}