package com.android.studyspot;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.studyspot.models.StudySpot;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MapViewModel extends AndroidViewModel {
    //the path to the studyspots collection in firebase
    public static final String TAG = "MapViewModel";
    private MutableLiveData<List<StudySpot>> mStudySpots;
    private StudySpotRepository mRepo;
    private Location mCurrentLocation = null;
    private MutableLiveData<StudySpot> mDetailsSpot;
    public static boolean isConnected;



    public MapViewModel(Application application){
        super(application);
        List<StudySpot> mSpots = new ArrayList<>();
        List<StudySpot> list = new ArrayList<StudySpot>();
        mStudySpots = new MutableLiveData<List<StudySpot>>();
        mStudySpots.setValue(list);
        mRepo = new StudySpotRepository(application.getApplicationContext());
        mDetailsSpot = new MutableLiveData<>();
    }


    /*
     *Update spot holder to hold all of the StudySpots from retrieved from firebase.
     * by mRepo.
     */
    void retrieveSpotsFromRepository(){
        mRepo.retrieveAllStudySpots(mStudySpots);
        isConnected = mRepo.getConnectionStatus();
    }

    /*
     *Saves the StudySpot to the database.
     */
    public void saveStudySpot(StudySpot spot){
        mRepo.saveStudySpot(spot);
        isConnected = mRepo.getConnectionStatus();
    }

    public MutableLiveData<List<StudySpot>> getSpots() {
        return mStudySpots;
    }

    /*
     *Adds the study spot to be stored in mSpots.
     */
    public void addStudySpot(StudySpot spot){
        mStudySpots.getValue().add(spot);
        mStudySpots.setValue(mStudySpots.getValue());
    }

    /*
     * Creates a new StudySpot, adds it to mStudySpots, and saves it to the database.
     * Populates the coords of the Studyspot by making a Google Maps Geocode request using the
     * spot's address. Make sure your address does not have the building name or floor name in it
     * Note: this method must run in the main thread.
     */

    public void createNewStudySpot(final String name, final String address, final String schedule){
        String apiKey = getApplication().getString(R.string.geocoding_api_key);
        mRepo.createNewStudySpot(name, address, schedule, apiKey, mStudySpots);
        isConnected = mRepo.getConnectionStatus();
    }

    /*
     *Deletes the StudySpot from mStudySpots and then deletes the spot from the database.
     */
    public void deleteStudySpot(final StudySpot spot){
        List<StudySpot> spots = mStudySpots.getValue();
        String otherSpotName;
        int pos = -1;
        for(int i = 0; i < spots.size(); i++){
            otherSpotName = spots.get(i).getName();
            if(spot.getName().compareTo(otherSpotName) == 0){
                pos = i;
                break;
            }
        }
        if(pos != -1){
            spots.remove(pos);
            mStudySpots.setValue(spots);
        }
        mRepo.deleteStudySpot(spot);
        isConnected = mRepo.getConnectionStatus();
    }

    public StudySpot findStudySpotByName(String name){
        List<StudySpot> spots = mStudySpots.getValue();
        StudySpot mSpot = null;
        String otherSpotName;
        int pos = -1;
        for(int i = 0; i < spots.size();i++){
            otherSpotName = spots.get(i).getName();
            if(name.compareTo(otherSpotName) == 0){
                mSpot = spots.get(i);
            }
        }
        return mSpot;
    }


    /*
     *Updates  the average rating, average noise, and or average light field for spot in the database
     * depending on the constants passed after spot.
     * Use StudySpot.KEY_AVG_LIGHT for light, StudySpot.KEY_AVG_NOISE for noise,
     * or StudySpot.KEY_AVG_RATING for rating
     * spot: the StudySpot to have its database document updated.
     */
    public void updateDBSpotAverages(StudySpot spot, String ... fieldNames){
        if(fieldNames.length > 0 ){
            mRepo.updateDBSpotAverages(spot, fieldNames);
            isConnected = mRepo.getConnectionStatus();

        }
    }

    /*Helper method to update the average light and light record for a spot in the database
     */
    public void updateDBSpotLight(StudySpot spot){
        updateDBSpotAverages(spot, new String[]{ StudySpot.KEY_AVG_LIGHT} );
        mRepo.saveLightRecord(spot);
        isConnected = mRepo.getConnectionStatus();

    }

    /*Helper method to update the average noise and noise record for a spot in the database
     */
    public void updateDBSpotNoise(StudySpot spot){
        updateDBSpotAverages(spot, new String[]{ StudySpot.KEY_AVG_NOISE} );
        mRepo.saveNoiseRecord(spot);
        isConnected = mRepo.getConnectionStatus();

    }

    public void setAverageCurrentRatingFromReview(StudySpot spot){
        mRepo.retrieveReviews(spot, mStudySpots);
        isConnected = mRepo.getConnectionStatus();
        Log.d(TAG, "setCurrentRatingFromReview() called by " + TAG);
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
    }


    public LiveData<StudySpot> getDetailsSpot() {
        return mDetailsSpot;
    }

    public void setDetailsSpot(StudySpot spot) {
        mDetailsSpot.setValue(spot);
    }
}
