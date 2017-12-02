package com.auwfar.themovielist.handler;

/**
 * Created by Auwfar on 21-Nov-17.
 */

public class AppConfig {
    public static String API_KEY                = "04eba631cf67c41a133b5035963a5f07";

    public static String BASE_URL               = "https://api.themoviedb.org/3/";
    public static String ASSETS_URL             = "http://image.tmdb.org/t/p/w342/";
    public static String YOUTUBE_IMAGE          = "https://i.ytimg.com/vi/";

    //Session
    public static String SESSION_SORT_BY        = "sortBy";

    public static String GET_MOVIES_CAROUSEL    = BASE_URL + "movie/now_playing?api_key=" + API_KEY;

    public static String BASE_MOVIE             = BASE_URL + "movie";
    public static String GET_MOVIES             = BASE_MOVIE +"?api_key=" + API_KEY;
    public static String GET_REVIEWS            = "reviews";
    public static String GET_ACTOR              = "credits";
    public static String GET_VIDEOS             = "videos";
}
