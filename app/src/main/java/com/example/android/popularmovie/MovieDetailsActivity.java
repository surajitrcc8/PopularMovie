package com.example.android.popularmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.utilities.GetMovieJsonUtils;
import com.example.android.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieDetails> {

    private int mMovieId = 0;
    private ProgressBar mDetailIndicatorProgressBar;
    private CollapsingToolbarLayout mMovieDetailsCollapsingToolbar;
    private ImageView mMoviePosterImageView;
    private TextView mReleaseDateTextView;
    private TextView mDurationTextView;
    private TextView mRatingTextView;
    private TextView mOverViewTextView;
    private LinearLayout mMovieDetailsLinearlayout;
    private TextView mDetailErrorMessageTextView;
    private static final String MOVIE_DETAILS_URL = "details";
    private static final String MOVIE_DETAILS = "moviedetails";
    private static final int MOVIE_DETAILS_LOADER_ID = 11;
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private MovieDetails mMovieDetails;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDetailIndicatorProgressBar = (ProgressBar)findViewById(R.id.pb_detail_indicator);
        mMovieDetailsCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ct_movie_details);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mReleaseDateTextView = (TextView)findViewById(R.id.tv_release_year);
        mDurationTextView = (TextView) findViewById(R.id.tv_duration);
        mRatingTextView = (TextView)findViewById(R.id.tv_rating);
        mOverViewTextView = (TextView)findViewById(R.id.tv_overview);
        mMovieDetailsLinearlayout = (LinearLayout) findViewById(R.id.ll_movie_details);
        mDetailErrorMessageTextView = (TextView)findViewById(R.id.tv_details_error_message);

        mActionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.movie_id))) {
            mMovieId = intent.getIntExtra(getString(R.string.movie_id),0);
        }
        URL mMovieDetailUrl = NetworkUtils.buildMovieDetailsUrl(mMovieId,BuildConfig.THE_MOVIE_DB_API_TOKEN);


        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DETAILS)){
            MovieDetails movieDetails = (MovieDetails) savedInstanceState.getParcelable(MOVIE_DETAILS);
            if(movieDetails != null){
                success(movieDetails);
            }
            else{
                error();
            }

        }else {
            Bundle bundle = new Bundle();
            bundle.putString(MOVIE_DETAILS_URL, mMovieDetailUrl.toString());
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<MovieDetails> loader = loaderManager.getLoader(MOVIE_DETAILS_LOADER_ID);
            if (loader == null) {
                loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, bundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_DETAILS_LOADER_ID, bundle, this);
            }
        }


    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_DETAILS, mMovieDetails);
        super.onSaveInstanceState(outState);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<MovieDetails> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDetails>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mDetailIndicatorProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public MovieDetails loadInBackground() {
                try {
                    String movieDetailsURL = args.getString(MOVIE_DETAILS_URL);
                    Log.d(TAG,"Details url is " + movieDetailsURL);
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(new URL(movieDetailsURL));

                    MovieDetails movieDetails = GetMovieJsonUtils
                            .getMovieDetailsFromJson(MovieDetailsActivity.this, jsonMovieResponse);

                    return movieDetails;

                } catch (Exception e) {
                    e.printStackTrace();
                    error();
                    return null;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDetails> loader, MovieDetails movieDetails) {
        mDetailIndicatorProgressBar.setVisibility(View.INVISIBLE);
        if(movieDetails!= null){
            success(movieDetails);
        }else{
            error();
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieDetails> loader) {

    }

    private void success(MovieDetails movieDetails){
        mMovieDetails = movieDetails;
        mMovieDetailsLinearlayout.setVisibility(View.VISIBLE);
        mDetailErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieDetailsCollapsingToolbar.setTitle(movieDetails.getOriginalTitle());
        String bannerPath = NetworkUtils.MOVIE_POSTER_BASE_URL + movieDetails.getMoviePoster();
        Picasso.with(this).load(bannerPath).into(mMoviePosterImageView);
        mReleaseDateTextView.setText(movieDetails.getReleaseDate().split("-")[0]);
        mDurationTextView.setText(movieDetails.getDuration() + "min");
        mRatingTextView.setText(String.format(getString(R.string.rating_value),movieDetails.getUserRating()));
        mOverViewTextView.setText(movieDetails.getOverview());

    }
    private void error(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMovieDetailsLinearlayout.setVisibility(View.INVISIBLE);
                mDetailErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        });

    }
}
