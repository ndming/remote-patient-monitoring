import json

jsStr = '{"name":{"firstName":"Minh","lastName":"Nguyen"},"id":[{"value":20, "auth":"family"},{"value":1952092, "auth":"school"}]}'

dic = json.loads(jsStr) # json string to python dictionary

print(dic["name"]["firstName"])
print(dic["id"][0]["value"])

someDict = {
    "name":{
        "firstName":"Minh",
        "lastName":"Nguyen"
    },
    "id":[
        {
            "value":20,
            "auth":"family"
        },
        {
            "value":1952092,
            "auth":"school"
        }
    ]
}

js = json.dumps(someDict)   # python dictionary to json string

print(js)