package data;


import android.util.Log;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.InsertManyOptions;

import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import static com.mongodb.client.model.Aggregates.limit;
import static java.lang.Thread.sleep;

public class Model implements Serializable {
    public HashMap<String,Measurement> measurements = null;
    private User usr;
    public Model() {
        measurements = new HashMap<String,Measurement>();

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
        Measurement mTemp = new Measurement();
        mess.findOne().getAsync(task -> {
            if (task.isSuccess()) {
                Document result = task.get();
                Log.v("MONGO", "successfully found a document: " + result);
            } else {
                Log.e("MONGO", "failed to find document with: ", task.getError());
            }
        });



        HashMap<String,Measurement> cropedmeasurements = null;

        List<Document> documents = new ArrayList<>();
        for(HashMap.Entry<String, Measurement> kv :measurements.entrySet()) {//change to cropped
            Document doc = new Document();
            doc.put("_id", kv.getKey());
            String json =  new Gson().toJson(kv.getValue());
            Object o = BasicDBObject.parse(json);
            DBObject dbObj = (DBObject) o;

            doc.put("query", dbObj);

            Log.d("MONGO", "---> "+doc.toString());


            documents.add(doc);
        }


        mess.insertMany(documents).getAsync(task -> {
            if (task.isSuccess()) {
                Log.v("MONGO", "successfully inserted a document  " + task.get());
            } else {
                Log.e("MONGO", "failed to insert documents with: " + task.getError().getErrorMessage());
            }
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





