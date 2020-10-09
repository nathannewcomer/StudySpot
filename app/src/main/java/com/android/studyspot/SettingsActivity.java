package com.android.studyspot;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

/*
 *Activity to hold the settings fragment.
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Toolbar settingsToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new SettingsFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }



    /*logging fragment lifecycle */
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called by" + TAG);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called by" + TAG);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called by" + TAG);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called by" + TAG);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called by" + TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
