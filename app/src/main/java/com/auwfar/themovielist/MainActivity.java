package com.auwfar.themovielist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auwfar.themovielist.handler.AppConfig;
import com.auwfar.themovielist.handler.AuwHelper;
import com.auwfar.themovielist.handler.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CirclePageIndicator;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @BindView(R.id.movie_list) RecyclerView movieList;
    @BindView(R.id.carouselView) CarouselView carouselView;
    @BindView(R.id.movie_sort) TextView movieSortText;
    @BindView(R.id.movie_empty) TextView movieEmpty;
    @BindView(R.id.movie_total) TextView movieTotal;
    @BindView(R.id.indicator) CirclePageIndicator indicator;

    DatabaseHelper MyDB;
    SharedPreferences session;
    private MovieAdapter movieAdapter;
    List<Movie> movieModel = new ArrayList<Movie>();
    List<Movie> movieCarouselModel = new ArrayList<Movie>();
    String sortDefault = "popular";

    ArrayList<String> carouselImages = new ArrayList<String>();
    ArrayList<String> carouselTitle = new ArrayList<String>();
    ArrayList<Double> carouselRate = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT < 21){
            movieList.setNestedScrollingEnabled(false);
        } else{
            movieList.setNestedScrollingEnabled(true);
        }

        movieAdapter = new MovieAdapter(movieModel);
        movieList.setAdapter(movieAdapter);

        indicator.setVisibility(View.GONE);

        // register SharedPreference
        session = PreferenceManager.getDefaultSharedPreferences(this);
        session.registerOnSharedPreferenceChangeListener(this);

        movieList.setLayoutManager(new GridLayoutManager(this, 2));
        setDataCarousel();

        String sortBy = session.getString( AppConfig.SESSION_SORT_BY, sortDefault);
        movieSortText.setText("Sort by " +sortBy.replace("_", " "));

        if (sortBy.equals("favorite")) {
            MyDB = new DatabaseHelper(this);
            Cursor data = MyDB.select();

            if (data.getCount() > 0) {
                setSQLToRecycler();
            } else {
                showEmptyMovie();
            }
        } else {
            setDataToRecycler();
        }

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            Movie movie = movieCarouselModel.get(position);
            String movieJson = new GsonBuilder().create().toJson(movie);
            intent.putExtra("movie", movieJson);
            startActivity(intent);
            }
        });
    }

    private void setSQLToRecycler() {
        showRecyclerMovie();
        MyDB = new DatabaseHelper(this);
        Cursor data = MyDB.select();

        if (data != null) {
            movieTotal.setText(data.getCount() +" movies");
            while (data.moveToNext()) {
                Movie model = new Movie();
                model.setMovieId(data.getString(data.getColumnIndex("movie_id")));
                model.setMovieImage(data.getString(data.getColumnIndex("movie_image")));
                model.setMovieBackdrop(data.getString(data.getColumnIndex("movie_backdrop")));
                model.setMovieVoteAverage(data.getString(data.getColumnIndex("movie_rate")));
                model.setMovieTitle(data.getString(data.getColumnIndex("movie_name")));
                model.setMovieOverview(data.getString(data.getColumnIndex("movie_overview")));
                model.setMovieReleaseDate(data.getString(data.getColumnIndex("movie_date")));

                movieModel.add(model);
            }
            movieAdapter = new MovieAdapter(movieModel);
            movieList.setAdapter(movieAdapter);
        }
    }

    private void setDataToRecycler() {
        showRecyclerMovie();
        String sortBy = session.getString(AppConfig.SESSION_SORT_BY, sortDefault);
        Uri GET_MOVIES = Uri.parse(AppConfig.GET_MOVIES).buildUpon().appendPath(sortBy).build();
        Log.d("URI", "URI: " + GET_MOVIES);

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

                        movieTotal.setText(data.length() +" movies");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject index = data.getJSONObject(i);

                            Movie model = new Movie();
                            model.setMovieId(index.getString("id"));
                            model.setMovieImage(AppConfig.ASSETS_URL + index.getString("poster_path"));
                            model.setMovieBackdrop(AppConfig.ASSETS_URL + index.getString("backdrop_path"));
                            model.setMovieVoteAverage(index.getString("vote_average"));
                            model.setMovieTitle(index.getString("title"));
                            model.setMovieOverview(index.getString("overview"));
                            model.setMovieReleaseDate(index.getString("release_date"));

                            movieModel.add(model);
                        }

                        movieAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error JSON Parse", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Run
        AuwHelper.request(this, callback, GET_MOVIES.toString(), AuwHelper.GET, param, false);
    }

    private void setDataCarousel() {
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

                            Movie movieCarousel = new Movie();
                            movieCarousel.setMovieId(index.getString("id"));
                            movieCarousel.setMovieImage(AppConfig.ASSETS_URL + index.getString("poster_path"));
                            movieCarousel.setMovieBackdrop(AppConfig.ASSETS_URL + index.getString("backdrop_path"));
                            movieCarousel.setMovieVoteAverage(index.getString("vote_average"));
                            movieCarousel.setMovieTitle(index.getString("title"));
                            movieCarousel.setMovieOverview(index.getString("overview"));
                            movieCarousel.setMovieReleaseDate(index.getString("release_date"));

                            movieCarouselModel.add(movieCarousel);
                        }

                        carouselView.setViewListener(carouselListener);
                        carouselView.setPageCount(movieCarouselModel.size());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error JSON Parse", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Run
        AuwHelper.request(this, callback, AppConfig.GET_MOVIES_CAROUSEL, AuwHelper.GET, param, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    ViewListener carouselListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.movie_carousel, null);
            //set view attributes here

            TextView movieTitle = customView.findViewById(R.id.movie_title);
            ImageView movieImage = customView.findViewById(R.id.movie_image);
            TextView movieRate = customView.findViewById(R.id.movie_rate);
            RatingBar movieRatebar = customView.findViewById(R.id.movie_ratebar);

            Glide.with(getApplicationContext())
                    .load(movieCarouselModel.get(position).getMovieImage())
                    .into(movieImage);
            movieTitle.setText(movieCarouselModel.get(position).getMovieTitle());
            movieRate.setText(movieCarouselModel.get(position).getMovieVoteAverage());

            double Ratebar = Double.parseDouble(movieCarouselModel.get(position).getMovieVoteAverage()) / 2;
            movieRatebar.setRating((float) Ratebar);

            return customView;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        SharedPreferences.Editor editor = session.edit();
        if (id == R.id.sort_popular) {
            editor.putString(AppConfig.SESSION_SORT_BY, "popular");
        } else if (id == R.id.sort_topRated) {
            editor.putString(AppConfig.SESSION_SORT_BY, "top_rated");
        } else if (id == R.id.sort_favorite) {
            editor.putString(AppConfig.SESSION_SORT_BY, "favorite");
        }
        editor.commit();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String sortBy = session.getString( AppConfig.SESSION_SORT_BY, sortDefault);
        movieSortText.setText("Sort by " +sortBy.replace("_", " "));

        movieModel.clear();
        if (sortBy.equals("favorite")) {
            MyDB = new DatabaseHelper(this);
            Cursor data = MyDB.select();

            if (data.getCount() > 0) {
                setSQLToRecycler();
            } else {
                showEmptyMovie();
            }
        } else {
            setDataToRecycler();
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void showEmptyMovie() {
        movieTotal.setText("empty");
        movieEmpty.setVisibility(View.VISIBLE);
        movieList.setVisibility(View.GONE);
    }

    private void showRecyclerMovie() {
        movieList.setVisibility(View.VISIBLE);
        movieEmpty.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortBy = session.getString( AppConfig.SESSION_SORT_BY, sortDefault);

        movieModel.clear();
        if (sortBy.equals("favorite")) {
            MyDB = new DatabaseHelper(this);
            Cursor data = MyDB.select();

            if (data.getCount() > 0) {
                setSQLToRecycler();
            } else {
                showEmptyMovie();
            }
        } else {
            setDataToRecycler();
        }
    }
}
