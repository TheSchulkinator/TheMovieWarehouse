package com.example.theschulk.themoviewarehouse.Utilities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by gregs on 3/26/2017.
 */

public final class NetworkUtils {

    //Establish all of the strings needed to build a URL to contact TMDB

    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String YOUTUBE_BASE_URL = "https://youtube.com/watch";

    //TODO Add API key here
    private static final String API_KEY_QUERY = "";
    private static final String QUERY_LANGUAGE = "en-US";
    private static final String PATH_MOVIE = "movie";
    private static final String PATH_REVIEW = "reviews";
    private static final String PATH_TRAILERS = "videos";

    //Build a Uri then use that to build a URL that we will contact TMDB with
    public static URL buildUrl(String pathSortQuery){
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                appendEncodedPath(pathSortQuery).
                appendQueryParameter("api_key", API_KEY_QUERY).
                appendQueryParameter("language", QUERY_LANGUAGE).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static Intent[] youtubeTrailerIntent(String youtubeQuery){
        Uri builtUri  = Uri.parse(YOUTUBE_BASE_URL).buildUpon().
                appendQueryParameter("v", youtubeQuery).build();
        Intent webTrailerIntent = new Intent(Intent.ACTION_VIEW, builtUri);
        Intent appTrailerIntent = new Intent (Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeQuery));

        Intent[] trailerArray = new Intent[2];
        trailerArray[0] = appTrailerIntent;
        trailerArray[1] = webTrailerIntent;

        return trailerArray;
    }


    //Build an array of URL's that will be used to make the calls to the api for the detail page
    public static URL[] buildMovieDetailUrl (String movieDetail){
        Uri reviewBuiltUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                appendEncodedPath(PATH_MOVIE).
                appendEncodedPath(movieDetail).
                appendEncodedPath(PATH_REVIEW).
                appendQueryParameter("api_key", API_KEY_QUERY).
                appendQueryParameter("language", QUERY_LANGUAGE).build();

        Uri trailerBuiltUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                appendEncodedPath(PATH_MOVIE).
                appendEncodedPath(movieDetail).
                appendEncodedPath(PATH_TRAILERS).
                appendQueryParameter("api_key", API_KEY_QUERY).
                appendQueryParameter("language", QUERY_LANGUAGE).build();

        URL[] url = new URL[2];
        try {
            url[0] = new URL(reviewBuiltUri.toString());
            url[1] = new URL(trailerBuiltUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //Create a HTTPConnection method that will be called in an ASYNC task to fetch data from the API

    public static String getResponseFromHTTPUrl (URL url) throws IOException{

        HttpURLConnection tmdbUrlConnection = (HttpURLConnection) url.openConnection();

        try{

            InputStream tmdbInputStream = tmdbUrlConnection.getInputStream();

            Scanner tmdbScanner = new Scanner(tmdbInputStream);
            tmdbScanner.useDelimiter("\\A");

            boolean hasInput = tmdbScanner.hasNext();
            if (hasInput){
                return tmdbScanner.next();
            } else {
                return null;
            }
        } finally {
            tmdbUrlConnection.disconnect();
        }

    }

}
