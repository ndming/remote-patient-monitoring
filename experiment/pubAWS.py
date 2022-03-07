from awscrt import io, mqtt, auth, http
from awsiot import mqtt_connection_builder
import time, json

HOST = "a28tut6cil5f32-ats.iot.us-west-2.amazonaws.com"    # endpoint
PORT = 8883                                                # non web-socket

CLIENT_ID = "PUB01"
TOPIC     = "experiment/temperature"
PUB_QOS   = mqtt.QoS.AT_LEAST_ONCE

CA_PATH = 'auth/AmazonRootCA1.pem'
CERTI_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-certificate.pem.crt'
KEY_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-private.pem.key'

MESSAGE = {
    "id":3,
    "name":"TEMPER",
    "data":20,
    "unit":"\u00b0C"
}

# spin-up resource
event_loop_group = io.EventLoopGroup(1)
host_resolver = io.DefaultHostResolver(event_loop_group)
client_bootstrap = io.ClientBootstrap(event_loop_group, host_resolver)
myAWSIoTClient = mqtt_connection_builder.mtls_from_path(
    endpoint=HOST,
    cert_filepath=CERTI_PATH,
    pri_key_filepath=KEY_PATH,
    client_bootstrap=client_bootstrap,
    ca_filepath=CA_PATH,
    client_id=CLIENT_ID,
    clean_session=False,
    keep_alive_secs=6
)

# connect to AWS IoT core
connect_future = myAWSIoTClient.connect()
connect_future.result() # waits until a result is available

# publish some message
myAWSIoTClient.publish(topic=TOPIC, payload=json.dumps(MESSAGE), qos=PUB_QOS, retain=True)
time.sleep(5)

# disconnect
connect_future = myAWSIoTClient.disconnect()
connect_future.result()