package com.sssstudios.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by sergioss on 12/14/2016.
 * Fragment presenting the Details information about a given movie.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent. We recover the object containing the movie from
        // the Intent, as a Parcelable.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(getString(R.string.movie_object_intent))) {
            Movie movie = intent.getParcelableExtra(getString(R.string.movie_object_intent));

            ImageView imageView = (ImageView)rootView.findViewById(R.id.imageview_details_poster);
            Picasso.with(getContext()).load(movie.getPosterUrl()).into(imageView);
            ((TextView) rootView.findViewById(R.id.textview_details_title))
                    .setText(movie.getTitle());
            ((TextView) rootView.findViewById(R.id.textview_details_date))
                    .setText(movie.getDate());
            ((TextView) rootView.findViewById(R.id.textview_details_plot))
                   .setText(movie.getPlot());
            ((RatingBar) rootView.findViewById(R.id.ratingbar_details_rating))
                    .setRating((float) movie.getRating()/2);
        }

        return rootView;
    }

}
