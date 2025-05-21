package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveList extends AppCompatActivity {

    private ArrayList<Leave> LeaveListAdapter;
    private LeaveAdapter leaveAdapter;
    ProgressDialog pDialog;
    RecyclerView rview;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_list);
        rview=findViewById(R.id.rview);
        context=getApplicationContext();
        list();

    }

    public void list() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("downloading, please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        pDialog.show();

        String url = "https://apiforprojects.shop/LeaveList.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("Response ", ">> " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            LeaveListAdapter = new ArrayList<>();
                            for (int i = 0; i < jsonData.length(); i++) {
                                Leave history = new Leave();
                                JSONObject jo = jsonData.getJSONObject(i);
                                history.Leave_id = jo.getString("Leave_id");
                                history.Student_Name = jo.getString("Student_Name");
                                history.Student_Number = jo.getString("Student_Number");
                                history.Reason = jo.getString("Reason");
                                history.Start_Date = jo.getString("Start_Date");
                                history.End_Date = jo.getString("End_Date");
                                history.Status = jo.getString("Status");
                                LeaveListAdapter.add(history);
                            }
                            leaveAdapter = new LeaveAdapter(getApplicationContext(), LeaveListAdapter);
                            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                            rview.setLayoutManager(layoutManager);
                            rview.setAdapter(leaveAdapter);
                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                String query ="SELECT CValue FROM Configuration WHERE CName ='user_id'";
                params.put("Status","new");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
}