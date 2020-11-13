package com.android.studyspot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static android.app.Activity.RESULT_OK;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.studyspot.models.Review;
import com.bumptech.glide.Glide;

public class LeaveReviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LeaveReviewFragment";
    private final static int REQUEST_STORAGE_PERMISSIONS = 0;
    private final static int REQUEST_TAKE_PHOTO = 1;
    private static final String DATE_FORMAT_FILE = "yyyyMMdd-HHmmss";
    private static final String PHOTO_PATH_PREFIX = "StudySpotPhoto_";
    private static final String KEY_IMAGE_PATH = "com.android.studyspot.recentphoto";
    public static final String NEW_REVIEW = "NewReview";
    private static final String[] STORAGE_PERMISSIONS = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView locationTextView;
    private RatingBar locationRatingBar;
    private EditText commentsEditText;
    private Button submitButton;
    private Button photoButton;
    private ImageView locationImageView;
    private String mSpotName;

    private String imagePath;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_leave_review,container, false);

        locationTextView = (TextView) v.findViewById(R.id.location_text_view);
        mSpotName = getActivity().getIntent().getStringExtra(LocationListFragment.REVIEW_NAME);
        locationTextView.setText(mSpotName);

        locationRatingBar = (RatingBar) v.findViewById(R.id.location_rating_bar);
        commentsEditText = (EditText) v.findViewById(R.id.comments_edit_text);
        submitButton = (Button) v.findViewById(R.id.submit_button);
        photoButton = (Button) v.findViewById(R.id.take_photo_button);
        locationImageView = (ImageView) v.findViewById(R.id.location_image_view);
        if(savedInstanceState != null){
            imagePath = savedInstanceState.getString(KEY_IMAGE_PATH,null);
        }
        if(imagePath != null && locationImageView != null){
            Glide.with(this).load(imagePath).into(locationImageView);
        }
        if(submitButton != null){
            submitButton.setOnClickListener(this);
        }

        if(photoButton != null){
            photoButton.setOnClickListener(this);
        }

        Log.d(TAG,"onCreateView() called by" + TAG);
        return v;
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
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        if(imagePath != null){
            outState.putString(KEY_IMAGE_PATH, imagePath);
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.submit_button:
                Review review = new Review();
                review.setSpotName(mSpotName);
                review.setRating(locationRatingBar.getRating());
                review.setComment(commentsEditText.getText().toString());
                // TODO: figure out likes and images

                // send review back to MainActivity
                Intent intent = new Intent();
                intent.putExtra(NEW_REVIEW, review);
                getActivity().setResult(RESULT_OK, intent);
                //added this to send us back to previous page
                getActivity().finish();

                break;
            case R.id.location_image_view:
                break;
            case R.id.take_photo_button:
                if(hasStoragePermissions()){
                    sendPhotoIntent();
                }
                else{
                    requestPermissions(STORAGE_PERMISSIONS, REQUEST_STORAGE_PERMISSIONS);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions,
                                            int[] grantResults){
        switch(requestCode){
            case REQUEST_STORAGE_PERMISSIONS:
                boolean granted = true;
                if(grantResults.length > 0){
                    for(int result: grantResults){
                        granted &= (result == PackageManager.PERMISSION_GRANTED);
                    }
                }
                else{
                    granted = false;
                }

                if(!granted){
                    photoButton.setEnabled(false);
                }
                break;
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent cameraIntent){
        if(resultCode == RESULT_OK && requestCode == REQUEST_TAKE_PHOTO){
            if(imagePath != null && locationImageView != null){
                Glide.with(this).load(imagePath).into(locationImageView);
            }
        }
    }

    private File createImageFile() throws IOException {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FILE);
        StringBuilder builder = new StringBuilder(PHOTO_PATH_PREFIX);
        builder.append(df.format(now.getTime()));
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(builder.toString(), ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        return image;
    }

    private void sendPhotoIntent(){
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                    "com.android.studyspot.fileprovider",
                    photoFile);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }

    }

    private boolean hasStoragePermissions(){
        Activity activity = requireActivity();
        boolean result = true;
        for(String permission: STORAGE_PERMISSIONS){
            result &= (ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }
}
