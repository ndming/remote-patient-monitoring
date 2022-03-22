from awscrt import io, mqtt, exceptions
import json


ENDPOINT = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

CLIENT_ID = "RPMDOC0000"
TOPIC     = "rpm/sos/RPMSOS000"    # subscribe topic
SUB_QOS   = mqtt.QoS.AT_LEAST_ONCE

CERTI_PATH = 'auth/RPMDOC/0cd28110e617ed0a83e0a667c5966bf54878cd197f471a3312a9b9be6ca6c0c4-certificate.pem.crt'
KEY_PATH = 'auth/RPMDOC/0cd28110e617ed0a83e0a667c5966bf54878cd197f471a3312a9b9be6ca6c0c4-private.pem.key'

MAX_RECONNECTION_ATTEMPTS = 3

def onMessage(topic: str, payload: bytes, dup: bool, qos: mqtt.QoS, retain: bool, **kwargs: dict):
    """
    This callback is invoked when ANY message is received
        *   `topic` (str): Topic receiving message.
        *   `payload` (bytes): Payload of message.
        *   `dup` (bool): DUP flag. If True, this might be re-delivery
            of an earlier attempt to send the message.
        *   `qos` (:class:`QoS`): Quality of Service used to deliver the message.
        *   `retain` (bool): Retain flag. If True, the message was sent
            as a result of a new subscription being made by the client.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[M] message received from '{topic}'")
    dict = json.loads(payload.decode())
    print(json.dumps(dict, indent=2))

countFail = 0
def onConnectionInterrupted(connection: mqtt.Connection, error: exceptions.AwsCrtError, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is lost.
    The MQTT client will automatically attempt to reconnect.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection.
        *   `error` (:class:`awscrt.exceptions.AwsCrtError`): Exception which caused connection loss.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[W] connection interrupted: {error.message}")
    global countFail
    countFail += 1

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

    # subscribe to a topic
    subscribeTuple = clientConnection.subscribe(
        topic=TOPIC,
        qos=SUB_QOS,
        callback=onMessage
    )
    subscribeFuture = subscribeTuple[0]
    while not subscribeFuture.done():
        if countFail > MAX_RECONNECTION_ATTEMPTS:
            print(f"[E] maximum reconnection attempts tried.")
            raise KeyboardInterrupt
    subscribeResult = subscribeFuture.result()
    print(f"[S] subscribed: subpacket_id<{subscribeResult['packet_id']}> | topic<{subscribeResult['topic']}> | QoS<{subscribeResult['qos']}>")

    # listening
    while True:
        pass

except KeyboardInterrupt:
    # disconnect
    disconnectFuture = clientConnection.disconnect()
    disconnectFuture.result()
    print("[C] disconnected")

except mqtt.SubscribeError as e:
    print(f"[S] subscription failed: {e}")

except exceptions.AwsCrtError as e:
    print(f"[E] connection failed: {e.message}")