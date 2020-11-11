package com.android.studyspot;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.android.studyspot.models.StudySpot;
import com.google.android.material.tabs.TabLayout;


public class MainActivity
        extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private MapViewModel viewModel;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        viewModel.retrieveSpotsFromRepository();


        //Use this to show creation in demo to grader
        /*
        StudySpot testSpot = new StudySpot();
        testSpot.setName("Test");
        viewModel.saveStudySpot(testSpot);
         */

        //Retrieval demo to grader is pretty obvious, just show them the location_list tab

        //Use this to show updating in demo to grader
        /*
        StudySpot testSpot = new StudySpot();
        testSpot.setAvgRating(1.0);
        viewModel.updateDBSpotAverages(testSpot, StudySpot.KEY_AVG_RATING);
         */

        //Use this to show deletion in demo to grader
        /*
        StudySpot testSpot = new StudySpot();
        testSpot.setName("Test");
        viewModel.deleteStudySpot(testSpot);
         */


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar bar = getSupportActionBar();
        //bar.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        TabAssistant tabAssistant = new TabAssistant(getSupportFragmentManager(), 2);
        viewPager.setAdapter(tabAssistant);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        Log.d(TAG,"onCreate() called by" + TAG);


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
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}




