package com.android.studyspot;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.android.studyspot.models.Review;
import com.android.studyspot.models.StudySpot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MapViewModel extends ViewModel {
    //the path to the studyspots collection in firebase
    public static final String COLLECTION_STUDYSPOTS = "studyspots";
    public static final String COLLECTION_REVIEW = "reviews";
    public static final String DOCUMENT_LIGHT = "light_record/singlerecord";
    public static final String DOCUMENT_NOISE = "noise_record/singlerecord";
    public static final String TAG = "MapViewModel";
    private FirebaseFirestore mDatabase;


    public void saveStudySpot(StudySpot spot){
         mDatabase = FirebaseFirestore.getInstance();
         String docName = spot.getName().replace(' ','_');
         Map<String,Object> docData = new HashMap<>();
         docData.put(StudySpot.KEY_NAME, spot.getName());
         docData.put(StudySpot.KEY_COORDS, spot.getCoords());
         docData.put(StudySpot.KEY_SCHEDULE, spot.getSchedule());
         docData.put(StudySpot.KEY_AVG_RATING, spot.getAvgRating());
         docData.put(StudySpot.KEY_AVG_NOISE, spot.getAvgNoise());
         docData.put(StudySpot.KEY_AVG_LIGHT, spot.getAvgLight());
         docData.put(StudySpot.KEY_ADDRESS, spot.getAddress());
         ArrayList<Review> reviews = spot.getReviews();
         Map<String,Double> lightRecord = spot.getLightRecord();
         Map<String,Double> noiseRecord = spot.getNoiseRecord();



         mDatabase.collection(COLLECTION_STUDYSPOTS).document(docName).set(spot)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG,"Spot was saved");
                    }
                    else{
                        Log.d(TAG,"Spot failed to save",task.getException());
                    }
                }
            });

         //save the following data to documents in subcollections of StudySpot
        //should not matter order saved since Firebase will create empty documents/collections.
        if(!reviews.isEmpty()){
            saveReview(reviews, docName);
        }

        if(!lightRecord.isEmpty()){
            saveLightRecord(lightRecord, docName);
        }

        if(!noiseRecord.isEmpty()){
            saveNoiseRecord(noiseRecord, docName);
        }
    }

    public void saveReview(ArrayList<Review> reviews, String docName){
        for(Review rev: reviews){
            mDatabase.collection(COLLECTION_STUDYSPOTS).document(docName)
                .collection(COLLECTION_REVIEW).add(rev)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        }
    }

    public void saveLightRecord(Map<String,Double> light_record, String docName){
        Map<String,Object> docData = new HashMap<>();
        docData.put(StudySpot.KEY_LIGHT_RECORD, light_record);
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(docName);
        builder.append("/");
        builder.append(DOCUMENT_LIGHT);

        mDatabase.document(builder.toString()).set(docData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "LightRecord was saved");
                        } else {
                            Log.d(TAG, "LightRecord failed to save", task.getException());
                        }
                    }
                });

    }

    public void saveNoiseRecord(Map<String,Double> noise_record, String docName){
        Map<String,Object> docData = new HashMap<>();
        docData.put(StudySpot.KEY_NOISE_RECORD, noise_record);
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(docName);
        builder.append("/");
        builder.append(DOCUMENT_NOISE);

        mDatabase.document(builder.toString()).set(docData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "NoiseRecord was saved");
                        } else {
                            Log.d(TAG, "NoiseRecord failed to save", task.getException());
                        }
                    }
                });

    }



}
