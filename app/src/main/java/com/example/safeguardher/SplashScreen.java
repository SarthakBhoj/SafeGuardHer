package com.example.safeguardher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        createDatabase();
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            String query = "SELECT * FROM Configuration";
//            Toast.makeText(SplashScreen.this,String.valueOf(DBClass.checkIfRecordExist(query)), Toast.LENGTH_SHORT).show();
            if (DBClass.checkIfRecordExist(query)){

                String val = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName='Screen_Number'");
//                Toast.makeText(SplashScreen.this,val, Toast.LENGTH_SHORT).show();
                if (val.equals("1")){
//                    Toast.makeText(SplashScreen.this,val , Toast.LENGTH_SHORT).show();
                    intent=new Intent(SplashScreen.this,TeacherModule.class);
                    startActivity(intent);
                    finish();
                }
                else if (val.equals("2")){
//                    Toast.makeText(SplashScreen.this, "Entering Main", Toast.LENGTH_SHORT).show();
                    intent=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(val.equals("3")){
                    intent=new Intent(SplashScreen.this,ParentModule.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    intent = new Intent(SplashScreen.this,User_Selection.class);
                    startActivity(intent);
                    finish();
                }

                finish();
            } else{
                intent = new Intent(SplashScreen.this,User_Selection.class);
                startActivity(intent);
                finish();
            }
        }
    },2700);
}
    public void createDatabase() {
        String query;
        DBClass.database = openOrCreateDatabase(DBClass.dbname, MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS Configuration(CName VARCHAR, CValue VARCHAR);";
        DBClass.execNonQuery(query);
//        Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();

    }
}