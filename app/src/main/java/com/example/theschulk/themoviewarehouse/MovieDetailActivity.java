package com.example.theschulk.themoviewarehouse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.theschulk.themoviewarehouse.Data.CurosorFragment;
import com.example.theschulk.themoviewarehouse.Data.MovieDbContract;
import com.example.theschulk.themoviewarehouse.Utilities.MovieDetailTMDBJsonUtils;
import com.example.theschulk.themoviewarehouse.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Object>, TrailerRecyclerViewAdapter.TrailerOnClickHandler,
            CurosorFragment.onDataPass{

    private TextView mTextViewTitle;
    private ImageView mImageViewPoster;
    private TextView mTextViewOverview;
    private TextView mTextViewAverageRatingAndReleaseDate;
    private static final int REVIEW_LOADER_ID = 1;
    private static final int TRAILER_LOADER_ID = 2;
    private static final String BUNDLE_MOVIE_ID = "BundleMovieId";
    private MyReviewRecyclerViewAdapter mReviewAdapter;
    private RecyclerView mReviewRecycleView;
    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private RecyclerView mTrailerRecycleView;
    private ToggleButton mFavoriteButton;
    private String[] extraStringArray;
    private int movieFavoriteRowId;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie__detail_);

        mTextViewOverview = (TextView) findViewById(R.id.tv_synopsis);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        mTextViewAverageRatingAndReleaseDate = (TextView) findViewById(R.id.tv_date_vote);
        mImageViewPoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mReviewAdapter = new MyReviewRecyclerViewAdapter();
        mFavoriteButton = (ToggleButton) findViewById(R.id.tb_favorite_button);


        Intent intentThatStartedActivity = getIntent();

        if (intentThatStartedActivity.hasExtra(Intent.EXTRA_TEXT)) {
            extraStringArray = intentThatStartedActivity.
                    getStringArrayExtra(Intent.EXTRA_TEXT);

            //Store the movie id into a bundle and call loaders to get review and trailer
            String movieIdString  = extraStringArray[5];
            Bundle movieIdBundle = new Bundle();
            movieIdBundle.putString(BUNDLE_MOVIE_ID, movieIdString);

            Fragment movieCursorFragment = CurosorFragment.newInstance(movieIdString);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(movieCursorFragment, "String").commit();

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(REVIEW_LOADER_ID, movieIdBundle, this).forceLoad();
            loaderManager.initLoader(TRAILER_LOADER_ID, movieIdBundle, this).forceLoad();


            mTextViewTitle.setText(extraStringArray[0]);
            mTextViewOverview.setText(extraStringArray[2]);
            mTextViewAverageRatingAndReleaseDate.setText(getString(R.string.release_date) + ": " + extraStringArray[4] +
                    System.getProperty("line.separator")
                    + getString(R.string.user_rating) + ": " + extraStringArray[3]);

            //get poster path URL and place image with picasso
            String posterPathUri =  "http://image.tmdb.org/t/p/w342/" + extraStringArray[1];
            Uri uri = Uri.parse(posterPathUri);

            Context context = mImageViewPoster.getContext();

            Picasso.with(context).load(uri).into(mImageViewPoster);

            mTrailerRecycleView = (RecyclerView) findViewById(R.id.rv_trailer);
            mTrailerAdapter = new TrailerRecyclerViewAdapter(this);
            mTrailerRecycleView.setAdapter(mTrailerAdapter);
            LinearLayoutManager trailerLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                    false);
            mTrailerRecycleView.setLayoutManager(trailerLinearLayoutManager);

            mReviewRecycleView = (RecyclerView) findViewById(R.id.rv_review);
            mReviewAdapter = new MyReviewRecyclerViewAdapter();
            mReviewRecycleView.setAdapter(mReviewAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mReviewRecycleView.setLayoutManager(linearLayoutManager);

        }
    }



    @Override
    public android.support.v4.content.Loader<Object> onCreateLoader(int id, final Bundle args) {

        Context context = getApplicationContext();

        if(id == TRAILER_LOADER_ID) {
            return new AsyncTaskLoader<Object>(context) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                }

                @Override
                public String[] loadInBackground() {
                    String MovieIdSearchString = args.getString(BUNDLE_MOVIE_ID);
                    if (MovieIdSearchString == null) {
                        return null;
                    }
                    URL[] UrlArray = NetworkUtils.buildMovieDetailUrl(MovieIdSearchString);
                    URL trailerUrl = UrlArray[1];
                    try {
                        String trailerJsonString = NetworkUtils.getResponseFromHTTPUrl(trailerUrl);

                        if (trailerJsonString == null || trailerJsonString.isEmpty()) {
                            return null;
                        }

                        String[] trailerParsedJson = MovieDetailTMDBJsonUtils.
                                getMovieTrailerResults(MovieDetailActivity.this, trailerJsonString);
                        return trailerParsedJson;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }else if (id == REVIEW_LOADER_ID){
            return new AsyncTaskLoader<Object>(context) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                }

                @Override
                public String[][] loadInBackground() {
                    String MovieIdSearchString = args.getString(BUNDLE_MOVIE_ID);
                    if (MovieIdSearchString == null) {
                        return null;
                    }
                    URL[] UrlArray = NetworkUtils.buildMovieDetailUrl(MovieIdSearchString);
                    URL reviewUrl = UrlArray[0];
                    try {
                        String reviewJsonString = NetworkUtils.getResponseFromHTTPUrl(reviewUrl);

                        if (reviewJsonString == null || reviewJsonString.isEmpty()) {
                            return null;
                        }

                        String[][] trailerParsedJson = MovieDetailTMDBJsonUtils.
                                getMovieReviewResults(MovieDetailActivity.this, reviewJsonString);
                        return trailerParsedJson;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Object> loader, Object data) {
                switch (loader.getId()){
                    case REVIEW_LOADER_ID:
                        if (data != null){
                            String[][] parsedJsonReview = (String[][]) data;
                            mReviewAdapter.setReviewData(parsedJsonReview);
                        }
                        break;
                    case TRAILER_LOADER_ID:
                        if(data != null){
                            String[] parsedJsonTrailer = (String[]) data;
                            mTrailerAdapter.setTrailerData(parsedJsonTrailer);
                        }
                }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Object> loader) {

    }

    public void onClickAddFavorite(View view){
        if (extraStringArray.length == 0){
            return;
        }

        if(isFavorite == false){
            int movieIdInt = Integer.parseInt(extraStringArray[5]);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_FAVORITE_BOOLEAN, 1);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_RELEASE_DATE, extraStringArray[4]);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_TITLE, extraStringArray[0]);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID, movieIdInt);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_USER_RATING, extraStringArray[3]);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_TRAILER_ID, extraStringArray[1]);
            contentValues.put(MovieDbContract.MovieDbEntry.COLUMN_SYNOPSIS, extraStringArray[2]);
            Uri uri = getContentResolver().insert(MovieDbContract.MovieDbEntry.CONTENT_URI, contentValues);
        } else if(isFavorite == true){
            String deleteRow = String.valueOf(movieFavoriteRowId);
            Uri deleteUri = MovieDbContract.MovieDbEntry.CONTENT_URI.buildUpon().appendPath(deleteRow).build();
            getContentResolver().delete(deleteUri, null, null);
            Fragment movieCursorFragment = CurosorFragment.newInstance(extraStringArray[5]);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(movieCursorFragment, "String").commit();
        }
    }

    @Override
    public void onClick(String trailerDetails) {
        Intent[] youtubeArrayIntent = NetworkUtils.youtubeTrailerIntent(trailerDetails);
        Intent appYoutubeIntent = youtubeArrayIntent[0];
        Intent webYouTubeIntent = youtubeArrayIntent[1];

        if(appYoutubeIntent.resolveActivity(getPackageManager()) != null){
            startActivity(appYoutubeIntent);
        } else if(webYouTubeIntent.resolveActivity(getPackageManager()) != null){
            startActivity(webYouTubeIntent);
        }
    }

    @Override
    public void onDataPass(Boolean movieExists, int rowId, String[][] MovieIds) {
        mFavoriteButton.setChecked(movieExists);
        isFavorite = movieExists;
        movieFavoriteRowId = rowId;
    }
}
