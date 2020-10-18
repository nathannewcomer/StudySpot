package com.android.studyspot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MapFragment extends Fragment{
    private static final String TAG = "MapFragment";
    private static final String GEOCODING_REQUEST_OK = "OK";
    private MapViewModel viewModel;
    private GoogleMap map;
    private ImageButton settingsButton;
    private RequestQueue mQueue;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate() called by" + TAG);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        viewModel.initialize();

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

        mQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        Log.d(TAG,"onCreateView() called by" + TAG);

        viewModel.retrieveAllStudySpots();
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


    /*
     *Returns a URL for a Google Maps Geocode request.
     *See https://developers.google.com/maps/documentation/geocoding/overview.
     *Make sure your address does not have the building name or floor in it
     */
    private String createGeocodeRequestURL(String address){
        address = address.replaceFirst( "\\d+-?\\d*\\z", "")
                .trim();  //gets rid of the zipcode at the end of the address
        //replaces all spaces or commmas with %20
        address = address.replaceAll("\\s","%20");

        String geocoding_request = "https://maps.googleapis.com/maps/api/geocode/json?";
        StringBuilder builder = new StringBuilder(geocoding_request);
        builder.append("address=");
        builder.append(address);
        builder.append("&key=");
        builder.append(getString(R.string.geocoding_api_key));
        builder.append("&language=en");
        return builder.toString();
    }


    //TODO this method probably does too much, maybe separate saving it into another call
    /*
     * Creates a new StudySpot,adds it to the ViewModel, and saves it to firebase.
     * Populates the coords of the Studyspot by making a Google Maps Geocode request using the
     * spot's address. Make sure your address does not have the building name or floor name in it
     * Note: the volley.add method needs to run in the main thread
     */

    private void createNewStudySpot(final String name, final String address, final String schedule){
        String url = createGeocodeRequestURL(address);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String status = response.getString("status");
                    if(status != null && status.compareToIgnoreCase(GEOCODING_REQUEST_OK) == 0){
                        JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
                                .getJSONObject("location");
                        GeoPoint coords = new GeoPoint(location.getDouble("lat"), location.getDouble("lng"));
                        StudySpot spot = new StudySpot(name, coords, schedule, address);
                        viewModel.addStudySpot(spot);
                        viewModel.saveStudySpot(spot);
                    }
                    else if(status != null){
                        throw new Exception("Geocoding request result status was: " + status);
                    }
                    else{
                        throw new Exception("Geocoding request result did not return status OK");
                    }
                }
                catch(Exception e){
                    String msg = e.getMessage();
                    if(msg != null){
                        Log.d(TAG,  msg);
                    }
                    else{
                        Log.d(TAG, "Error handling JSON");
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getLocalizedMessage());
            }
        });
        mQueue.add(jsonObjectRequest);
    }


}