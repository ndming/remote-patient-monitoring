from awscrt import io, mqtt, exceptions
import json, time, random

ENDPOINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

CLIENT_ID = "RPMSOS0000"
DATA_TOPIC = f"rpm/soss/{CLIENT_ID}"
STAT_TOPIC = f"rpm/tus/{CLIENT_ID}"
PUB_QOS   = mqtt.QoS.AT_LEAST_ONCE

NUM_OF_REPETITIONS = 1
PUBLISH_DELAY = 0  # in sec
MAX_RECONNECTION_ATTEMPTS = 2

CERTI_PATH_0000 = 'auth/RPMSOS0000/69a713daa8a9d05b477ea0172939360fff01619c064b7720f2ec5882503a79ee-certificate.pem.crt'
KEY_PATH_0000 = 'auth/RPMSOS0000/69a713daa8a9d05b477ea0172939360fff01619c064b7720f2ec5882503a79ee-private.pem.key'

CERTI_PATH_0001 = 'auth/RPMSOS0001/335e60492169df4893484de4644741ddd52dbe1abdc4cc54e1c1e060933605d7-certificate.pem.crt'
KEY_PATH_0001 = 'auth/RPMSOS0001/335e60492169df4893484de4644741ddd52dbe1abdc4cc54e1c1e060933605d7-private.pem.key'

DATA_MESSAGE = {
    "cid": CLIENT_ID,
    "size": 3,
    "time": None,
    "data": {
        "pulse": {
            "value": None,
            "unit": "bpm",
        },
        "oxi": {
            "value": None,
            "unit": "%",
        },
        "temper": {
            "value": None,
            "unit": "\u00b0C",
        },
    },
}

STAT_MESSAGE = {
    "cid": CLIENT_ID,
    "code": None
}

WILL_MESSAGE = {
    "cid": CLIENT_ID,
    "code": 2
}

isFailed = False
def onConnectionInterrupted(connection: mqtt.Connection, error: exceptions.AwsCrtError, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is lost.
    The MQTT client will automatically attempt to reconnect.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection.
        *   `error` (:class:`awscrt.exceptions.AwsCrtError`): Exception which caused connection loss.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[C] connection interrupted: {error.message}")
    global isFailed
    isFailed = True

def onConnectionResumed(connection: mqtt.Connection, return_code: mqtt.ConnectReturnCode, session_present: bool, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is automatically resumed.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection
        *   `return_code` (:class:`awscrt.mqtt.ConnectReturnCode`): Connect return
                code received from the server.
        *   `session_present` (bool): True if resuming existing session. False if new session.
                Note that the server has forgotten all previous subscriptions if this is False.
                Subscriptions can be re-established via resubscribe_existing_topics().
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[C] connection resumed: code<{return_code.name}> | session_present<{session_present}>")
    


def getDataMessage() -> dict:
    mess = DATA_MESSAGE
    mess['data']['pulse']['value'] = round(random.uniform(65.5, 75.5), 1)
    mess['data']['oxi']['value'] = round(random.uniform(85.5, 95.5), 1)
    mess['data']['temper']['value'] = round(random.uniform(36.5, 38.5), 1)
    mess['time'] = time.time()
    # print("your message:")
    # print(json.dumps(mess, indent=2))
    return mess

def getStatMessage(code: int) -> dict:
    mess = STAT_MESSAGE
    mess['code'] = code
    return mess

def publishMessage(cn: mqtt.Connection, topic: str, message: dict):
    global isFailed
    publishTuple = cn.publish(
        topic=topic,
        payload=json.dumps(message).encode(),
        qos=PUB_QOS,
        retain=True
    )
    publishFuture = publishTuple[0]
    while not publishFuture.done():
        if isFailed:
            raise exceptions.AwsCrtError(code=None, name="AWS_ERROR_PUBLISH", message="unauthorized topic")
    publishResult = publishFuture.result()
    print(f"[P] published to topic<{topic}>: packet_id<{publishResult['packet_id']}>")

def connectClient(cn: mqtt.Connection):
    connectFuture = cn.connect()
    connectResult = connectFuture.result()  # will raise an AwsCrtError on failure
    print(f"[C] connected: session_present<{connectResult['session_present']}>")
    publishMessage(cn, STAT_TOPIC, getStatMessage(0))

def disconnectClient(cn: mqtt.Connection):
    publishMessage(cn, STAT_TOPIC, getStatMessage(1))
    disconnectFuture = cn.disconnect()
    disconnectFuture.result()
    print("[C] disconnected")

# spin-up resource
eventLoopGroup = io.EventLoopGroup()
hostResolver = io.DefaultHostResolver(eventLoopGroup)
clientBootstrap = io.ClientBootstrap(eventLoopGroup, hostResolver)
tlsContextOptions = io.TlsContextOptions.create_client_with_mtls_from_path(
    cert_filepath=CERTI_PATH_0000,
    pk_filepath=KEY_PATH_0000
)
clientTlsContext = io.ClientTlsContext(tlsContextOptions)
myAWSIoTClient = mqtt.Client(bootstrap=clientBootstrap, tls_ctx=clientTlsContext)
clientWill = mqtt.Will(
    topic=STAT_TOPIC,
    qos=PUB_QOS,
    payload=json.dumps(WILL_MESSAGE).encode(),
    retain=True
)
clientConnection = mqtt.Connection(
    client=myAWSIoTClient,
    host_name=ENDPOINT,
    port=HOSTPORT,
    client_id=CLIENT_ID,
    clean_session=False,
    on_connection_interrupted=onConnectionInterrupted,
    on_connection_resumed=onConnectionResumed,
    will=clientWill
)

try:
    # connect to AWS IoT core and set status to 0 (online)
    connectClient(clientConnection)

    for i in range(NUM_OF_REPETITIONS):
        # publish a random-value message
        publishMessage(clientConnection, DATA_TOPIC, getDataMessage())

        # delay
        time.sleep(PUBLISH_DELAY)
    
    # disconnect on completed publish and set status to 1 (offline)
    disconnectClient(clientConnection)

except exceptions.AwsCrtError as e:
    print(f"[E] {e.name}: {e.message}")