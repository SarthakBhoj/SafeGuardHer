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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentLogin extends AppCompatActivity {

    EditText etxtUsername,etxtPassword;
    TextInputLayout user_TIL,pass_TIL;
    String username,password,Stud_number,Gurdian_Number,Stud_id,Stud_name;
    ProgressDialog progressDialog;
    boolean loginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        etxtUsername=findViewById(R.id.etxtUsername);
        etxtPassword=findViewById(R.id.etxtPassword);
//        user_TIL=findViewById(R.id.user_TIL);
//        pass_TIL=findViewById(R.id.pass_TIL);
    }

    public void btnLogIn(View view) {
        password = etxtPassword.getText().toString().trim();
        username = etxtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etxtUsername.setError("Enter Mobile Number");
            etxtUsername.requestFocus();
        }
        else if (password.isEmpty()) {
            etxtPassword.setError("Enter Password");
            etxtPassword.requestFocus();
        } else if (username.length()!=10) {
            etxtUsername.setError("Enter 10 Digit Mobile Number");
            etxtUsername.requestFocus();
        } else{
            progressDialog = new ProgressDialog(StudentLogin.this);
            progressDialog.setMessage("Validating Details.....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
            LoginUser(username,password);
        }
    }

    private void LoginUser(String username, String password) {
        String url="https://apiforprojects.shop/Student_Login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response",response);
                        progressDialog.dismiss();
                        try {
                            loginSuccess=false;
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("success")){
                                Stud_id = jsonObject.getString("Stud_id");
                                Stud_name=jsonObject.getString("Stud_Name");
                                Stud_number = jsonObject.getString("Stud_Number");
                                Gurdian_Number = jsonObject.getString("Gurdian_Number");
                                loginSuccess=true;
//                                Toast.makeText(StudentLogin.this, "Working", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(StudentLogin.this,MainActivity.class);
                                startActivity(intent);
                            }
                            if (loginSuccess){
                                String query ="DELETE FROM Configuration";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Stud_Name', '" +  Stud_name + "')";
                                DBClass.execNonQuery(query);

                                query="INSERT INTO Configuration(CName,CValue)";
                                query += "VALUES('Stud_id', '" + Stud_id + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Screen_Number', '2')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Stud_Number', '" + Stud_number + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) ";
                                query += "VALUES('Gurdian_Number', '" + Gurdian_Number + "')";
                                DBClass.execNonQuery(query);

                                Intent intent = new Intent(StudentLogin.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Username or Password not found...", Toast.LENGTH_LONG).show();
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
                progressDialog.dismiss();
                Log.e("Exception", error.toString());
                Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("Stud_Number",username);
                params.put("Stud_Password",password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void btnRegister(View view) {
        Intent intent = new Intent(StudentLogin.this,StudentRegister.class);
        startActivity(intent);
        finish();
    }
}