package de.bangle_bridge.bangle_bridge;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// Set main activity as layout
        Toolbar toolbar = findViewById(R.id.toolbar);//Get toolBar ID
        View screenView = findViewById(R.id.searchBackG);
        //screenView.setBackgroundColor(1);
        setSupportActionBar(toolbar);//Set tool bar as activity app bar
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)//Check if first boot
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();//Call devicesfragment Constructor
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
