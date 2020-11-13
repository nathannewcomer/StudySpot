package com.android.studyspot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.studyspot.models.StudySpot;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.slider.Slider;

import java.util.LinkedList;
import java.util.List;

public class FilterRatingDialog extends DialogFragment {

    private final int MIN = 0;
    private final int MAX = 5;
    private final int PROGRESS = 1;
    private int rating;
    private TextView mTitle;
    private TextView mMessage;
    private TextView mMin;
    private TextView mMax;
    private TextView mCurrentValue;
    private SeekBar mSeekBar;

    private List<StudySpot> spots;
    private List<Marker> markers;

    public FilterRatingDialog(List<StudySpot> spots, List<Marker> markers) {
        this.markers = markers;
        this.spots = spots;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_filter_slider, null);
        mTitle = root.findViewById(R.id.dialog_filter_type);
        mMessage = root.findViewById(R.id.dialog_filter_description);
        mSeekBar = root.findViewById(R.id.dialog_filter_seekbar);
        mMin = root.findViewById(R.id.dialog_filter_min);
        mMax = root.findViewById(R.id.dialog_filter_max);
        mCurrentValue = root.findViewById(R.id.dialog_filter_currentValue);

        // TODO: replace hard-coded values with resource strings
        mTitle.setText("Filter by rating");
        mMessage.setText("Only show spots with ratings at and above:");
        mSeekBar.setProgress(PROGRESS);
        mSeekBar.setMax(MAX);
        mMin.setText(Integer.toString(MIN));
        mMax.setText(Integer.toString(MAX));
        mCurrentValue.setText(Integer.toString(mSeekBar.getProgress()));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCurrentValue.setText(Integer.toString(mSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // pass null as the parent view because it's going in the dialog
        builder.setView(root)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rating = mSeekBar.getProgress();

                // get the spots to show
                List<String> goodSpots = new LinkedList<>();
                for (StudySpot spot : spots) {
                    if (spot.getAvgRating() >= rating) {
                        goodSpots.add(spot.getName());
                    }
                }

                // if the marker is not in the list, hide it
                for (Marker marker : markers) {
                    if (!goodSpots.contains(marker.getTitle())) {
                        marker.setVisible(false);
                    }
                }

            }
        })
                // we don't need an on-click listener for cancel
                .setNegativeButton("Cancel", null);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public int getRating() {
        return rating;
    }
}
