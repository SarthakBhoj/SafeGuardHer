package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentList extends AppCompatActivity {
    ProgressDialog pDialog;
    private Girl_List_Adapter girl_list_adapter;
    private ArrayList<GirlClass> arrayList;
    RecyclerView rview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        rview=findViewById(R.id.rview);
        list();
    }

    public void list(){
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
        String url="https://apiforprojects.shop/GirlsList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("Response ", ">> " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    arrayList= new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        GirlClass Gclass= new GirlClass();
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Gclass.Stud_Number = jo.getString("Stud_Number");
                        Gclass.Stud_Name = jo.getString("Stud_Name");
                        Gclass.Stud_id = jo.getString("Stud_id");
                        Gclass.Guardian_Number = jo.getString("Gurdian_Number");
                        Gclass.Stud_Password = jo.getString("Stud_Password");
                        arrayList.add(Gclass);
                    }
                    girl_list_adapter = new Girl_List_Adapter(getApplicationContext(),arrayList);
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    rview.setLayoutManager(layoutManager);
                    rview.setAdapter(girl_list_adapter);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}