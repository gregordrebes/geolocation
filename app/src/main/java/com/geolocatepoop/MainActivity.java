package com.geolocatepoop;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.geolocatepoop.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CardClickListnener {

    private ActivityMainBinding activityMainBinding;
    private Button btViewRoute = null;
    private ImageButton btNewRoute = null;
    private SwipeRefreshLayout pullToRefresh = null;
    private SwipeRefreshLayout pullToRefresh2 = null;
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

        ArrayList<Route> routesList = new ArrayList<>();

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
                    }

                    if(routesList.size() > 0) {
                        findViewById(R.id.swiperefresh2).setVisibility(View.INVISIBLE);
                        activityMainBinding.routList.setAdapter(new Adapter(routesList, MainActivity.this));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initComponents() {
        btNewRoute = findViewById(R.id.btNewRoute);
        pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh2 = findViewById(R.id.swiperefresh2);
        request = new VolleyRequest(this);

        loadAvailableRoutes();

        btNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAvailableRoutes();
                pullToRefresh.setRefreshing(false);
            }
        });
        pullToRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAvailableRoutes();
                pullToRefresh2.setRefreshing(false);
            }
        });
    }
}
