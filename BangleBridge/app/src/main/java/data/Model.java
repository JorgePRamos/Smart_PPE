package data;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import de.bangle_bridge.bangle_bridge.R;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

public class Model implements Serializable {
    public HashMap<String,Measurement> measurements = null;

    public Model() {
        measurements = new HashMap<String,Measurement>();
    }


    }





