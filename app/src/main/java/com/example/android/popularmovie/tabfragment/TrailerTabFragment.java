package com.example.android.popularmovie.tabfragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovie.MovieDetailsActivity;
import com.example.android.popularmovie.R;
import com.example.android.popularmovie.adapter.TrailerAdapter;
import com.example.android.popularmovie.model.MovieDetails;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by surajitbiswas on 7/20/17.
 */

public class TrailerTabFragment extends Fragment implements TrailerAdapter.TrailerListItemClickListener {

    private MovieDetails movieDetails = null;
    private RecyclerView mTrailerListRecyclerView;
    private static final String TAG = TrailerTabFragment.class.getSimpleName();
    private TrailerAdapter mTrailerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mNoTrailerTextView;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MovieDetailsActivity.MOVIE_DETAILS,movieDetails);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailer,container,false);
        mTrailerListRecyclerView = (RecyclerView) view.findViewById(R.id.rv_trailer_list);
        mNoTrailerTextView = (TextView)view.findViewById(R.id.tv_no_trailer);
        if(savedInstanceState != null && savedInstanceState.containsKey(MovieDetailsActivity.MOVIE_DETAILS)){
            this.movieDetails = (MovieDetails)savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
        }
        if(this.movieDetails != null) {
            mTrailerAdapter = new TrailerAdapter(this);
            mLinearLayoutManager = new LinearLayoutManager(container.getContext());
            mTrailerListRecyclerView.setLayoutManager(mLinearLayoutManager);
            mTrailerListRecyclerView.setAdapter(mTrailerAdapter);
            if(this.movieDetails.getTrailers() != null && this.movieDetails.getTrailers().size() > 0){
                mTrailerAdapter.setmTrailers(this.movieDetails.getTrailers());
                mTrailerListRecyclerView.setVisibility(View.VISIBLE);
                mNoTrailerTextView.setVisibility(View.INVISIBLE);
            }else{
                mTrailerListRecyclerView.setVisibility(View.INVISIBLE);
                mNoTrailerTextView.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }


    @Override
    public void onClickedTrailerListItem(String key) {
        String youTubeBaseUrl = "https://www.youtube.com";
        String path = "watch";
        String queryParam = "v";
        Uri youtubeUri = Uri.parse(youTubeBaseUrl);
        youtubeUri = youtubeUri.buildUpon().appendPath(path).appendQueryParameter(queryParam,key).build();
        Log.d(TAG,"Youtube url is " + youtubeUri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(youtubeUri);
        if(intent.resolveActivity(getContext().getPackageManager())!=null){
            startActivity(intent);
        }else{
            Toast.makeText(getContext(), getString(R.string.no_app_available), Toast.LENGTH_LONG).show();
        }

    }
    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }
}
