package data;

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
    Measurement nm = null;
    Double[] acc = {0.0,0.0,0.0,0.0,0.0}; // acc = {x,y,z,diff,mag}
    Double[] com = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};// mag = {x,y,z,dx,dy,dz,heading}
    Double[] gps = {0.0,0.0,0.0,0.0}; // gps = {lat,lon,alt,speed,etc}
        input = input.replace("#", "");//Clean end of string symbol
    try {
        String objectJson = "";
        JSONObject mess = new JSONObject(input);
        JSONObject mess2 = new JSONObject(objectJson);

        Object o1 = mess.get("acc");
        objectJson = o1.toString();
        Accelerometer accTemp = new Accelerometer(mess2.getDouble("x"),mess2.getDouble("y"),mess2.getDouble("z"),mess2.getDouble("diff"),mess2.getDouble("mag"));
        Object o2 = mess.get("com");
        objectJson = o2.toString();
        Compass comTemp = new Compass(mess2.getDouble("x"),mess2.getDouble("y"),mess2.getDouble("z"),mess2.getDouble("dx"),mess2.getDouble("dy"),mess2.getDouble("dz"),mess2.getDouble("heading"));
        Object o3 = mess.get("gps");
        objectJson = o3.toString();
        Gps gpstemp = new Gps(mess2.getDouble("lat"),mess2.getDouble("lon"),mess2.getDouble("alt"),mess2.getDouble("speed"));



         nm = new Measurement(mess.getDouble("hrm"),mess.getInt("step"),mess.getInt("batt"),accTemp,comTemp,gpstemp,(mess2.getString("time")));
    } catch (JSONException e) {
        e.printStackTrace();
    }

    return nm;

}



}
