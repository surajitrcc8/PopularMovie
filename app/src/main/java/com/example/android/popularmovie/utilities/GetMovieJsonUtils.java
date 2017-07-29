/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovie.utilities;

import android.content.Context;

import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.model.Reviews;
import com.example.android.popularmovie.model.Trailers;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class GetMovieJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the poster path of each movie.
     * <p/>
     *
     * @param context Application context
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings path to movie poster
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<Movie> getMoviesFromJson(Context context, String movieJsonStr)
            throws JSONException {

        final String MOVIE_LIST = "results";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        /* String array to hold each movie's poster path */
        ArrayList<Movie> movieList = new ArrayList<Movie>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {
            movieList.add(new Movie(movieArray.getJSONObject(i).getInt("id"),movieArray.getJSONObject(i).getString("poster_path")));
        }

        return movieList;
    }
    /**
     * This method parses JSON from a sql response and returns an array of Movie object
     * <p/>
     *
     * @param context Application context
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Movie object
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Movie getMoviesFromSql(Context context, String movieJsonStr,int id)
            throws JSONException {

        Gson gson = new Gson();
        MovieDetails movieDetails = gson.fromJson(movieJsonStr,MovieDetails.class);
        Movie movie = new Movie(id,movieDetails.getMoviePoster());
        return movie;
    }
    /**
     * This method parses JSON from a web response and returns MovieDetails object
     * describing the details of the movie.
     * <p/>
     * @param context Application context
     * @param movieDetailsJsonStr JSON response from server
     * @return MovieDetails object
     * @throws JSONException if JSON data cannot be properly parsed
     */
    public static MovieDetails getMovieDetailsFromJson(Context context, String movieDetailsJsonStr,String movieTrailersJsonStr,String movieReviewsJsonStr)
            throws JSONException {

        final String MOVIE_LIST = "results";


        JSONObject movieDetailsJson = new JSONObject(movieDetailsJsonStr);
        JSONArray movieTrailerJsonArray = new JSONObject(movieTrailersJsonStr).getJSONArray(MOVIE_LIST);
        JSONArray movieReviewsJsonArray = new JSONObject(movieReviewsJsonStr).getJSONArray(MOVIE_LIST);

        /* String array to hold each movie trailer details */
        ArrayList<Trailers> movieTrailers = new ArrayList<Trailers>(movieTrailerJsonArray.length());
        for (int i = 0; i < movieTrailerJsonArray.length(); i++) {
            movieTrailers.add(new Trailers(movieTrailerJsonArray.getJSONObject(i).getString("key"),movieTrailerJsonArray.getJSONObject(i).getString("name")));
        }

        /* String array to hold each movie trailer details */
        ArrayList<Reviews> movieReviews = new ArrayList<Reviews>(movieReviewsJsonArray.length());
        for (int i = 0; i < movieReviewsJsonArray.length(); i++) {
            movieReviews.add(new Reviews(movieReviewsJsonArray.getJSONObject(i).getString("author"),movieReviewsJsonArray.getJSONObject(i).getString("content")));
        }

        /* String array to hold each movie's poster path */
        MovieDetails movieDetails = new MovieDetails(movieDetailsJson.getString("original_title"),
                movieDetailsJson.getString("poster_path"),
                movieDetailsJson.getString("backdrop_path"),
                movieDetailsJson.getString("overview"),
                movieDetailsJson.getDouble("vote_average"),
                movieDetailsJson.getString("release_date"),
                movieDetailsJson.getInt("runtime"),
                movieTrailers,
                movieReviews);


        return movieDetails;
    }
    /**
     * This method parses JSON from a sql response and returns a MovieDetails object
     * <p/>
     *
     * @param context Application context
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Movie object
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static MovieDetails getMoviesDetailsFromSql(Context context, String movieJsonStr)
            throws JSONException {

        Gson gson = new Gson();
        MovieDetails movieDetails = gson.fromJson(movieJsonStr,MovieDetails.class);

        return movieDetails;
    }

}