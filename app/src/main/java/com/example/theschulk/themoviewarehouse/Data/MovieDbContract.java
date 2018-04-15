package com.example.theschulk.themoviewarehouse.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gregs on 5/24/2017.
 */

public class MovieDbContract {


    public static final String AUTHORITY = "com.example.theschulk.themoviewarehouse";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";


    public static final class MovieDbEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_FAVORITES).
                build();

        public static final String FAVORITE_MOVIE_TABLE = "favorites";
        public static final String COLUMN_FAVORITE_BOOLEAN = "favoriteBoolean";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TRAILER_ID = "trailerId";
        public static final String COLUMN_SYNOPSIS = "synopsis";
    }
}
