package com.android.studyspot;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.studyspot.models.Review;
import com.android.studyspot.models.StudySpot;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class MapViewModel extends AndroidViewModel {
    //the path to the studyspots collection in firebase
    public static final String COLLECTION_STUDYSPOTS = "studyspots";
    public static final String COLLECTION_REVIEW = "reviews";
    //the document holding the noise records for a studyspot. there should only be one per spot
    public static final String DOCUMENT_LIGHT = "light_record/singlerecord";
    //the document holding the sound records for a studyspot. there should only by one per spot
    public static final String DOCUMENT_NOISE = "noise_record/singlerecord";

    private static final String GEOCODING_REQUEST_OK = "OK";
    public static final String TAG = "MapViewModel";
    private FirebaseFirestore mDatabase;
    private ArrayList<StudySpot> mSpots;
    private RequestQueue mQueue;



    public MapViewModel(Application application){
        super(application);
        mDatabase = FirebaseFirestore.getInstance();
        mSpots = new ArrayList<StudySpot>();
        mQueue = Volley.newRequestQueue(application.getApplicationContext());

    }


    //methods below this point work, but could use more testing.

    /*
     *Retrieves all of the study spots in the database and puts them in mSpots.
     * Also retrieves all reviews and light and noise measurements for the spot.
     * Will only work when no StudySpots currently in mSpots.
     */
    public void retrieveAllStudySpots(){
        if(mSpots!= null && mSpots.size() == 0) {
            mDatabase.collection(COLLECTION_STUDYSPOTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            StudySpot spot = new StudySpot();
                            Map<String, Object> data = document.getData();
                            spot.setName((String) data.get(StudySpot.KEY_NAME));
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
                            if (avgRating instanceof Long) {
                                spot.setAvgRating(((Long) avgRating).doubleValue());
                            } else {
                                spot.setAvgRating((double) avgRating);
                            }
                            if (avgNoise instanceof Long) {
                                spot.setAvgNoise(((Long) avgNoise).doubleValue());
                            } else {
                                spot.setAvgNoise((double) avgNoise);
                            }
                            if (avgLight instanceof Long) {
                                spot.setAvgLight(((Long) avgLight).doubleValue());
                            } else {
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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Review review = document.toObject(Review.class);
                        spot.addReview(review);
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
                        for(Map.Entry<String, Double> record : noiseRecord.entrySet()){
                            spot.addNoiseRecord(record.getKey(), record.getValue());
                        }
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
                        for(Map.Entry<String, Double> record : lightRecord.entrySet()){
                            spot.addLightRecord(record.getKey(), record.getValue());
                        }
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
         String docName = spot.getDocumentName();
         Map<String,Object> docData = new HashMap<>();
         docData.put(StudySpot.KEY_NAME, spot.getName());
         docData.put(StudySpot.KEY_COORDS, spot.getCoords());
         docData.put(StudySpot.KEY_SCHEDULE, spot.getSchedule());
         docData.put(StudySpot.KEY_AVG_RATING, spot.getAvgRating());
         docData.put(StudySpot.KEY_AVG_NOISE, spot.getAvgNoise());
         docData.put(StudySpot.KEY_AVG_LIGHT, spot.getAvgLight());
         docData.put(StudySpot.KEY_ADDRESS, spot.getAddress());
         Map<String,Double> lightRecord = new HashMap<String,Double>();
         Map<String,Double> noiseRecord = new HashMap<String, Double>();
         for(Map.Entry<String,Double> record : spot.getAllLightRecords()){
             lightRecord.put(record.getKey(),record.getValue());
         }

        for(Map.Entry<String,Double> record : spot.getAllNoiseRecords()){
            noiseRecord.put(record.getKey(), record.getValue());
        }

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
        if(spot.numberOfReviews() > 0){
            ArrayList<Review> reviewsToSave = new ArrayList<Review>();
            for(int i = 0; i < spot.numberOfReviews(); i++){
                reviewsToSave.add(spot.getReview(i));
            }
            saveReview(reviewsToSave, spot);
        }

        if(!lightRecord.isEmpty()){
            saveLightRecord(lightRecord, spot);
        }

        if(!noiseRecord.isEmpty()){
            saveNoiseRecord(noiseRecord, spot);
        }
    }

    /*
     *Saves the review to the firebase database. Reviews is an array of one or more reviews to be added.
     * spot is the StudySpot that the reviews are for.
     */
    public void saveReview(ArrayList<Review> reviews, StudySpot spot){
        for(Review rev: reviews){
            mDatabase.collection(COLLECTION_STUDYSPOTS).document(spot.getDocumentName())
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
     *Saves the light record to the firebase database.
     *spot is the StudySpot that the light_record has been measured from
     */
    public void saveLightRecord(Map<String,Double> light_record, StudySpot spot){
        Map<String,Object> docData = new HashMap<>();
        //StudySpot.KEY_LIGHT_RECORD is the key for the entire map in its document
        docData.put(StudySpot.KEY_LIGHT_RECORD, light_record);
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(spot.getDocumentName());
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
     *Saves the noise record to the firebase database. spot is the StudySpot that the
     * noise_record has been measured from
     */
    public void saveNoiseRecord(Map<String,Double> noise_record, StudySpot spot){
        Map<String,Object> docData = new HashMap<>();
        docData.put(StudySpot.KEY_NOISE_RECORD, noise_record);
        StringBuilder builder = new StringBuilder(COLLECTION_STUDYSPOTS);
        builder.append("/");
        builder.append(spot.getDocumentName());
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
     */
    public void addStudySpot(StudySpot spot){
        mSpots.add(spot);
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
        builder.append(getApplication().getString(R.string.geocoding_api_key));
        builder.append("&language=en");
        return builder.toString();
    }



    /*
     * Creates a new StudySpot, adds it to the View model, and saves it to the database.
     * Populates the coords of the Studyspot by making a Google Maps Geocode request using the
     * spot's address. Make sure your address does not have the building name or floor name in it
     * Note: the volley.add method needs to run in the main thread
     */

    public void createNewStudySpot(final String name, final String address, final String schedule){
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
                        mSpots.add(spot);
                        saveStudySpot(spot);
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

    /*
     *Updates  the average rating, average noise, and or average light field for spot in the database
     * depending on the constants passed after spot.
     * Use StudySpot.KEY_AVG_LIGHT for light, StudySpot.KEY_AVG_NOISE for noise,
     * or StudySpot.KEY_AVG_RATING for rating
     * spot: the StudySpot to have its database document updated.
     */
    public void updateDBSpotAverages(StudySpot spot, String ... fieldNames){
        int i = 0;
        Map<String, Object> data = new HashMap<String, Object>();
        for(String field: fieldNames){
            if(i > 2){
                break;
            }
            if(field.compareTo(StudySpot.KEY_AVG_LIGHT) == 0){
                data.put(field, spot.getAvgLight());
            }
            else if(field.compareTo(StudySpot.KEY_AVG_NOISE) == 0){
                data.put(field, spot.getAvgNoise());
            }
            else if(field.compareTo(StudySpot.KEY_AVG_RATING) == 0 ){
                data.put(field, spot.getAvgRating());
            }
            i++;
        }
        if(data.size() > 0){
            mDatabase.collection(COLLECTION_STUDYSPOTS).document(spot.getDocumentName())
                    .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Averages updated");
                    } else {
                        Log.d(TAG, "Averages failed to update", task.getException());
                    }
                }
            });;
        }

    }


}
