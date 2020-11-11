package com.android.studyspot;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MapFragment extends Fragment{
    private static final String TAG = "MapFragment";
    private final static int REQUEST_FINE_LOC_PERM = 4;
    private final static String PERM_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_RECORD_AUDIO_PERM = 200;
    private final static String PERM_REC_AUDIO = Manifest.permission.RECORD_AUDIO;
    private MapViewModel viewModel;
    private MapView mMapView;
    private GoogleMap googleMap;
    private ImageButton settingsButton;
    private List<Marker> markers;
    private View mDetails;
    private Button mLightMeasButton;
    private Button mNoiseMeasButton;
    private TextView mLightLevel;
    private TextView mNoiseLevel;
    private StudySpot selectedSpot;
    private MapView mMapContainer;



    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate() called by" + TAG);
        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_map, container, false);
        settingsButton = (ImageButton) root.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent settingsIntent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        //initialize details page segment
        mDetails = root.findViewById(R.id.detail_container);
        Button mReview = root.findViewById(R.id.button_review);
        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            //start new activity to create a review
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getActivity().getApplicationContext(), ReviewActivity.class);
                startActivity(reviewIntent);
            }
        });
        ImageButton mBack = root.findViewById(R.id.search_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetails.setVisibility(View.GONE);
                mMapView.setVisibility(View.VISIBLE);
                settingsButton.setVisibility(View.VISIBLE);
            }
        });
        // initialize map
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        //small map for detail page
        mMapContainer = root.findViewById(R.id.small_map_container);
        mMapContainer.onCreate(savedInstanceState);
        mMapContainer.onResume(); //force maps to start
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                enableMyLocation();
                setMarkers(viewModel.getSpots().getValue());
                // add observer to studySpots
                viewModel.getSpots().observe(getViewLifecycleOwner(), new Observer<List<StudySpot>>() {
                    @Override
                    public void onChanged(List<StudySpot> studySpots) {
                        setMarkers(studySpots);

                    }
                });
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //logic for displaying detail screen
                        String mSpotTitle = marker.getTitle();
                        //need to set selectedSpot here so that we know what location we clicked on
                        selectedSpot = viewModel.findStudySpotByName(mSpotTitle);
                        RatingBar rating = mDetails.findViewById(R.id.location_rating);
                        rating.setRating((float) selectedSpot.getAvgRating());
                        //display the map view of the specified study spot
                        mMapContainer.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng coords = new LatLng(selectedSpot.getCoords().getLatitude(), selectedSpot.getCoords().getLongitude());
                                googleMap.addMarker(new MarkerOptions().title(selectedSpot.getName()).position(coords));
                            }
                        });
                        mDetails.setVisibility(View.VISIBLE);
                        mMapView.setVisibility(View.GONE);
                        settingsButton.setVisibility(View.GONE);

                        return true;
                    }
                });

            }
        });


        //TODO repetitive code for both fragments
        mLightLevel = (TextView) root.findViewById(R.id.details_light_level);
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
        //set markerOnClick properties

        Log.d(TAG,"onCreateView() called by" + TAG);
        return root;
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

    // create markers from StudySpot objects and put them into a list
    private void setMarkers(List<StudySpot> spots) {
        markers = new ArrayList<>();

        for (StudySpot spot : spots) {
            LatLng coords = new LatLng(spot.getCoords().getLatitude(), spot.getCoords().getLongitude());
            markers.add(googleMap.addMarker(new MarkerOptions().title(spot.getName()).position(coords)));
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)  == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            //TODO explain rationale for permissions
            requestPermissions(new String[]{PERM_FINE_LOC}, REQUEST_FINE_LOC_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions,
                                            int[] grantResults) {
        switch(requestCode) {
            case REQUEST_FINE_LOC_PERM:
                boolean granted = false;
                if (grantResults.length == 1) {
                    granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }
                break;
        }
    }

}