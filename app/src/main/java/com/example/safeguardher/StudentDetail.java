package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentDetail extends AppCompatActivity {

    String id,Number,Parent_Number,Latitude,Longitude,name;
    TextView studName,studNumber,parentNumber,parentName,studId;
    ProgressDialog progressDialog;
    Button call,track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        id = getIntent().getExtras().getString("number");
        studName = findViewById(R.id.name);
        studNumber =  findViewById(R.id.studNumber);
        studId  =  findViewById(R.id.studId);
        parentNumber = findViewById(R.id.parentNumber);
        parentName = findViewById(R.id.parentName);
        call = findViewById(R.id.call);
        track = findViewById(R.id.trackLocation);
        loadPage();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(StudentDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StudentDetail.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
                }else{
                    Intent call_intent = new Intent(Intent.ACTION_CALL);
                    call_intent.setData(Uri.parse("tel:" + Number));
                    startActivity(call_intent);
                }

            }
        });



        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q="+Latitude+","+Longitude+"&z=12&markers="+Latitude+","+Longitude+"&label="+name));
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
                        params.put("Gurdian_Number",Parent_Number);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            }
        });

//        https://www.google.com/maps?q=Latitude,Longitude&z=12&markers=Latitude,Longitude&label=San+Francisco

    }

    public void loadPage(){
        String url="https://apiforprojects.shop/Student_Details.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        studId.setText("Student ID : "+jsonObject.getString("Stud_id"));
                        studName.setText("Student Name : "+jsonObject.getString("Stud_Name"));
                        studNumber.setText("Student's Mobile Number : "+jsonObject.getString("Stud_Number"));
                        parentNumber.setText("Parent's Mobile Number : "+jsonObject.getString("Gurdian_Number"));
                        parentName.setText("Parent Name : "+jsonObject.getString("Parent_Name"));
                        Number = jsonObject.getString("Stud_Number");
                        Parent_Number = jsonObject.getString("Gurdian_Number");
                        name = jsonObject.getString("Stud_Name");
                    }
                }
                catch (JSONException e) {
                    Log.e("Exception", e.toString());
                    Toast.makeText(getApplicationContext(), "JSON Exception Check Internet Connection...", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                Log.e("Exception", error.toString());
                Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("Stud_Number",id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}