package data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Measurement {

    private double hrm;

    public Measurement(double hrm, Integer steps, Integer btt, Accelerometer acc, Compass com, Gps gps, String time) {
        this.hrm = hrm;
        this.steps = steps;
        this.btt = btt;
        this.acc = acc;
        this.com = com;
        this.gps = gps;
        this.time = time;
    }

    private Integer steps;
    private Integer btt;
    Accelerometer acc = new Accelerometer();
    Compass com = new Compass();
    Gps gps = new Gps();
    private String time;

    public double getHrm() {
        return hrm;
    }

    public void setHrm(double hrm) {
        this.hrm = hrm;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Integer getBtt() {
        return btt;
    }

    public void setBtt(Integer btt) {
        this.btt = btt;
    }

    public Accelerometer getAcc() {
        return acc;
    }

    public void setAcc(Accelerometer acc) {
        this.acc = acc;
    }

    public Compass getCom() {
        return com;
    }

    public void setCom(Compass com) {
        this.com = com;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static  Measurement fromJsonToObj(String input){
        Log.d("JsonNull","fromJsonToObj ONBUILD--> "+input);
    Measurement nm = null;
    Double[] acc = {0.0,0.0,0.0,0.0,0.0}; // acc = {x,y,z,diff,mag}
    Double[] com = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};// mag = {x,y,z,dx,dy,dz,heading}
    Double[] gps = {0.0,0.0,0.0,0.0}; // gps = {lat,lon,alt,speed,etc}
        input = input.replace("#", "");//Clean end of string symbol
    try {
        String objectJson = "";
        JSONObject mess = new JSONObject(input);
        

        Object o1 = mess.get("acc");
        objectJson = o1.toString();
        JSONObject mess2 = new JSONObject(objectJson);
        Accelerometer accTemp = new Accelerometer(mess2.getDouble("x"),mess2.getDouble("y"),mess2.getDouble("z"),mess2.getDouble("diff"),mess2.getDouble("mag"));
        Object o2 = mess.get("com");
        objectJson = o2.toString();
        JSONObject mess3 = new JSONObject(objectJson);
        Compass comTemp = new Compass(mess3.getDouble("x"),mess3.getDouble("y"),mess3.getDouble("z"),mess3.getDouble("dx"),mess3.getDouble("dy"),mess3.getDouble("dz"),mess3.getDouble("heading"));
        Object o3 = mess.get("gps");
        objectJson = o3.toString();
        JSONObject mess4 = new JSONObject(objectJson);
        Gps gpstemp = new Gps(mess4.getDouble("lat"),mess4.getDouble("lon"),mess4.getDouble("alt"),mess4.getDouble("speed"));



         nm = new Measurement(mess.getDouble("hrm"),mess.getInt("step"),mess.getInt("batt"),accTemp,comTemp,gpstemp,(mess4.getString("time")));
    } catch (JSONException e) {

        e.printStackTrace();
    }

    return nm;

}

    @Override
    public String toString() {
        return "Measurement To string{" +
                "hrm=" + hrm +
                ", steps=" + steps +
                ", btt=" + btt +
                ", acc=" + acc +
                ", com=" + com +
                ", gps=" + gps +
                ", time='" + time + '\'' +
                '}';
    }
}
