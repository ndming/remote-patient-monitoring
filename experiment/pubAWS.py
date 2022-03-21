from awscrt import io, mqtt, exceptions
import json, time, random

ENDPOINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

CLIENT_ID = "RPMSOS0000"
TOPIC     = "rpm/sos/0000"
PUB_QOS   = mqtt.QoS.AT_LEAST_ONCE
MAX_RECONNECTION_ATTEMPTS = 5

CERTI_PATH = 'auth/RPMSOS0000/f0cad44e7c59c5ccd48f76c3a8a3076ddbaeba5bac2414f94f19c16ac031706b-certificate.pem.crt'
KEY_PATH = 'auth/RPMSOS0000/f0cad44e7c59c5ccd48f76c3a8a3076ddbaeba5bac2414f94f19c16ac031706b-private.pem.key'

MESSAGE = {
    "cid": "RPMSOS0000",
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

def onConnectionInterrupted(connection: mqtt.Connection, error: exceptions.AwsCrtError, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is lost.
    The MQTT client will automatically attempt to reconnect.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection.
        *   `error` (:class:`awscrt.exceptions.AwsCrtError`): Exception which caused connection loss.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[C] connection interrupted: {error.message}")

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
    


def getMessage() -> dict:
    mess = MESSAGE
    mess['data']['pulse']['value'] = round(random.uniform(65.5, 75.5), 1)
    mess['data']['oxi']['value'] = round(random.uniform(85.5, 95.5), 1)
    mess['data']['temper']['value'] = round(random.uniform(36.5, 38.5), 1)
    mess['time'] = time.time()
    print("your message:")
    print(json.dumps(mess, indent=2))
    return mess

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
clientConnection = mqtt.Connection(
    client=myAWSIoTClient,
    host_name=ENDPOINT,
    port=HOSTPORT,
    client_id=CLIENT_ID,
    clean_session=False,
    on_connection_interrupted=onConnectionInterrupted,
    on_connection_resumed=onConnectionResumed
    # TODO: will
)


try:
    # connect to AWS IoT core
    connectFuture = clientConnection.connect()
    connectResult = connectFuture.result()  # will raise an AwsCrtError on failure
    print(f"[C] connected: session_present<{connectResult['session_present']}>")

    # get some value from input terminal
    mess = getMessage()

    # publish some message
    publishTuple = clientConnection.publish(
        topic=TOPIC,
        payload=json.dumps(mess).encode(),
        qos=PUB_QOS,
        retain=True
    )
    publishFuture = publishTuple[0]
    publishResult = publishFuture.result()
    print(f"[P] published: packet_id<{publishResult['packet_id']}>")

except KeyboardInterrupt:
    # disconnect(clientConnection)
    disconnectFuture = clientConnection.disconnect()
    disconnectFuture.result()
    print("[C] disconnected")

except exceptions.AwsCrtError as e:
    print(f"[E] connection failed: {e.message}")