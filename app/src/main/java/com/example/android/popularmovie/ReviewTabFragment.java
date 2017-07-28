package com.example.android.popularmovie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovie.adapter.ReviewAdapter;
import com.example.android.popularmovie.model.MovieDetails;

/**
 * Created by surajitbiswas on 7/20/17.
 */

public class ReviewTabFragment extends Fragment {
    MovieDetails  movieDetails = null;
    private RecyclerView mReviewListRecyclerView;
    private TextView mNoReviewTextView;
    private ReviewAdapter mReviewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = ReviewTabFragment.class.getSimpleName();



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
        View view = inflater.inflate(R.layout.fragment_review,container,false);
        //mReviewListRecyclerView = (RecyclerView)view.findViewById(R.id.rv_review_list);
        mNoReviewTextView = (TextView)view.findViewById(R.id.tv_no_review);
        if(savedInstanceState != null && savedInstanceState.containsKey(MovieDetailsActivity.MOVIE_DETAILS)){
            Log.d(TAG,"FOUND");
            this.movieDetails = (MovieDetails)savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
        }
        if(this.movieDetails != null){
            if(this.movieDetails.getReviews() != null && this.movieDetails.getReviews().size() > 0){
//                mReviewAdapter = new ReviewAdapter(getContext());
//                mLinearLayoutManager = new LinearLayoutManager(getContext());
//                mReviewListRecyclerView.setLayoutManager(mLinearLayoutManager);
//                mReviewListRecyclerView.setAdapter(mReviewAdapter);
//                mReviewAdapter.setmReviews(this.movieDetails.getReviews());
//                mReviewListRecyclerView.setVisibility(View.INVISIBLE);
                mNoReviewTextView.setVisibility(View.VISIBLE);
            }else{
                Log.d(TAG,"Review size is " + this.movieDetails.getReviews().size());
                //mReviewListRecyclerView.setVisibility(View.INVISIBLE);
                mNoReviewTextView.setVisibility(View.VISIBLE);
            }

        }
        return view;
    }
    private void fillTheReview(){

    }
}
