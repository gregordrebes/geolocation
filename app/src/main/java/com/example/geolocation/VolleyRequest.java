package com.example.geolocation;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VolleyRequest {
    private Context context;

    public VolleyRequest(Context context) {
        this.context = context;
    }

    public void executeGet(String route, Response.Listener responseListener) {

        RequestQueue queue = Volley.newRequestQueue(context);

        String urlConexao = "http://142.93.8.161" + route;   // link da API ou webpage

        StringRequest stringRequest = new StringRequest(
            Request.Method.GET,
            urlConexao,
            responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Falha na requisição = " + error, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    public void executePost(String route, JSONObject params, Response.Listener responseListener) {
        String urlConexao = "http://142.93.8.161" + route;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlConexao, params, responseListener, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Falha na requisição = " + error, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(request);
    }
}
