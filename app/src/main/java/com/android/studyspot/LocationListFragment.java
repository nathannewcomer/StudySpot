package com.android.studyspot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.studyspot.models.StudySpot;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationListFragment extends Fragment {

    private static final String TAG = "LocationListFragment";

    private RecyclerView listView;
    private ImageButton settingsButton;
    MapViewModel viewModel;


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

        Log.d(TAG,"onCreate() called by" + TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        //code for list init
        RecyclerView rvList = (RecyclerView) root.findViewById(R.id.recycler_view);
        final ListAdapter listAdapter = new ListAdapter(viewModel.getSpots().getValue());
        rvList.setAdapter(listAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getSpots().observe(getViewLifecycleOwner(), new Observer<List<StudySpot>>() {
            @Override
            public void onChanged(List<StudySpot> studySpots) {
                listAdapter.setSpots(studySpots);
                listAdapter.notifyDataSetChanged();
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


}
