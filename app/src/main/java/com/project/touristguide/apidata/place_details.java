package com.project.touristguide.apidata;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.project.touristguide.GoogleLogIn;
import com.project.touristguide.R;
import com.project.touristguide.StreetViewFragment;
import com.project.touristguide.gps.GPS;
import com.squareup.picasso.Picasso;

public class place_details extends AppCompatActivity {

    String name_str, address_str, phone_str, open_str, place_image_url, rating_image_url, rating, position;
    double latitude, longitude;
    Favorite_Manager fm_db;
    Button direction, favorite, share;
    ImageView rating_image, place_image;
    String f_type;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        fm_db = new Favorite_Manager(this);

        TextView place_name = (TextView) findViewById(R.id.details_name);
        TextView place_address = (TextView) findViewById(R.id.detail_address);
        TextView open_status = (TextView) findViewById(R.id.detail_open_status);
        final TextView phone = (TextView) findViewById(R.id.details_phone);

        share = (Button) findViewById(R.id.share);
        direction = (Button) findViewById(R.id.detail_direction);
        favorite = (Button) findViewById(R.id.detail_favorite);
        rating_image = (ImageView) findViewById(R.id.detail_rating_image);
        place_image = (ImageView) findViewById(R.id.detail_place_image);
        i = getIntent();
        position = i.getStringExtra("position");
        name_str = i.getStringExtra("name");
        place_name.setText(name_str);
        address_str = i.getStringExtra("address");
        place_address.setText(address_str);
        rating = i.getStringExtra("rating");
        open_str = i.getStringExtra("is_closed");
        open_status.setText(open_str);
        phone_str = i.getStringExtra("phone");
        phone.setText(phone_str);
        place_image_url = i.getStringExtra("place_image");
        rating_image_url = i.getStringExtra("rating_image");
        f_type = i.getStringExtra("f_type");

        Picasso.with(this).load(rating_image_url).into(rating_image);
        Picasso.with(this).load(place_image_url).into(place_image);


        place_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StreetViewFragment.class);
                i.putExtra("longitude", longitude);
                i.putExtra("latitude", latitude);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.zoom_in);
            }
        });

        latitude = i.getDoubleExtra("latitude", 0);
        longitude = i.getDoubleExtra("longitude", 0);


        if (f_type.equalsIgnoreCase("addtofavorite")) {
            favorite.setText("Add to Favorite");

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean d = fm_db.AddtoFavorite(name_str, address_str, phone_str, place_image_url, rating_image_url, latitude, longitude, rating);
                    if (d) {
                        MyDynamicToast.informationMessage(place_details.this, "Added to Favorites");
                        //Toast.makeText(getBaseContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        MyDynamicToast.informationMessage(place_details.this, "Already in Favorites");
                        //Toast.makeText(getBaseContext(), "Already in Favorites", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        }
        if (f_type.equalsIgnoreCase("removefromfavorite")) {
            favorite.setText("Remove Favorite");
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean d = fm_db.RemovefromFavorite(phone_str);
                    if (d) {
                        Toast.makeText(getBaseContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        i.putExtra("position", position);
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = name_str + " \n " + address_str + "\n" + phone_str + "\n" + place_image_url + "\n";
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tourist Guide, a Trip made Easy !!");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Share using"));

            }

        });
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    GPS gps = new GPS(getApplicationContext());
                    if (gps.isLocationEnabled()) {
                        Location gpsLocation = gps.getMyLocation();

                        double mlatitude = gpsLocation.getLatitude();
                        double mlongitude = gpsLocation.getLongitude();


                        Uri gmmIntentUri = Uri.parse("geo:" + mlatitude + "," + mlongitude + "?q=" + name_str + "&" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } else {
                        showDialogGPS();
                    }

                }

            }
        });
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
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

}
