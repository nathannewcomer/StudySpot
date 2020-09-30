package com.android.studyspot;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

public class MainActivity
        extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPageAdapter viewPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        TabLayout.Tab map_fragment = tabLayout.newTab();
        TabLayout.Tab list_fragment = tabLayout.newTab();
        tabLayout.addTab(map_fragment, true);
        tabLayout.addTab(list_fragment);
        map_fragment.setText("Map");
        list_fragment.setText("Location List");




}