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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MapViewModel extends ViewModel {
    //the path to the studyspots collection in firebase
    public static final String COLLECTION_STUDYSPOTS = "studyspots";
    public static final String COLLECTION_REVIEW = "reviews";
    //the document holding the noise records for a studyspot. there should only be one per spot
    public static final String DOCUMENT_LIGHT = "light_record/singlerecord";
    //the document holding the sound records for a studyspot. there should only by one per spot
    public static final String DOCUMENT_NOISE = "noise_record/singlerecord";
    public static final String TAG = "MapViewModel";
    private FirebaseFirestore mDatabase;
    private ArrayList<StudySpot> mSpots;

    /*
     * Initializes the instance variables of MapViewModel. Call this after
     * getting the view model for the first time.
     */
    public void initialize(){
        if(mDatabase == null){
            mDatabase = FirebaseFirestore.getInstance();
        }
        if(mSpots == null){
            mSpots = new ArrayList<StudySpot>();
        }
    }

    //methods below this point work, but could use more testing.

    /*
     *Retrieves all of the study spots in the database and puts them in mSpots.
     * Also retrieves all reviews and light and noise measurements for the spot.
     * Warning! mSpots will be overwritten.
     */
    public void retrieveAllStudySpots(){
        if (mDatabase == null){
            mDatabase = FirebaseFirestore.getInstance();
        }
        mSpots = new ArrayList<StudySpot>();
        mDatabase.collection(COLLECTION_STUDYSPOTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        StudySpot spot = new StudySpot();
                        Map<String, Object> data = document.getData();
                        spot.setName( (String) data.get(StudySpot.KEY_NAME));
                        spot.setCoords((GeoPoint) data.get(StudySpot.KEY_COORDS));
                        spot.setSchedule((String) data.get(StudySpot.KEY_SCHEDULE));
                        spot.setAddress((String) data.get(StudySpot.KEY_ADDRESS));

                        Object avgRating = data.get(StudySpot.KEY_AVG_RATING);
                        Object avgNoise = data.get(StudySpot.KEY_AVG_NOISE);
                        Object avgLight = data.get(StudySpot.KEY_AVG_LIGHT);

                        /*Dealt with issues where firebase stored number as a long if entered
                         *from the firebase console. Java does not let you use (double) to cast
                         *from Long to Double
                         */
                        if(avgRating instanceof Long){
                            spot.setAvgRating(((Long) avgRating).doubleValue());
                        }
                        else{
                            spot.setAvgRating((double) avgRating);
                        }
                        if(avgNoise instanceof Long){
                            spot.setAvgNoise(((Long) avgNoise).doubleValue());
                        }
                        else{
                            spot.setAvgNoise((double) avgNoise);
                        }
                        if(avgLight instanceof Long){
                            spot.setAvgLight(((Long) avgLight).doubleValue());
                        }
                        else{
                            spot.setAvgLight((double) avgLight);
                        }
                        mSpots.add(spot);
                        retrieveReviews(spot);
                        retrieveNoiseRecord(spot);
                        retrieveLightRecord(spot);
                    }
                } else {
                    Log.d(TAG, "Error retrieving StudySpots: ", task.getException());
                }
            }
        });
    }

    /*
     *Retrieves all of the reviews for a studyspot in the database and adds them to the studyspot.
     * Warning! does not check for duplicate reviews.
     */
    public void retrieveReviews( final StudySpot spot){
        mDatabase.collection(COLLECTION_STUDYSPOTS).document(spot.getDocumentName())
                .collection(COLLECTION_REVIEW).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Review> reviews = spot.getReviews();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Review review = document.toObject(Review.class);
                        reviews.add(review);
                    }
                } else {
                    Log.d(TAG, "Error getting reviews: ", task.getException());
                }
            }
        });
    }


    /*
     *Retrieves the noiseRecord for the provided StudySpot from the database and overwrites the
     * local(in-memory) noiseRecord.
     */
    public void retrieveNoiseRecord(final StudySpot spot){
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(spot.getDocumentName());
        builder.append("/");
        builder.append(DOCUMENT_NOISE);

        mDatabase.document(builder.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String,Double> noiseRecord = (Map<String, Double>) document.get(StudySpot.KEY_NOISE_RECORD);
                        spot.setNoiseRecord(noiseRecord);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /*
     *Retrieves the lightRecord for the provided StudySpot from the database and overwrites the
     * local(in-memory) noiseRecord.
     */
    public  void retrieveLightRecord(final StudySpot spot){
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(spot.getDocumentName());
        builder.append("/");
        builder.append(DOCUMENT_LIGHT);

        mDatabase.document(builder.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String,Double> lightRecord = (Map<String, Double>) document.get(StudySpot.KEY_LIGHT_RECORD);
                        spot.setLightRecord(lightRecord);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /*
     *Saves the studyspot to the firebase database
     */
    public void saveStudySpot(StudySpot spot){
        if (mDatabase == null){
            mDatabase = FirebaseFirestore.getInstance();
        }
         String docName = spot.getDocumentName();
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



         mDatabase.collection(COLLECTION_STUDYSPOTS).document(docName).set(docData)
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

    /*
     *Saves the review to the firebase database
     */
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
    /*
     *Saves the light record to the firebase database
     */
    public void saveLightRecord(Map<String,Double> light_record, String docName){
        Map<String,Object> docData = new HashMap<>();
        //StudySpot.KEY_LIGHT_RECORD is the key for the entire map in its document
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

    /*
     *Saves the noise record to the firebase database
     */
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

    public ArrayList<StudySpot> getSpots() {
        return mSpots;
    }
    /*
     *Adds the study spot to be stored in mSpots.
     *Will create a new ArrayList for mSpots if mSpots has not been initialized
     */
    public void addStudySpot(StudySpot spot){
        if(mSpots == null){
            mSpots = new ArrayList<StudySpot>();
        }
        mSpots.add(spot);
    }


}
