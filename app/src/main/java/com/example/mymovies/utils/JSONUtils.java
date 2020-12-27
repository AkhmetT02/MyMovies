package com.example.mymovies.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.mymovies.MainActivity;
import com.example.mymovies.data.Genres;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    private static final String KEY_RESULTS = "results";
    private static final String KEY_GENRES_ARRAY = "genres";

    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    private static final String KEY_KEY_OF_TRAILER = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_WITH_GENRES = "with_genres";
    private static final String KEY_GENRE_IDS = "genre_ids";

    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";
    public static final String SMALLER_POSTER_SIZE = "w500";

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject){
        ArrayList<Movie> movies = new ArrayList<>();
        if (jsonObject == null){
            return movies;
        }
        try {
            JSONArray array = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < array.length(); i++){
                JSONObject JSONMovie = array.getJSONObject(i);
                int id = JSONMovie.getInt(KEY_ID);
                int voteCount = JSONMovie.getInt(KEY_VOTE_COUNT);
                String title = JSONMovie.getString(KEY_TITLE);
                String originalTitle = JSONMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = JSONMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_IMAGE_URL + SMALL_POSTER_SIZE + JSONMovie.getString(KEY_POSTER_PATH);
                String smallPosterPath = BASE_IMAGE_URL + SMALLER_POSTER_SIZE + JSONMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_IMAGE_URL + BIG_POSTER_SIZE + JSONMovie.getString(KEY_POSTER_PATH);
                String backdropPath = BASE_IMAGE_URL + BIG_POSTER_SIZE + JSONMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = JSONMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = JSONMovie.getString(KEY_RELEASE_DATE);
                JSONArray jsonGenresArray = JSONMovie.getJSONArray(KEY_GENRE_IDS);
                int genres = jsonGenresArray.getInt(0);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, smallPosterPath, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate, genres);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
    public static ArrayList<Genres> getGenresFromJSON(JSONObject jsonObject){
        ArrayList<Genres> genres = new ArrayList<>();
        if (jsonObject == null){
            return genres;
        }
        try {
            JSONArray array = jsonObject.getJSONArray(KEY_GENRES_ARRAY);
            for (int i = 0; i < array.length(); i++){
                JSONObject jsonGenres = array.getJSONObject(i);
                String name = jsonGenres.getString(KEY_NAME);
                int id = jsonGenres.getInt(KEY_ID);
                genres.add(new Genres(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genres;
    }
    public static ArrayList<Trailer> getTrailerFromJSON(JSONObject jsonObject){
        ArrayList<Trailer> trailers = new ArrayList<>();
        if (jsonObject == null){
            return trailers;
        }
        try {
            JSONArray array = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < array.length(); i++){
                JSONObject jsonTrailers = array.getJSONObject(i);
                String key = BASE_URL_YOUTUBE + jsonTrailers.getString(KEY_KEY_OF_TRAILER);
                String name = jsonTrailers.getString(KEY_NAME);
                trailers.add(new Trailer(name, key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }
    public static ArrayList<Review> getReviewFromJSON(JSONObject jsonObject){
        ArrayList<Review> reviews = new ArrayList<>();
        if (jsonObject == null){
            return reviews;
        }
        try {
            JSONArray array = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < array.length(); i++){
                JSONObject jsonReview = array.getJSONObject(i);
                String author = jsonReview.getString(KEY_AUTHOR);
                String content = jsonReview.getString(KEY_CONTENT);
                reviews.add(new Review(author, content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
