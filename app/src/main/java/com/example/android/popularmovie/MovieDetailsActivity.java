package com.example.android.popularmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.model.Trailers;
import com.example.android.popularmovie.utilities.GetMovieJsonUtils;
import com.example.android.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieDetails> ,View.OnClickListener{

    private int mMovieId = 0;
    private ProgressBar mDetailIndicatorProgressBar;

    private static final String MOVIE_DETAILS_URL = "details";
    private static final String MOVIE_TRAILER_URL = "trailer";
    private static final String MOVIE_REVIEWS_URL = "reviews";
    public static final String MOVIE_DETAILS = "moviedetails";
    private static final int MOVIE_DETAILS_LOADER_ID = 11;
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private MovieDetails mMovieDetails;
    private ActionBar mActionBar;
    private NestedScrollView mScrollView;
    private ImageView mBackDropImageView;
    private TextView mDetailErrorMessageTextView;
    private CollapsingToolbarLayout mMovieDetailsCollapsingToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDetailIndicatorProgressBar = (ProgressBar)findViewById(R.id.pb_detail_indicator);
        mMovieDetailsCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ct_movie_details);

        mDetailErrorMessageTextView = (TextView)findViewById(R.id.tv_details_error_message);
        mBackDropImageView = (ImageView)findViewById(R.id.iv_backdrop);
        tabLayout = (TabLayout) findViewById(R.id.tl_movie);
        viewPager = (ViewPager) findViewById(R.id.vp_movie_page);

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
        URL mMovieTrailerlUrl = NetworkUtils.buildMovieTrailerUrl(mMovieId,BuildConfig.THE_MOVIE_DB_API_TOKEN);
        URL mMovieReviewsUrl = NetworkUtils.buildMovieReviewsUrl(mMovieId,BuildConfig.THE_MOVIE_DB_API_TOKEN);



        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DETAILS)){
            Log.d(TAG,"Oncreate called saved");
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
            bundle.putString(MOVIE_TRAILER_URL,mMovieTrailerlUrl.toString());
            bundle.putString(MOVIE_REVIEWS_URL,mMovieReviewsUrl.toString());
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<MovieDetails> loader = loaderManager.getLoader(MOVIE_DETAILS_LOADER_ID);
            Log.d(TAG,"Oncreate called");
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
            MovieDetails movieDetails;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(movieDetails != null){
                    deliverResult(movieDetails);
                }else {
                    mDetailIndicatorProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieDetails loadInBackground() {
                try {
                    String movieDetailsURL = args.getString(MOVIE_DETAILS_URL);
                    String movieTrailerURL = args.getString(MOVIE_TRAILER_URL);
                    String movieReviewURL = args.getString(MOVIE_REVIEWS_URL);
                    Log.d(TAG,"Details url is " + movieDetailsURL);
                    Log.d(TAG,"Trailer url is " + movieTrailerURL);
                    Log.d(TAG,"Review url is " + movieReviewURL);
                    //Get Movie Details
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(new URL(movieDetailsURL));

                    //Get Movie Trailer
                    String jsonMovieTrailerResponse = NetworkUtils
                            .getResponseFromHttpUrl(new URL(movieTrailerURL));

                    //Get Movie Review
                    String jsonMovieReviewResponse = NetworkUtils
                            .getResponseFromHttpUrl(new URL(movieReviewURL));


                    MovieDetails movieDetails = GetMovieJsonUtils
                            .getMovieDetailsFromJson(MovieDetailsActivity.this, jsonMovieResponse,jsonMovieTrailerResponse,jsonMovieReviewResponse);

                    return movieDetails;

                } catch (Exception e) {
                    e.printStackTrace();
                    error();
                    return null;
                }

            }

            @Override
            public void deliverResult(MovieDetails data) {
                super.deliverResult(data);
                movieDetails = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDetails> loader, MovieDetails movieDetails) {
        mDetailIndicatorProgressBar.setVisibility(View.INVISIBLE);
        Log.d(TAG,"Finished called");
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
        String backdropPath = NetworkUtils.MOVIE_POSTER_ORIGINAL_BASE_URL + movieDetails.getMovieOriginalPoster();
        mDetailErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieDetailsCollapsingToolbar.setTitle(movieDetails.getOriginalTitle());
        Picasso.with(this).load(backdropPath).into(mBackDropImageView);
        //Check if any trailer is present
        if(movieDetails.getTrailers().size() > 0){

        }

        //Check if any review is present
        if(movieDetails.getReviews().size() > 0){
            Toast.makeText(this, "Review url is " + movieDetails.getReviews().get(0), Toast.LENGTH_SHORT).show();
        }
        createTabBar(movieDetails);



    }

    private void createTabBar(MovieDetails movieDetails){
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Trailer"));
        tabLayout.addTab(tabLayout.newTab().setText("Review"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final TabAdapter adapter = new TabAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),movieDetails);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Tag is " + view.getTag(), Toast.LENGTH_SHORT).show();
    }

//    public void onClickedTrailer(View view){
//        Toast.makeText(this, "Tag is " + view.getContext().toString(), Toast.LENGTH_SHORT).show();
//    }
    private void error(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mMovieDetailsLinearlayout.setVisibility(View.INVISIBLE);
                //mDetailErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        });

    }
}
