package com.android.studyspot;
import com.android.studyspot.models.StudySpot;

import org.junit.Test;

import static org.junit.Assert.*;
public class StudySpotUnitTest {
    @Test
    public void getDocumentName_isCorrect1(){
        StudySpot spot = new StudySpot();
        spot.setName("My Test");
        assertTrue(spot.getDocumentName().compareTo("My_Test") == 0);
    }
    @Test
    public void getDocumentName_isCorrect2(){
        StudySpot spot = new StudySpot();
        spot.setName("My/Test");
        assertTrue(spot.getDocumentName().compareTo("My_Test") == 0);
    }

    @Test
    public void setAvgRating_noNegatives(){
        StudySpot spot = new StudySpot();
        spot.setAvgRating(-2.0);
        assertFalse(spot.getAvgRating() < 0);
    }


}
