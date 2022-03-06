from asyncore import loop
from cv2 import repeat
import paho.mqtt.client as mqtt
import time, json, random

HOST_NAME = "2f668bbef55945689d879335037e3110.s2.eu.hivemq.cloud" 
HOST_PORT = 8883        # tls secure 
USERNAME  = "angelus"
PASSWORD  = "1475963Angelus"

CLIENT_ID = "PUB00"
TOPIC = "experiment/humidity"
PUB_QOS = 0

def on_connect(client, userdata, flags, rc, properties=None):
    if rc != 0:
        print(f"connection failed: {mqtt.connack_string(rc)}")
    else:
        print(f"connect sucessfully!")

def on_disconnect(client, userdata, rc, properties=None):
    print(f"disconnected")
    if (rc != 0):
        print(f"unexpected disconnection: {mqtt.error_string(rc)}")

def on_publish(client, userdata, mid):
    print(f"published to broker: mid={mid}")

pub = mqtt.Client(client_id=CLIENT_ID, protocol=mqtt.MQTTv5)
pub.tls_set(tls_version=mqtt.ssl.PROTOCOL_TLS)
pub.username_pw_set(username=USERNAME, password=PASSWORD)

pub.on_connect = on_connect
pub.on_disconnect = on_disconnect
pub.on_publish = on_publish

pub.connect(host=HOST_NAME, port=HOST_PORT)
pub.loop_start()

times = 5
while times > 0:
    time.sleep(5)
    pubDone = pub.publish(topic=TOPIC, payload=random.randint(20, 30), qos=PUB_QOS, retain=True)
    pubDone.wait_for_publish()
    times -= 1

time.sleep(5)
pub.disconnect()

time.sleep(1)