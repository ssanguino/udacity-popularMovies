package com.sssstudios.popularmovies;

import java.util.ArrayList;

/**
 * Created by sergioss on 12/17/2016.
 * Interface to describe the callback to a Fragment after an AsyncTask
 */

public interface MoviesFragmentCallback {
    void onTaskDone(ArrayList<Movie> movies);
}
