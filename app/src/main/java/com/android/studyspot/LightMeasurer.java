package com.android.studyspot;

import android.content.Context;
import android.content.ContextWrapper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.studyspot.models.StudySpot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/*
 *  A class that takes light measurements for a StudySpot.
 *
 *Help from https://stackoverflow.com/questions/23209804/android-sensor-registerlistener
 * -in-a-separate-thread.
 */
public class LightMeasurer implements Runnable {
    private static final String TAG = "LightMeasurer";
    private static final String HANDLER_NAME = "LightMeasHandler";
    //the measuring time in milliseconds
    public static final int MEASURING_TIME_MS = 30000;
    private SensorManager mSensorManager;
    private SensorEventListener mListener;
    private HandlerThread mHandlerThread;
    private ContextWrapper mContext;
    private int numMeasurements = 0;
    private float cmaLight = 0; //the cumulative moving average of the light measurements in lux
    private MutableLiveData<Boolean> finished;
    private StudySpot mSpot;


    LightMeasurer(ContextWrapper context, StudySpot spot){
        finished = new MutableLiveData<>();
        finished.setValue(Boolean.valueOf(false));
        mContext = context;
        mSpot = spot;
    }

    @Override
    public void run() {
        Date now = new Date();
        takeLightMeasurements();
        try {
            Thread.sleep(MEASURING_TIME_MS);
        }
        catch(Exception e){
            Log.e(TAG,"Thread creating LightMeasurer was interrupted");
        }

        Log.d(TAG,"thread stopped");
        stopThread();
        mSpot.addLightRecord(now.toString(),cmaLight);
        mSpot.setAvgLight(mSpot.calculateAvgLight());
        finished.postValue(Boolean.TRUE);


    }

    private void takeLightMeasurements(){

        mSensorManager = (SensorManager) mContext.getSystemService(ContextWrapper.SENSOR_SERVICE);
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mHandlerThread = new HandlerThread(HANDLER_NAME, Process.THREAD_PRIORITY_DEFAULT); //adjust priority if necessary
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());

        mListener = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightInLux = event.values[0];
                /*compute the cumulative moving average,
                 *see https://en.wikipedia.org/wiki/Moving_average
                 */
                cmaLight = (lightInLux + numMeasurements * cmaLight)/(numMeasurements + 1);
                numMeasurements++;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //do nothing, this is required for the abstract class
            }

        };

        mSensorManager.registerListener(mListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST,
                handler);

    }

    /*
     *Use this to stop the thread from running.
     */
    private void stopThread(){
        if(mSensorManager != null){
            mSensorManager.unregisterListener(mListener);
        }

        if(mHandlerThread.isAlive()){
            mHandlerThread.quitSafely();
        }
    }

    /*
     *Returns a LiveData object representing whether the light measurements have completed.
     */
    public LiveData<Boolean> isFinished(){
        return finished;
    }
    /*
     *Returns the average illuminance read by the default light sensor on your device.
     * Will not be accurate until isFinished() has returned true.
     */
    public float getAverageLight(){
        return cmaLight;
    }
}
