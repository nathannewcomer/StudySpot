package com.android.studyspot;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabAssistant extends FragmentStatePagerAdapter {

    private static final String TAG = "TabAssistant";
    int tabs;

    public TabAssistant(@NonNull FragmentManager fm, int tab_count) {
        super(fm);
        this.tabs = tab_count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //Log.d(TAG,"getPageTitle() called by" + TAG);

        switch (position) {
            case 0:

                return App.getContext().getResources().getString(R.string.map_tab_title);
            case 1:
               return App.getContext().getResources().getString(R.string.list_tab_title);
        }
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //Log.d(TAG,"getItem() called by" + TAG);

        switch (position) {
            case 0:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;
            case 1:
                LocationListFragment locationListFragment = new LocationListFragment();
                return locationListFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        //Log.d(TAG,"getCount() called by" + TAG);

        return tabs;
    }
}
