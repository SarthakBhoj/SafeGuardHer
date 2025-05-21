package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Stud_Profile extends AppCompatActivity {

    String Number;
    TextView studName,studNumber,parentNumber,parentName,studId;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stud_profile);
        studName = findViewById(R.id.name);
        studNumber =  findViewById(R.id.studNumber);
        studId  =  findViewById(R.id.studId);
        parentNumber = findViewById(R.id.parentNumber);
        parentName = findViewById(R.id.parentName);
        loadPage();

    }

    public void loadPage(){
        progressDialog = new ProgressDialog(Stud_Profile.this);
        progressDialog.setMessage("Validating Details.....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        String url="https://apiforprojects.shop/Student_Details.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        progressDialog.dismiss();
                        studId.setText("Student ID : "+jsonObject.getString("Stud_id"));
                        studName.setText("Student Name : "+jsonObject.getString("Stud_Name"));
                        studNumber.setText("Student's Mobile Number : "+jsonObject.getString("Stud_Number"));
                        parentNumber.setText("Parent's Mobile Number : "+jsonObject.getString("Gurdian_Number"));
                        parentName.setText("Parent Name : "+jsonObject.getString("Parent_Name"));
                        Number = jsonObject.getString("Stud_Number");
                    }
                }
                catch (JSONException e) {
                    Log.e("Exception", e.toString());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "JSON Exception Check Internet Connection...", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                Log.e("Exception", error.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                String id = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'Stud_Number'");
                Log.d("Value", "getParams: "+id);
                params.put("Stud_Number",id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}