package com.sssstudios.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sergioss on 12/13/2016.
 * Class holding all the information about a movie.
 * Implements Parcelable to be able to be send through an Intent.
 */

public class Movie implements Parcelable {
    private int id;
    private String posterUrl;
    private String title;
    private String plot;
    private double rating;
    private String date;

    public Movie(int id, String posterUrl, String title, String plot, double rating, String date) {
        this.id = id;
        this.posterUrl = posterUrl;
        this.title = title;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
    }

    private Movie(Parcel in) {
        setId(in.readInt());
        setPosterUrl(in.readString());
        setTitle(in.readString());
        setPlot(in.readString());
        setRating(in.readDouble());
        setDate(in.readString());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(posterUrl);
        parcel.writeString(title);
        parcel.writeString(plot);
        parcel.writeDouble(rating);
        parcel.writeString(date);
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String poster) {
        this.posterUrl = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
