package com.example.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText n, e, m;
    Button save, show;
    SharedPreferences sp;
    int f = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        n = findViewById(R.id.name);
        e = findViewById(R.id.email);
        m = findViewById(R.id.mobile);
        save = findViewById(R.id.save);
        show = findViewById(R.id.show);

        sp = getSharedPreferences("preference", Context.MODE_PRIVATE);

        save.setOnClickListener(v -> {
            f = 1;

            String name = n.getText().toString();
            String email = e.getText().toString();
            String mobile = m.getText().toString();

            if (name.equals("")) {
                n.setError("Name cannot be empty !");
                n.requestFocus();
                return;
            }
            if (email.equals("")) {
                e.setError("Email cannot be empty !");
                e.requestFocus();
                return;
            }
            if (mobile.equals("")) {
                m.setError("Mobile No. cannot be empty !");
                m.requestFocus();
                return;
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("mobile", mobile);
            editor.apply();

            Toast.makeText(getApplicationContext(), "Saved successfully !", Toast.LENGTH_SHORT).show();
        });

        show.setOnClickListener(v -> {

            if (f == 0) {
                Toast.makeText(getApplicationContext(), "Nothing saved !", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, Activity2.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"QueryPermissionsNeeded", "NonConstantResourceId"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myapp:
                PackageManager pm = getPackageManager();
                Intent intent1 = pm.getLaunchIntentForPackage("com.example.fragment");
                if (intent1 != null) {
                    startActivity(intent1);
                }
                break;
            case R.id.selfie:
                Intent intent2 = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                if (intent2.resolveActivity(getPackageManager())!=null) {
                    startActivity(intent2);
                }
                break;
            case R.id.exit:
                finishAffinity();
                System.exit(0);
                break;
            case R.id.control:
                Intent intent3 = new Intent(MainActivity.this, Activity1.class);
                startActivity(intent3);
                break;
        }
                return super.onOptionsItemSelected(item);

    }
}