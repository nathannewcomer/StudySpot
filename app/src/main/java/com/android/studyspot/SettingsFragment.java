package com.android.studyspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
       setPreferencesFromResource(R.xml.settings, rootKey);

    }

    /*logging fragment lifecycle */
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"onStart() called by" + TAG);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called by" + TAG);
    }
    @Override
    public void onPause() {
        super.onPause();
        Context context = requireActivity();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, "onPause() called by" + TAG);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called by" + TAG);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called by" + TAG);
    }
}
