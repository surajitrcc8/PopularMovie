package com.example.android.popularmovie;

import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by surajitbiswas on 7/20/17.
 */



public class DetailsTabFragment extends Fragment {
    private MovieDetails movieDetails = null;
    private ImageView mMoviePosterImageView;
    private TextView mReleaseDateTextView;
    private TextView mDurationTextView;
    private RatingBar mRatingBar;
    private TextView mOverViewTextView;
    private LinearLayout mMovieDetailsLinearlayout;
    private NestedScrollView mDetailsContainerScrollView;

    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MovieDetailsActivity.MOVIE_DETAILS,movieDetails);
        super.onSaveInstanceState(outState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details,container,false);
        mMovieDetailsLinearlayout = (LinearLayout)view.findViewById(R.id.ll_movie_details);
        mMoviePosterImageView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        mReleaseDateTextView = (TextView)view.findViewById(R.id.tv_release_year);
        mDurationTextView = (TextView) view.findViewById(R.id.tv_duration);
        mRatingBar = (RatingBar) view.findViewById(R.id.rb_rating);
        mOverViewTextView = (TextView)view.findViewById(R.id.tv_overview);
        if(savedInstanceState != null && savedInstanceState.containsKey(MovieDetailsActivity.MOVIE_DETAILS)){
            this.movieDetails = savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
        }

        createDetailsTab();
        return view;
    }
    public void createDetailsTab(){
        if(this.movieDetails != null) {
            mMovieDetailsLinearlayout.setVisibility(View.VISIBLE);
            String bannerPath = NetworkUtils.MOVIE_POSTER_BASE_URL + movieDetails.getMoviePoster();
            Picasso.with(getContext()).load(bannerPath).into(mMoviePosterImageView);
            mReleaseDateTextView.setText(movieDetails.getReleaseDate().split("-")[0]);
            mDurationTextView.setText(movieDetails.getDuration() + "min");
            mRatingBar.setRating((float) movieDetails.getUserRating());
            mOverViewTextView.setText(movieDetails.getOverview());
        }
    }
}
