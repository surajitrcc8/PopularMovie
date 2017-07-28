package com.example.android.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovie.provider.MovieContract;
import com.example.android.popularmovie.adapter.PopularMovieAdapter;
import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.utilities.GetMovieJsonUtils;
import com.example.android.popularmovie.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

public class PopularMovieActivity extends AppCompatActivity implements PopularMovieAdapter.MovieBanerClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {


    private static final String TAG = PopularMovieActivity.class.getSimpleName();

    //No of columns we want to see in a row of gird.
    private static final int NO_OF_MOVIE_PER_ROW = 2;
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
    private ArrayList<Movie> mMovieList = null;
    private static int mSortType = 0;
    private static final int VERTICAL_SPAN = 2;
    private static final int HORIZONTAL_SPAN = 3;
    private boolean isOnstop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movie);
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
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            mPopularMovieAdapter.setItems(mMovieList);
            if (mMovieList == null) {
                //There is no movie list list. May be device is offline so try to fetch it again.
                if (savedInstanceState.containsKey(SORT_BY)) {
                    GetMovie(savedInstanceState.getInt(SORT_BY));
                }
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
                movieRequestUrl = NetworkUtils.buildPopularMovieUrl(NetworkUtils.SORT_BY_POPULARITY, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                break;
            case R.id.mi_top_rated:
                movieRequestUrl = NetworkUtils.buildPopularMovieUrl(NetworkUtils.SORT_BY_TOP_RATED, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                break;
        }
        try {
            Bundle bundle = new Bundle();
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<ArrayList<Movie>> loader = loaderManager.getLoader((sortType == R.id.mi_favourite) ? FAVOURITE_LOADER_ID : LOADER_ID);
            if(sortType != R.id.mi_favourite) {
                bundle.putString(MOVIE_URL, movieRequestUrl.toString());
            }
            //There is no loader is running currently
            if (loader == null) {
                loaderManager.initLoader((sortType == R.id.mi_favourite) ? FAVOURITE_LOADER_ID : LOADER_ID, (sortType == R.id.mi_favourite) ? null : bundle, this);

            } else {
                //Loader was running so restart it.
                loaderManager.restartLoader((sortType == R.id.mi_favourite) ? FAVOURITE_LOADER_ID : LOADER_ID, (sortType == R.id.mi_favourite) ? null : bundle, this);

            }

        } catch (Exception e) {
            Log.e(TAG, "Not able to Start the loader ");
            e.printStackTrace();
        }

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
    public Loader<ArrayList<Movie>> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            ArrayList<Movie> movies;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                //Check if loader is not start the loading because app is resume from onStop; If so then stop loading.
                if(!isOnstop) {
                    if (movies != null) {
                        deliverResult(movies);
                    } else {
                            mMoviePosterProgressBar.setVisibility(View.VISIBLE);
                            forceLoad();

                    }
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                if (id == FAVOURITE_LOADER_ID) {
                    Cursor cursor = getContentResolver().query(MovieContract.MoviewEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    if (cursor != null && cursor.getCount() > 0) {
                        movies = new ArrayList<Movie>(cursor.getCount());

                        cursor.moveToPosition(0);
                        do{
                            try {
                                movies.add(GetMovieJsonUtils.getMoviesFromSql(PopularMovieActivity.this,
                                        cursor.getString(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_JSON_STRING)),
                                        cursor.getInt(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_ID))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return  null;
                            }
                        }while (cursor.moveToNext());
                        return movies;
                    }else {
                        return null;
                    }

                } else {
                    String url = args.getString(MOVIE_URL);

                    Log.d(TAG, "Url is " + url);

                    try {
                        URL movieRequestUrl = new URL(url);
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieRequestUrl);

                        ArrayList<Movie> moviePosterData = GetMovieJsonUtils
                                .getMoviesFromJson(PopularMovieActivity.this, jsonMovieResponse);

                        return moviePosterData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        errorResponse();
                        return null;
                    }
                }


            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                super.deliverResult(data);
                if(data != null) {
                    movies = data;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> posterList) {
        mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
        if (posterList != null) {
            mPopularMovieAdapter.setItems(posterList);
            mMovieList = posterList;
            successResponse();
        }else{
            errorResponse();
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

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
                    mErrorMessageTextView.setText("There is no favourite movie");
                }else {
                    mErrorMessageTextView.setText(getString(R.string.error_message));
                }
                mMoviePosterProgressBar.setVisibility(View.INVISIBLE);
                mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageTextView.setVisibility(View.VISIBLE);

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        isOnstop = true;
    }
    public static void upDateFavouriteMovieList(Context context,Cursor cursor) {
        if (mSortType == R.id.mi_favourite) {
            ArrayList<Movie> movies = null;
            if (cursor != null && cursor.getCount() > 0) {
                movies = new ArrayList<Movie>(cursor.getCount());
                cursor.moveToPosition(0);
                do {
                    try {
                        movies.add(GetMovieJsonUtils.getMoviesFromSql(context,
                                cursor.getString(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_JSON_STRING)),
                                cursor.getInt(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_ID))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mPopularMovieAdapter.setItems(null);
                        mErrorMessageTextView.setText("There is no favourite movie");
                        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                        mErrorMessageTextView.setVisibility(View.VISIBLE);
                    }
                } while (cursor.moveToNext());
                mPopularMovieAdapter.setItems(movies);
                mMovieListRecyclerView.setVisibility(View.VISIBLE);
                mErrorMessageTextView.setVisibility(View.INVISIBLE);
            } else {

                mPopularMovieAdapter.setItems(null);
                mErrorMessageTextView.setText("There is no favourite movie");
                mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
