package com.android.studyspot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.studyspot.models.Review;
import com.android.studyspot.models.StudySpot;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;



public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private static final int REQUEST_RECORD_AUDIO_PERM = 200;
    private final static String PERM_REC_AUDIO = Manifest.permission.RECORD_AUDIO;
    private MapViewModel viewModel;
    private Button mLightMeasButton;
    private Button mNoiseMeasButton;
    private TextView mLightLevel;
    private TextView mNoiseLevel;
    private TextView mLocationName;
    private StudySpot selectedSpot;
    private RatingBar mRatingBar;
    private ImageButton mBackButton;

    private final int REVIEW_REQUEST_CODE = 1;
    public static final String REVIEW_NAME = "ReviewName";


    public DetailsFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_details, container, false);
        Button mReview = root.findViewById(R.id.button_review);
        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            //start new activity to create a review
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getActivity().getApplicationContext(), ReviewActivity.class);
                startActivity(reviewIntent);
            }
        });

        mLightLevel = (TextView) root.findViewById(R.id.details_light_level);
        /*
        mLightLevel.setText(String.format(getString(R.string.measured_average_light),
                selectedSpot.getAvgLight()));
         */

        mLightMeasButton = (Button) root.findViewById(R.id.button_take_light_measurement);
        mLightMeasButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                takeLightMeasurements(requireContext());
            }
        });

        mNoiseLevel = (TextView) root.findViewById(R.id.details_noise_level);
        /*
         mNoiseLevel.setText(String.format(getString(R.string.measured_average_noise),
                selectedSpot.getAvgNoise()));

         */

        mNoiseMeasButton = (Button) root.findViewById(R.id.button_take_noise_measurement);
        mNoiseMeasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                takeNoiseMeasurements();
            }
        });

        mRatingBar = root.findViewById(R.id.location_rating);
        //mRatingBar.setRating((float) selectedSpot.getAvgRating());

        mBackButton = root.findViewById(R.id.search_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                fm.beginTransaction().hide(DetailsFragment.this).commit();
            }
        });

        mLocationName = root.findViewById(R.id.detail_location_name);
        //mLocationName.setText(selectedSpot.getName());

        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getActivity().getApplicationContext(), ReviewActivity.class);
                reviewIntent.putExtra(REVIEW_NAME, selectedSpot.getName());
                startActivityForResult(reviewIntent, REVIEW_REQUEST_CODE);
            }
        });

        viewModel.getDetailsSpot().observe(getViewLifecycleOwner() , new Observer<StudySpot>() {
            @Override
            public void onChanged(StudySpot studySpot) {
                setSelectedSpot(studySpot);
            }
        });

        return root;
    }



    public void setSelectedSpot(StudySpot spot){
        selectedSpot = spot;
        mRatingBar.setRating((float) selectedSpot.getAvgRating());
        mLightLevel.setText(String.format(getString(R.string.measured_average_light),
                selectedSpot.getAvgLight()));
        mNoiseLevel.setText(String.format(getString(R.string.measured_average_noise),
                selectedSpot.getAvgNoise()));
        mLocationName.setText(selectedSpot.getName());
    }

    public StudySpot getSelectedSpot(){
        return selectedSpot;
    }


    // receives the review from ReviewActivity and puts it into the database
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // only do this if the request code is the same and result code is ok
        if (requestCode == REVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Review review = data.getParcelableExtra(LeaveReviewFragment.NEW_REVIEW);

            StudySpot spot = null;
            // put review into local list
            for (StudySpot studySpot : viewModel.getSpots().getValue()) {
                if (studySpot.getName().equals(review.getSpotName())) {
                    spot = studySpot;
                    spot.addReview(review);
                    break;
                }
            }

            // put review into database
            if (spot != null) {
                StudySpotRepository repo = new StudySpotRepository(getContext());
                repo.saveReview(review, spot);
                if(!repo.getConnectionStatus()){
                    Toast.makeText(getContext(), R.string.database_connection_failed, Toast.LENGTH_LONG).show();
                }
                //why not just call
                spot.setAvgRating(spot.calculateAvgRating());
                mRatingBar.setRating((float) spot.getAvgRating());
                viewModel.updateDBSpotAverages(spot, StudySpot.KEY_AVG_RATING);
            } else {
                Toast.makeText(getContext(), R.string.leave_review_failed, Toast.LENGTH_LONG).show();
            }

            //viewModel.setAverageCurrentRatingFromReview(spot);

        }

    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions,
                                            int[] grantResults){
        switch(requestCode){
            case REQUEST_RECORD_AUDIO_PERM:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    takeNoiseMeasurements();
                }
                break;
        }
    }

    public boolean hasMicrophone(){
        return requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void takeNoiseMeasurements() {
        if(hasMicrophone()){
            if(ContextCompat.checkSelfPermission(requireContext(),PERM_REC_AUDIO)
                    == PackageManager.PERMISSION_GRANTED){
                final NoiseMeasurer meas = new NoiseMeasurer(selectedSpot);
                Thread noiseMeasThread = new Thread(meas,"NoiseMeasThread");
                meas.isFinished().observe(requireActivity(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean finished) {
                        if(finished){
                            if(meas.hadErrors()){
                                Toast.makeText(requireContext(),
                                        getString(R.string.noise_meas_error),
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                viewModel.updateDBSpotNoise(selectedSpot);
                                String text = String.format(getString(R.string.measured_average_noise),
                                        meas.getAverageNoise());
                                Toast.makeText(requireContext(), text ,Toast.LENGTH_LONG).show();
                                mNoiseLevel.setText(String.format(
                                        getString(R.string.measured_average_noise),
                                        selectedSpot.getAvgNoise()));
                            }
                            meas.isFinished().removeObservers(requireActivity());
                        }
                    }
                });
                noiseMeasThread.start();
            }
            else{
                requestPermissions(new String[]{PERM_REC_AUDIO}, REQUEST_RECORD_AUDIO_PERM);
            }
        }
        else{
            Toast.makeText(requireContext(),getString(R.string.no_microphone),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void takeLightMeasurements(Context context){
        final ContextWrapper contextWrapper = new ContextWrapper(context);
        String greetingText = String.format(getString(R.string.pre_measured_average_light),
                LightMeasurer.MEASURING_TIME_MS/1000);
        Toast.makeText(requireContext(), greetingText,Toast.LENGTH_LONG).show();
        final LightMeasurer meas = new LightMeasurer(contextWrapper, selectedSpot);
        Thread lightMeasThread = new Thread(meas,"LightMeasThread");
        meas.isFinished().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean finished) {
                if(finished){
                    viewModel.updateDBSpotLight(selectedSpot);
                    String text = String.format(getString(R.string.measured_average_light),
                            meas.getAverageLight());
                    Toast.makeText(requireContext(), text ,Toast.LENGTH_LONG).show();
                    String lightLevelText = String.format(getString(R.string.measured_average_light),
                            selectedSpot.getAvgLight());
                    mLightLevel.setText(lightLevelText);
                    meas.isFinished().removeObservers(requireActivity());
                }
            }
        });
        lightMeasThread.start();
    }



}