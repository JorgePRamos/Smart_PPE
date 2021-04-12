package data;

public class Gps {
    // gps = {lat,lon,alt,speed,etc}
    private Double lat;
    private Double lon;
    private Double alt;
    private Double speed;

    public Gps(Double lat, Double lon, Double alt, Double speed) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.speed = speed;
    }

    public Gps() {
        this.lat = 0.0;
        this.lon = 0.0;
        this.alt = 0.0;
        this.speed = 0.0;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
