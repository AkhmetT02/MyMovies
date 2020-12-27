package com.example.mymovies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.data.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<Review> reviews;
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.authorTv.setText(review.getAuthor());
        holder.contentTv.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView authorTv;
        private TextView contentTv;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTv = itemView.findViewById(R.id.review_author_tv);
            contentTv = itemView.findViewById(R.id.review_content_tv);
        }
    }
}
