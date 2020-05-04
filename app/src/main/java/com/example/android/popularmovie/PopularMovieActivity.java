package com.example.android.popularmovie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovie.IdlingResource.SimpleIdlingResource;
import com.example.android.popularmovie.adapter.PopularMovieAdapter;
import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class PopularMovieActivity extends AppCompatActivity implements PopularMovieAdapter.MovieBanerClickListener {


    private static final String TAG = PopularMovieActivity.class.getSimpleName();


    //static final string key to store the movie list in bundle
    //so that we can prevent web service call when onCreate called for some reasons such as device rotate.
    private static final String MOVIE_LIST = "movielist";

    //static string key for movie url;
    private static final String MOVIE_URL = "movieurl";
    //Unique loader id
    private static final int LOADER_ID = 10;
    private static final int FAVOURITE_LOADER_ID = 20;

    //static string key to store user selected sort type
    public static String SORT_BY = "sort";

    private static RecyclerView mMovieListRecyclerView;
    private static PopularMovieAdapter mPopularMovieAdapter;
    private ProgressBar mMoviePosterProgressBar;
    private static TextView mErrorMessageTextView;
    private static ArrayList<MovieDetails> mMovieList = null;
    private static int mSortType = 0;
    //No of columns when in vertical
    private static final int VERTICAL_SPAN = 2;
    //No of columns when in horizontal
    private static final int HORIZONTAL_SPAN = 3;
    private boolean isOnstop = false;
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movie);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},10);
        }
        mMovieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        mMoviePosterProgressBar = (ProgressBar) findViewById(R.id.pb_movie_poster);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mPopularMovieAdapter = new PopularMovieAdapter(this, this);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(this, VERTICAL_SPAN));
        } else {
            mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(this, HORIZONTAL_SPAN));
        }
        mMovieListRecyclerView.setAdapter(mPopularMovieAdapter);
        //check if onCreate getting called due to some lifecycle change
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            int sortBy = savedInstanceState.containsKey(SORT_BY) ? savedInstanceState.getInt(SORT_BY) : -1;
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mPopularMovieAdapter.setItems(mMovieList);
            if(sortBy == R.id.mi_favourite) {
                GetMovie(sortBy);
            }else if(mMovieList == null){
                //There is no movie list list. May be device is offline so try to fetch it again.
                GetMovie(sortBy);
            }

        } else {
            //First app launch
            GetMovie(R.id.mi_popularity);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_BY, mSortType);
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList);
        super.onSaveInstanceState(outState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GetMovie(item.getItemId());
        return true;
    }

    @Override
    public void onClickListener(int movieId) {
        Log.d(TAG, "Movie id is " + movieId);
        Intent intent = new Intent(PopularMovieActivity.this, MovieDetailsActivity.class);
        intent.putExtra(getString(R.string.movie_id), movieId);
        intent.putExtra(SORT_BY,mSortType);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnstop = true;
    }

    /**
     * This get the url based on user selection and then called the loader to load list of movies
     *
     * @param sortType This represent the user selected soty type (popularity/top rated)
     */
    public void GetMovie(int sortType) {
        isOnstop = false;
        mSortType = sortType;
        URL movieRequestUrl = null;
        mPopularMovieAdapter.setItems(null);
        switch (sortType) {
            case R.id.mi_popularity:
                mMoviePosterProgressBar.setVisibility(View.VISIBLE);
                NetworkUtils.getInstance().api().getPopularMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Movie>() {
                            @Override
                            public void onSuccess(Movie movie) {
                                updateUI(movie);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
                                errorResponse();
                            }
                        });
                break;
            case R.id.mi_top_rated:
                NetworkUtils.getInstance().api().getTopRatedMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Movie>() {
                            @Override
                            public void onSuccess(Movie movie) {
                                updateUI(movie);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
                                errorResponse();
                            }
                        });
                break;
        }

    }

    private void updateUI(Movie movie){
        mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
        if (movie != null) {
            mPopularMovieAdapter.setItems(movie.getResults());
            mMovieList = movie.getResults();
            successResponse();
        }else{
            errorResponse();
        }
        if(mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }
    /**
     * This function handle the success response of fetch movie list web service.
     */
    public void successResponse() {
        mMovieListRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * This function handle the error response of fetch movie list web service.
     */
    public void errorResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mSortType == R.id.mi_favourite){
                    mErrorMessageTextView.setText(getString(R.string.no_favourite_movie));
                }else {
                    mErrorMessageTextView.setText(getString(R.string.error_message));
                }
                mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
                mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mMovieList = null;

            }
        });
    }


    /**
     * This function get called from details page when movie is set to favourite
     * or unfavourite to refresh the favourite movie grid view
     * @param context Context
     * @param cursor This holds the favourite movie data set.
     */
    public static void upDateFavouriteMovieList(Context context,Cursor cursor) {
        if (mSortType == R.id.mi_favourite) {
            ArrayList<MovieDetails> movies = null;
            if (cursor != null && cursor.getCount() > 0) {
                movies = new ArrayList<MovieDetails>(cursor.getCount());
                cursor.moveToPosition(0);
                do {
                    /*try {
                        movies.add(GetMovieJsonUtils.getMoviesFromSql(context,
                                cursor.getString(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_JSON_STRING)),
                                cursor.getInt(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_ID))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mPopularMovieAdapter.setItems(null);
                        mErrorMessageTextView.setText(context.getString(R.string.no_favourite_movie));
                        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                        mErrorMessageTextView.setVisibility(View.VISIBLE);
                        mMovieList = null;
                    }*/
                } while (cursor.moveToNext());
                mPopularMovieAdapter.setItems(movies);
                mMovieListRecyclerView.setVisibility(View.VISIBLE);
                mErrorMessageTextView.setVisibility(View.INVISIBLE);
                mMovieList = movies;
            } else {
                mPopularMovieAdapter.setItems(null);
                mErrorMessageTextView.setText(context.getString(R.string.no_favourite_movie));
                mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mMovieList = null;
            }
        }
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {;
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @VisibleForTesting
    @NonNull
    public AppCompatActivity getDetailsActivity () {
        return new MovieDetailsActivity();
    }
}
