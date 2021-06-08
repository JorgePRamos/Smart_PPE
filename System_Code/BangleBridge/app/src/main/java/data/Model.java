package data;


import android.util.Log;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.FindIterable;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import static com.mongodb.client.model.Indexes.descending;
import static java.lang.Thread.sleep;

public class Model implements Serializable {
    public TreeMap<String,Measurement> measurements = null;
    public Map<String,Measurement> sortenMap  = new TreeMap<String,Measurement>();;
    public Measurement lastInsert = null;
    private String lastUpload = "2021-04-29T00:00:00.000Z";
    private User usr;
    public Model() {
        measurements = new TreeMap<String,Measurement>();

    }



    public void mongoUpMap(User usr){

        Log.d("MONGO","MOngo upMap");
        MongoClient mongoClient = usr.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase =
                mongoClient.getDatabase("MeasurementsDB");
        MongoCollection<Document> mess =
                mongoDatabase.getCollection("Measurements");

        //Log.d("MONGO", "ESte es el ultimo---> "+myDoc.toString());

        //RealmResultTask<Document> yoyo =  mess.find().first();
        //RealmResultTask<Document> yoyo =
     //  Measurement mTemp = new Measurement();
        //FindIterable<Document> ressult= mess.find().limit(1).sort(new BasicDBObject("_id",-1));



        //****

       //String a= mess.find().limit(1).sort(new BasicDBObject("_id",-1)).first();


        BasicDBObject getQuery = new BasicDBObject();
        //        getQuery.put("employeeId", new BasicDBObject("$gt", 2).append("$lt", 5));

        Log.d("MONGO","LASTUPLOAD--->"+ lastUpload);
        getQuery.put("_id", new BasicDBObject("$eq", lastUpload));



        //****
        mess.findOne(getQuery).getAsync(task -> {
            if (task.isSuccess()) {
                Document result = task.get();
                if (result != null){
                    for (HashMap.Entry<String, Measurement> m:measurements.entrySet()) {
                        Log.d("MONGO", "Mess---> "+ m.getKey()+"\n");
                    }
                    sortenMap = measurements.tailMap(lastUpload);
                    sortenMap.remove(lastUpload);
                    Log.v("MONGO", "-->successfully found a document: " + result+"\n Numeber in sorterd-->"+sortenMap.size());
                    for (HashMap.Entry<String, Measurement> m:sortenMap.entrySet()) {
                        Log.d("MONGO", "shorted map #### "+ m.getKey()+"\n");
                    }

                }else {
                    for (HashMap.Entry<String, Measurement> m:measurements.entrySet()) {
                        Log.d("MONGO", "Mess---> "+ m.getKey()+"\n");
                    }

                    sortenMap = measurements;
                    Log.d("MONGO", "## Result null: "+result +"\nNumeber in Meassuremap-->"+measurements.size());
                    for (HashMap.Entry<String, Measurement> m:sortenMap.entrySet()) {
                        Log.d("MONGO", "shorted map #### "+ m.getKey()+"\n");
                    }
                }

            } else {
                Log.e("MONGO", "failed to find document with: ", task.getError());
            }

            HashMap<String,Measurement> cropedmeasurements = null;

            List<Document> documents = new ArrayList<>();
            for(HashMap.Entry<String, Measurement> kv :sortenMap.entrySet()) {//change to cropped
                Document doc = new Document();

                doc.put("_id", kv.getKey());
                String json =  new Gson().toJson(kv.getValue());
                Object o = BasicDBObject.parse(json);
                DBObject dbObj = (DBObject) o;

                doc.put("query", dbObj);

                Log.d("MONGO", "****> "+doc.toString());

                lastUpload = kv.getKey();
                documents.add(doc);
            }


            mess.insertMany(documents).getAsync(task2 -> {
                if (task2.isSuccess()) {
                    Log.v("MONGO", "successfully inserted a document  " + task2.get());
                } else {
                    Log.e("MONGO", "failed to insert documents with: " + task2.getError().getErrorMessage());
                }
            });
        });






    }




    /*
    public void  mongoDownMap(){
        MongoClient client = MongoClients.create("mongodb+srv://Jorge:06051999@smartppe.sr2rn.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        MongoDatabase database = client.getDatabase("MeasurementsDB");
        MongoCollection<Document> mess = database.getCollection("Measurements");
        List<Document> myDocs = mess.find(new Document("hrm", "88.35")).into(new ArrayList<Document>());//change

        for (Document d : myDocs) {
            Gson gson = new Gson();
            String filtered = d.get("query").toString();

            Measurement importedMess = gson.fromJson(filtered, Measurement.class);
            //Check if imported mess is correct
            this.measurements.put(importedMess.getTime(),importedMess);

        }

    }*/
    }





