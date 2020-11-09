package com.android.studyspot.models;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StudySpot {
    public static final String KEY_NAME = "name";
    public static final String KEY_COORDS = "coords";
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_AVG_RATING = "avg_rating";
    public static final String KEY_AVG_NOISE = "avg_noise";
    public static final String KEY_AVG_LIGHT = "avg_light";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LIGHT_RECORD = "light_record";
    public static final String KEY_NOISE_RECORD = "noise_record";

    public static final float DISTANCE_UNKNOWN = -1;

    //data that should be saved in the database
    private String mName;
    private GeoPoint mCoords;
    private String mSchedule;
    private double mAvgRating, mAvgNoise, mAvgLight;
    private String mAddress;
    private ArrayList<Review> mReviews;
    private Map<String, Double> mLightRecord;
    private Map<String, Double> mNoiseRecord;

    //data that should not be saved in the database
    private float mDistanceToUser;




    public StudySpot() {
        mName = null;
        mCoords = null;
        mSchedule = null;
        mAddress = null;
        mAvgNoise = 0;
        mAvgLight = 0;
        mAvgRating = 0;
        mReviews = new ArrayList<Review>();
        mLightRecord = new HashMap<>();
        mNoiseRecord = new HashMap<>();
        mDistanceToUser = DISTANCE_UNKNOWN;
    }

    public StudySpot(String name, GeoPoint coords, String schedulePath, String address) {
        mName = name;
        mCoords = new GeoPoint(coords.getLatitude(), coords.getLongitude());
        mSchedule = schedulePath;
        mAddress = address;
        mAvgLight = 0;
        mAvgNoise = 0;
        mAvgRating = 0;
        mReviews = new ArrayList<Review>();
        mLightRecord = new HashMap<>();
        mNoiseRecord = new HashMap<>();
        mDistanceToUser = DISTANCE_UNKNOWN;
    }

    public StudySpot(String name, GeoPoint coords, String schedulePath, String address, double avgRating,
                     double avgNoise, double avgLight) {
        mName = name;
        mCoords = new GeoPoint(coords.getLatitude(), coords.getLongitude());
        mSchedule = schedulePath;
        mAddress = address;
        mAvgLight = avgLight;
        mAvgNoise = avgNoise;
        mAvgRating = avgRating;
        mReviews = new ArrayList<Review>();
        mLightRecord = new HashMap<>();
        mNoiseRecord = new HashMap<>();
        mDistanceToUser = DISTANCE_UNKNOWN;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    //TODO move this maybe?
    public String getDocumentName() {
        String docName = new String(mName);
        docName = docName.replaceAll("\\s|/", "_");
        return docName;
    }

    public GeoPoint getCoords() {
        return mCoords;
    }

    public void setCoords(GeoPoint coords) {
        mCoords = coords;
    }

    public String getSchedule() {
        return mSchedule;
    }

    public void setSchedule(String schedule) {
        mSchedule = schedule;
    }

    public double getAvgRating() {
        return mAvgRating;
    }

    public void setAvgRating(double avgRating) {
        if(avgRating >= 0.0){
            mAvgRating = avgRating;
        }
    }

    public double getAvgNoise() {
        return mAvgNoise;
    }

    public void setAvgNoise(double avgNoise) {
        mAvgNoise = avgNoise;
    }

    public double getAvgLight() {
        return mAvgLight;
    }

    public void setAvgLight(double avgLight) {
        mAvgLight = avgLight;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    //clears the reviews for the spot and sets the avg rating to 0
    public void clearReviews() {
        mReviews.clear();
        mAvgRating = 0;
    }

    //adds a review to the spot
    public void addReview(Review review) {
        mReviews.add(review);
    }

    //adds a review to the spot at position pos and shifts all reviews at and or after pos 1 to the right
    public void addReview(int pos, Review review) {
        if (pos >= 0 && pos <= mReviews.size()) {
            mReviews.add(pos, review);
        }
    }

    //removes a review at position pos if it exists, otherwise return null
    public Review removeReview(int pos) {
        if (pos >= 0 && pos < mReviews.size()) {
            return mReviews.remove(pos);
        } else {
            return null;
        }
    }

    //get the review at position pos, otherwise return null
    public Review getReview(int pos) {
        if (pos >= 0 && pos < mReviews.size()) {
            return mReviews.get(pos);
        } else {
            return null;
        }
    }

    //returns the number of reviews
    public int numberOfReviews() {
        return mReviews.size();
    }

    public double calculateAvgRating(){
        int numReviews = mReviews.size();
        double avg = 0;
        for(Review review: mReviews){
            avg += (review.getRating() / numReviews);
        }
        return avg;
    }

    public double calculateAvgNoise(){
        int numRecords = mNoiseRecord.size();
        double avg = 0;
        for(Map.Entry<String, Double> record: mNoiseRecord.entrySet()){
            avg += ( record.getValue() / numRecords);
        }
        return avg;
    }

    public double calculateAvgLight(){
        int numRecords = mLightRecord.size();
        double avg = 0;
        for(Map.Entry<String, Double> record: mLightRecord.entrySet()){
            avg += ( record.getValue() / numRecords);
        }
        return avg;
    }

    //returns the number of light records
    public int numberOfLightRecords() {
        return mLightRecord.size();
    }

    //returns the number of noise records
    public int numberOfNoiseRecords() {
        return mNoiseRecord.size();
    }

    //clears the light records for a spot and sets the average light to 0
    public void clearLightRecords() {
        mLightRecord.clear();
        mAvgLight = 0;
    }


    //clears the light records for a spot and sets the average light to 0
    public void clearNoiseRecords() {
        mNoiseRecord.clear();
        mAvgNoise = 0;
    }


    public Double getLightRecord(String timestamp) {
        return mLightRecord.get(timestamp);
    }

    public Double getNoiseRecord(String timestamp) {
        return mNoiseRecord.get(timestamp);
    }

    public void addLightRecord(String timestamp, double value) {
        mLightRecord.put(timestamp, value);
    }

    public void addNoiseRecord(String timestamp, double value) {
        mNoiseRecord.put(timestamp, value);
    }

    public Set<Map.Entry<String, Double>> getAllLightRecords(){
        return mLightRecord.entrySet();
    }

    public Set<Map.Entry<String, Double>> getAllNoiseRecords(){
        return mNoiseRecord.entrySet();
    }


    public float getDistanceToUser() {
        return mDistanceToUser;
    }

    public void updateDistanceToUser(Location userLocation){
        if(userLocation != null){
            Location spotLocation = new Location("");
            spotLocation.setLatitude(mCoords.getLatitude());
            spotLocation.setLongitude(mCoords.getLongitude());
            mDistanceToUser = userLocation.distanceTo(spotLocation);
        }
    }


}
