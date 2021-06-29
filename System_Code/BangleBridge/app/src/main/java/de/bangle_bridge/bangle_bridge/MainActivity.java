package de.bangle_bridge.bangle_bridge;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
/**
 * Main class called when the Main Activity is opened at the application launch. Supports the rest of the functionalities via fragment loading.
 * @author Jorge
 * @version 1.5
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Set main activity as layout
        Toolbar toolbar = findViewById(R.id.toolbar);//Get toolBar ID

        //screenView.setBackgroundColor(1);
        setSupportActionBar(toolbar);//Set tool bar as activity app bar
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)//Check if first boot
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new ConnectionFragment(), "devices").commit();//Call devicesfragment Constructor
        else
            onBackStackChanged();
    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
