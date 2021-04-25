from flask import Blueprint

from .extensions import mongo 

main = Blueprint('main', __name__)

@main.route('/')
def index():
    user_collection = mongo.db.users
    user_collection.insert({'name' : 'Cristina'})
    user_collection.insert({'name' : 'Derek'})
    return '<h1>Added a User!</h1>'

   
    {"hrm":"88.35","step":6,"batt":100,"acc":{"x":0.02429199218,"y":-0.81811523437,"z":-0.5361328125,"mag":0.97843805641,"diff":0.00460158125},"com":{"x":45,"y":115,"z":167,"dx":21,"dy":-4,"dz":7,"heading":100.78429786756},"gps":{"lat":null,"lon":null,"alt":null,"speed":null,"course":null,"time":"2021-04-22T11:21:23.000Z","satellites":0,"fix":0,"hdop":99.99}}

  {_id=12.0/37.0/9.0/25/4/2021, query={"acc":{"diff":0.00418257853,"mag":1.01064861067,"x":0.00402832031,"y":0.02795410156,"z":1.01025390625},"btt":100,"com":{"dx":5.0,"dy":5.0,"dz":3.0,"heading":0.0,"x":20.0,"y":415.0,"z":134.0},"gps":{"alt":818.9,"lat":40.93972983333,"lon":-5.6453845,"speed":0.400032},"hrm":91.47,"steps":117,"time":"12.0/37.0/9.0/25/4/2021"}}
