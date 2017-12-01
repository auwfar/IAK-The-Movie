package com.auwfar.themovielist.Actor;

import android.content.Context;
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

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {
    List<Actor> actorList;
    Context context;

    public ActorAdapter(List<Actor> actorList) {
        this.actorList = actorList;
    }

    @Override
    public ActorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View viewContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.actor_item, parent, false);
        return new ActorAdapter.ActorViewHolder(viewContent);
    }

    @Override
    public void onBindViewHolder(ActorViewHolder holder, int position) {
        Glide.with(context)
                .load(actorList.get(position).getActorImage())
                .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.def_avatar).error(R.drawable.def_avatar))
                .into(holder.actorItemImage);

        holder.actorItemName.setText(actorList.get(position).getActorName());
        holder.actorItemAs.setText(actorList.get(position).getActorAs());
    }

    @Override
    public int getItemCount() {
        return (actorList == null) ? 0 : actorList.size();
    }

    public class ActorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.actor_image) ImageView actorItemImage;
        @BindView(R.id.actor_name) TextView actorItemName;
        @BindView(R.id.actor_as) TextView actorItemAs;

        public ActorViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
