package com.project.touristguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.project.touristguide.gps.GPS;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nbpat on 10/30/2016.
 */

public class CurrentLocationActivity extends AppCompatActivity {


    ImageView ivWeatherImage;
    TextView tvWeatherCityName, tvWeatherDescription, tvWeatherTemperature;
    public static String myCity;

    Animation anim;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        GPS gps = new GPS(getApplicationContext());
        if (!gps.isLocationEnabled())
            showDialogGPS();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        GPS gps = new GPS(getApplicationContext());
        if (!gps.isLocationEnabled())
            showDialogGPS();

        super.onRestart();
    }

    @Override
    protected void onResume() {
        GPS gps = new GPS(getApplicationContext());
        if (!gps.isLocationEnabled())
            showDialogGPS();
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_location);


        ivWeatherImage = (ImageView) findViewById(R.id.weatherImage);
        tvWeatherCityName = (TextView) findViewById(R.id.weatherCity);
        tvWeatherDescription = (TextView) findViewById(R.id.weatherDescription);
        tvWeatherTemperature = (TextView) findViewById(R.id.weatherTemperature);


        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        // Find My Location

        GPS gps = new GPS(getApplicationContext());
        Location gpsLocation = null;
        if (isOnline()) {
            gpsLocation = gps.getMyLocation();
        } else {
            MyDynamicToast.informationMessage(this, "Please connect to internet...");
        }


        //GPSLocation gpsLocation = new GetMyLocation(getApplication().getApplicationContext()).fetchLocation();
        if (gpsLocation == null) {
            gpsLocation.setLongitude(0.0);
            gpsLocation.setLatitude(0.0);
        } else {

            String weatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + gpsLocation.getLatitude() + "&lon=" + gpsLocation.getLongitude() + "&appid=c7f890f8a41038c8e7b18a1a04f29b65&units=Imperial";
            new FindWeather().execute(weatherURL);
        }
    }

    public class FindWeather extends AsyncTask<String, Void, Boolean> {

        String cityName, weatherDesc, weatherTemp;

        @Override
        protected Boolean doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder().url(params[0]).build();
                Response response = client.newCall(request).execute();
                int status = response.code();

                if (status == 200) {
                    String data = response.body().string();
                    Log.d("Response", data);

                    JSONObject jObj = new JSONObject(data);

                    JSONArray jArray = jObj.getJSONArray("weather");

                    // Parsing Description
                    JSONObject jDescObj = jArray.getJSONObject(0);
                    weatherDesc = jDescObj.getString("description").toUpperCase();

                    //Parsing Temperature
                    JSONObject jTemp = (JSONObject) jObj.get("main");
                    weatherTemp = jTemp.getString("temp");

                    //parsing City Name and Country Name
                    myCity = jObj.getString("name");
                    Log.d("Weather", "City:" + myCity);

                    JSONObject jCountry = (JSONObject) jObj.get("sys");
                    String country = jCountry.getString("country");
                    Log.d("Weather", "Country:" + country);

                    cityName = myCity + ", " + country;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            tvWeatherCityName.setText(cityName);
            tvWeatherDescription.setText(weatherDesc);
            tvWeatherTemperature.setText(weatherTemp + "\u00B0 F");


            switch (weatherDesc) {
                case "CLEAR SKY":
                    ivWeatherImage.setImageResource(R.drawable.sunny);
                    break;
                case "FEW CLOUDS":
                    ivWeatherImage.setImageResource(R.drawable.sky);
                    break;
                case "SCATTERD CLOUDS":
                    ivWeatherImage.setImageResource(R.drawable.sunset);
                    break;
                case "BROKEN CLOUDS":
                    ivWeatherImage.setImageResource(R.drawable.sky);
                    break;
                case "SHOWER RAIN":
                    ivWeatherImage.setImageResource(R.drawable.weather);
                    break;
                case "RAIN":
                    ivWeatherImage.setImageResource(R.drawable.rain);
                    break;
                case "THUNDERSTROM":
                    ivWeatherImage.setImageResource(R.drawable.storm);
                    break;
                case "SNOW":
                    ivWeatherImage.setImageResource(R.drawable.snow);
                    break;
                case "MIST":
                    ivWeatherImage.setImageResource(R.drawable.mist);
                    break;
                default:
                    ivWeatherImage.setImageResource(R.drawable.sky);
                    break;
            }
            tvWeatherTemperature.setAnimation(anim);
            tvWeatherCityName.setAnimation(anim);
            tvWeatherDescription.setAnimation(anim);

            ivWeatherImage.setAnimation(anim);
        }
    }

    private void showDialogGPS() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
//        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
