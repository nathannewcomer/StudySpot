package com.android.studyspot;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

import com.android.studyspot.models.StudySpot;


public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private static final int REQUEST_RECORD_AUDIO_PERM = 200;
    private final static String PERM_REC_AUDIO = Manifest.permission.RECORD_AUDIO;
    private MapViewModel viewModel;
    private Button mLightMeasButton;
    private Button mNoiseMeasButton;
    private TextView mLightLevel;
    private TextView mNoiseLevel;
    private StudySpot selectedSpot;
    private RatingBar mRatingBar;
    private ImageButton mBackButton;

    //TODO add a way to go back/hide this fragment


    public DetailsFragment(StudySpot spot) {
        selectedSpot = spot;
    }


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
        mLightLevel.setText(String.format(getString(R.string.measured_average_light),
                selectedSpot.getAvgLight()));

        mLightMeasButton = (Button) root.findViewById(R.id.button_take_light_measurement);
        final ContextWrapper contextWrapper = new ContextWrapper(requireContext());
        mLightMeasButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
        });

        mNoiseLevel = (TextView) root.findViewById(R.id.details_noise_level);
        mNoiseLevel.setText(String.format(getString(R.string.measured_average_noise),
                selectedSpot.getAvgNoise()));

        mNoiseMeasButton = (Button) root.findViewById(R.id.button_take_noise_measurement);
        mNoiseMeasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(requireContext(),PERM_REC_AUDIO)
                        == PackageManager.PERMISSION_GRANTED){
                    final NoiseMeasurer meas = new NoiseMeasurer(selectedSpot);
                    Thread noiseMeasThread = new Thread(meas,"NoiseMeasThread");
                    meas.isFinished().observe(requireActivity(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean finished) {
                            if(finished){
                                viewModel.updateDBSpotNoise(selectedSpot);
                                String text = String.format(getString(R.string.measured_average_noise),
                                        meas.getAverageNoise());
                                Toast.makeText(requireContext(), text ,Toast.LENGTH_LONG).show();
                                mNoiseLevel.setText(text);
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
        });

        mRatingBar = root.findViewById(R.id.location_rating);
        mRatingBar.setRating((float) selectedSpot.getAvgRating());

        mBackButton = root.findViewById(R.id.search_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                fm.beginTransaction().hide(DetailsFragment.this).commit();
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

    }

    public StudySpot getSelectedSpot(){
        return selectedSpot;
    }


}