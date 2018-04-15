package com.example.theschulk.themoviewarehouse.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gregs on 5/24/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 9;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Boolean Column 0 = false 1 = True

        final String CREATE_MOVIE_FAVORITE_TABLE = "CREATE TABLE " +
                MovieDbContract.MovieDbEntry.FAVORITE_MOVIE_TABLE + " (" +
                MovieDbContract.MovieDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieDbContract.MovieDbEntry.COLUMN_FAVORITE_BOOLEAN + " INTEGER NOT NULL DEFAULT 0, " +
                MovieDbContract.MovieDbEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieDbContract.MovieDbEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieDbContract.MovieDbEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MovieDbContract.MovieDbEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieDbContract.MovieDbEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL" +
                ");";

                db.execSQL(CREATE_MOVIE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDbContract.MovieDbEntry.FAVORITE_MOVIE_TABLE);
        onCreate(db);
    }
}
