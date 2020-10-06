package com.android.studyspot;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


public class ReviewActivity extends AppCompatActivity {

    private final String TAG = "ReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Log.d(TAG,"onCreate() called by" + TAG);

    }
}
