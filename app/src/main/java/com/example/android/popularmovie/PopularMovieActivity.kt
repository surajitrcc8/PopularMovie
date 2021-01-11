package com.example.android.popularmovie

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingResource
import com.example.android.popularmovie.IdlingResource.SimpleIdlingResource
import com.example.android.popularmovie.adapter.PopularMovieAdapter
import com.example.android.popularmovie.model.Movie
import com.example.android.popularmovie.model.MovieDetails
import com.example.android.popularmovie.utilities.Network
import com.example.android.popularmovie.utilities.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import java.net.URL
import java.util.*

class PopularMovieActivity : AppCompatActivity(), PopularMovieAdapter.MovieBanerClickListener{

    private val TAG = PopularMovieActivity::class.simpleName

    //string key to store the movie list in bundle
    //so that we can prevent web service call when onCreate called for some reasons such as device rotate.
    private val MOVIE_LIST = "movielist"

    private lateinit var mMoviePosterProgressBar: ProgressBar

    //No of columns when in vertical
    private val VERTICAL_SPAN = 2

    //No of columns when in horizontal
    private val HORIZONTAL_SPAN = 3
    private var isOnstop = false
    private  var mIdlingResource: SimpleIdlingResource? = null

    companion object {

        //key to store user selected sort type
        const val SORT_BY = "sort"
        private var mSortType : Int = 0
        private lateinit var mPopularMovieAdapter: PopularMovieAdapter
        private lateinit var mMovieListRecyclerView: RecyclerView
        private lateinit var mErrorMessageTextView: TextView
        private var mMovieList: ArrayList<MovieDetails>? = null
        /**
         * This function get called from details page when movie is set to favourite
         * or unfavourite to refresh the favourite movie grid view
         * @param context Context
         * @param cursor This holds the favourite movie data set.
         */
        fun upDateFavouriteMovieList(context: Context, cursor: Cursor?) {
            if (mSortType == R.id.mi_favourite) {
                var movies: ArrayList<MovieDetails>? = null
                if (cursor != null && cursor.count > 0) {
                    movies = ArrayList(cursor.count)
                    cursor.moveToPosition(0)
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
                    } while (cursor.moveToNext())
                    mPopularMovieAdapter.setItems(movies)
                    mMovieListRecyclerView.setVisibility(View.VISIBLE)
                    mErrorMessageTextView.setVisibility(View.INVISIBLE)
                    mMovieList = movies
                } else {
                    mPopularMovieAdapter.setItems(null)
                    mErrorMessageTextView.setText(context.getString(R.string.no_favourite_movie))
                    mMovieListRecyclerView.setVisibility(View.INVISIBLE)
                    mErrorMessageTextView.setVisibility(View.VISIBLE)
                    mMovieList = null
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_movie)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        }
        mMovieListRecyclerView = findViewById<View>(R.id.rv_movie_list) as RecyclerView
        mMoviePosterProgressBar = findViewById<View>(R.id.pb_movie_poster) as ProgressBar
        mErrorMessageTextView = findViewById<View>(R.id.tv_error_message) as TextView
        mPopularMovieAdapter = PopularMovieAdapter(this, this)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mMovieListRecyclerView.setLayoutManager(GridLayoutManager(this, VERTICAL_SPAN))
        } else {
            mMovieListRecyclerView.setLayoutManager(GridLayoutManager(this, HORIZONTAL_SPAN))
        }
        mMovieListRecyclerView.setAdapter(mPopularMovieAdapter)
        //check if onCreate getting called due to some lifecycle change
        //check if onCreate getting called due to some lifecycle change
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST)) {
            val sortBy = if (savedInstanceState.containsKey(SORT_BY)) savedInstanceState.getInt(SORT_BY) else -1
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST)
            mPopularMovieAdapter.setItems(mMovieList)
            if (sortBy == R.id.mi_favourite) {
                GetMovie(sortBy)
            } else if (mMovieList == null) {
                //There is no movie list list. May be device is offline so try to fetch it again.
                GetMovie(sortBy)
            }
        } else {
            //First app launch
            GetMovie(R.id.mi_popularity)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SORT_BY, mSortType)
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        GetMovie(item.itemId)
        return true
    }

    override fun onClickListener(movieId: Int) {
        Log.d(TAG, "Movie id is $movieId")
        val intent = Intent(this@PopularMovieActivity, MovieDetailsActivity::class.java)
        intent.putExtra(getString(R.string.movie_id), movieId)
        intent.putExtra(SORT_BY, mSortType)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        isOnstop = true
    }

    /**
     * This get the url based on user selection and then called the loader to load list of movies
     *
     * @param sortType This represent the user selected soty type (popularity/top rated)
     */
    fun GetMovie(sortType: Int) {
        isOnstop = false
        mSortType = sortType
        mPopularMovieAdapter.setItems(null)
        when (sortType) {
            R.id.mi_popularity -> {
                mMoviePosterProgressBar.visibility = View.VISIBLE

                Network.movieClient.getPopularMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Movie?>() {
                            override fun onSuccess(movie: Movie?) {
                                updateUI(movie)
                            }
                            override fun onError(e: Throwable) {
                                mMoviePosterProgressBar.visibility = View.INVISIBLE
                                errorResponse()
                            }
                        })
            }
            R.id.mi_top_rated -> Network.movieClient.getTopRatedMovie(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Movie?>() {
                        
                        override fun onSuccess(movie: Movie?) {
                            updateUI(movie)
                        }

                        override fun onError(e: Throwable) {
                            mMoviePosterProgressBar.visibility = View.INVISIBLE
                            errorResponse()
                        }

                        
                    })
        }
    }

    private fun updateUI(movie: Movie?) {
        mMoviePosterProgressBar.visibility = View.INVISIBLE
        if (movie != null) {
            mPopularMovieAdapter.setItems(movie.results)
            mMovieList = movie.results
            successResponse()
        } else {
            errorResponse()
        }
        mIdlingResource?.setIdleState(true)
    }

    /**
     * This function handle the success response of fetch movie list web service.
     */
    fun successResponse() {
        mMovieListRecyclerView.visibility = View.VISIBLE
        mErrorMessageTextView.visibility = View.GONE
    }

    /**
     * This function handle the error response of fetch movie list web service.
     */
    fun errorResponse() {
        runOnUiThread {
            if (mSortType == R.id.mi_favourite) {
                mErrorMessageTextView.setText(getString(R.string.no_favourite_movie))
            } else {
                mErrorMessageTextView.setText(getString(R.string.error_message))
            }
            mMoviePosterProgressBar.visibility = View.INVISIBLE
            mMovieListRecyclerView.setVisibility(View.INVISIBLE)
            mErrorMessageTextView.setVisibility(View.VISIBLE)
            mMovieList = null
        }
    }

    /**
     * Only called from test, creates and returns a new [SimpleIdlingResource].
     */
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {

        return mIdlingResource?:SimpleIdlingResource()
    }

    @VisibleForTesting
    fun getDetailsActivity(): AppCompatActivity {
        return MovieDetailsActivity()
    }
}