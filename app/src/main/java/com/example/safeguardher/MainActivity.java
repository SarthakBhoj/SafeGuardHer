package com.example.safeguardher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.LocalServerSocket;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private static final int REQUEST_CALL_PHONE = 1;
    public static int LOCATION_REQUEST_CODE = 100;
    int count = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    CardView SoSCard,LeaveCard,profile,logout;
    LocalTime time;
    private SensorManager sm;
    LocationManager locationManager;
    boolean LocationOff;
    String Message, mob_no;
    TextView locationText, txtGreet;
    Handler mHandler = new Handler();
    boolean isShake = false;
    Location userLocation;
    Double lat1, long1;
    private float acelVal, acelLast, shake;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,MyLocationService.class));
        setContentView(R.layout.activity_main);
        txtGreet = findViewById(R.id.txtGreet);
        SoSCard = findViewById(R.id.SoSCard);
        LeaveCard = findViewById(R.id.RequestLeave);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.logout);
        locationText = findViewById(R.id.locationText);
        
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        shake = 0.00f;
        isShake = false;
        LocationOff = false;

        getPermissions();
        getLocation();
        Greeting();
        getLocation();

        String query = "SELECT CValue FROM Configuration WHERE CName='Stud_Number'";
        mob_no = DBClass.getSingleValue(query);
        SoSCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendsms();
                Intent call_intent = new Intent(Intent.ACTION_CALL);
                String query = "SELECT CValue FROM Configuration WHERE CName = 'Gurdian_Number'";
                call_intent.setData(Uri.parse("tel:" + DBClass.getSingleValue(query)));
                startActivity(call_intent);
            }
        });

        LeaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LeaveActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Stud_Profile.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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
                        Intent intent = new Intent(MainActivity.this,User_Selection.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });

    }


    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x * x) + (y * y) + (z * z));
            float delta = acelVal - acelLast;
            shake = shake * 0.7f + delta;
//            getLocation();
            if (shake > 24) {
                if (!isShake) {
                    isShake = true;
                    try {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, 100);
                            sendsms();
                            shake = shake * 0.9f - delta;
                        } else {
                            shake = shake * 0.9f - delta;
                            sendsms();
                            shake = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed To Send Message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void sendsms() {
        String query = "SELECT CValue FROM Configuration WHERE CName = 'Gurdian_Number'";
        SmsManager smsManager = SmsManager.getDefault();
        Message = "Emergency at " + Message;
        smsManager.sendTextMessage(DBClass.getSingleValue(query), null, Message, null, null);
        Message = "";
    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, MainActivity.this);
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double longi = location.getLongitude();
                        showLocation(lat, longi);
                        userLocation = location;
                        userLocation.setLatitude(lat);
                        userLocation.setLongitude(longi);
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Message = addresses.get(0).getAddressLine(0);
                        Log.e("Result", "onSuccess: "+Message);
                        locationText.setText(Message);
                    }
                }
            });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        if (!LocationOff) {
            LocationOff = true;
            count++;
            String query = "SELECT CValue FROM Configuration WHERE CName ='Gurdian_Number'";
            SmsManager smsManager = SmsManager.getDefault();
            if (count == 1) {
                sendMessage(query, smsManager);
            }
        }
    }

    public void sendMessage(String query, SmsManager smsManager) {
        Message = "Your Child Has Closed Location";
        smsManager.sendTextMessage(DBClass.getSingleValue(query), null, Message, null, null);
        Message = "";
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);

    }


    public void btnSOS(View view) throws InterruptedException {
//        sendsms();
        Intent call_intent = new Intent(Intent.ACTION_CALL);
        String query = "SELECT CValue FROM Configuration WHERE CName = 'Gurdian_Number'";
        call_intent.setData(Uri.parse("tel:" + DBClass.getSingleValue(query)));
        Thread.sleep(2000);
        startActivity(call_intent);
//        UpdateLocation();
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
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }
    }

    public void showLocation(Double latitude, Double longitude) {
        lat1 = latitude;
        long1 = longitude;
    }

    public void showMapLocation(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.parse("geo:"+String.valueOf(lat1))+String.valueOf(long1)));
        startActivity(intent);
    }
}
