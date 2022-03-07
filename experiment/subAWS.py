from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTClient
import json


def customCallback(client, userdata, message):
    print(f"[received from topic '{message.topic}']")
    dict = json.loads(message.payload)  # save as a Py dictionay
    print(json.dumps(dict, indent=2))   # print as JSON format
    

def onOnline():
    print("client is ONLINE!")

def onOffline():
    print("client is OFFLINE!")


HOST = "a28tut6cil5f32-ats.iot.us-west-2.amazonaws.com"    # endpoint
PORT = 8883                                                # non web-socket

CLIENT_ID = "SUB01"
TOPIC     = "experiment/temperature"
SUB_QOS   = 1

CA_PATH = 'auth/AmazonRootCA1.pem'
CERTI_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-certificate.pem.crt'
KEY_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-private.pem.key'

# create an MQTT client
myAWSMQTTClient = AWSIoTMQTTClient(CLIENT_ID)
myAWSMQTTClient.configureEndpoint(HOST, PORT)
myAWSMQTTClient.configureCredentials(
    CAFilePath=CA_PATH,
    KeyPath=KEY_PATH,
    CertificatePath=CERTI_PATH
)


myAWSMQTTClient.onOnline = onOnline
myAWSMQTTClient.onOffline = onOffline

myAWSMQTTClient.connect()
myAWSMQTTClient.subscribe(topic=TOPIC, QoS=SUB_QOS, callback=customCallback)

try:
    while True:
        pass
except KeyboardInterrupt:   # break loop with Ctrl C
    myAWSMQTTClient.disconnect()