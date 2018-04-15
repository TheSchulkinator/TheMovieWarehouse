package com.example.theschulk.themoviewarehouse.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by gregs on 6/7/2017.
 */

public class FavoriteContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    private MovieDbHelper movieDbHelper;
    private  static final UriMatcher sUriMatcher = buildUriMatcher();
    private final String TABLE_NAME = MovieDbContract.MovieDbEntry.FAVORITE_MOVIE_TABLE;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_FAVORITES + "/*", FAVORITES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case FAVORITES:
                returnCursor = db.query(TABLE_NAME, projection, selection,selectionArgs,
                        null, null, sortOrder);
                break;
            case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                returnCursor = db.query(TABLE_NAME, projection, mSelection, mSelectionArgs,
                        null, null, sortOrder);
            default:
                   throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match){
            case FAVORITES:
                long id = db.insert(TABLE_NAME ,null, values);
                if (id > 0 ){
                    returnUri = ContentUris.withAppendedId(MovieDbContract.MovieDbEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numberOfDeletedItems;

       switch (match){
           case FAVORITES_WITH_ID:
               String id = uri.getPathSegments().get(1);
               String mSelection = "_id=?";
               String[] mSelectionArgs = new String[] {id};

               numberOfDeletedItems = db.delete(TABLE_NAME, mSelection, mSelectionArgs);
               break;
           default:
               throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        return numberOfDeletedItems;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
