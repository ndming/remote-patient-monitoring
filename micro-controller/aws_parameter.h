#include <ArduinoJson.h>
#include "string.h"

const char* THINGNAME = "RPMSOS0000";
char AWS_IOT_ENDPOINT[] = "a2r0f2fq44oocy-ats.iot.us-east-1.amazonaws.com";
const String cid = "RPMSOS0000";

#define AWS_IOT_PUBLISH_TOPIC  "rpm/sos/RPMSOS0000"
#define AWS_IOT_PUBLISH_STATUS "rpm/tus/RPMSOS0000"

#define OFFLINE 1
#define ONLINE  0
