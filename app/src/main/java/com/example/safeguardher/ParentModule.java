package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ParentModule extends AppCompatActivity {

    CardView track, Emergency, Reached,logout;
    String Latitude, Longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    double ParentLatitude,ParentLongitude;
    String Message;
    TextView txtGreet;
    LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_module);
        track = findViewById(R.id.track);
        Emergency = findViewById(R.id.Emergency);
        Reached = findViewById(R.id.Reached);
        logout=findViewById(R.id.logout);
        txtGreet = findViewById(R.id.txtGreet);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        getNumber();
        Greeting();
        getPermissions();

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                String url = "https://apiforprojects.shop/getLocation.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Response", response);
                            if(jsonObject.getString("status").equals("success")){
                                Latitude=jsonObject.getString("Latitude");
                                Longitude=jsonObject.getString("Longitude");
//                                Toast.makeText(ParentModule.this, ""+ParentLongitude+" "+ParentLatitude, Toast.LENGTH_SHORT).show();
                                Log.d("Values", "onResponse: "+ParentLongitude+" "+ParentLatitude);
//                                Toast.makeText(ParentModule.this, ""+Longitude+" "+Longitude, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query="+Latitude+","+Longitude));
                                startActivity(intent);
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
                        String query="SELECT CValue FROM Configuration WHERE CName='Parent_Number'";
                        params.put("Gurdian_Number",DBClass.getSingleValue(query));
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            }
        });


        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call_intent = new Intent(Intent.ACTION_CALL);
                    String query="SELECT CValue FROM Configuration WHERE CName='Teacher_Number'";
                    call_intent.setData(Uri.parse("tel:" + DBClass.getSingleValue(query)));
                    startActivity(call_intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentModule.this);

                        builder.setTitle("LOG OUT ALERT");
                        builder.setMessage("Do You Really Want To Log out ? ");
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String query = "DELETE FROM Configuration";
                                DBClass.execNonQuery(query);
                                Intent intent = new Intent(ParentModule.this,User_Selection.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.show();
                    }
                });
            }
        });

        Reached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name;
                String query = "SELECT CValue FROM Configuration WHERE CName = 'Stud_Name'";
                Name=DBClass.getSingleValue(query);
                Message = Name+" Reached Home Successfully!";
                SmsManager smsManager = SmsManager.getDefault();
                query = "SELECT CValue FROM Configuration WHERE CName = 'Teacher_Number'";
                smsManager.sendTextMessage(DBClass.getSingleValue(query), null, Message, null, null);
                Message = "";
                Toast.makeText(ParentModule.this, "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void getNumber() {
        String url = " https://apiforprojects.shop/get_Info.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("Response", response);
                    if(jsonObject.getString("status").equals("success")){
                        DBClass.execNonQuery("INSERT INTO Configuration (CName, CValue) VALUES ('Teacher_Number','"+jsonObject.getString("Teacher_Number")+"')");
                        DBClass.execNonQuery("INSERT INTO Configuration (CName, CValue) VALUES ('Stud_Name','"+jsonObject.getString("Stud_Name")+"')");
                        Log.d("Values", "onResponse: "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'Stud_Name'"));
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
                params.put("Stud_Number",DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Student_Number'"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getLocation(){

        if (ContextCompat.checkSelfPermission(ParentModule.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ParentModule.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        ParentLatitude = location.getLatitude();
                        ParentLongitude = location.getLongitude();
                    }
                }
            });
        }
    }
    public void Greeting() {
        time = LocalTime.now();
        String hrs = String.valueOf(time);
        hrs = hrs.substring(0, 2);
        int currentTime = Integer.parseInt(hrs);
        if (currentTime >= 0 && currentTime < 12) {
            txtGreet.setText("Good Morning");
        }
        if (currentTime >= 12 && currentTime < 18) {
            txtGreet.setText("Good Afternoon");
        }
        if (currentTime >= 18 && currentTime < 24) {
            txtGreet.setText("Good Evening");
        }
    }
    public void getPermissions() {
        if (ContextCompat.checkSelfPermission(ParentModule.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ParentModule.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        if (ContextCompat.checkSelfPermission(ParentModule.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ParentModule.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }
        if (ContextCompat.checkSelfPermission(ParentModule.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ParentModule.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNumber();
        getNumber();
    }
}