package com.android.studyspot.models;

public class Review {
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
        mComment = null;
        mPhotoPath = null;
    }
    public Review(double rating, int likes, String comment, String photoPath){
        mRating = rating;
        mLikes = likes;
        mComment = comment;
        mPhotoPath = photoPath;
    }
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
}
