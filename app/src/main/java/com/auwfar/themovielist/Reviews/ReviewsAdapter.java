package com.auwfar.themovielist.Reviews;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.auwfar.themovielist.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Auwfar on 30-Nov-17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    List<Reviews> reviewsList;
    Context context;

    public ReviewsAdapter(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View viewContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_item, parent, false);
        return new ReviewsAdapter.ReviewsViewHolder(viewContent);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, final int position) {
        holder.reviewsItemAuthor.setText(reviewsList.get(position).getReviewsAuthor());
        holder.reviewsItemContent.setText(reviewsList.get(position).getReviewsContent());
    }

    @Override
    public int getItemCount() {
        return (reviewsList == null) ? 0 : reviewsList.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reviews_author) TextView reviewsItemAuthor;
        @BindView(R.id.reviews_content) TextView reviewsItemContent;

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
