package com.example.android.popularmovie.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovie.provider.MovieContract;

/**
 * Created by surajitbiswas on 7/23/17.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATA_BASE_NAME = "movie.db";
    private static final int DATA_BASE_VERSION = 1;
    public MovieDBHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + MovieContract.MoviewEntry.TABLE_NAME + " (" +
                MovieContract.MoviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MoviewEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MoviewEntry.MOVIE_JSON_STRING + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + MovieContract.MoviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
