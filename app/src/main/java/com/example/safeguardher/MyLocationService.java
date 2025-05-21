package com.example.safeguardher;

import static android.content.Intent.getIntent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyLocationService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    Context context;
    Handler handler;
    double latitude,longitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        Log.d("Service Working", "hiiiiii");
        getLocation();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            finalize();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }

    public void getLocation() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            latitude=location.getLatitude();
                            longitude=location.getLongitude();
//                            Toast.makeText(getApplicationContext(), String.valueOf(latitude)+"\n"+String.valueOf(longitude), Toast.LENGTH_SHORT).show();
                            String url = "https://apiforprojects.shop/UpdateLocation.php";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getString("status").equals("success")){
//                                            Toast.makeText(MyLocationService.this, ""+latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
//                                            Log.d("Location", "Location Updated Successfully");
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                protected Map<String, String> getParams()
                                {
                                    Map<String, String> params = new HashMap<>();
//                                    Log.e("Validate", "getParams: "+username+" "+password );
                                    String query = "SELECT CValue FROM Configuration WHERE CName = 'Stud_Number'";
                                    Log.d("Parameters", "getParams: "+DBClass.getSingleValue(query)+" "+latitude+" "+longitude);
                                    params.put("Stud_Number",DBClass.getSingleValue(query));
                                    params.put("Latitude",""+latitude);
                                    params.put("Longitude",""+longitude);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    }
                });

                handler.postDelayed(this,300000);
            }
        };
            handler.post(runnable);

    }
}
