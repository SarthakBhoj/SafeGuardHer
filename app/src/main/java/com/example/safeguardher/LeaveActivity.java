package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LeaveActivity extends AppCompatActivity {

    private  DatePickerDialog EndDate;
    private DatePickerDialog StartDate;
    private Button btnStartdate,btnEnddate,timeButton,bookbtn;
    ProgressDialog pDialog;

   EditText etxtFullName,etxtMobNo,etxtReason;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        btnStartdate=findViewById(R.id.btnStartdate);
        btnEnddate=findViewById(R.id.btnEnddate);
        bookbtn=findViewById(R.id.btnba);
        etxtFullName=findViewById(R.id.etxtFullName);
        etxtMobNo = findViewById(R.id.etxtMobNo);
        etxtReason = findViewById(R.id.etxtReason);
        setValues();
        initDatePicker();
        btnStartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartDate.show();

            }
        });
        btnEnddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndDate.show();

            }
        });


    }
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener StartdateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1 + 1;
                btnStartdate.setText(i+"-"+i1+"-"+i2);
            }
        };

        DatePickerDialog.OnDateSetListener EnddateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                btnEnddate.setText(year+"-"+month+"-"+dayOfMonth);
            }
        };
        Calendar cal=Calendar.getInstance();
        int Startyear=cal.get(Calendar.YEAR);
        int Startmonth=cal.get(Calendar.MONTH);
        int Startday=cal.get(Calendar.DAY_OF_MONTH);

        int style= AlertDialog.THEME_HOLO_DARK;
        StartDate=new DatePickerDialog(this,style,StartdateSetListener,Startyear,Startmonth,Startday);
        StartDate.getDatePicker().setMinDate(cal.getTimeInMillis()+ 86400000);

        EndDate = new DatePickerDialog(this,style,EnddateSetListener,Startyear,Startmonth,Startday);
        EndDate.getDatePicker().setMinDate(cal.getTimeInMillis()+86400000);

    }

    public void btnregisterClick(View view) {
        String Name = etxtFullName.getText().toString().trim();
        String Number = etxtMobNo.getText().toString().trim();
        String Reason = etxtReason.getText().toString().trim();
        String StartDate = btnStartdate.getText().toString();
        String EndDate = btnEnddate.getText().toString();

        if (Name.isEmpty()){
            etxtFullName.setError("Enter Name");
            etxtFullName.requestFocus();
        }if (Number.isEmpty()){
            etxtMobNo.setError("Enter mobile number");
            etxtMobNo.requestFocus();
        }if (Reason.isEmpty()){
            etxtReason.setError("Enter Reason");
            etxtReason.requestFocus();
        }if (StartDate.isEmpty()){
            btnStartdate.setError("Select Date");
            btnStartdate.requestFocus();
        }if (EndDate.isEmpty()){
            btnEnddate.setError("Enter Date");
            btnEnddate.requestFocus();
        }

        else {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Uploading your Details");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            String url = "https://apiforprojects.shop/RequestLeave.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(LeaveActivity.this, "Application Sent Successful", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LeaveActivity.this, TeacherModule.class);
//                            startActivity(intent);
//                            finish();
                            SmsManager s = SmsManager.getDefault();
                            s.sendTextMessage(DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Gurdian_Number'"),null,"Your Child Has Submitted Leave Applicatoin",null,null);
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
                    params.put("Student_Name", Name);
                    params.put("Student_Number", Number);
                    params.put("Reason", Reason);
                    params.put("StartDate", StartDate);
                    params.put("EndDate", EndDate);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        
    }
    public void setValues(){
        String name = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Stud_Name'");
        etxtFullName.setText(name);
        String number = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Stud_Number'");
        etxtMobNo.setText(number);


    }

}