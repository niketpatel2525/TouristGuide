package com.project.touristguide.gps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.project.touristguide.GoogleLogIn;

/**
 * Created by nbpat on 11/4/2016.
 */

public class GPS implements LocationListener {
    LocationManager locationManager;
    Context context;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public GPS(Context c) {
        this.context = c;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getMyLocation() {
        // If location is not enabled ... gives error and redirect to the Settings to turn it on
        Location gLoc;
        if (!isLocationEnabled())
            MyDynamicToast.informationMessage(context, "Please turn on GPS...");


        gLoc = findLocationUsingGPS();
        if (gLoc == null) {
            gLoc = findLocationUsingNetwork();
        }
        return gLoc;
    }

    private Location findLocationUsingNetwork() {
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //  Log.d("Location:GPS:", location.getLatitude() + "");
        return location;
    }


    private Location findLocationUsingGPS() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return loc;
    }


    public void showAlert(final Context c) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.getApplicationContext().startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });

        dialog.show();
    }

    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
