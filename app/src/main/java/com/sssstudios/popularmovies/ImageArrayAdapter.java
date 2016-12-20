package com.sssstudios.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sergioss on 12/13/2016.
 * Custom adapter to handle the GridView display of posters in MoviesFragment
 */

public class ImageArrayAdapter extends ArrayAdapter {
    private Context mContext;

    public ArrayList<Movie> getData() {
        return data;
    }

    private ArrayList<Movie> data;


    public ImageArrayAdapter(Context context, int layoutResourceId, ArrayList<Movie> data) {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.data = data;
    }

    // Create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = data.get(position);
        String posterUrl = movie.getPosterUrl();

        // Load the poster image using Picasso library.
        Picasso.with(mContext)
                .load(posterUrl)
                .placeholder(R.drawable.unknown_poster)
                .error(R.drawable.error_poster)
                .into(imageView);
        Picasso.with(mContext).load(posterUrl).into(imageView);
        return imageView;
    }

}
