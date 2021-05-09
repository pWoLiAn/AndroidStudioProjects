package com.example.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    TextView n, e, m;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        SharedPreferences sp=getApplicationContext().getSharedPreferences("preference", Context.MODE_PRIVATE);

        String name=sp.getString("name","");
        String email=sp.getString("email","");
        String mobile=sp.getString("mobile","");

        n=findViewById(R.id.name1);
        e=findViewById(R.id.email1);
        m=findViewById(R.id.mobile1);
        back=findViewById(R.id.back1);

        n.setText(name);
        e.setText(email);
        m.setText(mobile);

        back.setOnClickListener(v -> {
            Intent intent=new Intent(Activity2.this, MainActivity.class);
            startActivity(intent);
        });
    }
}