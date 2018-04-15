package com.example.theschulk.themoviewarehouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.theschulk.themoviewarehouse.Data.CurosorFragment;
import com.example.theschulk.themoviewarehouse.Data.MovieDbHelper;
import com.example.theschulk.themoviewarehouse.Utilities.NetworkUtils;
import com.example.theschulk.themoviewarehouse.Utilities.TMDBJsonUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ThumbnailAdapter.ThumbnailAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[][]>, CurosorFragment.onDataPass{

    private RecyclerView mMovieImageRecyclerView;
    private TextView mErrorMessageTV;
    private ProgressBar mLoadingBar;
    private ThumbnailAdapter mThumbnailAdapter;
    public static final int MOVIE_LOADER_ID = 8;
    private final String SETTINGS_BUNDLE_EXTRA = "SettingsBundleExtra";
    private SQLiteDatabase favoriteDb;
    private String[][] onSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMovieImageRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_thumb_nail);
        mErrorMessageTV = (TextView) findViewById(R.id.tv_error_message);
        mLoadingBar = (ProgressBar) findViewById(R.id.pb_loading_bar);
        mThumbnailAdapter = new ThumbnailAdapter(this);
        mMovieImageRecyclerView.setAdapter(mThumbnailAdapter);
        GridLayoutManager gridLayout = new GridLayoutManager(this, numberOfColumns());
        mMovieImageRecyclerView.setLayoutManager(gridLayout);
        mMovieImageRecyclerView.setHasFixedSize(true);

        MovieDbHelper movieDbHelper = new MovieDbHelper(this);
        favoriteDb = movieDbHelper.getWritableDatabase();

        if(savedInstanceState == null) {
            setupSharedPreferences();
        }else {
            String[][] dataString = (String[][]) savedInstanceState.getSerializable("keyData");
            onSaveData = dataString;
            mThumbnailAdapter.setTmdbData(dataString);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(getString(R.string.on_state_key));
            mMovieImageRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("keyData", onSaveData);
        outState.putParcelable(getString(R.string.on_state_key), mMovieImageRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void setupSharedPreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String spValue = sp.getString(getString(R.string.sort_key), getString(R.string.top_rated_value));
        if(spValue.equals(getString(R.string.favorite_value)))
        {
            Fragment movieCursorFragment = CurosorFragment.newInstance("String");
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(movieCursorFragment, "String").commit();
        }else{
        loadSortFromPreferences(sp);
        }
    }

    private void loadSortFromPreferences(SharedPreferences sp){
        loadMovieData(sp.getString(getString(R.string.sort_key),
                getString(R.string.top_rated_value)));
    }

    public void loadMovieData(String settingString){
        showRecyclerViewImage();
        Bundle sortPreferenceBundle = new Bundle();
        sortPreferenceBundle.putString(SETTINGS_BUNDLE_EXTRA, settingString);
        Bundle queryBundle = settingsSortBundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[][]> TMDBLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if(TMDBLoader == null){
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad();
        }
        else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad();
        }
    }

    @Override
    public void onClick(String[] movieDetails) {
        Context context = this;

        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartMovieDetailActivity = new Intent(context, destinationClass);
        intentToStartMovieDetailActivity.putExtra(Intent.EXTRA_TEXT, movieDetails);
        startActivity(intentToStartMovieDetailActivity);
    }

    @Override
    public Loader<String[][]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[][]>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){return;}
                mLoadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public String[][] loadInBackground() {
                String searchQueryString = args.getString(SETTINGS_BUNDLE_EXTRA);
                if(searchQueryString  == null){
                    return null;
                }
                URL tmdbRequestUrl = NetworkUtils.buildUrl(searchQueryString);
                try{
                    String jsonTmdbResponse = NetworkUtils.getResponseFromHTTPUrl(tmdbRequestUrl);

                    String[][] parsedJsonTmdbResults = TMDBJsonUtils.
                            getMovieStringsFromJson(MainActivity.this, jsonTmdbResponse);
                    return parsedJsonTmdbResults;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String[][] movieData) {
        mLoadingBar.setVisibility(View.INVISIBLE);
        if(movieData != null){
            onSaveData = movieData;
            showRecyclerViewImage();
            mThumbnailAdapter.setTmdbData(movieData);
        }else {
            showErrorMessageView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[][]> loader) {

    }


    private void showRecyclerViewImage(){
        mMovieImageRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTV.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessageView(){
        mMovieImageRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTV.setVisibility(View.VISIBLE);
    }


    //Will retrieve the current settings state and send a bundle
    public Bundle settingsSortBundle(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String settingsSortString = sharedPreferences.getString(getString(R.string.sort_key),
                getString(R.string.top_rated_value));

        Bundle sortPreferenceBundle = new Bundle();
        sortPreferenceBundle.putString(SETTINGS_BUNDLE_EXTRA, settingsSortString);
        return  sortPreferenceBundle;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_by_meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectId = item.getItemId();
        SharedPreferences sortSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sortEditor;
        //make a switch statement to toggle between top rate and popular shows when menu item selected.
        switch (selectId){
            case R.id.sort_popular:
                sortEditor = sortSharedPreference.edit();
                sortEditor.putString(getString(R.string.sort_key), getString(R.string.popular_value));
                sortEditor.commit();
                loadMovieData(getString(R.string.popular_value));
                break;
            case R.id.sort_top_rated:
                sortEditor = sortSharedPreference.edit();
                sortEditor.putString(getString(R.string.sort_key), getString(R.string.top_rated_value));
                sortEditor.commit();
                loadMovieData(getString(R.string.top_rated_value));
                break;
            case R.id.sort_favorite:
                sortEditor = sortSharedPreference.edit();
                sortEditor.putString(getString(R.string.sort_key), getString(R.string.favorite_value));
                sortEditor.commit();
                Fragment movieCursorFragment = CurosorFragment.newInstance("String");
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(movieCursorFragment, "String").commit();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //method to return the number of columns for the gridlayout
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    @Override
    public void onDataPass(Boolean movieExists, int rowId, String[][] MovieIds) {
        mThumbnailAdapter.setTmdbData(MovieIds);
    }
}
