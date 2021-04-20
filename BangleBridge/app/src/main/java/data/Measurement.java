package data;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Measurement fromJsonToObj(String input) {
        Log.d("JsonNull", "fromJsonToObj ONBUILD--> " + input);
        Log.d("JsonNull", "########################");
        Measurement nm = null;
        Double[] acc = {0.0, 0.0, 0.0, 0.0, 0.0}; // acc = {x,y,z,diff,mag}
        Double[] com = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};// mag = {x,y,z,dx,dy,dz,heading}
        Double[] gps = {0.0, 0.0, 0.0, 0.0}; // gps = {lat,lon,alt,speed,etc}
        input = input.replace("#", "");//Clean end of string symbol
        try {

                String objectJson = "";
                JSONObject mess = new JSONObject(input);

                //Accelerometre
                Object o1 = mess.get("acc");
                objectJson = o1.toString();
                JSONObject mess2 = new JSONObject(objectJson);
                Double x = 0.0;
                Double y = 0.0;
                Double z = 0.0;
                Double diff = 0.0;
                Double mag = 0.0;
            try {
                if ((x = mess2.getDouble("x")) == null) {
                    x = 0.0;
                }
                if ((y = mess2.getDouble("y")) == null) {
                    y = 0.0;
                }
                if ((z = mess2.getDouble("z")) == null) {
                    z = 0.0;
                }
                if ((diff = mess2.getDouble("diff")) == null) {
                    diff = 0.0;
                }
                if ((mag = mess2.getDouble("mag")) == null) {
                    mag = 0.0;
                }
            }catch (JSONException e) {

                e.printStackTrace();

            }
            Accelerometer accTemp = new Accelerometer(x, y, z, diff, mag);
            //Compass
            Object o2 = mess.get("com");
            objectJson = o2.toString();
            JSONObject mess3 = new JSONObject(objectJson);
            Double dx = 0.0;
            Double dy = 0.0;
            Double dz = 0.0;
            Double heading = 0.0;
            try {
                if ((x = mess3.getDouble("x")) == null) {
                    x = 0.0;
                }
                if ((y = mess3.getDouble("y")) == null) {
                    y = 0.0;
                }
                if ((z = mess3.getDouble("z")) == null) {
                    z = 0.0;
                }
                if ((dx = mess3.getDouble("dx")) == null) {
                    dx = 0.0;
                }
                if ((dy = mess3.getDouble("dy")) == null) {
                    dy = 0.0;
                }
                if ((dz = mess3.getDouble("dz")) == null) {
                    dz = 0.0;
                }
                if ((heading = mess3.getDouble("heading")) == null) {
                    heading = 0.0;
                }
            } catch (JSONException e) {
                Log.d("JsonNull", "No hay Gps");
                e.printStackTrace();

            }

            Compass comTemp = new Compass(x, y, z, dx, dy, dz, heading);
            //GPS
            Object o3 = mess.get("gps");
            objectJson = o3.toString();
            JSONObject mess4 = new JSONObject(objectJson);
            Double lat = 0.0;
            Double lon = 0.0;
            Double alt = 0.0;
            Double speed = 0.0;
            try {


                if ((lat = mess4.getDouble("lat")) == null) {
                    lat = 0.0;
                }
                if ((lon = mess4.getDouble("lon")) == null) {
                    lon = 0.0;
                }
                if ((alt = mess4.getDouble("alt")) == null) {
                    alt = 0.0;
                }
                if ((speed = mess4.getDouble("speed")) == null) {
                    speed = 0.0;
                }

            } catch (JSONException e) {
                Log.d("JsonNull", "No hay Gps");
                e.printStackTrace();

            }
            Gps gpstemp = new Gps(lat, lon, alt, speed);
            //Clean up time
            String og = (mess4.getString("time"));
            String trimedTime = og.substring(0, og.indexOf("."));

            nm = new Measurement(mess.getDouble("hrm"), mess.getInt("step"), mess.getInt("batt"), accTemp, comTemp, gpstemp, (trimedTime));
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
