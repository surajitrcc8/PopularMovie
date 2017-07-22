package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by surajitbiswas on 7/19/17.
 */

public class Reviews implements Parcelable {
    String url;

    public Reviews(String url){
        this.url = url;
    }

    protected Reviews(Parcel in) {
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public String getUrl() {
        return url;
    }
}
