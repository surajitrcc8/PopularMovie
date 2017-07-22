package com.example.android.popularmovie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.popularmovie.R;
import com.example.android.popularmovie.model.MovieDetails;

/**
 * Created by surajitbiswas on 7/20/17.
 */

public class ReviewTabFragment extends Fragment {
    MovieDetails  movieDetails = null;
    private LinearLayout mReviewLinearLayout;
    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review,container,false);
       // mReviewLinearLayout = (LinearLayout)view.findViewById(R.id.ll_review_container);
        fillTheReview();
        if(this.movieDetails != null){

        }
        return view;
    }
    private void fillTheReview(){

    }
}
