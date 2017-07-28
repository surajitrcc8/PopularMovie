package com.example.android.popularmovie.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovie.PopularMovieActivity;
import com.example.android.popularmovie.provider.MovieContract;

/**
 * Created by surajitbiswas on 7/23/17.
 */

public class FavourireMovieLoaderUtil implements LoaderManager.LoaderCallbacks {

    public Context mContext;
    public int mMovieId;
    public String  mMovieDetails;
    public static final int REMOVE_MOVIE_FAVOURITE_LOADER_ID = 13;
    public static final int INSERT_MOVIE_FAVOURITE_LOADER_ID = 14;

    public FavourireMovieLoaderUtil(Context context, int mMovieId,String  mMovieDetails) {
        this.mContext = context;
        this.mMovieId = mMovieId;
        this.mMovieDetails = mMovieDetails;
    }


    @Override
    public Loader onCreateLoader(final int id, Bundle args) {

        return new AsyncTaskLoader(mContext) {
            Cursor cursor = null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                switch(id) {
                    case REMOVE_MOVIE_FAVOURITE_LOADER_ID:
                    case INSERT_MOVIE_FAVOURITE_LOADER_ID:
                        if(cursor != null){
                            deliverResult(cursor);
                        }else{
                            forceLoad();
                        }
                        break;

                }

            }
            @Override
            public Object loadInBackground() {
                switch(id) {
                    case REMOVE_MOVIE_FAVOURITE_LOADER_ID:
                        //Movie is already favourite so remove it for favourite
                        Uri deleteUri = ContentUris.withAppendedId(MovieContract.MoviewEntry.CONTENT_URI, mMovieId);
                        int deletedId = mContext.getContentResolver().delete(deleteUri, null, null);
                        if (deletedId > 0) {
                            Cursor cursor = mContext.getContentResolver().query(MovieContract.MoviewEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return cursor;
                        }else{
                        return null;
                    }

                    case INSERT_MOVIE_FAVOURITE_LOADER_ID:

                        //Make it favourite;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieContract.MoviewEntry.MOVIE_ID, mMovieId);
                        contentValues.put(MovieContract.MoviewEntry.MOVIE_JSON_STRING, mMovieDetails);
                        mContext.getContentResolver().insert(MovieContract.MoviewEntry.CONTENT_URI, contentValues);
                        Cursor cursor = mContext.getContentResolver().query(MovieContract.MoviewEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null,
                                null);
                        return cursor;
                }
                return null;
            }
            @Override
            public void deliverResult(Object data) {
                super.deliverResult(data);
                switch(id) {
                    case REMOVE_MOVIE_FAVOURITE_LOADER_ID:
                    case INSERT_MOVIE_FAVOURITE_LOADER_ID:

                        cursor = (Cursor)data;
                        break;
                }

            }

        };
    }



    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(data != null){
            switch(loader.getId()){
                case REMOVE_MOVIE_FAVOURITE_LOADER_ID:
                case INSERT_MOVIE_FAVOURITE_LOADER_ID:
                    if(data instanceof Cursor){
                        PopularMovieActivity.upDateFavouriteMovieList(mContext,(Cursor)data);
                    }
                    break;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
