package com.auwfar.themovielist.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Auwfar on 1/23/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "themovie";
    public static final String TABLE_NAME = "movie";

    public static final String col_movie_id = "movie_id";
    public static final String col_movie_name = "movie_name";
    public static final String col_movie_image = "movie_image";
    public static final String col_movie_rate = "movie_rate";
    public static final String col_movie_overview = "movie_overview";
    public static final String col_movie_date = "movie_date";
    public static final String col_movie_backdrop = "movie_backdrop";

    private SQLiteDatabase db;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +col_movie_id +" STRING PRIMARY KEY, " +col_movie_name +" TEXT, " +col_movie_image +" TEXT, " +col_movie_rate +" TEXT, " +col_movie_overview +" TEXT, " +col_movie_date +" TEXT, " +col_movie_backdrop +" TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insert(HashMap<String, String> data) {
        ContentValues values = new ContentValues();

        for(Map.Entry<String, String> e : data.entrySet()){
            values.put(e.getKey(), e.getValue());
        }

        db.insert(TABLE_NAME, null, values);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME, null);

        return res;
    }

    public Boolean update(String user_id, HashMap<String, String> data) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> e : data.entrySet()){
            if (!first) sb.append(", ");
            sb.append(" " + e.getKey() + " = '" + e.getValue() + "' ");
            first = false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("update " + TABLE_NAME +" Set " +sb.toString() +" where movie_id = ?", new String[] {user_id});
        if (!res.moveToNext()) {
            return true;
        } else {
            return false;
        }
    }
    public Boolean delete(String movie_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME +" where movie_id='" +movie_id +"'");
        return null;
    }
    public Cursor select_id(String movie_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME +" where movie_id=?", new String[] {movie_id});

        return res;
    }
}
