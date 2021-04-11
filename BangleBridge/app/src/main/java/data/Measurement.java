package data;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;

import static java.nio.file.Paths.get;

public class Measurement {

    private double hrm;

    public Measurement(double hrm, Integer steps, Integer btt, Double[] acc, Double[] com, Double[] gps, LocalTime time) {
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
    private Double[] acc = {0.0,0.0,0.0,0.0,0.0}; // acc = {x,y,z,diff,mag}
    private Double[] com = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};// mag = {x,y,z,dx,dy,dz,heading}
    private Double[] gps = {0.0,0.0,0.0,0.0}; // gps = {lat,lon,alt,speed,etc}
    private LocalTime time;

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

    public Double[] getAcc() {
        return acc;
    }

    public void setAcc(Double[] acc) {
        this.acc = acc;
    }

    public Double[] getCom() {
        return com;
    }

    public void setCom(Double[] com) {
        this.com = com;
    }

    public Double[] getGps() {
        return gps;
    }

    public void setGps(Double[] gps) {
        this.gps = gps;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

public Measurement fromJasonToMeas(String input){
    Measurement nm = null;
    Double[] acc = {0.0,0.0,0.0,0.0,0.0}; // acc = {x,y,z,diff,mag}
    Double[] com = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};// mag = {x,y,z,dx,dy,dz,heading}
    Double[] gps = {0.0,0.0,0.0,0.0}; // gps = {lat,lon,alt,speed,etc}
        input = input.replace("#", "");//Clean end of string symbol
    try {
        JSONObject mess = new JSONObject(input);

         nm = new Measurement(mess.getDouble("hrm"),mess.getInt("step"),mess.getInt("batt"),mess.get("acc"),mess.get("mag"),mess.get("com"),mess.get("gps"));
    } catch (JSONException e) {
        e.printStackTrace();
    }

    return nm;

}



}
