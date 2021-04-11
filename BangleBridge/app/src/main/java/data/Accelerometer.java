package data;

public class Accelerometer {

   // acc = {x,y,z,diff,mag}
    private Double x;
    private Double y;
    private Double z;
    private Double diff;
    private Double mag;
    public Accelerometer(Double x, Double y, Double z, Double diff, Double mag) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.diff = diff;
        this.mag = mag;
    }



    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getDiff() {
        return diff;
    }

    public void setDiff(Double diff) {
        this.diff = diff;
    }

    public Double getMag() {
        return mag;
    }

    public void setMag(Double mag) {
        this.mag = mag;
    }
}
