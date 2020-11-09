package com.android.studyspot.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private double mRating;
    private int mLikes;
    private String mComment;
    //TODO replace this once figure out implmentation of cloud storage for photos
    private String mPhotoPath;

    /*this empty constructor is required for firebase to
     *call the DocumentReference.set() method on this object
     */
    public Review(){
        mRating = 0;
        mLikes = 0;
        mComment = "";
        mPhotoPath = "";
    }
    public Review(double rating, int likes, String comment, String photoPath){
        mRating = rating;
        mLikes = likes;
        mComment = comment;
        mPhotoPath = photoPath;
    }

    protected Review(Parcel in) {
        mRating = in.readDouble();
        mLikes = in.readInt();
        mComment = in.readString();
        mPhotoPath = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        if(rating > 0.0){
            mRating = rating;
        }
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mRating);
        dest.writeInt(mLikes);
        dest.writeString(mComment);
        dest.writeString(mPhotoPath);
    }
}
