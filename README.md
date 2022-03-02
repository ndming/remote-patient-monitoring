# Remote Patient Monitoring
**Brief:** statistic says that more than 30% of medical patients re-admit into the hospital after surgery. One of the causing is attributed to the lack of monitoring infrastructure when the patients go home. In the time when Covid-19 is threatening face-to-face interaction, the situation gets even worse. With remote monitoring IoT system, doctors can now monitor patient status through sensors embedded on patients' wearable devices, hence preventing fatal cases from emerging.

## Expected Outcome
- Two or more sensor nodes that capture health status from an end user (patient).
- A central microcontroller that controls the sensors and transmits captured data through a wireless channel.
- A gateway software that receives data from the wireless channel, then processes and publishes data to an MQTT broker.
- A mobile application that subscribes and listens to certain topics from the broker and intuitively displays the data to the end user (doctor).
- A database server for authentication.

## Functionality
The system initiates when there is a compromise between a doctor and a patient. The patient receives a Set of Sensors (or a wearable that is capable of doing the same functionality) named SOS. The number of SOSes is finite, and each SOS has a unique preset ID which, in turn, associates with a Group of Topics[^1] on the MQTT broker.

On the patient side, they will have to make a connection between the SOS and the gateway software using the monitoring application. The application provides 3 profiles: *patient*, *observer* and *doctor*. The patient uses the *patient profile* to establish the SOS-gateway communication with [Bluetooth](https://www.bluetooth.com/). This profile type is simple and requires only the ID of the SOS associated with the patient. To access the service, a password must also be provided. The patients can get the password from the doctor that assigns them the SOS. The profile provides a simple notification service and a messaging system so that the other 2 profiles can prompt the patient when needed. Anyone who wishes to observe the status of this patient can use the *observer profile* to keep track of the target's health status. Again, the ID and a password are needed to access the service.

On the doctor side, they also use the mobile application to monitor their associated patients. Unlike the observers, a doctor may have a couple of patients to keep track of, and the *doctor profile* is solely for this purpose. The doctor will need to create an account. Each doctor account will have a finite number of SOS in store for his or her own patients. The doctor profile offers basic operations such as add or remove a patient from the list of associated patients. When a patient is added to the list with the specified SOS's ID, the system will also return the password associated with this SOS so that the doctor can provide the patient with this password (if they want the observing service). The doctor may see multiple patients' status at a time on their mobile phone. There will also be a couple of useful hands-on services for the *doctor profile* such as alerting or sending a short message to the patient. When a patient is removed from the list, the password is renewed, hence the returned SOS can be reused for another patient.

## List of Devices
No.  | Device Name | Part Name | Description | JSON |
---  | ---         | ---       | ---         | ---  |
1 | Pulse Sensor | [MAX30102](https://hshop.vn/products/cam-bien-nhip-tim-va-oxy-trong-mau-max30102-mh-et-live) | INPUT: measures heart rate | { <br> `"id":"1"` <br> `"name":"PULSE"` <br> `"data":"X"` <br> `"unit":"bpm"` <br>} <br> *X: pulse value (bpm)* |
2 | Oximeter Sensor | [MAX30102](https://hshop.vn/products/cam-bien-nhip-tim-va-oxy-trong-mau-max30102-mh-et-live) | INPUT: measures oxygen concentration in blood | { <br> `"id":"2"` <br> `"name":"OXYGEN"` <br> `"data":"X"` <br> `"unit":"%"` <br>} <br> *X: concentration value (%)* |
3 | Temperature Sensor | [DHT11](https://hshop.vn/products/grove-temperature-humidity-sensor-dht11-cam-bien-nhiet-do-do-am) | INPUT: measures temperature | { <br> `"id":"3"` <br> `"name":"TEMPER"` <br> `"data":"X"` <br> `"unit":"°C"` <br>} <br> *X: temperature value (°C)* |
4 | Arduino Board | [UNO R3](https://docs.arduino.cc/hardware/uno-rev3) | CONTROLLER: controls sensors and the transmitter | N/A |
5 | Bluetooth Module | [HM-10](https://hshop.vn/products/mach-thu-phat-bluetooth-4-0uart-hm10ra-chon) | TRANSMITTER: sends data from the Arduino board to the gateway | N/A |


[^1]: The Adafruit server offers a feature known as *group*. Since a patient will require at least 3 feeds tracking his status, a group of 3 or more topics is intuitive to represent a patient. 
