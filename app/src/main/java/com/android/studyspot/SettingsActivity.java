package com.android.studyspot;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

/*
 *Activity to hold the settings fragment. Credit for idea to the 3rd Edition of
 *"Android Programming: The Big Nerd Ranch Guide" by Bill Phillips, Chris Stewart,
 * and Kristin Marsicano.
 */
public class SettingsActivity extends SingleFragmentActivity {
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    protected Fragment createFragment(){
        return new SettingsFragment();
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
}
