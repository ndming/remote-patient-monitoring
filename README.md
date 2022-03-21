# Remote Patient Monitoring
**Brief:** statistic says that more than 30% of medical patients re-admit into the hospital after surgery. One of the causing is attributed to the lack of monitoring infrastructure when the patients go home. In the time when Covid-19 is threatening face-to-face interaction, the situation gets even worse. With remote monitoring IoT system, doctors can now monitor patient status through sensors embedded on patients' wearable devices, hence preventing fatal cases from emerging.

## Expected Outcome
- Two or more sensor nodes that capture health status from an end user (patient).
- A central microcontroller that controls the sensors and transmits captured data through a wireless channel.
- A gateway software that receives data from the wireless channel, then processes and publishes data to an MQTT broker.
- A mobile application that subscribes and listens to certain topics from the broker and intuitively displays the data to the end user (doctor).
- A database server for authentication.

![](https://i.imgur.com/uWWDiZm.png "IoT Modules")

## Architecture Overview
The system initiates when there is a conpromise between a doctor and a patient. The patient receives a Set of Sensors (or a wearable that is capable of doing the same functionality) named SOS. The number of SOS is finite, and each SOS has a unique preset serial number (ID) which, in turn, associates with a [Thing](https://docs.aws.amazon.com/iot/latest/developerguide/iot-thing-management.html) on the AWS IoT cloud. Each thing will be registered a unique set of related attributes through [Thing Type](https://docs.aws.amazon.com/iot/latest/developerguide/thing-types.html). The use of 'things' provides an extra layer of security and management for connected devices. It defines the [policies](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) for authorization and [rules](https://docs.aws.amazon.com/iot/latest/developerguide/iot-rules-tutorial.html) for extra AWS-service utility.

The bridge between the sensors and the AWS IoT cloud is a microcontroller chip. The chip must support network connection and (optionally) a processing system. The [ESP32](https://hshop.vn/products/kit-rf-thu-phat-wifi-ble-esp32-s2-nodemcu-32-s2-ai-thinker) is best fit for this purpose. It can process the data measured by the sensors, wraps it into JSON and publishes to the AWS IoT cloud through the AWS [Gateway](https://docs.aws.amazon.com/iot-sitewise/latest/userguide/gateways-ggv2.html). Once received, the AWS Gateway will automatically transfer the payload to the AWS IoT message broker. From here, any controling or observing MQTT client can subscribe to the desired topics for data monitoring (eg. a doctor client monitors his associated patient)

AWS requires strict authoriation for each of its 'thing'. The process requires sufficient [Certificate Authorities (CAs)](https://en.wikipedia.org/wiki/Certificate_authority) files for successful handshake between the client and the server. On the mobile client, the process gets even more complicated, and this is where [vended credential service](https://docs.aws.amazon.com/lake-formation/latest/dg/how-vending-works.html) comes in. AWS offers its native service known as [Amazon Cognito](https://docs.aws.amazon.com/iot/latest/developerguide/cognito-identities.html) that enables the mobile client to process the authorization process through an email address of a Google or Facebook account, easing the identification mechanism substantially.

The doctors are the major mobile clients. They will use an application named [Urgent](https://github.com/hescul/remote-patient-monitoring/tree/main/Urgent) to monitor their patients remotely. Currently, the application is built natively on Android using Kotlin. The UI is also implemented through Kotlin code with [Jetpack Compose](https://developer.android.com/jetpack/compose). The app architecture conforms to the MVVM pattern in which [View Model](https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=en) and [Unidirectional Data Flow](https://developer.android.com/jetpack/compose/architecture#:~:text=A%20unidirectional%20data%20flow%20%28UDF%29%20is%20a%20design,app%20using%20unidirectional%20data%20flow%20looks%20like%20this%3A) play the major role. The framework for backend is also backed by AWS, an open-source software framework called [Amplify](https://docs.amplify.aws/lib/q/platform/android/).

![](https://i.imgur.com/9b2N8HB.png "System Components")

## List of Devices
No.  | Device Name | Part Name | Description |
---  | ---         | ---       | ---         |
1 | Pulse Sensor | [MAX30102](https://hshop.vn/products/cam-bien-nhip-tim-va-oxy-trong-mau-max30102-mh-et-live) | INPUT: measures heart rate |
2 | Oximeter Sensor | [MAX30102](https://hshop.vn/products/cam-bien-nhip-tim-va-oxy-trong-mau-max30102-mh-et-live) | INPUT: measures oxygen concentration in blood |
3 | Temperature Sensor | [DHT11](https://hshop.vn/products/grove-temperature-humidity-sensor-dht11-cam-bien-nhiet-do-do-am) | INPUT: measures temperature |
4 | SoC RF Kit | [ESP32](https://hshop.vn/products/kit-rf-thu-phat-wifi-ble-esp32-s2-nodemcu-32-s2-ai-thinker) | CONTROLLER: controls sensors and the transmission | N/A |

