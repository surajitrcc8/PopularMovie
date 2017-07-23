package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by surajitbiswas on 7/19/17.
 */

public class Reviews implements Parcelable {
    String author;
    String content;

    public Reviews(String author, String content) {
        this.author = author;
        this.content = content;
    }

    protected Reviews(Parcel in) {
        author = in.readString();
        content = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
