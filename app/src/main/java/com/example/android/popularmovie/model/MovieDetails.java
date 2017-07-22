package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieDetails implements Parcelable {
    String originalTitle;
    String moviePoster;
    String movieOriginalPoster;
    String overview;
    double userRating;
    String releaseDate;
    int duration;
    ArrayList<Trailers>trailers;
    ArrayList<Reviews>reviews;


    protected MovieDetails(Parcel in) {
        originalTitle = in.readString();
        moviePoster = in.readString();
        movieOriginalPoster = in.readString();
        overview = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        duration = in.readInt();
        trailers = in.createTypedArrayList(Trailers.CREATOR);
        reviews = in.createTypedArrayList(Reviews.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(moviePoster);
        dest.writeString(movieOriginalPoster);
        dest.writeString(overview);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(duration);
        dest.writeTypedList(trailers);
        dest.writeTypedList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
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


    //Default constructor


    public MovieDetails(String originalTitle, String moviePoster, String movieOriginalPoster, String overview, double userRating, String releaseDate, int duration, ArrayList<Trailers> trailers, ArrayList<Reviews> reviews) {
        this.originalTitle = originalTitle;
        this.moviePoster = moviePoster;
        this.movieOriginalPoster = movieOriginalPoster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.trailers = trailers;
        this.reviews = reviews;
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

    public ArrayList<Trailers> getTrailers() {
        return trailers;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public String getMovieOriginalPoster() {
        return movieOriginalPoster;
    }
}
