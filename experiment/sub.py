import paho.mqtt.client as mqtt
import time, json

HOST_NAME = "2f668bbef55945689d879335037e3110.s2.eu.hivemq.cloud"   # hiveMQ
HOST_PORT = 8883
USERNAME  = "angelus"
PASSWORD  = "1475963Angelus"

CLIENT_ID = "SUB00"
TOPIC = "experiment/+"
SUB_QOS = 0

def on_connect(client, userdata, flags, rc, properties=None):
    if rc != 0:
        print(f"connection failed: {mqtt.connack_string(rc)}")
    else:
        print(f"connect sucessfully!")

def on_disconnect(client, userdata, rc, properties=None):
    print(f"disconnected")
    if (rc != 0):
        print(f"unexpected disconnection: {mqtt.error_string(rc)}")

def on_subscribe(client, userdata, mid, rc, properties=None):
    print(f"subscribed to target topic!")

def on_message(client, userdata, message):
    print(f"received {message.payload.decode()}")

sub = mqtt.Client(client_id=CLIENT_ID, protocol=mqtt.MQTTv5)
sub.tls_set(tls_version=mqtt.ssl.PROTOCOL_TLS)
sub.username_pw_set(username=USERNAME, password=PASSWORD)

sub.on_connect = on_connect
sub.on_disconnect = on_disconnect
sub.on_subscribe = on_subscribe
sub.on_message = on_message

sub.connect(host=HOST_NAME, port=HOST_PORT)
sub.loop_start()

sub.subscribe(topic=TOPIC, qos=SUB_QOS)

try:
    while True:
        pass
except KeyboardInterrupt:
    sub.disconnect()
    time.sleep(1)
