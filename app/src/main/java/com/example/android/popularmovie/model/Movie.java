package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by surajitbiswas on 7/6/17.
 */

public class Movie implements Parcelable {
    ArrayList<MovieDetails> results;

    protected Movie(Parcel in) {
        results = in.createTypedArrayList(MovieDetails.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(results);
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

    public ArrayList<MovieDetails> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
