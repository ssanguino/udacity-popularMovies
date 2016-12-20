package com.sssstudios.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sergioss on 12/17/2016.
 * AsyncTask class to handle the download of the information about movies.
 */

public class MoviesDownloadTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private MoviesFragmentCallback mFragmentCallback;
    private final String LOG_TAG = MoviesDownloadTask.class.getSimpleName();

    public MoviesDownloadTask(MoviesFragmentCallback context) {
        this.mFragmentCallback = context;
    }


    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_LIST = "results";
        final String TMDB_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w342/";

        JSONObject moviesJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray(TMDB_LIST);

        ArrayList<Movie> moviesData = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJsonObject = moviesArray.getJSONObject(i);

            int id = movieJsonObject.getInt(TMDB_ID);
            String posterPath = POSTER_BASE_URL + POSTER_SIZE + movieJsonObject.getString(TMDB_POSTER_PATH);
            String releaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
            String originalTitle = movieJsonObject.getString(TMDB_ORIGINAL_TITLE);
            double rating = movieJsonObject.getDouble(TMDB_VOTE_AVERAGE);
            String overview = movieJsonObject.getString(TMDB_OVERVIEW);

            Movie movie = new Movie(id, posterPath, originalTitle, overview, rating, releaseDate);
            moviesData.add(movie);
        }
        Log.v(LOG_TAG, moviesJsonString);
        return moviesData;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        // Verify size of params. There should be at least one parameter containing the preferred
        // sorting type.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonString = null;

        String sortingType = params[0];

        try {
            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + sortingType + "?";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDB, and open the connection
            Log.d(url.toString(), LOG_TAG);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Adding newLine for easy debugging.
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonString = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) try {
                reader.close();
            } catch (final IOException e) {
                Log.e(LOG_TAG, "Error closing stream", e);
            }
        }

        try {
            return getMoviesDataFromJson(moviesJsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movies.
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> result) {
        mFragmentCallback.onTaskDone(result);
    }
}
