package com.android.studyspot;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.android.studyspot.models.StudySpot;


import java.util.Comparator;
import java.util.List;

//implementation came from the help of
// https://guides.codepath.com/android/using-the-recyclerview. All rights reserved to CodePath

public class ListAdapter extends RecyclerView.Adapter <ListAdapter.ViewHolder> {

    interface ListItemClickListener{
        void onListItemClick(int position);
    }
    private static final String TAG = "ListAdapter";
    final private ListItemClickListener mOnClickListener;

    /*Constants specifying which StudySpot field to sort the list on and also which, if any,
     * extra information should appear in the location_details TextView.
     */
    public enum SortOption {
        NAME,
        RATING,
        QUIET,
        LOUD,
        BRIGHT,
        DARK,
        DISTANCE
    }



    private SortOption chosenOption;
    private List<StudySpot> mSpots;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView location_name;
        public TextView location_details;

        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            location_name = view.findViewById(R.id.location_name);
            location_details = view.findViewById(R.id.location_details);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);

        }
    }

    public ListAdapter(List<StudySpot> spots, ListItemClickListener listItemClickListener){
        mSpots = spots;
        chosenOption = SortOption.NAME;
        this.mOnClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)   {
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
        StudySpot spot = mSpots.get(position);
        String location = spot.getName();
        holder.location_name.setText(location);
        switch(chosenOption){
            case NAME:
                holder.location_details.setText("");
                break;
            case RATING:
                double rating = spot.getAvgRating();
                holder.location_details.setText(String.format("Rating: %.2f stars", rating));
                break;
            case QUIET:
            case LOUD:
                double noise = spot.getAvgNoise();
                holder.location_details.setText(String.format("Noise: %.2f dB", noise));
                break;
            case BRIGHT:
            case DARK:
                double light = spot.getAvgLight();
                holder.location_details.setText(String.format("Light: %.2f lux", light));
                break;
            case DISTANCE:
                float distance = spot.getDistanceToUser();
                if(distance != StudySpot.DISTANCE_UNKNOWN){
                    holder.location_details.setText(String.format("Distance: %.0f m", distance));
                }
                break;
        }

    }

    public StudySpot getStudySpot(int position){
        return mSpots.get(position);
    }
    @Override
    public int getItemCount() {
        //Log.d(TAG,"getItemCount() called by" + TAG);
        return mSpots.size();
    }

    public void setSpots(List<StudySpot> spots) {
        mSpots = spots;
    }


    /*
     *Sorts mSpots by the SortOption passed. Only works on Android N or later.
     * Choosing NAME sorts by StudySpot name lexographically. Choosing RATING sorts by
     * rating in descending order. QUIET and LOUD sort by average noise. BRIGHT and DARK
     * sort by average light. DISTANCE sorts by increasing distance.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortSpots(SortOption option){
        switch(option){
            case NAME:
                chosenOption = SortOption.NAME;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case RATING:
                chosenOption = SortOption.RATING;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Double.compare(o2.getAvgRating(), o1.getAvgRating());
                    }
                });
                break;
            case QUIET:
                chosenOption = SortOption.QUIET;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Double.compare(o1.getAvgNoise(), o2.getAvgNoise());
                    }
                });
                break;
            case LOUD:
                chosenOption = SortOption.QUIET;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Double.compare(o2.getAvgNoise(), o1.getAvgNoise());
                    }
                });
                break;
            case BRIGHT:
                chosenOption = SortOption.DARK;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Double.compare(o2.getAvgLight(), o1.getAvgLight());
                    }
                });
                break;
            case DARK:
                chosenOption = SortOption.DARK;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Double.compare(o1.getAvgLight(), o2.getAvgLight());
                    }
                });
                break;
            case DISTANCE:
                chosenOption = SortOption.DISTANCE;
                mSpots.sort(new Comparator<StudySpot>() {
                    @Override
                    public int compare(StudySpot o1, StudySpot o2) {
                        return Float.compare(o1.getDistanceToUser(), o2.getDistanceToUser());
                    }
                });
                break;
        }
    }

}
