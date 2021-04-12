package data;

import java.io.Serializable;
import java.util.ArrayList;

public class Model implements Serializable {
    public ArrayList<Measurement> measurements = null;

    public Model() {
        measurements = new ArrayList<Measurement>();
    }



}
