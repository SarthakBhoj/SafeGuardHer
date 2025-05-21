package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class Parent_Register extends AppCompatActivity {

    EditText etxtUsername,etxtStudMobNo,etxtGardianNumber,etxtPassword;
    LinearLayout reslinear;
    ProgressDialog pDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_register);
        etxtUsername=findViewById(R.id.etxtUsername);
        etxtStudMobNo=findViewById(R.id.etxtStudMobNo);
        etxtGardianNumber=findViewById(R.id.etxtGardianNumber);
        etxtPassword=findViewById(R.id.etxtPassword);
        reslinear=findViewById(R.id.reslinear);
    }

    public void register(View view) {
        String username = etxtUsername.getText().toString().trim();
        String ParentMobNo = etxtStudMobNo.getText().toString().trim();
        String StudentMobNo = etxtGardianNumber.getText().toString().trim();
        String password = etxtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etxtUsername.setError("Enter Username");
            etxtUsername.requestFocus();
        }
        else if (ParentMobNo.isEmpty()) {
            etxtStudMobNo.setError("Enter Mobile Number");
            etxtStudMobNo.requestFocus();
        }
        else if (StudentMobNo.isEmpty()) {
            etxtGardianNumber.setError("Enter your Child's Mobile number");
            etxtGardianNumber.requestFocus();
        }
        else if (password.isEmpty()) {
            etxtPassword.setError("Enter Password");
            etxtPassword.requestFocus();
        } else if (ParentMobNo.length()!=10) {
            etxtStudMobNo.setError("Enter 10 Digit Mobile Number");
            etxtStudMobNo.requestFocus();
        } else if (StudentMobNo.length()!=10) {
            etxtGardianNumber.setError("Enter 10 Digit Mobile Number");
            etxtGardianNumber.requestFocus();
        } else if (ParentMobNo.equals(StudentMobNo)) {
            etxtGardianNumber.setError("Student's Mobile Number Must be Different From Parent's Mobile Number");
            etxtGardianNumber.requestFocus();
        } else {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Uploading your Details");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = "https://apiforprojects.shop/Parent_Register.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(Parent_Register.this, "Registered Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Parent_Register.this, Parent_Login.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Parent_Name", username);
                    params.put("Parent_Number", ParentMobNo);
                    params.put("Student_Number", StudentMobNo);
                    params.put("Parent_Password", password);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}