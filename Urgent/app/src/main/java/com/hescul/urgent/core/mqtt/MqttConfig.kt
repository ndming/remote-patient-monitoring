package com.hescul.urgent.core.mqtt

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions

object MqttBrokerConfig {
    const val END_POINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"
    const val DOCTOR_POLICY = "RPMDOC_policy"
    const val REGION = "us-east-1"
}

object MqttClientConfig {
    const val DATA_TOPIC_PREFIX = "rpm/sos/"
    const val STATUS_TOPIC_PREFIX = "rpm/tus/"
    val SUBSCRIBE_QOS = AWSIotMqttQos.QOS1

    const val MAX_AUTO_RECONNECTION_ATTEMPTS = 2
    const val CLEAN_SESSION = false
}