package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    private static final String API_KEY = "26c9c63599343ed01a5489135d890a5e";
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_TRAILER = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";
    private static final String BASE_URL_GENRES = "https://api.themoviedb.org/3/genre/movie/list";
    private static final String BASE_URL_SIMILAR_MOVIES = "https://api.themoviedb.org/3/movie/%s/similar";
    private static final String BASE_URL_SEARCH = "https://api.themoviedb.org/3/search/movie";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";
    private static final String PARAMS_WITH_GENRES = "with_genres";
    private static final String PARAMS_QUERY = "query";

    private static final String LANGUAGE_VALUE = "en-US";
    private static final String SORT_BY_RATING = "vote_average.desc";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static  final int RATING = 1;

    public static URL buildURL(int sortBy, int page, int genreId){
        URL url = null;
        String sort;
        if (sortBy == 1){
            sort = SORT_BY_RATING;
        } else {
            sort = SORT_BY_POPULARITY;
        }
        Uri uri = null;
        if (genreId < 0){
            uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                    .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                    .appendQueryParameter(PARAMS_SORT, sort)
                    .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                    .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                    .build();
        } else {
            uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                    .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                    .appendQueryParameter(PARAMS_SORT, sort)
                    .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                    .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                    .appendQueryParameter(PARAMS_WITH_GENRES, Integer.toString(genreId))
                    .build();
        }
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildURLSearch(String query){
        query.trim();
        String q = query.replaceAll(" ", "%20");
        Uri uri = Uri.parse(BASE_URL_SEARCH).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_QUERY, q).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URL buildURLSimilarMovies(int id){
        Uri uri = Uri.parse(String.format(BASE_URL_SIMILAR_MOVIES, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(1)).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URL buildURLGenres(){
        Uri uri = Uri.parse(BASE_URL_GENRES).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URL buildURLTrailer(int id){
        Uri uri = Uri.parse(String.format(BASE_URL_TRAILER, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URL buildURLReviews(int id){
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject>{
        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        //SETTERS
        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        //Constructor
        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        public interface OnStartLoadingListener{
            void onStartLoading();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null){
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null){
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = null;
            StringBuilder builder = new StringBuilder();
            if (url == null){
                return null;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                jsonObject = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return jsonObject;
        }
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page, int genreId){
        JSONObject jsonObject = null;
        URL url = buildURL(sortBy, page, genreId);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject getJSONFromSearch(String query){
        JSONObject jsonObject = null;
        URL url = buildURLSearch(query);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject getJSONForSimilarMovies(int id){
        JSONObject jsonObject = null;
        URL url = buildURLSimilarMovies(id);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject getJSONForGenres(){
        JSONObject jsonObject = null;
        URL url = buildURLGenres();
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject getJSONForTrailer(int id){
        JSONObject jsonObject = null;
        URL url = buildURLTrailer(id);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject getJSONForReview(int id){
        JSONObject jsonObject = null;
        URL url = buildURLReviews(id);
        try {
            jsonObject = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject jsonObject = null;
            StringBuilder builder = new StringBuilder();
            if (urls == null || urls.length == 0){
                return null;
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                jsonObject = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return jsonObject;
        }
    }

}
