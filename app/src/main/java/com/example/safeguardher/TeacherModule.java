package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.time.LocalTime;

public class TeacherModule extends AppCompatActivity {

    LocalTime time;
    TextView txtGreet;
    CardView LeaveList,StudentList,logout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_module);
        LeaveList=findViewById(R.id.LeaveList);
        StudentList=findViewById(R.id.StudList);
        logout = findViewById(R.id.logout);
        txtGreet = findViewById(R.id.txtGreet);
        Greeting();
        getPermissions();

        LeaveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherModule.this,LeaveList.class);
                startActivity(intent);
            }
        });



        StudentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherModule.this,StudentList.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherModule.this);

                builder.setTitle("LOG OUT ALERT");
                builder.setMessage("Do You Really Want To Log out ? ");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String query = "DELETE FROM Configuration";
                        DBClass.execNonQuery(query);
                        Intent intent = new Intent(TeacherModule.this,User_Selection.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });
    }

    public void Greeting() {
        time = LocalTime.now();
        String hrs = String.valueOf(time);
        hrs = hrs.substring(0, 2);
        int currentTime = Integer.parseInt(hrs);
        if (currentTime >= 0 && currentTime < 12) {
            txtGreet.setText("Good Morning");
        }
        if (currentTime >= 12 && currentTime < 18) {
            txtGreet.setText("Good Afternoon");
        }
        if (currentTime >= 18 && currentTime < 24) {
            txtGreet.setText("Good Evening");
        }
    }

    public void getPermissions() {
        if (ContextCompat.checkSelfPermission(TeacherModule.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TeacherModule.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        if (ContextCompat.checkSelfPermission(TeacherModule.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TeacherModule.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }
        if (ContextCompat.checkSelfPermission(TeacherModule.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TeacherModule.this, new String[]{android.Manifest.permission.CALL_PHONE}, 100);
        }
    }
}