package com.geolocatepoop;

import android.content.Intent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.geolocation.databinding.ActivityMainBinding;

import com.geolocatepoop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CardClickListnener {

    private ActivityMainBinding activityMainBinding;
    private Button btViewRoute = null;
    private ImageButton btNewRoute = null;
    private Button btRefresh = null;
    private Spinner spRoutes = null;
    private VolleyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        activityMainBinding.routList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        initComponents();

    }

    @Override
    public void onClick(Route route) {
        Bundle bundle = new Bundle();
        bundle.putInt("route", route.getId());

        Intent intent = new Intent(MainActivity.this, RouteActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void loadAvailableRoutes() {
//        btRefresh.setEnabled(false);

        ArrayList<Route> routesList = new ArrayList<Route>();

        request.executeGet("/routes", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray arr = new JSONArray(response);

                    for (int i=0; i < arr.length(); i++) {
                        JSONObject respObj = arr.getJSONObject(i);
                        int id = respObj.getInt("id");
                        String name = respObj.getString("name");

                        Route route = new Route(id, name);

                        routesList.add(route);

//                        ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(
//                            MainActivity.this,
//                            android.R.layout.simple_list_item_1,
//                            routesList
//                        );
//
//                        spRoutes.setAdapter(adapter);
//                        btRefresh.setEnabled(true);
                    }

                    if(routesList.size() > 0) {
                        findViewById(R.id.alert_icon).setVisibility(View.INVISIBLE);
                        findViewById(R.id.alert_message).setVisibility(View.INVISIBLE);
                        activityMainBinding.routList.setAdapter(new Adapter(routesList, MainActivity.this));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initComponents() {
        btNewRoute = (ImageButton) findViewById(R.id.btNewRoute);
//        btRefresh = (Button) findViewById(R.id.btRefresh);
        request = new VolleyRequest(this);

        loadAvailableRoutes();

        btNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        });

//        btRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadAvailableRoutes();
//            }
//        });
    }
}
