package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by surajitbiswas on 7/19/17.
 */

public class Trailers implements Parcelable {

    String key;
    String name;

    public Trailers(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    protected Trailers(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel in) {
            return new Trailers(in);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(name);
    }
}
