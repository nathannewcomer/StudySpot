package com.android.studyspot;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.android.studyspot.models.StudySpot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment{
    private static final String TAG = "MapFragment";
    private final static int REQUEST_FINE_LOC_PERM = 4;
    private final static String PERM_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION;
    private MapViewModel viewModel;
    private MapView mMapView;
    private GoogleMap googleMap;
    private List<Marker> markers;
    private DetailsFragment detailsFrag;
    private View root;
    private StudySpot selectedSpot;

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

        if(connectionStatus()) {
            // Inflate the layout for this fragment

            root = inflater.inflate(R.layout.fragment_map, container, false);

            //initialize details page segment

            /*
             *Only show the options menu for sorting spots if at or above API Level 24(Version N)
             * because it is required for List.sort()
             */
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setHasOptionsMenu(true);
            }

            // initialize map
            mMapView = root.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // needed to get the map to display immediately
            //small map for detail page

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
                    //if failed to retrieve spots from view model due to connection
                    if (!viewModel.isConnected) {
                        Toast.makeText(getContext(), getString(R.string.failed_connection), Toast.LENGTH_LONG).show();
                    }
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            String mSpotTitle = marker.getTitle();
                            //need to set selectedSpot here so that we know what location we clicked on

                            viewModel.setDetailsSpot(viewModel.findStudySpotByName(mSpotTitle));
                            FragmentManager fm = getChildFragmentManager();
                            if (detailsFrag == null) {
                                detailsFrag = new DetailsFragment();
                                fm.beginTransaction().add(R.id.layout_map, detailsFrag).commit();
                            } else {
                                fm.beginTransaction().show(detailsFrag).commit();
                                //detailsFrag.setSelectedSpot(selectedSpot);
                            }

                            return true;
                        }
                    });

                }
            });
        }else{
            root = inflater.inflate(R.layout.connection_error, container, false);
        }


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

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
    }

    // TODO: maybe create dialogs somewhere else
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(MenuItem item) {

        // showing/hiding of markers is handled in the Dialog classes
        switch (item.getItemId()) {
            case R.id.filter_distance:
                if (ContextCompat.checkSelfPermission(requireContext(),PERM_FINE_LOC)
                        == PackageManager.PERMISSION_GRANTED) {
                    final FusedLocationProviderClient fusedLocationClient = LocationServices.
                            getFusedLocationProviderClient(getActivity());
                    fusedLocationClient
                        .getLocationAvailability()
                        .addOnCompleteListener(
                                new OnCompleteListener<LocationAvailability>(){
                                    @Override
                                    public void onComplete(@NonNull Task<LocationAvailability> task) {
                                        if(task.isSuccessful() && task.getResult() != null &&
                                                task.getResult().isLocationAvailable()){
                                            makeLocationRequest(fusedLocationClient);
                                        }
                                        else{
                                            Toast.makeText(requireContext(),
                                                    R.string.location_request_failed,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                }
                return true;
            case R.id.filter_rating:
                FilterRatingDialog rDialog = new FilterRatingDialog(viewModel.getSpots().getValue(), markers);
                rDialog.show(getParentFragmentManager(), "filterRating");
                return true;
            case R.id.filter_name:
                FilterNameDialog nDialog = new FilterNameDialog(viewModel.getSpots().getValue(), markers);
                nDialog.show(getParentFragmentManager(), "filterName");
                return true;
            case R.id.filter_none:
                for (Marker marker : markers) {
                    marker.setVisible(true);
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeLocationRequest(FusedLocationProviderClient fClient){
        if(ContextCompat.checkSelfPermission(requireContext(),PERM_FINE_LOC)
                == PackageManager.PERMISSION_GRANTED) {
            fClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                null)
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FilterDistanceDialog dDialog =
                                    new FilterDistanceDialog(viewModel.getSpots().getValue(),
                                            markers, task.getResult());
                            dDialog.show(getParentFragmentManager(), "filterDistance");
                        } else {
                            Toast.makeText(requireContext(), R.string.location_request_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }

    // create markers from StudySpot objects and put them into a list
    private void setMarkers(List<StudySpot> spots) {
        googleMap.clear();
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

    public boolean connectionStatus(){
        ConnectivityManager cm = (ConnectivityManager) getContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null){ return true; }
        else{return false;}

    }

}