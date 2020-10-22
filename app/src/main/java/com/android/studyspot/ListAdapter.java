package com.android.studyspot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.android.studyspot.models.StudySpot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//implementation came from the help of
// https://guides.codepath.com/android/using-the-recyclerview. All rights reserved to CodePath

public class ListAdapter extends RecyclerView.Adapter <ListAdapter.ViewHolder> {

    private static final String TAG = "ListAdapter";
    private List<StudySpot> mSpots;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView location_name;
        public ViewHolder(View view){
            super(view);
            location_name = view.findViewById(R.id.location_name);
        }
    }

    public ListAdapter(List<StudySpot> spots){
        mSpots = spots;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        //create Layout inflator from context
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View location = layoutInflater.inflate(R.layout.location_item, parent, false);
        ViewHolder holder = new ViewHolder(location);
        //Log.d(TAG,"onCreateViewHolder() called by" + TAG);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String location = mSpots.get(position).getName();
        //holder.location_name.setText(location);
        holder.location_name.setText(location);
        //Log.d(TAG,"onBindViewHolder() called by" + TAG);

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"getItemCount() called by" + TAG);
        return mSpots.size();
    }

    public void setSpots(List<StudySpot> spots) {
        mSpots = spots;
    }

}
