package com.example.geolocation;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geolocation.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private VolleyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        request = new VolleyRequest(this);

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

        request.executeGet("/routes/" + routeId, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respObj = new JSONObject(response);
                    String rawCoordinates = respObj.getString("coordinates");

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
