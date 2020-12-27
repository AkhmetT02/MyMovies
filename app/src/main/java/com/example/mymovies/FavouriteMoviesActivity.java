package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mymovies.adapter.MovieAdapter;
import com.example.mymovies.adapter.SimilarMovieAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMoviesActivity extends AppCompatActivity {

    private RecyclerView favouriteMoviesRv;
    private MovieAdapter movieAdapter;
    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_main:
                Intent main_intent = new Intent(this, MainActivity.class);
                startActivity(main_intent);
                break;
            case R.id.menu_favourites:
                Intent favourites_intent = new Intent(this, FavouriteMoviesActivity.class);
                startActivity(favourites_intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);

        favouriteMoviesRv = findViewById(R.id.favourite_movies_rv);
        favouriteMoviesRv.setLayoutManager(new GridLayoutManager(this, 2));

        movieAdapter = new MovieAdapter();
        favouriteMoviesRv.setAdapter(movieAdapter);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<FavouriteMovie>> favouriteMoviesLiveData = viewModel.getFavouriteMovies();
        favouriteMoviesLiveData.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null){
                    movies.addAll(favouriteMovies);
                    movieAdapter.setMovies(movies);
                }
            }
        });

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(FavouriteMoviesActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
    }
}