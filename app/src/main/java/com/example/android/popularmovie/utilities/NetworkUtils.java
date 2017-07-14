package com.example.android.popularmovie.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {


    public static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String SORT_BY_POPULARITY="popularity";
    public static final String SORT_BY_TOP_RATED="top_rated";
    public static final String PARAM_API_KEY = "api_key";


    /**
     * Builds the URL used to query GitHub.
     *
     * @param sortBy Sort by either popularity or top rated
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildPopularMovieUrl(String sortBy,String apiKey) {
        Uri builtUri = null;
        URL url = null;

        switch (sortBy){
            case SORT_BY_POPULARITY:
                builtUri = Uri.parse(TMDB_BASE_URL + "popular").buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, apiKey)
                        .build();
                break;
            case SORT_BY_TOP_RATED:
                builtUri = Uri.parse(TMDB_BASE_URL + "top_rated").buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, apiKey)
                        .build();
                break;
        }
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query GitHub.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */

    public static URL buildMovieDetailsUrl(int id,String apiKey) {
        Uri builtUri = null;
        URL url = null;

        builtUri = Uri.parse(TMDB_BASE_URL + id).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}