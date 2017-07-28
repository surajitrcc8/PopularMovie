package com.example.android.popularmovie.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by surajitbiswas on 7/23/17.
 */

public class MovieContract {

    public static final String AUTHORIRY = "com.example.android.popularmovie.data";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORIRY);
    public static final String PATH = "movie";

    public static class MoviewEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build();
        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movieid";
        public static final String MOVIE_JSON_STRING = "json";
    }
}
