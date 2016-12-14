package com.mytrackerapp.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] cords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapInterface = mMap.getUiSettings();
        mapInterface.setZoomControlsEnabled(true);
        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();

        if(!extBundle.isEmpty()) {
            boolean hasGpsCords = extBundle.containsKey("cords");
            if (hasGpsCords) {
                cords = extBundle.getStringArray("cords");
                LatLng[] places = new LatLng[cords.length / 2];
                int howManyPlaces = cords.length / 2;
                if (cords.length / 2 == 1) {
                    System.out.println("im here " + cords[0] + cords[1]);
                    places[0] = new LatLng(Double.parseDouble(cords[0]), Double.parseDouble(cords[1]));
                    mMap.addMarker(new MarkerOptions().position(places[0]).title("You Are Here"));
                } else {
                    for (int i = 0; i < howManyPlaces; i++) {
                        places[i] = new LatLng(Double.parseDouble(cords[i]), Double.parseDouble(cords[i + 1]));
                        mMap.addMarker(new MarkerOptions().position(places[i]).title("Friend "+i));
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places[0],14));
            }
        }
    }
}
