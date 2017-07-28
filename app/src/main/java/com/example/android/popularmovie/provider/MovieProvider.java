package com.example.android.popularmovie.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovie.utilities.MovieDBHelper;

/**
 * Created by surajitbiswas on 7/23/17.
 */

public class MovieProvider extends ContentProvider {

    private MovieDBHelper mMoviewDBHelper;
    private static UriMatcher sUrimatcher = getUriMatcher();
    private static final int WITHOUT_ID = 100;
    private static final int WITH_ID = 101;
    private static UriMatcher getUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORIRY, MovieContract.PATH,WITHOUT_ID);
        uriMatcher.addURI(MovieContract.AUTHORIRY, MovieContract.PATH + "/#",WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMoviewDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase mDb = mMoviewDBHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sUrimatcher.match(uri)) {
            case WITHOUT_ID:
                cursor = mDb.query(MovieContract.MoviewEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                if(cursor == null){
                    throw new android.database.SQLException("Not able to query " + uri.toString());
                }
                break;
            case WITH_ID:
                String mMovieId = uri.getPathSegments().get(1);
                String mSelection = MovieContract.MoviewEntry.MOVIE_ID + "=?";
                String []mSelectionArgs = new String[]{mMovieId};
                cursor = mDb.query(MovieContract.MoviewEntry.TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);

                if(cursor == null){
                    throw new android.database.SQLException("Not able to find favourite movie " + uri.toString());
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri " + uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase mDB = mMoviewDBHelper.getWritableDatabase();
        Uri insertUri = null;
        switch (sUrimatcher.match(uri)){
            case WITHOUT_ID :
                long id = mDB.insert(MovieContract.MoviewEntry.TABLE_NAME,null,contentValues);
                if(id > 0) {
                    insertUri = ContentUris.withAppendedId(MovieContract.MoviewEntry.CONTENT_URI, id);
                }else{
                    throw new android.database.SQLException("Not able to insert " + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri " + uri.toString());

        }
        getContext().getContentResolver().notifyChange(insertUri,null);
        mDB.close();
        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase mDB = mMoviewDBHelper.getWritableDatabase();
        int id = 0;
        switch (sUrimatcher.match(uri)){
            case WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                String mSselection = MovieContract.MoviewEntry.MOVIE_ID + "=?";
                String []mSelectionArgs = new String[]{movieId};
                id = mDB.delete(MovieContract.MoviewEntry.TABLE_NAME,mSselection,mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
