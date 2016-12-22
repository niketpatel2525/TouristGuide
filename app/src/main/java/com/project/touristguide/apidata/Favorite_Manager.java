package com.project.touristguide.apidata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Harshit on 04 Nov 2016.
 */

public class Favorite_Manager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tourist_Guide.db";
    ArrayList<place_data> data;

    public Favorite_Manager(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists favorite_places (Phone text primary key,Name text,Address text,PlaceImage text,RatingImage text,latitude text,longitude text,rating text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Favorite_places");
        onCreate(db);
    }

    public boolean AddtoFavorite(String name_str, String address_str, String phone, String place_image_url, String rating_image_url, double latitude, double longitude, String rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table if not exists favorite_places (Phone text primary key,Name text,Address text,PlaceImage text,RatingImage text,latitude text,longitude text,rating text)");
        ContentValues values = new ContentValues();
        values.put("Phone", String.valueOf(phone));
        values.put("Name", name_str);
        values.put("Address", address_str);
        values.put("PlaceImage", place_image_url);
        values.put("RatingImage", rating_image_url);
        values.put("latitude", latitude + "");
        values.put("longitude", longitude + "");
        values.put("rating", rating);
        try {
            db.insert("favorite_places", null, values);
            db.close();
            return true;
        } catch (Exception e) {
            db.close();
            return false;
        }
    }

    public boolean RemovefromFavorite(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete("favorite_places", "Phone=" + phone, null);
            db.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false;
        }
    }

    public void getFavorite() {
        data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        new readdatabase(db).execute();
    }

    public class readdatabase extends AsyncTask<String, Void, Boolean> {
        SQLiteDatabase db;

        public readdatabase(SQLiteDatabase sql_db) {
            this.db = sql_db;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Cursor cursor = db.rawQuery("SELECT * FROM favorite_places", null);
                if (cursor != null) {
                    data.clear();
                    cursor.moveToFirst();
                    while (cursor.isAfterLast() == false) {
                        String phone = cursor.getString(0);
                        String name_str = cursor.getString(1);
                        String address_str = cursor.getString(2);
                        String place_image_url = cursor.getString(3);
                        String rating_image_url = cursor.getString(4);
                        double latitude = Double.parseDouble(cursor.getString(5));
                        double longitude = Double.parseDouble(cursor.getString(6));
                        String rating = cursor.getString(7);
                        data.add(new place_data(phone, name_str, address_str, place_image_url, rating_image_url, latitude, longitude, rating));
                        cursor.moveToNext();
                    }
                } else {
                    data.add(new place_data());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Boolean aBoolean) {
            db.close();

            favorite_list_activity.updatefavorite(data);
            favorite_list_activity.r.setVisibility(View.GONE);

        }
    }
}
