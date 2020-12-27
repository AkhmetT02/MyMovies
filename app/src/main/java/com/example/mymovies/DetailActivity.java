package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapter.MovieAdapter;
import com.example.mymovies.adapter.ReviewAdapter;
import com.example.mymovies.adapter.SimilarMovieAdapter;
import com.example.mymovies.adapter.TrailerAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView bigPosterIv;
    private TextView titleTv;
    private TextView originalTitleTv;
    private TextView ratingTv;
    private TextView releaseDateTv;
    private TextView overviewTv;

    private RecyclerView trailersRv;
    private RecyclerView reviewsRv;
    private RecyclerView similarMoviesRv;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private SimilarMovieAdapter similarMovieAdapter;

    private Movie movie;

    private ImageView favouriteIv;

    private MainViewModel viewModel;
    private int id;
    private FavouriteMovie favouriteMovie;

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
        setContentView(R.layout.activity_detail);

        bigPosterIv = findViewById(R.id.big_poster_iv);
        titleTv = findViewById(R.id.detail_title_tv);
        originalTitleTv = findViewById(R.id.detail_original_title_tv);
        ratingTv = findViewById(R.id.detail_rating_tv);
        releaseDateTv = findViewById(R.id.detail_release_date_tv);
        overviewTv = findViewById(R.id.detail_overview_tv);
        favouriteIv = findViewById(R.id.favourite_iv);

        trailersRv = findViewById(R.id.trailers_rv);
        reviewsRv = findViewById(R.id.reviews_rv);
        similarMoviesRv = findViewById(R.id.similar_movies_rv);
        trailerAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();
        similarMovieAdapter = new SimilarMovieAdapter();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra("id")){
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(bigPosterIv);
        titleTv.setText(movie.getTitle());
        originalTitleTv.setText(movie.getOriginalTitle());
        ratingTv.setText(Double.toString(movie.getVoteAverage()));
        releaseDateTv.setText(movie.getRealiseDate());
        overviewTv.setText(movie.getOverview());
        setFavourite();

        favouriteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteMovie == null){
                    viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(DetailActivity.this, "Added to favourite movies!", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.deleteFavouriteMovieById(favouriteMovie);
                    Toast.makeText(DetailActivity.this, "Removed in favourite movies!", Toast.LENGTH_SHORT).show();
                }
                setFavourite();
            }
        });

        trailersRv.setAdapter(trailerAdapter);
        reviewsRv.setAdapter(reviewAdapter);
        trailersRv.setLayoutManager(new LinearLayoutManager(this));
        reviewsRv.setLayoutManager(new LinearLayoutManager(this));

        JSONObject trailersJsonObject = NetworkUtils.getJSONForTrailer(movie.getId());
        JSONObject reviewsJsonObject = NetworkUtils.getJSONForReview(movie.getId());
        ArrayList<Trailer> trailers = JSONUtils.getTrailerFromJSON(trailersJsonObject);
        ArrayList<Review> reviews = JSONUtils.getReviewFromJSON(reviewsJsonObject);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        JSONObject jsonSimilarMovies = NetworkUtils.getJSONForSimilarMovies(movie.getId());
        final ArrayList<Movie> similarMovies = JSONUtils.getMoviesFromJSON(jsonSimilarMovies);
        similarMovieAdapter.setMovies(similarMovies);
        similarMoviesRv.setAdapter(similarMovieAdapter);
        similarMoviesRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        similarMovieAdapter.setOnPosterClickListener(new SimilarMovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie simMovie = similarMovieAdapter.getMovies().get(position);
                viewModel.insertMovie(simMovie);
                Intent intentToMovie = new Intent(DetailActivity.this, DetailActivity.class);
                intentToMovie.putExtra("id", simMovie.getId());
                startActivity(intentToMovie);
            }
        });
    }

    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null){
            favouriteIv.setImageResource(R.drawable.ic_not_favourite);
        } else {
            favouriteIv.setImageResource(R.drawable.ic_in_favourite);
        }
    }
}