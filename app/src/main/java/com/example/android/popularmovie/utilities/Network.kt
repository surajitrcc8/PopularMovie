package com.example.android.popularmovie.utilities

import android.net.Uri
import com.example.android.popularmovie.IdlingResource.SimpleIdlingResource
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object Network {

    val TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/"
    val MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185/"
    val MOVIE_POSTER_ORIGINAL_BASE_URL = "https://image.tmdb.org/t/p/original/"
    val SORT_BY_POPULARITY = "popularity"
    val SORT_BY_TOP_RATED = "top_rated"
    val PARAM_API_KEY = "api_key"
    val MOVIE_TRAILER_PATH = "videos"
    val MOVIE_REVIEWS_PATH = "reviews"
    lateinit var movieClient: MovieClient

    init {
        setupRetrofit()
    }
    private fun setupRetrofit() {
        val retrofit = buildRetrofit()
        movieClient = retrofit.create(MovieClient::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(loggingInterceptor)
        client.addNetworkInterceptor(StethoInterceptor())
        return Retrofit.Builder()
                .baseUrl(NetworkUtils.TMDB_BASE_URL)
                .client(client.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    /**
     * Builds the URL used to query GitHub.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */
    fun buildMovieDetailsUrl(id: Int, apiKey: String?): URL? {
        var builtUri: Uri? = null
        var url: URL? = null
        builtUri = Uri.parse(NetworkUtils.TMDB_BASE_URL + id).buildUpon()
                .appendQueryParameter(NetworkUtils.PARAM_API_KEY, apiKey)
                .build()
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * Builds the URL used to query GitHub to get movie trailer.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */
    fun buildMovieTrailerUrl(id: Int, apiKey: String?): URL? {
        var builtUri: Uri? = null
        var url: URL? = null
        builtUri = Uri.parse(NetworkUtils.TMDB_BASE_URL + id).buildUpon()
                .appendPath(NetworkUtils.MOVIE_TRAILER_PATH)
                .appendQueryParameter(NetworkUtils.PARAM_API_KEY, apiKey)
                .build()
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * Builds the URL used to query GitHub to get movie reviews.
     *
     * @param id Selected move id
     * @param apiKey The api key for TMDB access.
     * @return The URL to use to query the GitHub.
     */
    fun buildMovieReviewsUrl(id: Int, apiKey: String?): URL? {
        var builtUri: Uri? = null
        var url: URL? = null
        builtUri = Uri.parse(NetworkUtils.TMDB_BASE_URL + id).buildUpon()
                .appendPath(NetworkUtils.MOVIE_REVIEWS_PATH)
                .appendQueryParameter(NetworkUtils.PARAM_API_KEY, apiKey)
                .build()
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL, mIdlingResource: SimpleIdlingResource?): String? {
        mIdlingResource?.setIdleState(false)
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val `in` = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}