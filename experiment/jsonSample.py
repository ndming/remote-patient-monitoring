import json

PIPELINE_ATTRIBUTES = {
    "clientId": "RPMSOS00A5",
    "timestamp": 1647755842.2021348,
    "pulse": 69.8,
    "oxi": 91.1,
    "temper": 37.2,
}

with open('pipeline.json', 'w') as jfile:
    json.dump(PIPELINE_ATTRIBUTES, jfile)