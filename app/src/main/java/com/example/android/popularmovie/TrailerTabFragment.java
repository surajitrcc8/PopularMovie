package com.example.android.popularmovie;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.android.popularmovie.adapter.TrailerAdapter;
import com.example.android.popularmovie.model.MovieDetails;

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
        View view = inflater.inflate(R.layout.fragment_trailer,container,false);
        mTrailerListRecyclerView = (RecyclerView) view.findViewById(R.id.rv_trailer_list);
        mNoTrailerTextView = (TextView)view.findViewById(R.id.tv_no_trailer);
        if(savedInstanceState != null && savedInstanceState.containsKey(MovieDetailsActivity.MOVIE_DETAILS)){
            Log.d(TAG,"FOUND");
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
            Toast.makeText(getContext(), "There is no application installed which can play this trailer", Toast.LENGTH_LONG).show();
        }

    }
}
