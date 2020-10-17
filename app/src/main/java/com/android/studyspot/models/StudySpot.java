package com.android.studyspot.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudySpot {
    public static final String KEY_NAME = "name";
    public static final String KEY_COORDS = "coords";
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_AVG_RATING = "avg_rating";
    public static final String KEY_AVG_NOISE = "avg_noise";
    public static final String KEY_AVG_LIGHT = "avg_light";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LIGHT_RECORD ="light_record";
    public static final String KEY_NOISE_RECORD = "noise_record";


    private String mName;
    private GeoPoint mCoords;
    private String mSchedule;
    private double mAvgRating, mAvgNoise, mAvgLight;
    //private Map<String, String> address;
    private String mAddress;
    private ArrayList<Review> mReviews;

    //maybe consider making these their own classes?
    private Map<String, Double> lightRecord;
    private Map<String, Double> noiseRecord;


    public StudySpot(){
        mName = null;
        mCoords = null;
        mSchedule = null;
        mAddress = null;
        mAvgNoise = 0;
        mAvgLight = 0;
        mAvgRating = 0;
        mReviews = new ArrayList<Review>();
        lightRecord = new HashMap<>();
        noiseRecord = new HashMap<>();
    }
    public StudySpot(String name, GeoPoint coords, String schedulePath, String address){
        mName = name;
        mCoords = new GeoPoint(coords.getLatitude(), coords.getLongitude());
        mSchedule = schedulePath;
        mAddress = address;
        mAvgLight = 0;
        mAvgNoise = 0;
        mAvgRating = 0;
        mReviews = new ArrayList<Review>();
        lightRecord = new HashMap<>();
        noiseRecord = new HashMap<>();
    }

    public StudySpot(String name, GeoPoint coords, String schedulePath, String address, double avgRating,
                     double avgNoise, double avgLight){
        mName = name;
        mCoords = new GeoPoint(coords.getLatitude(), coords.getLongitude());
        mSchedule = schedulePath;
        mAddress = address;
        mAvgLight = avgLight;
        mAvgNoise = avgNoise;
        mAvgRating = avgRating;
        mReviews = new ArrayList<Review>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
        mAvgRating = avgRating;
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


    //TODO this could probably be implemented better but i don't have time right now
    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    public Map<String, Double> getLightRecord() {
        return lightRecord;
    }

    public Map<String, Double> getNoiseRecord() {
        return noiseRecord;
    }

    //TODO method to calculate average,noise,light, rating
}
