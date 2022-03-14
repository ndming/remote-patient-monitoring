from awscrt import io, mqtt, auth, exceptions
from awsiot import mqtt_connection_builder
import json
from concurrent.futures import Future


ENDPOINT = "a28tut6cil5f32-ats.iot.us-west-2.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

CLIENT_ID = "SUB03"
TOPIC     = "expecto/temper"
SUB_QOS   = mqtt.QoS.AT_LEAST_ONCE

CA_PATH = 'auth/AmazonRootCA1.pem'
CERTI_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-certificate.pem.crt'
KEY_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-private.pem.key'


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

def onConnectionInterrupted(connection: mqtt.Connection, error: exceptions.AwsCrtError, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is lost.
    The MQTT client will automatically attempt to reconnect.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection.
        *   `error` (:class:`awscrt.exceptions.AwsCrtError`): Exception which caused connection loss.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[W] connection interrupted: {error.message}")

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
# clientConnection = mqtt_connection_builder.mtls_from_path(
#     cert_filepath=CERTI_PATH,
#     pri_key_filepath=KEY_PATH,
#     endpoint=ENDPOINT,
#     client_bootstrap=clientBootstrap,
#     client_id=CLIENT_ID,
#     on_connection_interrupted=onConnectionInterrupted,
#     on_connection_resumed=onConnectionResumed,
#     clean_session=False,
#     ca_filepath=CA_PATH
# )
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
    clean_session=True,
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