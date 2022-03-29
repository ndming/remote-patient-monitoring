package com.hescul.urgent.ui.screens.confirm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hescul.urgent.core.utils.InfoValidator

class ConfirmViewModel : ViewModel() {
    var confirmCodeTextInput by mutableStateOf("")
        private set
    var isProgressing by mutableStateOf(false)
        private set
    var isConfirmSucceeded by mutableStateOf(false)
        private set
    var failCause by mutableStateOf("")
        private set
    fun onConfirmTextInputChange(text: String) {
        confirmCodeTextInput = text
    }

    fun onConfirmProgress() {
        failCause = ""
        isProgressing = true
    }

    fun onConfirmSuccess() {
        isConfirmSucceeded = true
        isProgressing = false
    }

    fun onConfirmFailure(cause: String) {
        isProgressing = false
        failCause = cause
    }

    fun isConfirmCodeError(): Boolean {
        return confirmCodeTextInput.isNotEmpty() && !InfoValidator.isConfirmCodeValid(confirmCodeTextInput)
    }

    fun reset() {
        confirmCodeTextInput = ""
        isProgressing = false
        isConfirmSucceeded = false
        failCause = ""
    }
}