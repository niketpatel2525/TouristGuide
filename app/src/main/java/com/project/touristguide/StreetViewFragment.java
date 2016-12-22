package com.project.touristguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

/**
 * Created by nbpat on 11/16/2016.
 */

public class StreetViewFragment extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    StreetViewPanoramaFragment streetViewPanoramaFragment;
    double longitude, latitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streetview_activity);

        Intent i = getIntent();
        longitude = i.getDoubleExtra("longitude", 151.20689);
        latitude = i.getDoubleExtra("latitude", -33.87365);

        streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);


        StreetViewPanorama mSvp = streetViewPanoramaFragment.getStreetViewPanorama();
        int PAN_BY = 30;
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                .zoom(mSvp.getPanoramaCamera().zoom)
                .tilt(mSvp.getPanoramaCamera().tilt)
                .bearing(mSvp.getPanoramaCamera().bearing - PAN_BY)
                .build();

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(new LatLng(latitude, longitude));
    }
}
