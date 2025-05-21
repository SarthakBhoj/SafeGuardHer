package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class User_Selection extends AppCompatActivity {

    LocalTime time;
    CardView student,teacher,parent;
    TextView txtGreet;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        student = findViewById(R.id.StudentModule);
        teacher = findViewById(R.id.Teacher_Module);
        parent = findViewById(R.id.ParentModule);
        txtGreet = findViewById(R.id.txtGreet);
        time = LocalTime.now();
        String hrs= String.valueOf(time);
        hrs = hrs.substring(0,2);
//        Toast.makeText(this, hrs, Toast.LENGTH_SHORT).show();
        int currentTime = Integer.parseInt(hrs);

        if(currentTime>=0 && currentTime<12){
            txtGreet.setText("Good Morning");
        }if(currentTime >= 12 && currentTime < 18) {
            txtGreet.setText("Good Afternoon");
        }if(currentTime >= 18 && currentTime < 24){
            txtGreet.setText("Good Evening");
        }
        cardEvent();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (ContextCompat.checkSelfPermission(User_Selection.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(User_Selection.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }
    public void cardEvent(){

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Selection.this,StudentLogin.class);
                startActivity(intent);
            }
        });


        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Selection.this,Parent_Login.class);
                startActivity(intent);
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Selection.this, TeacherLogin.class);
                startActivity(intent);
            }
        });
    }
}