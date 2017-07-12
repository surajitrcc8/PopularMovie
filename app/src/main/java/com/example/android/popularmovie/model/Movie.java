package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by surajitbiswas on 7/6/17.
 */

public class Movie implements Parcelable {
    int id;
    String bannerPath;
    //Default constructor
    public Movie(int movieId, String movieBannerPath) {
        id = movieId;
        bannerPath = movieBannerPath;
    }


    protected Movie(Parcel in) {
        id = in.readInt();
        bannerPath = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getBannerPath() {
        return bannerPath;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(bannerPath);
    }

}
