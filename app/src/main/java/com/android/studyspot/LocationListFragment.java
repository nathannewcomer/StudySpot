    package com.android.studyspot;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.studyspot.models.StudySpot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationListFragment extends Fragment implements ListAdapter.ListItemClickListener {

    private static final String TAG = "LocationListFragment";
    private final static int REQUEST_FINE_LOC_PERM = 4;
    private static final int REQUEST_RECORD_AUDIO_PERM = 200;
    private final static String PERM_REC_AUDIO = Manifest.permission.RECORD_AUDIO;
    private final static String PERM_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;

    private RecyclerView listView;
    private ImageButton settingsButton;
    private ListAdapter mAdapter;
    private MapViewModel viewModel;

    private GoogleMap googleMap;
    private View root;
    private View details;
    private Marker marker;
    private MapView mapView;
    private Button mLightMeasButton;
    private Button mNoiseMeasButton;
    private TextView mLightLevel;
    private TextView mNoiseLevel;

    private StudySpot selectedSpot;
    private FusedLocationProviderClient mFusedLocationClient;

    private boolean acceptRecordAudioPerm = false;

    public LocationListFragment() {
        // Required empty public constructor

    }
    public static LocationListFragment newInstance(String param1, String param2) {
        LocationListFragment fragment = new LocationListFragment();
        Log.d(TAG,"newInstance() called by" + TAG);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Log.d(TAG,"onCreate() called by" + TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        /*
         *Only show the options menu for sorting spots if at or above API Level 24(Version N)
         * because it is required for List.sort()
         */
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            setHasOptionsMenu(true);
        }

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_list, container, false);
        details = root.findViewById(R.id.detail_container);
        Button mReview = root.findViewById(R.id.button_review);
        ImageButton back = root.findViewById(R.id.search_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setVisibility(View.GONE);
            }
        });
        details.setVisibility(View.GONE);
        mReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(getActivity().getApplicationContext(), ReviewActivity.class);
                startActivity(reviewIntent);
            }
        });

        //map initialize
        mapView = root.findViewById(R.id.small_map_container);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); //force maps to start



        //code for list init
        RecyclerView rvList = (RecyclerView) root.findViewById(R.id.recycler_view);
        mAdapter = new ListAdapter(viewModel.getSpots().getValue(), this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getSpots().observe(getViewLifecycleOwner(), new Observer<List<StudySpot>>() {
            @Override
            public void onChanged(List<StudySpot> studySpots) {
                mAdapter.setSpots(studySpots);
                mAdapter.notifyDataSetChanged();
            }
        });

        //code for settings button
        settingsButton = (ImageButton) root.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
                Intent settingsIntent = new Intent(requireActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
           }
        });
        Log.d(TAG,"onCreateView() called by" + TAG);

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

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.location_menu, menu);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loc_sort_name:
                mAdapter.sortSpots(ListAdapter.SortOption.NAME);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_rating:
                mAdapter.sortSpots(ListAdapter.SortOption.RATING);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_bright:
                mAdapter.sortSpots(ListAdapter.SortOption.BRIGHT);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_dark:
                mAdapter.sortSpots(ListAdapter.SortOption.DARK);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_loud:
                mAdapter.sortSpots(ListAdapter.SortOption.LOUD);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_quiet:
                mAdapter.sortSpots(ListAdapter.SortOption.QUIET);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.loc_sort_distance:
                if(ContextCompat.checkSelfPermission(requireContext(),PERM_FINE_LOC)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                            null)
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                sortSpotsByDistance(location);
                            }
                        }
                    });
                }
                else{
                    requestPermissions(new String[]{ PERM_FINE_LOC},
                            REQUEST_FINE_LOC_PERM);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onRequestPermissionsResult (int requestCode, String[] permissions,
                                            int[] grantResults){
        switch(requestCode){
            case REQUEST_FINE_LOC_PERM:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(requireContext(),PERM_FINE_LOC)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                                null)
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        sortSpotsByDistance(location);
                                    }
                                });
                    }
                }
                break;
            case REQUEST_RECORD_AUDIO_PERM:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //TODO run the noise measurement thread stuff
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortSpotsByDistance(Location location){
        if(location!= null){
            viewModel.setCurrentLocation(location);
            List<StudySpot> spots = viewModel.getSpots().getValue();
            for(StudySpot spot: spots){
                spot.updateDistanceToUser(location);
            }
            viewModel.getSpots().setValue(spots);
            mAdapter.sortSpots(ListAdapter.SortOption.DISTANCE);
            mAdapter.notifyDataSetChanged();

        }
    }

    public void setDataForDetailsPage(int position){
        StudySpot studySpot = mAdapter.getStudySpot(position);

    }

    public void onListItemClick(int position) {

//        FragmentManager fm = getChildFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.detail_fragment);
//        if (fragment == null) {
//            fragment = new DetailsFragment();
//            fm.beginTransaction()
//                    .add(R.id.detail_container, fragment)
//                    .commit();
//        }
        selectedSpot = mAdapter.getStudySpot(position);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coords = new LatLng(selectedSpot.getCoords().getLatitude(), selectedSpot.getCoords().getLongitude());
                googleMap.addMarker(new MarkerOptions().title(selectedSpot.getName()).position(coords));

              //TODO add observer for individual study spot
            }
        });
        details.setVisibility(View.VISIBLE);




        Log.d(TAG, "onClick() called by" + TAG);

    }

}
