package com.hescul.urgent.ui.screens.home

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.HashMap

/**
 * Any patient message classes (eg. [PatientDataMessage]) should inherit this abstract class
 * to get access to the [Jackson](https://github.com/FasterXML/jackson-module-kotlin) object mapper.
 */
abstract class PatientMessage {
    companion object {
        @JvmStatic
        protected val mapper = jacksonObjectMapper()
    }
    fun format(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }
    fun serialize(): String {
        return mapper.writeValueAsString(this)
    }
}

/**
 * This data class working with [Jackson](https://github.com/FasterXML/jackson-module-kotlin) to help
 * deserialize the incoming data message. The messages sent by the patient's device are expected
 * to be in the following json format
 * ```json
 * {
 *  "cid": "RPMSOS00A5",
 *  "size": 2,
 *  "time": 201564813,
 *  "data": {
 *      "pulse": {
 *          "value": 72,
 *          "unit": "bpm"
 *      },
 *      "spo2": {
 *          "value": 60,
 *          "unit": "%"
 *          }
 *      }
 *  }
 * ```
 */
@Suppress("SpellCheckingInspection")
class PatientDataMessage(
    val cid: String,
    val size: Int,
    val time: Long,
    val data: HashMap<String, HashMap<String, Any>>
) : PatientMessage() {
    companion object {
        /**
         * Attempt to create an instance of [PatientDataMessage] with the input json string.
         * If the process fails, null will be returned and the callback [onFailure] will be called
         * with the reason string.
         */
        fun create(input: String, onFailure: (String) -> Unit): PatientDataMessage? {
            return try {
                mapper.readValue(input, PatientDataMessage::class.java)
            }
            catch (exception: JsonProcessingException) {
                onFailure(exception.originalMessage)
                null
            }
        }
    }
}

/**
 * This data class working with [Jackson](https://github.com/FasterXML/jackson-module-kotlin) to help
 * deserialize the incoming status message. The status messages are expected to be in the following:
 * ```json
 * {
 *  "cid": "RPMSOS00A5",
 *  "code": 0
 * }
 * ```
 */
@Suppress("SpellCheckingInspection")
class PatientStatusMessage(
    val cid: String,
    val code: Int,
) : PatientMessage() {
    companion object {
        /**
         * Attempt to create an instance of [PatientStatusMessage] with the input json string.
         * If the process fails, null will be returned and the callback [onFailure] will be called
         * with the reason string.
         */
        fun create(input: String, onFailure: (String) -> Unit): PatientStatusMessage? {
            return try {
                mapper.readValue(input, PatientStatusMessage::class.java)
            }
            catch (exception: JsonProcessingException) {
                onFailure(exception.originalMessage)
                null
            }
        }
    }
}