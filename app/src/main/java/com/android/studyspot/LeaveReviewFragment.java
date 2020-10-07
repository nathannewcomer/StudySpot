package com.android.studyspot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

public class LeaveReviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LeaveReviewFragment";
    private TextView locationTextView;
    private RatingBar locationRatingBar;
    private EditText commentsEditText;
    private Button submitButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_leave_review,container, false);

        locationTextView = (TextView) v.findViewById(R.id.location_text_view);
        locationRatingBar = (RatingBar) v.findViewById(R.id.location_rating_bar);
        commentsEditText = (EditText) v.findViewById(R.id.comments_edit_text);
        submitButton = (Button) v.findViewById(R.id.submit_button);

        if(submitButton != null){
            submitButton.setOnClickListener(this);
        }

        Log.d(TAG,"onCreateView() called by" + TAG);
        return v;
    }

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

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.submit_button:
                break;
        }
    }
}
