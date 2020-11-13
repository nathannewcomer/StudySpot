package com.android.studyspot;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.studyspot.models.StudySpot;

import java.util.Date;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/*
 *A class that calculates the average Sound Pressure Level in DB over 1s
 * using your device's default microphone. Uses the TarsosDSP library by JorenSix
 * for all audio processing. https://github.com/JorenSix/TarsosDSP.
 */
public class NoiseMeasurer implements Runnable {
    private static final String TAG = "NoiseMeasurer";
    private static final int SAMPLING_RATE_HZ = 44100;
    private static final int BUFFER_OVERLAP = 0;
    private static final double PROCESSING_TIME = 1.0; //processing time in seconds
    private static final double CALIBRATION = 120.0;
    private int numMeasurements = 0;
    private double cmaNoise = 0; //the cumulative moving average of the noise measurements in dB.
    private Thread mDispatcherThread;
    private AudioDispatcher mDispatcher;
    private MutableLiveData<Boolean> finished;
    private StudySpot mSpot;


    public NoiseMeasurer(StudySpot spot){
        finished = new MutableLiveData<>();
        finished.setValue(Boolean.valueOf(false));
        mSpot = spot;

    }

    @Override
    public void run() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE_HZ,
                android.media.AudioFormat.CHANNEL_IN_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT);
        mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLING_RATE_HZ, bufferSize,
                BUFFER_OVERLAP);
        mDispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                if(mDispatcher.secondsProcessed() < PROCESSING_TIME){
                    double noise = audioEvent.getdBSPL() + CALIBRATION;
                    cmaNoise = (noise + numMeasurements * cmaNoise)/(numMeasurements + 1);
                    numMeasurements++;
                    return true;
                }
                else{
                    return false;
                }
            }

            @Override
            public void processingFinished() {
                //required for the AudioProcessor interface
            }
        });

        mDispatcherThread = new Thread(mDispatcher, "AudioDispatcher");
        mDispatcherThread.start();

        try{
            Thread.sleep((long) (PROCESSING_TIME * 1000));
        }
        catch(Exception e){
            Log.e(TAG,"NoiseMeasurer was interrupted");
        }
        mDispatcher.stop();
        Date timeFinished = new Date();
        mSpot.addNoiseRecord(timeFinished.toString(),cmaNoise);
        mSpot.setAvgNoise(mSpot.calculateAvgNoise());
        finished.postValue(Boolean.TRUE);
    }

    /*
     *Returns a LiveData object representing whether the light measurements have completed.
     */
    public LiveData<Boolean> isFinished(){
        return finished;
    }
    /*
     *Returns the average Sound Pressure Level in DB over 1s recorded by mDispatcher
     * using your device's default microphone.
     * Will not be accurate until isFinished() has returned true.
     */
    public double getAverageNoise(){
        return cmaNoise;
    }
}
