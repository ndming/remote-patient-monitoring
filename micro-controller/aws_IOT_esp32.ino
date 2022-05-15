#include "Arduino.h"
#include "aws_certificate.h"
#include "aws_parameter.h"
#include "wifi_credential.h"
#include "string.h"
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include "WiFi.h"
#include "time.h"
#include "aws_IOT_esp32.h"

// Secure connection
WiFiClientSecure net = WiFiClientSecure();
// Client MQTT
PubSubClient client(net);
void connectAWS()
{
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
 
  Serial.println("Connecting to Wi-Fi");
 
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
 
  // Configure WiFiClientSecure to use the AWS IoT device credentials
  net.setCACert(AWS_CERT_CA);
  net.setCertificate(AWS_CERT_CRT);
  net.setPrivateKey(AWS_CERT_PRIVATE);
 
  // Connect to the MQTT broker on the AWS endpoint we defined earlier
  client.setServer(AWS_IOT_ENDPOINT, 8883);
 
  // Create a message handler
 
  Serial.println("Connecting to AWS IOT");
  StaticJsonDocument<200> doc_off;
  doc_off["cid"]  = cid;
  doc_off["code"] = OFFLINE;
  char msg_offline[512];
  serializeJson(doc_off, msg_offline);
  Serial.println(msg_offline);
  while (!client.connect(THINGNAME, AWS_IOT_PUBLISH_STATUS, 1, true, msg_offline))
  {
    Serial.print(".");
    delay(100);
  }
 
  if (!client.connected())
  {
    Serial.println("AWS IoT Timeout!");
    return;
  }
 
  // Subscribe to a topic
//  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC);
  StaticJsonDocument<200> doc_on;
  doc_on["cid"]  = cid;
  doc_on["code"] = ONLINE;
  char msg_online[512];
  serializeJson(doc_on, msg_online);
  Serial.println(msg_online);
  client.publish(AWS_IOT_PUBLISH_STATUS, msg_online, true);
  Serial.println("AWS IoT Connected!");
}
void publishMessage(String cid, int s_size, double p_val, double o_val)
{
  StaticJsonDocument<200> doc;
  doc["cid"] = cid;
  doc["size"] = s_size;
  doc["time"]= Get_Epoch_Time();
  doc["data"]["pulse"] = p_val;
  doc["data"]["spo2"]  = o_val;
  char jsonBuffer[512];
  serializeJson(doc, jsonBuffer); // print to clien
  client.publish(AWS_IOT_PUBLISH_TOPIC, jsonBuffer, true);
  Serial.println(jsonBuffer);
}


unsigned long Get_Epoch_Time() {
  time_t now;
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    Serial.println("Failed to obtain time");
    return(0);
  }
  time(&now);
  return now;
}
