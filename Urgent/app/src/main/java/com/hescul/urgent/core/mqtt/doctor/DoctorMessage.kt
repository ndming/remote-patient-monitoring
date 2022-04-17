package com.hescul.urgent.core.mqtt.doctor

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hescul.urgent.core.mqtt.patient.Patient
import com.hescul.urgent.core.mqtt.patient.PatientAttribute


class DoctorMessage {
    class PatientMetadata(
        val deviceId: String,
        val name: String,
        val attributes: List<PatientAttribute>
    )
    companion object {
        @JvmStatic
        private val mapper = jacksonObjectMapper()

        fun create(input: String, onFailure: (String) -> Unit): List<PatientMetadata> {
            val patients: List<PatientMetadata> =  try {
                mapper.readValue(input)
            } catch (exception: JsonProcessingException) {
                onFailure(exception.originalMessage)
                listOf()
            }
            return patients
        }

        fun serialize(patients: List<Patient>): String {
            val patientMetadataList = patients.map {
                PatientMetadata(
                    deviceId = it.deviceId,
                    name = it.name,
                    attributes = it.attributes
                )
            }
            return mapper.writeValueAsString(patientMetadataList)
        }
    }
}