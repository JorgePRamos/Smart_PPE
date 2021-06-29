package data;


import android.util.Log;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
/**  Model class reposnible of the storage of Measurements and synchronization of these to the server.
 * @author Jorge
 * @version 1.5
 * @since 1.0
 */
public class Model implements Serializable {


    private String workerID = "11122333G"; //guest ID
    public TreeMap<String, Measurement> measurements = null;
    public Map<String, Measurement> sortenMap = new TreeMap<String, Measurement>();
    ;
    public Measurement lastInsert = null;
    private String lastUpload = "2021-04-29T00:00:00.000Z";
    private User usr;

    public Model(String workerID) {
        measurements = new TreeMap<String, Measurement>();
        this.workerID = workerID;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID(String workerID) {
        this.workerID = workerID;
    }

    public boolean validateDNI(String dni) {

        boolean regex = Pattern.compile("^[0-9]{8,8}[A-Za-z]$").matcher(dni).matches();
        boolean letter = validarLetra(dni);


        return regex && letter;

    }

    public static boolean validarLetra(String dni) {
        String[] asignacionLetra = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};

        Integer num = 0;
        String subCadena = dni.substring(0, (dni.length() - 1));
        try {
            num = Integer.parseInt(subCadena);

        } catch (java.lang.NumberFormatException ex) {

            return false;
        }

        int resto = num % 23;
        System.out.println(dni.substring(dni.length() - 1, dni.length()));
        if (asignacionLetra[resto].equalsIgnoreCase(dni.substring(dni.length() - 1, dni.length()))) {

            return true;
        }
        return false;

    }

    public void mongoUpMap(User usr) {

        Log.d("MONGO", "Mongo upMap");
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

        Log.d("MONGO", "LASTUPLOAD--->" + lastUpload);
        getQuery.put("_time", new BasicDBObject("$eq", lastUpload));


        //****
        mess.findOne(getQuery).getAsync(task -> {
            if (task.isSuccess()) {
                Document result = task.get();
                if (result != null) {// Si la mess fue encontrada
                    for (HashMap.Entry<String, Measurement> m : measurements.entrySet()) {//Monstramos todas
                        Log.d("MONGO", "Mess---> " + m.getKey() + "\n");
                    }
                    sortenMap = measurements.tailMap(lastUpload);
                    sortenMap.remove(lastUpload);
                    Log.v("MONGO", "-->successfully found a document: " + result + "\n Numeber in sorterd-->" + sortenMap.size());
                    for (HashMap.Entry<String, Measurement> m : sortenMap.entrySet()) {
                        Log.d("MONGO", "shorted map #### " + m.getKey() + "\n");
                    }

                } else {
                    for (HashMap.Entry<String, Measurement> m : measurements.entrySet()) {
                        Log.d("MONGO", "Mess---> " + m.getKey() + "\n");
                    }

                    sortenMap = measurements;
                    Log.d("MONGO", "## Result null: " + result + "\nNumeber in Meassuremap-->" + measurements.size());
                    for (HashMap.Entry<String, Measurement> m : sortenMap.entrySet()) {
                        Log.d("MONGO", "shorted map #### " + m.getKey() + "\n");
                    }
                }

            } else {
                Log.e("MONGO", "failed to find document with: ", task.getError());
            }

            HashMap<String, Measurement> cropedmeasurements = null;

            List<Document> documents = new ArrayList<>();
            for (HashMap.Entry<String, Measurement> kv : sortenMap.entrySet()) {//change to cropped
                Document doc = new Document();

                doc.put("_id", kv.getValue().getWorker());
                String json = new Gson().toJson(kv.getValue());
                Object o = BasicDBObject.parse(json);
                DBObject dbObj = (DBObject) o;
                doc.put("_time", kv.getValue().getTime());

                doc.put("_workerId", this.workerID);

                doc.put("query", dbObj);

                Log.d("MONGO", "****> " + doc.toString());

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





