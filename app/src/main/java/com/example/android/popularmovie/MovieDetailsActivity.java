package com.example.android.popularmovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.test.espresso.IdlingResource;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovie.IdlingResource.SimpleIdlingResource;
import com.example.android.popularmovie.adapter.TabAdapter;
import com.example.android.popularmovie.model.Movie;
import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.provider.MovieContract;
import com.example.android.popularmovie.utilities.FavourireMovieLoaderUtil;
import com.example.android.popularmovie.utilities.GetMovieJsonUtils;
import com.example.android.popularmovie.utilities.NetworkUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieDetails>{

    private int mMovieId = 0;
    private ProgressBar mDetailIndicatorProgressBar;

    private static final String MOVIE_DETAILS_URL = "details";
    private static final String MOVIE_TRAILER_URL = "trailer";
    private static final String MOVIE_REVIEWS_URL = "reviews";
    public static final String MOVIE_DETAILS = "moviedetails";
    public static final String MOVIE_FAVOURITE = "favourite";
    private static final int MOVIE_DETAILS_LOADER_ID = 11;
    private static final int QUERY_MOVIE_FAVOURITE_LOADER_ID = 12;
    private static final int MOVIE_DETAILS_FROM_SQL_LOADER_ID = 15;
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    private MovieDetails mMovieDetails;
    private ActionBar mActionBar;
    private ImageView mBackDropImageView;
    private TextView mDetailErrorMessageTextView;
    private CollapsingToolbarLayout mMovieDetailsCollapsingToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean isFavourite = false;
    private FloatingActionButton mFabFloatingActionButton;
    private MovieFavourite mMovieFavourite;
    private int mSortType;
    private SimpleIdlingResource mIdlingResource;

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
        mFabFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab);

        mActionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.movie_id))) {
            mMovieId = intent.getIntExtra(getString(R.string.movie_id),0);
        }
        if(intent.hasExtra(PopularMovieActivity.SORT_BY)) {
            mSortType = intent.getIntExtra(PopularMovieActivity.SORT_BY,0);
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DETAILS)){
            MovieDetails movieDetails = (MovieDetails) savedInstanceState.getParcelable(MOVIE_DETAILS);
            if(movieDetails != null){
                if(savedInstanceState.containsKey(MOVIE_FAVOURITE)){
                    setResetFavourite(savedInstanceState.getBoolean(MOVIE_FAVOURITE));
                }
                success(movieDetails);
            }
            else{
                error();
            }

        }else {
            //Check if sort type is not favourite
            if(mSortType != R.id.mi_favourite) {
                Bundle bundle = new Bundle();
                URL mMovieDetailUrl = NetworkUtils.buildMovieDetailsUrl(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                URL mMovieTrailerlUrl = NetworkUtils.buildMovieTrailerUrl(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                URL mMovieReviewsUrl = NetworkUtils.buildMovieReviewsUrl(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                bundle.putString(MOVIE_DETAILS_URL, mMovieDetailUrl.toString());
                bundle.putString(MOVIE_TRAILER_URL, mMovieTrailerlUrl.toString());
                bundle.putString(MOVIE_REVIEWS_URL, mMovieReviewsUrl.toString());
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<MovieDetails> loader = loaderManager.getLoader(MOVIE_DETAILS_LOADER_ID);
                if (loader == null) {
                    loaderManager.initLoader(MOVIE_DETAILS_LOADER_ID, bundle, this);
                } else {
                    loaderManager.restartLoader(MOVIE_DETAILS_LOADER_ID, bundle, this);
                }

                mDetailIndicatorProgressBar.setVisibility(View.VISIBLE);
                NetworkUtils.getInstance().api().getMovieDetails(mMovieId, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MovieDetails>() {
                            @Override
                            public void onSuccess(MovieDetails movieDetails) {
                                mDetailIndicatorProgressBar.setVisibility(View.INVISIBLE);
                                if(movieDetails!= null){
                                    success(movieDetails);
                                }else{
                                    error();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mDetailIndicatorProgressBar.setVisibility(View.INVISIBLE);
                                error();
                            }
                        });
                //Check if this movie is already set as favourite.
                //isMovieFavourite();
            }else{
                //Sort type is favourite so do not make web service call
                //Rather make a query to local data base
                LoaderManager loaderManager = getSupportLoaderManager();
                Loader<MovieDetails> loader = loaderManager.getLoader(MOVIE_DETAILS_FROM_SQL_LOADER_ID);
                if (loader == null) {
                    loaderManager.initLoader(MOVIE_DETAILS_FROM_SQL_LOADER_ID, null, this);
                } else {
                    loaderManager.restartLoader(MOVIE_DETAILS_FROM_SQL_LOADER_ID, null, this);
                }
            }
        }



    }
    private void isMovieFavourite(){
        // Loader to Check if movie is already set as favourite or not
        mMovieFavourite = new MovieFavourite();
        LoaderManager loaderManager = getSupportLoaderManager();
        if(loaderManager.getLoader(QUERY_MOVIE_FAVOURITE_LOADER_ID) == null){
            loaderManager.initLoader(QUERY_MOVIE_FAVOURITE_LOADER_ID,null,mMovieFavourite);
        }else{
            loaderManager.restartLoader(QUERY_MOVIE_FAVOURITE_LOADER_ID,null,mMovieFavourite);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_DETAILS, mMovieDetails);
        outState.putBoolean(MOVIE_FAVOURITE,isFavourite);
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
    public Loader<MovieDetails> onCreateLoader(final int id, final Bundle args) {
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
                    if(id != MOVIE_DETAILS_FROM_SQL_LOADER_ID) {
                        String movieDetailsURL = args.getString(MOVIE_DETAILS_URL);
                        String movieTrailerURL = args.getString(MOVIE_TRAILER_URL);
                        String movieReviewURL = args.getString(MOVIE_REVIEWS_URL);
                        Log.d(TAG, "Details url is " + movieDetailsURL);
                        Log.d(TAG, "Trailer url is " + movieTrailerURL);
                        Log.d(TAG, "Review url is " + movieReviewURL);
                        //Get Movie Details
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(new URL(movieDetailsURL), mIdlingResource);

                        //Get Movie Trailer
                        String jsonMovieTrailerResponse = NetworkUtils
                                .getResponseFromHttpUrl(new URL(movieTrailerURL), mIdlingResource);

                        //Get Movie Review
                        String jsonMovieReviewResponse = NetworkUtils
                                .getResponseFromHttpUrl(new URL(movieReviewURL), mIdlingResource);


//                        MovieDetails movieDetails = GetMovieJsonUtils
//                                .getMovieDetailsFromJson(MovieDetailsActivity.this, jsonMovieResponse, jsonMovieTrailerResponse, jsonMovieReviewResponse);
                        return movieDetails;
                    }else{
                        Uri uri = MovieContract.MoviewEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build();
                        Cursor cursor = getContentResolver().query(uri,
                                null,
                                null,
                                null,
                                null);

                        if(cursor != null && cursor.moveToFirst()){
                            MovieDetails movieDetails = GetMovieJsonUtils.getMoviesDetailsFromSql(MovieDetailsActivity.this,
                                    cursor.getString(cursor.getColumnIndex(MovieContract.MoviewEntry.MOVIE_JSON_STRING)));
                            return movieDetails;

                        }else{
                            return null;
                        }

                    }

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
        if(movieDetails!= null){
            if(loader.getId() == MOVIE_DETAILS_FROM_SQL_LOADER_ID){
                setResetFavourite(true);
            }
            success(movieDetails);
        }else{
            error();
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieDetails> loader) {

    }

    @SuppressLint("RestrictedApi")
    private void success(MovieDetails movieDetails){
        mMovieDetails = movieDetails;
        String backdropPath = NetworkUtils.MOVIE_POSTER_ORIGINAL_BASE_URL + movieDetails.getMovieOriginalPoster();
        mFabFloatingActionButton.setVisibility(View.VISIBLE);
        mDetailErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMovieDetailsCollapsingToolbar.setTitle(movieDetails.getOriginalTitle());
        Picasso.with(this).load(backdropPath).into(mBackDropImageView);
        createTabBar(movieDetails);
    }

    private void createTabBar(MovieDetails movieDetails){
        tabLayout.addTab(tabLayout.newTab().setText(R.string.details_tab_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.trailer_tab_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.review_tab_title));
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


    private void error(){
        runOnUiThread(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                mDetailErrorMessageTextView.setVisibility(View.VISIBLE);
                mFabFloatingActionButton.setVisibility(View.INVISIBLE);

            }
        });

    }
    public void onClickedFavourite(View view){
        int loaderId = (isFavourite) ? FavourireMovieLoaderUtil.REMOVE_MOVIE_FAVOURITE_LOADER_ID : FavourireMovieLoaderUtil.INSERT_MOVIE_FAVOURITE_LOADER_ID;
        Gson gson = new Gson();
        String movieDetails = gson.toJson(mMovieDetails);
        FavourireMovieLoaderUtil favourireMovieLoaderUtil = new FavourireMovieLoaderUtil(getApplicationContext(),mMovieId,movieDetails);
        getSupportLoaderManager().initLoader(loaderId,null,favourireMovieLoaderUtil);
        setResetFavourite(!isFavourite);

    }
    public class MovieFavourite implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {
                Cursor cursor = null;
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(cursor != null){
                        deliverResult(cursor);
                    }else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    Uri queryUri = MovieContract.MoviewEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build();
                    return getContentResolver().query(queryUri,
                            null,
                            null,
                            null,
                            null);
                }

                @Override
                public void deliverResult(Cursor data) {
                    super.deliverResult(data);
                    if(data != null){
                        cursor = data;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if(data != null && data.getCount() > 0){
                    setResetFavourite(true);
                }else{
                    setResetFavourite(false);
                }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public void setResetFavourite(boolean favourite){
        isFavourite = favourite;
        if(isFavourite){
            mFabFloatingActionButton.setImageResource(R.drawable.ic_favorite_24dp);
        }else {
            mFabFloatingActionButton.setImageResource(R.drawable.ic_favorite_border_24dp);
        }
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {;
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
