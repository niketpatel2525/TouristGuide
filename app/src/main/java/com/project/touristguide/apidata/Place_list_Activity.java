package com.project.touristguide.apidata;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.touristguide.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place_list_Activity extends AppCompatActivity {
    public List<String> place_name = new ArrayList<>();
    public List<String> address = new ArrayList<>();
    public List<String> phone_list = new ArrayList<>();
    public List<String> open = new ArrayList<>();
    public List<String> plImageUrl = new ArrayList<>();
    public List<String> plRatingUrl = new ArrayList<>();
    public List<String> rating = new ArrayList<>();
    public List<Double> latitude = new ArrayList<>();
    public List<Double> longitude = new ArrayList<>();

    YelpAPI yelp;
    CustomAdapter ca;
    String category = "Museum", city = "", state = "";
    double lati, longi;
    RelativeLayout r;
    static Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_place);
        TextView cat = (TextView) findViewById(R.id.category_name);
//        Button back = (Button) findViewById(R.id.place_back);
        ListView museums = (ListView) findViewById(R.id.place_list);
        r = (RelativeLayout) findViewById(R.id.loadingPanel);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Intent i = getIntent();
        category = i.getStringExtra("category");
        city = i.getStringExtra("city");
        //    state = i.getStringExtra("state");
        lati = i.getDoubleExtra("latitude", 0.0);
        longi = i.getDoubleExtra("longitude", 0.0);

        Log.d("Tourist", "PlaceList:" + lati + " " + longi);
        cat.setText(category + " list");

        yelp = new YelpAPI(YelpAPI.CONSUMER_KEY, YelpAPI.CONSUMER_SECRET, YelpAPI.TOKEN, YelpAPI.TOKEN_SECRET);

        ca = new CustomAdapter(this, R.layout.custom_list_item, place_name, address, plImageUrl, rating);
        museums.setAdapter(ca);
        new FindLocation().execute(category, city, state, lati + "", longi + "");

        museums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Place_list_Activity.this, place_details.class);
                i.putExtra("name", place_name.get(position));
                i.putExtra("address", address.get(position));
                i.putExtra("rating", rating.get(position));
                i.putExtra("rating_image", plRatingUrl.get(position));
                i.putExtra("place_image", plImageUrl.get(position));
                i.putExtra("is_closed", open.get(position));
                i.putExtra("phone", phone_list.get(position));
                i.putExtra("latitude", latitude.get(position));
                i.putExtra("longitude", longitude.get(position));
                i.putExtra("f_type", "addtofavorite");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
            }
        });

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    public class FindLocation extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... par) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                String data = yelp.searchForBusinessesByLocation(par[0], par[1] + "," + par[2], par[3], par[4]);
                JSONObject jo = new JSONObject(data);
                JSONArray data_jarr = jo.getJSONArray("businesses");
                for (int i = 0; i < data_jarr.length(); i++) {
                    JSONObject pl = data_jarr.getJSONObject(i);
                    String name = pl.getString("name");
                    String rate = pl.get("rating") + "";
                    String place_image_url = pl.getString("image_url");
                    String rating_image_url = pl.getString("rating_img_url_large");
                    String is_closed = pl.getBoolean("is_closed") ? "Closed Now :(" : "Open Now :)";
                    String phone = pl.getString("phone");
                    JSONObject loc = pl.getJSONObject("location");
                    JSONArray dis_add = loc.getJSONArray("display_address");
                    JSONObject cord = loc.getJSONObject("coordinate");
                    double lat = cord.getDouble("latitude");
                    double lon = cord.getDouble("longitude");

                    String add = "";
                    for (int j = 0; j < dis_add.length() - 1; j++) {
                        add += dis_add.get(j) + " ";
                    }
                    add += "\n" + dis_add.get(dis_add.length() - 1);
                    plImageUrl.add(place_image_url);
                    plRatingUrl.add(rating_image_url);
                    place_name.add(name);
                    address.add(add);
                    rating.add(rate);
                    phone_list.add(phone);
                    open.add(is_closed);
                    longitude.add(lon);
                    latitude.add(lat);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ca.notifyDataSetChanged();

            r.setVisibility(View.GONE);
        }

    }


}
