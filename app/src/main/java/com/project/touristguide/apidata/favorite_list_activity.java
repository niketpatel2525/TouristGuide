package com.project.touristguide.apidata;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.touristguide.R;
import com.project.touristguide.apidata.CustomAdapter;
import com.project.touristguide.apidata.Favorite_Manager;
import com.project.touristguide.apidata.place_data;
import com.project.touristguide.apidata.place_details;

import java.util.ArrayList;
import java.util.List;

public class favorite_list_activity extends AppCompatActivity {

    static CustomAdapter ca;
    static public List<String> place_name = new ArrayList<>();
    static public List<String> address = new ArrayList<>();
    static public List<String> phone_list = new ArrayList<>();
    static public List<String> open = new ArrayList<>();
    static public List<String> plImageUrl = new ArrayList<>();
    static public List<String> plRatingUrl = new ArrayList<>();
    static public List<String> rating = new ArrayList<>();
    static public List<Double> latitude = new ArrayList<>();
    static public List<Double> longitude = new ArrayList<>();
    static RelativeLayout r;
    static public TextView tvInvisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        Favorite_Manager fm_db = new Favorite_Manager(getBaseContext());
        tvInvisible = (TextView) findViewById(R.id.invisibleText);
        ListView fav_list = (ListView) findViewById(R.id.fav_place_list);
        ca = new CustomAdapter(this, R.layout.custom_list_item, place_name, address, plImageUrl, rating);
        fav_list.setAdapter(ca);
        r = (RelativeLayout) findViewById(R.id.loadingPanel);
        fm_db.getFavorite();

        fav_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(favorite_list_activity.this, place_details.class);
                i.putExtra("position", position + "");
                i.putExtra("name", place_name.get(position));
                i.putExtra("address", address.get(position));
                i.putExtra("rating_image", plRatingUrl.get(position));
                i.putExtra("place_image", plImageUrl.get(position));
                i.putExtra("is_closed", "NA");
                i.putExtra("phone", phone_list.get(position));
                i.putExtra("latitude", latitude.get(position));
                i.putExtra("longitude", longitude.get(position));
                i.putExtra("f_type", "removefromfavorite");
                startActivityForResult(i, 111);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                int position = Integer.parseInt(data.getStringExtra("position").toString());
                place_name.remove(position);
                address.remove(position);
                plRatingUrl.remove(position);
                plImageUrl.remove(position);
                phone_list.remove(position);
                latitude.remove(position);
                longitude.remove(position);
                ca.notifyDataSetChanged();
            }
        }
    }

    public static void updatefavorite(ArrayList<place_data> data) {
        tvInvisible.setVisibility(View.GONE);
        place_name.clear();
        address.clear();
        phone_list.clear();
        plImageUrl.clear();
        plRatingUrl.clear();
        latitude.clear();
        longitude.clear();
        rating.clear();
        Log.d("Tourist", "Data Size:" + data.size());
        if (data.size() == 0) {
            tvInvisible.setVisibility(View.VISIBLE);
        }

        for (place_data place : data) {
            place_name.add(place.getName_str());
            address.add(place.getAddress_str());
            phone_list.add(place.getPhone());
            plImageUrl.add(place.getPlace_image_url());
            plRatingUrl.add(place.getRating_image_url());
            latitude.add(place.getLatitude());
            longitude.add(place.getLongitude());
            rating.add(place.getRating());
        }
        data.clear();
        ca.notifyDataSetChanged();
    }
}
