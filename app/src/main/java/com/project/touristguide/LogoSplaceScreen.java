package com.project.touristguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.touristguide.searchedlocation.LocationManager_check;

/**
 * Created by nbpat on 11/16/2016.
 */

public class LogoSplaceScreen extends AppCompatActivity {

    ImageView ivLogo;
    TextView tvTitle;
    Animation anim;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        tvTitle = (TextView) findViewById(R.id.tvSplaceTitile);
        ivLogo = (ImageView) findViewById(R.id.splash);
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);

        final Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Harlow Solid Italic Italic.ttf");
        tvTitle.setTypeface(font);

        tvTitle.setAnimation(anim);
        ivLogo.setAnimation(anim);

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            showDialogGPS();
        } else {
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                                          @Override
                                          public void run() {
                                              // This method will be executed once the timer is over
                                              // Start your app main activity
                                              Intent intent = new Intent(getApplicationContext(),
                                                      MainActivity.class);
                                              intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                              startActivity(intent);
                                              // close this activity
                                              finish();
                                          }
                                      }
                    , 2000);
        }
    }

    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps_enabled;
    }

    LocationManager locationManager;
    Boolean locationServiceBoolean = false;
    int providerType = 0;
    static AlertDialog alert;

    public void LocationManagerwd(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkIsEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (networkIsEnabled == true && gpsIsEnabled == true) {
            locationServiceBoolean = true;
            providerType = 1;

        } else if (networkIsEnabled != true && gpsIsEnabled == true) {
            locationServiceBoolean = true;
            providerType = 2;

        } else if (networkIsEnabled == true && gpsIsEnabled != true) {
            locationServiceBoolean = true;
            providerType = 1;
        }

    }

    public Boolean isLocationServiceAvailable() {
        return locationServiceBoolean;
    }

    public int getProviderType() {
        return providerType;
    }

    public void createLocationServiceError(final Activity activityObj) {

        // show alert dialog if Internet is not connected
        AlertDialog.Builder builder = new AlertDialog.Builder(activityObj);

        builder.setMessage(
                "You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
                .setTitle("LostyFound")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activityObj.startActivity(intent);
                                alert.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                            }
                        });
        alert = builder.create();
        alert.show();
    }
}
