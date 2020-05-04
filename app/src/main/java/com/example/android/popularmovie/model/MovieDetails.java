package com.example.android.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieDetails implements Parcelable {
    int id;
    String original_title;
    String poster_path;
    String backdrop_path;
    String overview;
    double vote_average;
    String release_date;
    int runtime;
    ArrayList<Trailers>trailers;
    ArrayList<Reviews>reviews;


    protected MovieDetails(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
        runtime = in.readInt();
        trailers = in.createTypedArrayList(Trailers.CREATOR);
        reviews = in.createTypedArrayList(Reviews.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
        dest.writeInt(runtime);
        dest.writeTypedList(trailers);
        dest.writeTypedList(reviews);
    }


    public int getDuration() {
        return runtime;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getMoviePoster() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public double getUserRating() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public ArrayList<Trailers> getTrailers() {
        return trailers;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public String getMovieOriginalPoster() {
        return backdrop_path;
    }
}
