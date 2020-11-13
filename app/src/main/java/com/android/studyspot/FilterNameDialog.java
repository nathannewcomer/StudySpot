package com.android.studyspot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.studyspot.models.StudySpot;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;
import java.util.List;

public class FilterNameDialog extends DialogFragment {

    private TextView mTitle;
    private TextView mMessage;
    private EditText mName;
    private List<StudySpot> spots;
    private List<Marker> markers;

    public FilterNameDialog(List<StudySpot> spots, List<Marker> markers) {
        this.markers = markers;
        this.spots = spots;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_filter_name, null);
        mTitle = root.findViewById(R.id.dialog_filter_type);
        mMessage = root.findViewById(R.id.dialog_filter_description);
        mName = root.findViewById(R.id.dialog_filter_name);

        // TODO: replace hard-coded values with resource strings
        mTitle.setText("Filter by name");
        mMessage.setText("Only show spots with this name:");

        // pass null as the parent view because it's going in the dialog
        builder.setView(root)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = mName.getText().toString();

                        // if the marker is not in the list, hide it
                        for (Marker marker : markers) {
                            if (!marker.getTitle().contains(name)) {
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

}
