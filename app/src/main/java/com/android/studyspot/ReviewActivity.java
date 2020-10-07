package com.android.studyspot;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ReviewActivity";
    private TextView locationTextView;
    private RatingBar locationRatingBar;
    private EditText commentsEditText;
    private Button submitButton;
    private Toolbar reviewToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        locationTextView = (TextView) findViewById(R.id.location_text_view);
        locationRatingBar = (RatingBar) findViewById(R.id.location_rating_bar);
        commentsEditText = (EditText) findViewById(R.id.comments_edit_text);
        submitButton = (Button) findViewById(R.id.submit_button);
        reviewToolbar = (Toolbar) findViewById(R.id.review_toolbar);
        setSupportActionBar(reviewToolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        if(submitButton != null){
            submitButton.setOnClickListener(this);
        }

    }

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
    public void onClick(View v){
        switch(v.getId()){
            case R.id.submit_button:
                break;
        }
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
