package com.example.mymovies.data;

import androidx.room.Entity;

@Entity(tableName = "favourite_movie")
public class FavouriteMovie extends Movie {
    public FavouriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String smallPosterPath, String posterPath, String bigPosterPath, String backdropPath, double voteAverage, String realiseDate, int genres) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, smallPosterPath, posterPath, bigPosterPath, backdropPath, voteAverage, realiseDate, genres);
    }
    public FavouriteMovie(Movie movie){
        super(movie.getUniqueId(), movie.getId(), movie.getVoteCount(), movie.getTitle(), movie.getOriginalTitle(), movie.getOverview(), movie.getSmallPosterPath(), movie.getPosterPath(), movie.getBigPosterPath(), movie.getBackdropPath(), movie.getVoteAverage(), movie.getRealiseDate(), movie.getGenres());
    }
}
