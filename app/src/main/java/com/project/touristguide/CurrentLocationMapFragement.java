package com.project.touristguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;


import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.touristguide.gps.GPS;
import com.project.touristguide.apidata.favorite_list_activity;
import com.project.touristguide.searchedlocation.SeachedLoaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nbpat on 10/30/2016.
 */

public class CurrentLocationMapFragement extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AdapterView.OnItemClickListener {

    private GoogleMap mMap;
    Button btnMenu;
    Location loc;
    Button btnFav;


    private final String PREF_ACTIVITY = "GOOGLE_SIGN_IN";
    private final String PREF_SIGNIN = "SIGNIN";


    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";


    private static final String API_KEY = "AIzaSyCkaG1VKP9aKNVybBovll4380N90NnWYlk";
    AutoCompleteTextView autocomplete;
    static Context cxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        cxt = this.getApplicationContext();
        // Autocomplete
        autocomplete = (AutoCompleteTextView) findViewById(R.id.autotext);
        autocomplete.setAdapter(new GooglePlacesAutocompleteAdapter(getApplicationContext(), R.layout.map_list_city_item));
        autocomplete.setOnItemClickListener(this);

//
//        // Find Location
        GPS gps = new GPS(this.getApplicationContext());
        if (isOnline())
            loc = gps.getMyLocation();
        else {
            MyDynamicToast.informationMessage(this, "Please connect to internet...");
        }

        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnFav = (Button) findViewById(R.id.myFavorite);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btnMenu.setOnClickListener(this);
        btnFav.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        if (loc != null) {
            LatLng sydney = new LatLng(loc.getLatitude(), loc.getLongitude());

            mMap.addMarker(new MarkerOptions().position(sydney).title("You are Here")).showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 14.0f));
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setIndoorEnabled(true);

            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .radius(1000)
                    .strokeWidth(0f)
                    .fillColor(0x38FFBB19));
            // Get My Location'
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addr = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnMenu) {
            GPS gps = new GPS(getApplication().getApplicationContext());
            if (!gps.isLocationEnabled())
                showDialogGPS();
            else {
                if (isOnline()) {
                    Intent i = new Intent(this, CurrentLocationActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
//                finish();
                } else {
                    MyDynamicToast.informationMessage(this, "Please connect to internet...");
                }
            }
        } else if (v.getId() == R.id.myFavorite) {


            GPS gps = new GPS(getApplication().getApplicationContext());
            if (!gps.isLocationEnabled())
                showDialogGPS();
            else {
                if (isOnline()) {
                    Intent i = new Intent(this, favorite_list_activity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
//                finish();}
//                }
                } else {
                    MyDynamicToast.informationMessage(this, "Please connect to internet...");
                }

            }
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public static ArrayList autocomplete(String input) {

        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
//            sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            Log.d("Tourist", sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //       Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            //    Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));

                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        LatLng lng = getLocationFromAddress(this, str);
//        Log.d("Tourist", "" + lng.latitude + "  :  :  " + lng.longitude);

        Intent i = new Intent(this, SeachedLoaction.class);
        i.putExtra("latitude2", lng.latitude);
        i.putExtra("longitude2", lng.longitude);
        Log.d("Tourist", "1::" + lng.latitude + "  :  :  " + lng.longitude);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);


    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
}
