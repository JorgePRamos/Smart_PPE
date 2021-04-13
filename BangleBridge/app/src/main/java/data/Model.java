package data;

import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import de.bangle_bridge.bangle_bridge.R;

public class Model implements Serializable {
    public ArrayList<Measurement> measurements = null;

    public Model() {
        measurements = new ArrayList<Measurement>();
    }





}
