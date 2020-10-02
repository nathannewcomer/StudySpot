package com.android.studyspot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabAssistant extends FragmentStatePagerAdapter {

    int tabs;

    public TabAssistant(@NonNull FragmentManager fm, int tab_count) {
        super(fm);
        this.tabs = tab_count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";
            case 1:
               return "Location List";
        }
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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
        return tabs;
    }
}
