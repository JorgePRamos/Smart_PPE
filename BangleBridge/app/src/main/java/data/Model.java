package data;


import android.util.Log;

import com.google.gson.Gson;

import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class Model implements Serializable {
    public HashMap<String,Measurement> measurements = null;
    private User usr;
    public Model() {
        measurements = new HashMap<String,Measurement>();

    }



    public void mongoUpMap(){
        Log.d("MONGO","MOngo upMap");
        MongoClient mongoClient = usr.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase =
                mongoClient.getDatabase("MeasurementsDB");
        MongoCollection<Document> mess =
                mongoDatabase.getCollection("Measurements");

        Document toy = new Document("name", "yoyo") .append("ages", new Document("min", 5));
        mess.insertOne(toy).getAsync(task -> {
            if (task.isSuccess()) {
                Log.v("MONGO", "successfully inserted a document with id: " + task.get().getInsertedId());
            } else {
                Log.e("MONGO", "failed to insert documents with: " + task.getError().getErrorMessage());
            }
        });;
       // Document pepe = (Document) mess.findOne();
        //Log.d("TimeDebug","DOWN ------>"+pepe.toJson());
        //System.out.println(pepe.toJson());
        Log.d("MONGO","TOY --> "+toy);
         HashMap<String,Measurement> cropedmeasurements = null;

        List<Document> documents = new ArrayList<>();
        for(HashMap.Entry<String, Measurement> kv :measurements.entrySet()) {//change to cropped
            Document doc = new Document();
            doc.put("\"_id\"", kv.getKey());

            doc.put("\"query\"", new Gson().toJson(kv.getValue()));



            documents.add(doc);
        }
        mess.insertMany(documents);

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





