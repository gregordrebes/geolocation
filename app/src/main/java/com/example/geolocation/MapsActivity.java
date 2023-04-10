package com.example.geolocation;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geolocation.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        int routeId = bundle.getInt("route");

        Cursor res = ReadableDatabaseManager.getInstance(this).rawQuery( "select * from coordinates where route_id = " + routeId + " order by created_at", null );

        if (res != null && res.getCount() != 0) {
            res.moveToFirst();

            @SuppressLint("Range") String rawCoordinates = res.getString(res.getColumnIndex("coordinates"));

            System.out.println("raw: " + rawCoordinates);
            if (rawCoordinates == null || rawCoordinates.isEmpty()) {
                return;
            }

            List<String> coordinates = Arrays.asList(rawCoordinates.split(";"));

            PolylineOptions polylineOptions = new PolylineOptions();

            for (String coordinate : coordinates) {
                Double latitude = Double.parseDouble(coordinate.split(",")[0]);
                Double longitude = Double.parseDouble(coordinate.split(",")[1]);

                polylineOptions.color(Color.RED);
                polylineOptions.width(5);

                LatLng position = new LatLng(latitude, longitude);

                polylineOptions.add(position);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));

                boolean isLast = coordinate.equals(coordinates.get(coordinates.size() - 1));

                if (isLast) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(position);

                    mMap.addMarker(marker);
                }
            }

            mMap.addPolyline(polylineOptions);
        }
    }
}