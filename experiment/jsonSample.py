import json

PIPELINE_ATTRIBUTES = {
    "clientId": "RPMSOS00A5",
    "timestamp": 1647755842.2021348,
    "pulse": 69.8,
    "oxi": 91.1,
    "temper": 37.2,
}

MESSAGE_SJSON = "{\"cid\": \"RPMSOS0000\",\"size\": 3,\"time\": 1647923624.7255058,\"data\": {\"pulse\": {\"value\": 69.2,\"unit\": \"bpm\"},\"oxi\": {\"value\": 89.9,\"unit\": \"%\"},\"temper\": {\"value\": 38.0,\"unit\": \"\u00b0C\"}}}"

with open('pipeline.json', 'w') as jfile:
    json.dump(PIPELINE_ATTRIBUTES, jfile)