package com.example.theschulk.themoviewarehouse.Data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class CurosorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_MOVIE_PARAM = "param1";
    private static final int FAVORITE_LOADER_ID = 3;
    private String mMovieIdString;

    public interface onDataPass{
        public void onDataPass(Boolean movieExists, int rowId, String[][] movieData);
    }

    onDataPass dataPasser;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (onDataPass) context;
    }

    public CurosorFragment() {
    }

    public static CurosorFragment newInstance(String param1) {
        CurosorFragment fragment = new CurosorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieIdString = getArguments().getString(ARG_MOVIE_PARAM);
        }

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<Cursor> favoriteLoader = loaderManager.getLoader(FAVORITE_LOADER_ID);
        if(favoriteLoader == null){
            loaderManager.initLoader(FAVORITE_LOADER_ID, null, this).forceLoad();
        }
        else {
            loaderManager.restartLoader(FAVORITE_LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity(),MovieDbContract.MovieDbEntry.CONTENT_URI,
                null,
                MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID,
                null,
                null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String[][] favoriteMovieData;
        int Counter = 0;
            if(data != null) {
                int dataCount = data.getCount();
                String[] originalTitle = new String[dataCount];
                String[]posterPath = new String[dataCount];
                String[] overview = new String[dataCount];
                String[] voteAverage = new String[dataCount];
                String[]releaseDate = new String[dataCount];
                String[] id = new String[dataCount];
                if (data.moveToFirst()) {
                    do {
                        String currentMovieId = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID));
                        int currentRowId = data.getInt(data.getColumnIndex(MovieDbContract.MovieDbEntry._ID));
                        String currentTrailer = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_TRAILER_ID));
                        String currentSynopsis = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_SYNOPSIS));
                        String currentReleaseDate = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_RELEASE_DATE));
                        String currentTitle = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_TITLE));
                        String currentUserRating = data.getString(data.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_USER_RATING));

                        originalTitle[Counter] = currentTitle;
                        posterPath[Counter] = currentTrailer;
                        overview[Counter] = currentSynopsis;
                        voteAverage[Counter] = currentUserRating;
                        releaseDate[Counter] = currentReleaseDate;
                        id[Counter] = currentMovieId;

                        favoriteMovieData = new String[][]{originalTitle,posterPath,overview,voteAverage,releaseDate, id};

                        Counter++;

                        if (currentMovieId.equals(mMovieIdString)) {
                            dataPasser.onDataPass(true, currentRowId, favoriteMovieData);
                            data.close();
                            return;
                        }
                    }
                    while (data.moveToNext());
                    data.close();
                    dataPasser.onDataPass(false, 0, favoriteMovieData);
                }
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
