package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeacherLogin extends AppCompatActivity {

    EditText etxtUsername,etxtPassword;
    TextInputLayout user_TIL,pass_TIL;
    String username,password,Parent_Number,Student_Number,Parent_ID,Parent_Name;
    ProgressDialog progressDialog;
    boolean loginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        etxtUsername=findViewById(R.id.etxtUsername);
        etxtPassword=findViewById(R.id.etxtPassword);
    }

    public void btnLogIn(View view) {
        password = etxtPassword.getText().toString().trim();
        username = etxtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etxtUsername.setError("Enter Number");
            etxtUsername.requestFocus();
        }
        else if (password.isEmpty()) {
            etxtPassword.setError("Enter Password");
            etxtPassword.requestFocus();
        }
        else if(etxtUsername.length()!=10){
            etxtUsername.setError("Enter 10 Digit Number");
            etxtUsername.requestFocus();
        }
        else{
            progressDialog = new ProgressDialog(TeacherLogin.this);
            progressDialog.setMessage("Validating Details.....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
            LoginUser(username,password);
        }
    }

    private void LoginUser(String username, String password) {
        String url="https://apiforprojects.shop/Teacher_Login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response.

                        Log.d("Response",response);
                        JSONObject jsonObject = null;
                        progressDialog.dismiss();
                        try {
                            loginSuccess=false;
                            jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("success")){
                                Parent_ID = jsonObject.getString("Teacher_id");
                                Parent_Name=jsonObject.getString("Teacher_Name");
                                Parent_Number = jsonObject.getString("Teacher_Number");
                                loginSuccess=true;
                                Intent intent = new Intent(TeacherLogin.this,MainActivity.class);
                                startActivity(intent);
                            }
                            if (loginSuccess){
                                String query ="DELETE FROM Configuration";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Teacher_Name', '" +  Parent_Name + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Screen_Number', '1')";
                                DBClass.execNonQuery(query);

                                query="INSERT INTO Configuration(CName,CValue)";
                                query += "VALUES('Teacher_ID', '" + Parent_ID + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Teacher_Number', '" + Parent_Number + "')";
                                DBClass.execNonQuery(query);

                                Intent intent = new Intent(TeacherLogin.this, TeacherModule.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Username or Password not found...", Toast.LENGTH_LONG).show();
                                etxtUsername.setError("Enter Correct Number");
                                etxtUsername.requestFocus();
                                etxtPassword.setError("Enter Correct Password");
                                etxtPassword.requestFocus();
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e("Exception", error.toString());
                Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                Log.e("Validate", "getParams: "+username+" "+password );
                params.put("Teacher_Number",username);
                params.put("Teacher_Password",password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}