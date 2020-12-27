package com.example.mymovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.DetailActivity;
import com.example.mymovies.MainActivity;
import com.example.mymovies.R;
import com.example.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieViewHolder> {

    private List<Movie> movies;
    private OnPosterClickListener onPosterClickListener;

    public SimilarMovieAdapter() {
        movies = new ArrayList<>();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    @NonNull
    @Override
    public SimilarMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_movie_item, parent, false);
        return new SimilarMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarMovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.get().load(movie.getSmallPosterPath()).into(holder.smallerPosterIV);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    class SimilarMovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView smallerPosterIV;

        public SimilarMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            smallerPosterIV = itemView.findViewById(R.id.smaller_poster_iv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear() {
        this.movies.clear();
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }
}
