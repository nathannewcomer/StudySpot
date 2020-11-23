package com.android.studyspot;

import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.studyspot.models.Review;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ReviewTest {
    private final double DOUBLE_EPSILON = 1.0e-10;
    private Review mReview;
    @Before
    public void createReview(){
        mReview = new Review();
    }

    @Test
    public void review_ParcelableWriteRead(){
        mReview.setSpotName("Test");
        mReview.setRating(1.0);
        mReview.setComment("Something");
        mReview.setLikes(20);
        mReview.setPhotoPath("www.google.com");

        Parcel parcel = Parcel.obtain();
        mReview.writeToParcel(parcel,mReview.describeContents());
        parcel.setDataPosition(0);

        Review reviewFromParcel = Review.CREATOR.createFromParcel(parcel);
        assertEquals(mReview.getSpotName(),reviewFromParcel.getSpotName());
        assertEquals(mReview.getRating(), reviewFromParcel.getRating(),DOUBLE_EPSILON);
        assertEquals(mReview.getLikes(),reviewFromParcel.getLikes());
        assertEquals(mReview.getComment(),reviewFromParcel.getComment());
        assertEquals(mReview.getPhotoPath(),reviewFromParcel.getPhotoPath());

    }

}
