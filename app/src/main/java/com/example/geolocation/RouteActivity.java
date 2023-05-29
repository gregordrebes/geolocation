package com.example.geolocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RouteActivity extends AppCompatActivity implements LocationListener {
    private Button btStartStop = null;
    private Button btShowMap = null;
    private TextInputEditText tfRouteName = null;

    private int currentStatus = GEOLOCATION_STATUS.STOPPED.getStatus();
    private int routeId = 0;
    private String coordinates = "";
    private VolleyRequest request;

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

        initComponents();

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String separator = ";";

        if (!coordinates.isEmpty()) {
            coordinates += separator;
        }

        coordinates += location.getLatitude() + "," + location.getLongitude();
    }

    private void onStartTracking() {
        currentStatus = GEOLOCATION_STATUS.TRACKING.getStatus();
        tfRouteName.setEnabled(false);
        btStartStop.setText("Parar");
    }

    private void onStopTracking() {
        currentStatus = GEOLOCATION_STATUS.STOPPED.getStatus();
        btStartStop.setText("Iniciar");
        btStartStop.setEnabled(false);
        btShowMap.setEnabled(true);

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
                System.out.println(response);
            }
        });

        // TODO alerts
    }

    private void initComponents() {
        btShowMap = (Button) findViewById(R.id.btShowMap);
        btStartStop = (Button) findViewById(R.id.btStartStop);
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

        btShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btShowMap.setEnabled(false);

                Bundle bundle = new Bundle();
                bundle.putInt("route", routeId);

                Intent intent = new Intent(RouteActivity.this, MapsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}