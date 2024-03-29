package data;
/** Represents a compass entry.
 * @author Jorge
 * @version 1.5
 * @since 1.0
 */
public class Compass {

    // mag = {x,y,z,dx,dy,dz,heading}
    private Double x;
    private Double y;
    private Double z;
    private Double dx;
    private Double dy;
    private Double dz;
    private Double heading;

    public Compass(Double x, Double y, Double z, Double dx, Double dy, Double dz, Double heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.heading = heading;
    }

    public Compass() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.dx = 0.0;
        this.dy = 0.0;
        this.dz = 0.0;
        this.heading = 0.0;
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

    public Double getDx() {
        return dx;
    }

    public void setDx(Double dx) {
        this.dx = dx;
    }

    public Double getDy() {
        return dy;
    }

    public void setDy(Double dy) {
        this.dy = dy;
    }

    public Double getDz() {
        return dz;
    }

    public void setDz(Double dz) {
        this.dz = dz;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }
}
