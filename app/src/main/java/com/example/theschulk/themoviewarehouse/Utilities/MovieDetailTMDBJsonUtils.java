package com.example.theschulk.themoviewarehouse.Utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gregs on 5/30/2017.
 */

public class MovieDetailTMDBJsonUtils {

    public static String[][] getMovieReviewResults (Context context, String tmdbJsonString)
    throws JSONException{
            /// /Define the JSON vars that are needed to parse out the trailer and Review for movie detail screen
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String RESULT_LIST = "results";


            //Create the arrays that will hold the data from the api call

            String[] author = null;
            String[] content = null;

            //Compiled arrays that hold the data for the calls
            String[][] reviewResults = null;

            JSONObject tmdbJson = new JSONObject(tmdbJsonString);
            JSONArray tmdbJsonArray = tmdbJson.getJSONArray(RESULT_LIST);

            author = new String[tmdbJsonArray.length()];
            content = new String[tmdbJsonArray.length()];

            for (int i = 0; i < tmdbJsonArray.length(); i++){
                JSONObject indvidualMoveJsonObject = tmdbJsonArray.getJSONObject(i);

                author[i] = indvidualMoveJsonObject.getString(AUTHOR);
                content[i] = indvidualMoveJsonObject.getString(CONTENT);
            }

            reviewResults = new String[][]{author,content};

        return reviewResults;
    }

    public static String[] getMovieTrailerResults (Context context, String tmdbJsonString)
    throws JSONException{
        //Trailers
        final String TRAILER_KEY = "key";
        final String RESULT_LIST = "results";

        JSONObject tmdbJson = new JSONObject(tmdbJsonString);
        JSONArray tmdbJsonArray = tmdbJson.getJSONArray(RESULT_LIST);
        String[] trailerResults = new  String[tmdbJsonArray.length()];

        for (int i = 0; i < tmdbJsonArray.length(); i++){
            JSONObject individualMovieJsonObject = tmdbJsonArray.getJSONObject(i);

            trailerResults[i] = individualMovieJsonObject.getString(TRAILER_KEY);
        }
        return  trailerResults;
    }
}
