package com.example.mymovies.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;
    private MovieDB database;

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDB.getInstance(getApplication());
        movies = database.moviesDao().getAllMovies();
        favouriteMovies = database.moviesDao().getAllFavouriteMovies();
    }

    public Movie getMovieById(int id){
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteMovieById(Movie movie){
        new DeleteMovie().execute(movie);
    }
    public void deleteAllMovies(){
        new DeleteAllMoviesTask().execute();
    }
    public void insertMovie(Movie movie){
        new InsertMovieTask().execute(movie);
    }


    public FavouriteMovie getFavouriteMovieById(int id){
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteFavouriteMovieById(FavouriteMovie movie){
        new DeleteFavouriteMovie().execute(movie);
    }
    public void insertFavouriteMovie(FavouriteMovie movie){
        new InsertFavouriteMovieTask().execute(movie);
    }


    private class GetMovieTask extends AsyncTask<Integer, Void, Movie>{
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return database.moviesDao().getMovieById(integers[0]);
            }
            return null;
        }
    }
    private class DeleteMovie extends AsyncTask<Movie, Void, Void>{
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0){
                database.moviesDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }
    private class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            database.moviesDao().deleteAllMovies();
            return null;
        }
    }
    private class InsertMovieTask extends AsyncTask<Movie, Void, Void>{
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0){
                database.moviesDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie>{
        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return database.moviesDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }
    private class DeleteFavouriteMovie extends AsyncTask<FavouriteMovie, Void, Void>{
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0){
                database.moviesDao().deleteFavouriteMovie(movies[0]);
            }
            return null;
        }
    }
    private class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void>{
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0){
                database.moviesDao().insertFavouriteMovie(movies[0]);
            }
            return null;
        }
    }
}
