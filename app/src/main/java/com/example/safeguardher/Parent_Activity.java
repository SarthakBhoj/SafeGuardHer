package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

public class Parent_Activity extends AppCompatActivity {
    CardView Emergency,Reached;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Emergency =  findViewById(R.id.Emergency);
        Reached = findViewById(R.id.Reached);
        if (ContextCompat.checkSelfPermission(Parent_Activity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Parent_Activity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
        }if (ContextCompat.checkSelfPermission(Parent_Activity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Parent_Activity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Parent_Activity.this, "Calling..", Toast.LENGTH_SHORT).show();
                Intent call_intent = new Intent(Intent.ACTION_CALL);
                call_intent.setData(Uri.parse("tel:8390320270"));
                startActivity(call_intent);

            }
        });
        Reached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager =SmsManager.getDefault();
                String message;
                smsManager.sendTextMessage("8390320270",null,"Child is Reached",null,null);
                Toast.makeText(Parent_Activity.this, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}