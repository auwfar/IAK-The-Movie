package com.auwfar.themovielist.Videos;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Auwfar on 30-Nov-17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    List<Videos> videosList;
    Context context;

    public VideosAdapter(List<Videos> videosList) {
        this.videosList = videosList;
    }

    @Override
    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View viewContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_item, parent, false);
        return new VideosAdapter.VideosViewHolder(viewContent);
    }

    @Override
    public void onBindViewHolder(VideosViewHolder holder, final int position) {
        Glide.with(context)
                .load(videosList.get(position).getVideosImage())
                .into(holder.videosItemImage);

        holder.videosItemName.setText(videosList.get(position).getVideosName());
        holder.videosItemType.setText(videosList.get(position).getVideosType());

        holder.videosItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videosList.get(position).getVideosKey()));
                final Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + videosList.get(position).getVideosKey()));

                new android.support.v7.app.AlertDialog.Builder(context)
                        .setMessage("Play this video on Youtube ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    context.startActivity(appIntent);
                                } catch (ActivityNotFoundException ex) {
                                    context.startActivity(webIntent);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (videosList == null) ? 0 : videosList.size();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videos_image) ImageView videosItemImage;
        @BindView(R.id.videos_name) TextView videosItemName;
        @BindView(R.id.videos_type) TextView videosItemType;

        public VideosViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
