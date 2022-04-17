from awscrt import io, mqtt, exceptions
import json, time, random, re

from credentials import Target

ENDPOINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

DEVICE_PATTERN = "RPMSOS([0-9]|[A-F]){4,4}"
CLIENT_ID = str()
try:
    while (not re.match(DEVICE_PATTERN, CLIENT_ID)):
        if (CLIENT_ID != ""):
            print("Invalid device Id")
        CLIENT_ID = str(input("Enter target device Id: "))
except KeyboardInterrupt:
    exit(1)


TARGET = Target.getCredentials(CLIENT_ID)
if (TARGET is None):
    print("This device is not registered yet")
    exit(1)
CERTI_PATH = TARGET.certi_path
KEY_PATH = TARGET.key_path

DATA_TOPIC = f"rpm/sos/{CLIENT_ID}"
STAT_TOPIC = f"rpm/tus/{CLIENT_ID}"
PUB_QOS   = mqtt.QoS.AT_LEAST_ONCE
MAX_RECONNECTION_ATTEMPTS = 2

DATA_MESSAGE = {
    "cid": CLIENT_ID,
    "size": 3,
    "time": None,
    "data": {
        "pulse": None,
        "spo2": None
    },
}

STAT_MESSAGE = {
    "cid": CLIENT_ID,
    "code": None
}

WILL_MESSAGE = {
    "cid": CLIENT_ID,
    "code": 1
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
    print(f"[C] {error.name}: {error.message}")
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
    mess['data']['pulse'] = random.randint(60, 80)
    mess['data']['spo2'] = random.randint(80, 100)
    mess['time'] = round(time.time())
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
            raise exceptions.AwsCrtError(code=None, name="AWS_PUBLISH_ERROR", message="unauthorized topic")
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
    cert_filepath=CERTI_PATH,
    pk_filepath=KEY_PATH
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

numOfMessageToPublish = int(input("No. of messages: "))
if numOfMessageToPublish < 0: numOfMessageToPublish = 1
publishDelay = int(input("Delay in-between(s): "))
if publishDelay < 5: publishDelay = 5

try:
    # connect to AWS IoT core and set status to 0 (online)
    connectClient(clientConnection)

    for i in range(numOfMessageToPublish):
        # publish a random-value message
        publishMessage(clientConnection, DATA_TOPIC, getDataMessage())

        # delay
        time.sleep(publishDelay)
    
    # disconnect on completed publish and set status to 1 (offline)
    disconnectClient(clientConnection)

except exceptions.AwsCrtError as e:
    print(f"[E] {e.name}: {e.message}")
except KeyboardInterrupt:
    disconnectClient(clientConnection)