package com.android.studyspot;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;



@LargeTest
@RunWith(AndroidJUnit4.class)
public class MapFragment_UITest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    /*Help from
     *https://stackoverflow.com/questions/29924564/using-espresso-to-unit-test-google-maps.
     * Tests that clicking on the Architecture Library marker in the Map tab opens the details
     * fragment and loads the Architecture Library StudySpot data.
     */
    public void markerClickDisplaysDetails_Test() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Architecture Library"));
        marker.clickAndWaitForNewWindow();
        onView(withId(R.id.detail_location_name)).check(matches(withText("Architecture Library")));
    }
}
