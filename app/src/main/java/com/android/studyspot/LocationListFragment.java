package com.android.studyspot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

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
    List<String> names;


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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        //code for list init
        RecyclerView rvList = (RecyclerView) root.findViewById(R.id.recycler_view);
        names = Arrays.asList(Address.NAMES);
        ListAdapter listAdapter = new ListAdapter(names);
        rvList.setAdapter(listAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

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


}
