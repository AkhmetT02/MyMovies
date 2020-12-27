package com.example.mymovies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.data.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private ArrayList<Trailer> trailers;
    private OnTrailerClickListener onTrailerClickListener;

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }
    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    public interface OnTrailerClickListener {
        void onTrailerClick(String url);
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.trailerNameTv.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{
        private TextView trailerNameTv;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerNameTv = itemView.findViewById(R.id.trailer_name_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTrailerClickListener != null){
                        onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey());
                    }
                }
            });
        }
    }
}
