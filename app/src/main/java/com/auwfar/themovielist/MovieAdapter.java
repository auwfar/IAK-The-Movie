package com.auwfar.themovielist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Auwfar on 20-Nov-17.
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    List<Movie> movieList;
    Context context;

    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View viewContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(viewContent);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Glide.with(context)
            .load(movieList.get(position).getMovieImage())
            .into(holder.movieItemImage);
    }

    @Override
    public int getItemCount() {
        return (movieList == null) ? 0 : movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.movie_image) ImageView movieItemImage;
        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
            Movie movie = movieList.get(getAdapterPosition());
            String movieJson = new GsonBuilder().create().toJson(movie);
            intent.putExtra("movie", movieJson);
            view.getContext().startActivity(intent);
        }
    }
}