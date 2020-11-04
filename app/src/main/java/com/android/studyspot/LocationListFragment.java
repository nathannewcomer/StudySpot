package com.android.studyspot;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.studyspot.models.StudySpot;
import com.android.studyspot.services.SpotLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationListFragment extends Fragment {

    private static final String TAG = "LocationListFragment";
    private final static int REQUEST_FINE_LOC_PERM = 4;
    private final static String PERM_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;

    private RecyclerView listView;
    private ImageButton settingsButton;
    private ListAdapter mAdapter;
    private MapViewModel viewModel;

    private FusedLocationProviderClient mFusedLocationClient;

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
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        //code for list init
        RecyclerView rvList = (RecyclerView) root.findViewById(R.id.recycler_view);
        mAdapter = new ListAdapter(viewModel.getSpots().getValue());
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



}
