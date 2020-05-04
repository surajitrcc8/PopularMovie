package com.example.android.popularmovie.utilities;

import android.net.Uri;

import com.example.android.popularmovie.IdlingResource.SimpleIdlingResource;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {


    public static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185/";
    public static final String MOVIE_POSTER_ORIGINAL_BASE_URL = "https://image.tmdb.org/t/p/original/";
    public static final String SORT_BY_POPULARITY="popularity";
    public static final String SORT_BY_TOP_RATED="top_rated";
    public static final String PARAM_API_KEY = "api_key";
    public static final String MOVIE_TRAILER_PATH = "videos";
    public static final String MOVIE_REVIEWS_PATH = "reviews";
    private static MovieClient movieClient;
    protected static NetworkUtils instance;

    public static NetworkUtils getInstance(){
        if(instance == null) {
            setupRetrofit();
            instance = new NetworkUtils();
        }
        return instance;
    }

    private static void setupRetrofit() {
        Retrofit retrofit = buildRetrofit();
        movieClient = retrofit.create(MovieClient.class);
    }

    private static Retrofit buildRetrofit(){
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(loggingInterceptor);
        client.addNetworkInterceptor(new StethoInterceptor());
        return new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .client(client.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static MovieClient api(){
        return movieClient;
    }
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
     * Builds the URL used to query GitHub to get movie trailer.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */

    public static URL buildMovieTrailerUrl(int id,String apiKey) {
        Uri builtUri = null;
        URL url = null;

        builtUri = Uri.parse(TMDB_BASE_URL + id).buildUpon()
                .appendPath(MOVIE_TRAILER_PATH)
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
     * Builds the URL used to query GitHub to get movie reviews.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */

    public static URL buildMovieReviewsUrl(int id,String apiKey) {
        Uri builtUri = null;
        URL url = null;

        builtUri = Uri.parse(TMDB_BASE_URL + id).buildUpon()
                .appendPath(MOVIE_REVIEWS_PATH)
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
    public static String getResponseFromHttpUrl(URL url, SimpleIdlingResource mIdlingResource) throws IOException {

        if(mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
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
