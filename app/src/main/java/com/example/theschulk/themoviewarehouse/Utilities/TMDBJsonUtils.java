package com.example.theschulk.themoviewarehouse.Utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class TMDBJsonUtils {

    //Create a method that will take a json string and parse the data

    public  static String[][] getMovieStringsFromJson (Context context, String tmdbJsonString)
        throws JSONException{
        //Define the var's that I need to parse out of the JSON stream

        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String ERROR_CODE = "status_code";
        final String RESULT_LIST = "results";
        final String ID = "id";

        //Create the arrays that will hold the parsed data to be returned.
        String[] originalTitle = null;
        String[] posterPath  = null;
        String[] overview = null;
        String[] voteAverage = null;
        String[] releaseDate = null;
        String[] id = null;

        String[][] parsedTmdbData = null;

        //Check to see if an error status code was received instead of the needed data

        JSONObject tmdbJson = new JSONObject(tmdbJsonString);

        if(tmdbJson.has(ERROR_CODE)){
            int jsonErrorInt = tmdbJson.getInt(ERROR_CODE);
            return null;
            }

        JSONArray tmdbJsonArray = tmdbJson.getJSONArray(RESULT_LIST);

        originalTitle = new String[tmdbJsonArray.length()];
        posterPath = new String[tmdbJsonArray.length()];
        overview = new String[tmdbJsonArray.length()];
        voteAverage = new String[tmdbJsonArray.length()];
        releaseDate = new String[tmdbJsonArray.length()];
        id = new String[tmdbJsonArray.length()];

        for (int i = 0; i < tmdbJsonArray.length(); i++){

            //get a JSON object that represents each movie inside of the JSON array

            JSONObject indvidualMoveJsonObject = tmdbJsonArray.getJSONObject(i);

            //Store all of the data into the corresponding arrays
            originalTitle[i] = indvidualMoveJsonObject.getString(ORIGINAL_TITLE);
            posterPath[i] = indvidualMoveJsonObject.getString(POSTER_PATH);
            overview[i]= indvidualMoveJsonObject.getString(OVERVIEW);
            voteAverage[i] = indvidualMoveJsonObject.getString(VOTE_AVERAGE);
            releaseDate[i] = indvidualMoveJsonObject.getString(RELEASE_DATE);
            id[i] = indvidualMoveJsonObject.getString(ID);

        }

        parsedTmdbData = new String[][]{originalTitle,posterPath,overview,voteAverage,releaseDate, id};

        return parsedTmdbData;

    }







}

