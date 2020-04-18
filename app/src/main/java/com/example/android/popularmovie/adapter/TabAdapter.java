package com.example.android.popularmovie.adapter;



import com.example.android.popularmovie.model.MovieDetails;
import com.example.android.popularmovie.tabfragment.DetailsTabFragment;
import com.example.android.popularmovie.tabfragment.ReviewTabFragment;
import com.example.android.popularmovie.tabfragment.TrailerTabFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by surajitbiswas on 7/20/17.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    MovieDetails movieDetails;

    public TabAdapter(FragmentManager fm, int tabNos, MovieDetails movieDetails) {
        super(fm);
        this.mNumOfTabs = tabNos;
        this.movieDetails = movieDetails;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                DetailsTabFragment detailsTabFragment = new DetailsTabFragment();
                detailsTabFragment.setMovieDetails(movieDetails);
                return detailsTabFragment;
            case 1:
                TrailerTabFragment trailerTabFragment = new TrailerTabFragment();
                trailerTabFragment.setMovieDetails(movieDetails);
                return trailerTabFragment;
            case 2:
                ReviewTabFragment reviewTabFragment = new ReviewTabFragment();
                reviewTabFragment.setMovieDetails(movieDetails);
                return reviewTabFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
