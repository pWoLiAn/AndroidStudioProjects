package com.example.imagegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    TextView Result1, Result2, Result3;
    String values1, values2, values3;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Result1= findViewById(R.id.result1);
        Result2= findViewById(R.id.result2);
        Result3= findViewById(R.id.result3);

        sp= getSharedPreferences("MySP", Context.MODE_PRIVATE);

        values1= sp.getString("value1", "");
        values2= sp.getString("value2", "");
        values3= sp.getString("value3", "");

        Result1.setText("last game >> " + values1);
        Result2.setText("second last game >> " + values2);
        Result3.setText("third last game >> " + values3);

        if(values2.equals(""))
            Result2.setVisibility(View.INVISIBLE);
        if(values3.equals(""))
            Result3.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            startActivity(new Intent(ResultsActivity.this, MainActivity.class));
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}