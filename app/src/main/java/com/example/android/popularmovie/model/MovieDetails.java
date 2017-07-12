package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetails implements Parcelable {
    String originalTitle;
    String moviePoster;
    String overview;
    double userRating;
    String releaseDate;

    protected MovieDetails(Parcel in) {
        originalTitle = in.readString();
        moviePoster = in.readString();
        overview = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        duration = in.readInt();
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    public int getDuration() {
        return duration;
    }

    int duration;

    //Default constructor
    public MovieDetails(String originalTitle, String moviePoster, String overview, double userRating, String releaseDate, int duration) {
        this.originalTitle = originalTitle;
        this.moviePoster = moviePoster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getOverview() {
        return overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(moviePoster);
        parcel.writeString(overview);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
        parcel.writeInt(duration);
    }
}
