package com.example.safeguardher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Leave> leaveArrayList;
    Context context;

    public LeaveAdapter(Context context,ArrayList<Leave> leaveArrayList){
        this.leaveArrayList=leaveArrayList;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.leave_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.Leave_Id.setText(holder.Leave_Id.getText()+leaveArrayList.get(position).Leave_id);
        holder.Student_Name.setText(holder.Student_Name.getText()+leaveArrayList.get(position).Student_Name);
        holder.Student_Number.setText(holder.Student_Number.getText()+leaveArrayList.get(position).Student_Number);
        holder.Reason.setText(holder.Reason.getText()+leaveArrayList.get(position).Reason);
        holder.StartDate.setText(holder.StartDate.getText()+leaveArrayList.get(position).Start_Date);
        holder.EndDate.setText(holder.EndDate.getText()+leaveArrayList.get(position).End_Date);
        holder.Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Check", "onClick: "+holder.Student_Number.getText().toString() );
                String url = "https://apiforprojects.shop/LeaveDecision.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {

                                SmsManager smsManager = SmsManager.getDefault();
                                String Message = "Your Application Has Been Approved";
                                smsManager.sendTextMessage(leaveArrayList.get(position).Student_Number, null, Message, null, null);
                                Toast.makeText(context.getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();

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
                        params.put("Decision", "approved");
                        params.put("Student_Number", leaveArrayList.get(position).Student_Number);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });

        holder.Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://apiforprojects.shop/LeaveDecision.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {

                                SmsManager smsManager = SmsManager.getDefault();
                                String Message = "Your Application Has Been Rejected";
                                smsManager.sendTextMessage(leaveArrayList.get(position).Student_Number, null, Message, null, null);
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
                        params.put("Decision", "rejected");
                        params.put("Student_Number", leaveArrayList.get(position).Student_Number);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveArrayList.size();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Leave_Id,Student_Name,Student_Number,Reason,StartDate,EndDate;
        Button Approve,Reject;
        LinearLayout LayoutLeave;
        public MyViewHolder(View view) {
            super(view);
            context= itemView.getContext();
            Leave_Id= view.findViewById(R.id.txtId);
            Student_Name= view.findViewById(R.id.txtName);
            Student_Number= view.findViewById(R.id.txtMob);
            Reason= view.findViewById(R.id.txtReason);
            StartDate= view.findViewById(R.id.txtStartDate);
            EndDate= view.findViewById(R.id.txtEndDate);
            Approve = view.findViewById(R.id.btnApprove);
            Reject = view.findViewById(R.id.btnReject);
            LayoutLeave =  view.findViewById(R.id.layoutLeave);
        }
    }

}
