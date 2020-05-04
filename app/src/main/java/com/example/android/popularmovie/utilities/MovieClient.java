package com.example.android.popularmovie.utilities;

import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.model.Reviews;
import com.example.android.popularmovie.model.Trailers;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieClient {
    @GET("popular")
    Single<Movie> getPopularMovie(@Query("api_key") String apiKey);

    @GET("top_rated")
    Single<Movie> getTopRatedMovie(@Query("api_key") String apiKey);

    @GET("{id}")
    Single<MovieDetails> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Single<ArrayList<Trailers>> getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Single<ArrayList<Reviews>> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey);
}
