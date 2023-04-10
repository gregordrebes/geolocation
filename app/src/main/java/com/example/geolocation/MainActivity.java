package com.example.geolocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btViewRoute = null;
    private Button btNewRoute = null;
    private Button btRefresh = null;
    private Spinner spRoutes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        initComponents();
    }

    private void loadAvailableRoutes() {
        btRefresh.setEnabled(false);

        ArrayList<Route> routesList = new ArrayList<Route>();
        Cursor res = ReadableDatabaseManager.getInstance(this).rawQuery( "select * from routes", null );

        if (res != null && res.getCount() != 0) {
            res.moveToFirst();

            while(res.isAfterLast() == false) {
                @SuppressLint("Range") Route route = new Route(res.getInt(res.getColumnIndex("id")), res.getString(res.getColumnIndex("name")));

                routesList.add(route);
                res.moveToNext();
            }

            ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(
                this,
                android.R.layout.simple_list_item_1,
                routesList
            );

            spRoutes.setAdapter(adapter);
        }

        btRefresh.setEnabled(true);
    }

    private void initComponents() {
        btViewRoute = (Button) findViewById(R.id.btViewRoute);
        btNewRoute = (Button) findViewById(R.id.btNewRoute);
        btRefresh = (Button) findViewById(R.id.btRefresh);
        spRoutes = (Spinner) findViewById(R.id.spRoutes);

        loadAvailableRoutes();

        btNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAvailableRoutes();
            }
        });

        btViewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Route selectedRoute = (Route) spRoutes.getSelectedItem();

                if (selectedRoute == null) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Selecione uma rota")
                            .setMessage("Você deve selecionar uma rota")
                            .show();

                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("route", selectedRoute.getId());

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}