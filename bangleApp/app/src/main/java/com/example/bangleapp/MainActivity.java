package com.example.bangleapp;


import android.content.Context;
import android.content.res.Resources;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.InputStream;
import java.util.Properties;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import android.content.res.Resources;
public class MainActivity extends AppCompatActivity {





        public static String runScript(android.content.Context androidContextObject) {
            // Get the JavaScript in previous section
            try {

                Resources resources = androidContextObject.getResources();
                InputStream rawResource = resources.openRawResource(R.raw.config);


                Properties properties = new Properties();
                properties.load(rawResource);

                String source = properties.getProperty("jsExecute");
                String functionName = "getRhinoHello";
                Object[] functionParams = new Object[]{};
                // Every Rhino VM begins with the enter()
                // This Context is not Android's Context
                org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

                // Turn off optimization to make Rhino Android compatible
                rhino.setOptimizationLevel(-1);

                Scriptable scope = rhino.initStandardObjects();

                // This line set the javaContext variable in JavaScript
                //ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(androidContextObject, scope));

                // Note the forth argument is 1, which means the JavaScript source has
                // been compressed to only one line using something like YUI
                rhino.evaluateString(scope, source, "JavaScript", 1, null);

                // We get the hello function defined in JavaScript
                Object obj = scope.get(functionName, scope);

                if (obj instanceof Function) {
                    Function function = (Function) obj;
                    // Call the hello function with params
                    Object result = function.call(rhino, scope, scope, functionParams);
                    // After the hello function is invoked, you will see logcat output

                    // Finally we want to print the result of hello function
                    String response = org.mozilla.javascript.Context.toString(result);
                    return response;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                // We must exit the Rhino VM
                org.mozilla.javascript.Context.exit();
            }

            return null;
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connect(View v){
        Log.d("debugApp", "Conectando.....");
        Button bt = (Button)findViewById(R.id.connectBtt);
        bt.setText("ME TOCASTE!!!");
        Log.d("Rhino", "onCreate: "+ runScript(this));

        //----------------------------------





        //----------------------------------
    }
}