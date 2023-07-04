package com.geolocatepoop;

import android.graphics.Color;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geolocatepoop.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements LocationListener, AddAlertDialog.DialogListener, OnMapReadyCallback {


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ImageButton btStartStop = null;
    private final Button btShowMap = null;

    private ImageButton btAddAlert = null;
    private TextInputEditText tfRouteName = null;
    private int currentStatus = GEOLOCATION_STATUS.STOPPED.getStatus();
    private int routeId = 0;
    private String coordinates = "";
    private VolleyRequest request;
    private String lastCoordinate;
    private final List<AlertDataType> alerts = new ArrayList<>();

    PolylineOptions livePolyline = new PolylineOptions();

    private enum GEOLOCATION_STATUS {
        TRACKING(0),
        STOPPED(1);
        private final int status;
        GEOLOCATION_STATUS(final int newStatus) {
            this.status = newStatus;
        }
        public int getStatus() { return status; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        try {
            final Bundle bundle = getIntent().getExtras();
            routeId = bundle.getInt("route", 0);

            if (routeId != 0) {
                findViewById(R.id.btAddAlert).setActivated(false);
                findViewById(R.id.btAddAlert).setVisibility(View.INVISIBLE);

                findViewById(R.id.btStartStop).setActivated(false);
                findViewById(R.id.btStartStop).setVisibility(View.INVISIBLE);

                findViewById(R.id.textInputLayout).setActivated(false);
                findViewById(R.id.textInputLayout).setVisibility(View.INVISIBLE);
            }
        } catch (Exception ignored) {
        }

        initComponents();

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String separator = ";";

        if (!coordinates.isEmpty()) {
            coordinates += separator;
        }

        if (lastCoordinate != null && !isEqualToLastCoordinate(location)) {
            updateMapLines(location);
        }

        lastCoordinate = location.getLatitude() + "," + location.getLongitude();
        coordinates += lastCoordinate;

        if (currentStatus == 0 && !btAddAlert.isEnabled()) {
            btAddAlert.setEnabled(true);
        }
    }

    private boolean isEqualToLastCoordinate(@NonNull Location location) {
        return lastCoordinate.equals(location.getLatitude() + "," + location.getLongitude());
    }

    private void updateMapLines(@NonNull Location location) {
        livePolyline.color(Color.RED);
        livePolyline.width(5);

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        livePolyline.add(position);

        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
            mMap.addPolyline(livePolyline);
        }
    }

    private void onStartTracking() {
        currentStatus = GEOLOCATION_STATUS.TRACKING.getStatus();
        tfRouteName.setEnabled(false);
//        btStartStop.setText("Parar");
    }

    private void onStopTracking() {
        currentStatus = GEOLOCATION_STATUS.STOPPED.getStatus();
//        btStartStop.setText("Iniciar");
        btStartStop.setEnabled(false);
        //btShowMap.setEnabled(true);

        createRouteAndAlert();
    }

    private void createRouteAndAlert() {
        JSONObject params = new JSONObject();

        try {
            params.put("name", tfRouteName.getText().toString());
            params.put("coordinates", coordinates);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.executePost("/routes", params, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        routeId = response.getInt("id");
                        createAlert();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void createAlert() throws Exception {
        if (!alerts.isEmpty()){
            JSONObject params = new JSONObject();

            for (AlertDataType a : alerts) {
                params.put("name", a.getName());
                params.put("description", a.getDescription());
                params.put("categoryId", a.getCategoryId());
                params.put("routeId", routeId);
                params.put("photo64", a.getBase64Image());
                params.put("coordinates", a.getCoordinates());

                request.executePost("/alerts", params, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {}
                });
            }
        }
    }

    private void initComponents() {
//        btShowMap = (Button) findViewById(R.id.btShowMap);
        btStartStop = (ImageButton) findViewById(R.id.btStartStop);
        btAddAlert = (ImageButton) findViewById(R.id.btAddAlert);
        tfRouteName = (TextInputEditText) findViewById(R.id.tfRouteName);

        request = new VolleyRequest(this);

        btStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isRouteNameEmpty = tfRouteName.getText().toString().trim().length() <= 3;

                if (isRouteNameEmpty) {
                    new AlertDialog.Builder(RouteActivity.this)
                            .setTitle("Preencha o nome da rota")
                            .setMessage("Você deve preencher um nome de rota com pelo menos 4 caracteres")
                            .show();

                    return;
                }

                boolean isStopped = currentStatus == GEOLOCATION_STATUS.STOPPED.getStatus();

                if (isStopped) {
                    onStartTracking();
                    return;
                }

                onStopTracking();
            }
        });
//        btShowMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btShowMap.setEnabled(false);
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("route", routeId);
//
//                Intent intent = new Intent(RouteActivity.this, MapsActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    public void openDialog() {
        final AddAlertDialog exampleDialog = new AddAlertDialog();
        final Bundle bundle = new Bundle();
        bundle.putString("coordinates", lastCoordinate);
        exampleDialog.setArguments(bundle);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String name, String description, int categoryId, String imageBase64, String coordinates) {
        alerts.add(new AlertDataType(name, description, categoryId, imageBase64, coordinates));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            final Bundle bundle = getIntent().getExtras();
            routeId = bundle.getInt("route", 0);
            request.executeGet("/routes/" + routeId, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject respObj = new JSONObject(response);
                        final String rawCoordinates = respObj.getString("coordinates");
                        final String routeName = respObj.getString("name");

                        Log.i("cacamaster", response);

                        final TextView t = findViewById(R.id.topBarTitle);
                        t.setText(routeName);

                        if (rawCoordinates == null || rawCoordinates.isEmpty()) {
                            return;
                        }

                        List<String> coordinates = Arrays.asList(rawCoordinates.split(";"));

                        PolylineOptions polylineOptions = new PolylineOptions();

                        for (String coordinate : coordinates) {
                            final Double latitude = Double.parseDouble(coordinate.split(",")[0]);
                            final Double longitude = Double.parseDouble(coordinate.split(",")[1]);

                            polylineOptions.color(Color.RED);
                            polylineOptions.width(5);

                            final LatLng position = new LatLng(latitude, longitude);

                            polylineOptions.add(position);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
                        }

                        mMap.addPolyline(polylineOptions);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            });

            request.executeGet("/alerts/" + routeId, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);

                    for (int i=0; i < arr.length(); i++) {
                        JSONObject respObj = arr.getJSONObject(i);
                        final String alertCoordinates = respObj.getString("coordinates");

                        if (alertCoordinates == null || alertCoordinates.isEmpty()) {
                            return;
                        }

                        final Double latitude = Double.parseDouble(alertCoordinates.split(",")[0]);
                        final Double longitude = Double.parseDouble(alertCoordinates.split(",")[1]);

                        final LatLng position = new LatLng(latitude, longitude);

                        final MarkerOptions marker = new MarkerOptions();
                        marker.position(position);

                        mMap.addMarker(marker);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        } catch (Exception ignored) {}
    }
}
