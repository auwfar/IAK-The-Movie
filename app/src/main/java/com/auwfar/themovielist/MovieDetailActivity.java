package com.auwfar.themovielist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auwfar.themovielist.Actor.Actor;
import com.auwfar.themovielist.Actor.ActorAdapter;
import com.auwfar.themovielist.Videos.Videos;
import com.auwfar.themovielist.Videos.VideosAdapter;
import com.auwfar.themovielist.handler.AppConfig;
import com.auwfar.themovielist.handler.AuwHelper;
import com.auwfar.themovielist.handler.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.movie_image) ImageView movieImage;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_rate) TextView movieRate;
    @BindView(R.id.movie_ratebar) RatingBar movieRatebar;
    @BindView(R.id.movie_backdrop) ImageView movieBackdrop;
    @BindView(R.id.movie_date) TextView movieDate;
    @BindView(R.id.movie_overview) TextView movieOverview;
    @BindView(R.id.actor_list)RecyclerView actorList;
    @BindView(R.id.videos_list)RecyclerView videosList;

    @BindView(R.id.btn_favorite) LinearLayout btnFavorite;
    @BindView(R.id.favorite) ImageView favorite;
    @BindView(R.id.unfavorite) ImageView unfavorite;
    @BindView(R.id.label_favorit) TextView labelFavorite;

    DatabaseHelper MyDB;
    private static final String TAG = "MovieDetailActivity";
    private ActorAdapter actorAdapter;
    List<Actor> actorModel = new ArrayList<Actor>();
    private VideosAdapter videosAdapter;
    List<Videos> videosModel = new ArrayList<Videos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        String movieJson = getIntent().getStringExtra("movie");
        final Movie movie = new GsonBuilder().create().fromJson(movieJson, Movie.class);
        setTitle(movie.getMovieTitle());

        Glide.with(getApplicationContext())
                .load(movie.getMovieImage())
                .apply(requestOptions)
                .into(movieImage);

        Glide.with(getApplicationContext())
                .load(movie.getMovieBackdrop())
                .into(movieBackdrop);

        movieTitle.setText(movie.getMovieTitle());
        movieRate.setText(movie.getMovieVoteAverage());
        movieDate.setText("Release Date : " +movie.getMovieReleaseDate());
        movieOverview.setText(movie.getMovieOverview());

        double Ratebar = Double.parseDouble(movie.getMovieVoteAverage()) / 2;
        movieRatebar.setRating((float) Ratebar);

        MyDB = new DatabaseHelper(this);
        Cursor data = MyDB.select_id(movie.getMovieId());
        Boolean favoriteStatus = false;
        if (data.getCount() > 0) {
            favoriteStatus = true;
            setFavorite();
        } else {
            setUnfavorite();
        }

        final Boolean finalFavoriteStatus = favoriteStatus;
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(finalFavoriteStatus) {
                MyDB.delete(movie.getMovieId());
                setUnfavorite();
            } else {
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("movie_id", movie.getMovieId());
                param.put("movie_name", movie.getMovieTitle());
                param.put("movie_image", movie.getMovieImage());
                param.put("movie_rate", movie.getMovieVoteAverage());
                param.put("movie_overview", movie.getMovieOverview());
                param.put("movie_date", movie.getMovieReleaseDate());
                param.put("movie_backdrop", movie.getMovieBackdrop());

                MyDB.insert(param);
                setFavorite();
            }
            }
        });

        actorList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setActorToRecycler(movie.getMovieId());
        videosList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setVideosToRecycler(movie.getMovieId());
    }

    private void setActorToRecycler(String movieId) {
        Uri GET_ACTOR = Uri.parse(AppConfig.BASE_MOVIE).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(AppConfig.GET_ACTOR)
                .appendQueryParameter("api_key", AppConfig.API_KEY)
                .build();
        //Declare For Request
            //Parameter
        HashMap<String,String> param = new HashMap<String,String>();

            //Process JSON
        AuwHelper.VolleyCallback callback = new AuwHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        JSONObject jObj = new JSONObject(result);
                        JSONArray data = jObj.getJSONArray("cast");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject index = data.getJSONObject(i);

                            Actor model = new Actor();
                            model.setActorName(index.getString("name"));
                            model.setActorAs(index.getString("character"));
                            model.setActorImage(AppConfig.ASSETS_URL + index.getString("profile_path"));

                            actorModel.add(model);
                        }

                        actorAdapter = new ActorAdapter(actorModel);
                        actorList.setAdapter(actorAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error JSON Parse", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Run
        AuwHelper.request(getApplicationContext(), callback, GET_ACTOR.toString(), AuwHelper.GET, param, false);
    }

    private void setVideosToRecycler(String movieId) {
        Uri GET_ACTOR = Uri.parse(AppConfig.BASE_MOVIE).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(AppConfig.GET_VIDEOS)
                .appendQueryParameter("api_key", AppConfig.API_KEY)
                .build();
        //Declare For Request
            //Parameter
        HashMap<String,String> param = new HashMap<String,String>();

            //Process JSON
        AuwHelper.VolleyCallback callback = new AuwHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (result != null) {
                        JSONObject jObj = new JSONObject(result);
                        JSONArray data = jObj.getJSONArray("results");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject index = data.getJSONObject(i);

                            Videos model = new Videos();
                            model.setVideosName(index.getString("name"));
                            model.setVideosType(index.getString("type"));
                            model.setVideosKey(index.getString("key"));
                            model.setVideosImage(AppConfig.YOUTUBE_IMAGE + index.getString("key") +"/sddefault.jpg");

                            videosModel.add(model);
                        }

                        videosAdapter = new VideosAdapter(videosModel);
                        videosList.setAdapter(videosAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error JSON Parse", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Run
        AuwHelper.request(getApplicationContext(), callback, GET_ACTOR.toString(), AuwHelper.GET, param, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setFavorite() {
        btnFavorite.setBackgroundResource(R.drawable.border_golden);
        favorite.setVisibility(View.VISIBLE);
        unfavorite.setVisibility(View.GONE);
        labelFavorite.setTextColor(Color.parseColor("#FFEA00"));
    }

    private void setUnfavorite() {
        btnFavorite.setBackgroundResource(R.drawable.border);
        favorite.setVisibility(View.GONE);
        unfavorite.setVisibility(View.VISIBLE);
        labelFavorite.setTextColor(Color.parseColor("#FFFFFF"));
    }
}
