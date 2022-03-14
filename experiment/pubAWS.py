from awscrt import io, mqtt, auth, exceptions
from awsiot import mqtt_connection_builder
from concurrent.futures import Future
import json

ENDPOINT = "a28tut6cil5f32-ats.iot.us-west-2.amazonaws.com"    # endpoint
HOSTPORT = 8883                                                # non web-socket

CLIENT_ID = "PUB01"
TOPIC     = "expecto/temper"
PUB_QOS   = mqtt.QoS.AT_LEAST_ONCE

CA_PATH = 'auth/AmazonRootCA1.pem'
CERTI_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-certificate.pem.crt'
KEY_PATH = 'auth/f4de10df05578d316754fc9e7f26dd69286fb885784c38fba3af5b83ebef6617-private.pem.key'

MESSAGE = {
    "id":3,
    "name":"TEMPER",
    "data":21,
    "unit":"\u00b0C"
}

def onConnectionInterrupted(connection: mqtt.Connection, error: exceptions.AwsCrtError, **kwargs: dict):
    """
    Callback invoked whenever the MQTT connection is lost.
    The MQTT client will automatically attempt to reconnect.
        *   `connection` (:class:`awscrt.mqtt.Connection`): This MQTT Connection.
        *   `error` (:class:`awscrt.exceptions.AwsCrtError`): Exception which caused connection loss.
        *   `**kwargs` (dict): Forward-compatibility kwargs.
    """
    print(f"[C] connection interrupted: {error.name} -> {error.message}")

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
    print(f"[C] connection resumed: code <{return_code.name}> | session_present <{session_present}>")



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

    # publish some message
    publishTuple = clientConnection.publish(
        topic=TOPIC,
        payload=json.dumps(MESSAGE).encode(),
        qos=PUB_QOS,
        retain=True
    )
    publishFuture = publishTuple[0]
    publishResult = publishFuture.result()
    print(f"[P] published: packet_id<{publishResult['packet_id']}>")

except KeyboardInterrupt:
    # disconnect
    disconnectFuture = clientConnection.disconnect()
    disconnectFuture.result()
    print("[C] disconnected")

except exceptions.AwsCrtError as e:
    print(f"[E] connection failed: {e.message}")