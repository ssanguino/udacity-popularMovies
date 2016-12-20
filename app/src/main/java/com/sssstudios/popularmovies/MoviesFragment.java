package com.sssstudios.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sergioss on 12/13/2016.
 * Fragment in charge of showing the main view information, with a GridView holding the top
 * movies by the sorting order selected by the user.
 */

public class MoviesFragment extends Fragment {

    private static final String SPINNER_POSITION_STATE_KEY = "spinnerPosition";
    private ImageArrayAdapter mMoviesAdapter;
    private int spinnerPosition;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ImageArrayAdapter will take data from the Movie DB API and
        // use it to populate the GridView it's attached to.
        mMoviesAdapter = new ImageArrayAdapter(
                        getActivity(),
                        R.layout.fragment_main,
                        new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Recover the movie the user selected, put the object in a new Intent, and open the
                // MovieDetailsActivity with the information for the selected movie.
                Movie movie = mMoviesAdapter.getData().get(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class)
                        .putExtra(getString(R.string.movie_object_intent), movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.sorting_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.pref_sorting_options, R.layout.spinner_sorting_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spinnerPosition = pos;
                updateMovies();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Function to check whether or not the device has an Internet connection available.
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        Toast.makeText(getContext(), getString(R.string.internet_connection_needed),Toast.LENGTH_SHORT).show();
        return false;
    }

    private void updateMovies() {
        if (isInternetAvailable()) {
            MoviesDownloadTask moviesTask = new MoviesDownloadTask(new MoviesFragmentCallback() {
                @Override
                public void onTaskDone(ArrayList<Movie> movies) {
                    if (movies != null) {
                        mMoviesAdapter.clear();
                        mMoviesAdapter.addAll(movies);
                    }
                }
            });
            String[] sortingValues = getResources().getStringArray(R.array.pref_sorting_values);
            String sortingType = sortingValues[spinnerPosition];
            moviesTask.execute(sortingType);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving the position of the sorting type spinner
        outState.putInt(SPINNER_POSITION_STATE_KEY, spinnerPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Recovering the position of the sorting type spinner
            spinnerPosition = savedInstanceState.getInt(SPINNER_POSITION_STATE_KEY, 0);
        }
    }

}
