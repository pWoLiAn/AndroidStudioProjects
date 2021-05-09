package com.example.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateActivity extends menuOptions {

    SqlHelper helper;
    Student student;
    EditText id, name, cgpa;
    Button update, cancel;
    Context context;
    int idValue;
    String nameStr;
    float cgpaValue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        helper = new SqlHelper(this);
        context = this;
        id = findViewById(R.id.idUpdEt);
        name = findViewById(R.id.nameUpdEt);
        cgpa = findViewById(R.id.cgpaUpdEt);
        update = findViewById(R.id.updBtn);
        cancel = findViewById(R.id.cancelBtn);

        Cursor cursor = helper.getSingleStudent(getIntent().getIntExtra("id", -1));

        if (cursor.moveToFirst()) {
            id.setText("" + cursor.getInt(cursor.getColumnIndex("Id")));
            name.setText(cursor.getString(cursor.getColumnIndex("Name")));
            cgpa.setText("" + cursor.getFloat(cursor.getColumnIndex("Cgpa")));
        }

        update.setOnClickListener(view -> {

            idValue = Integer.parseInt(id.getText().toString());
            nameStr = name.getText().toString();
            cgpaValue = Float.parseFloat(cgpa.getText().toString());

            if (id.getText().toString().isEmpty() || nameStr.isEmpty() || cgpa.getText().toString().isEmpty()) {
                Toast.makeText(context, "No field should be empty", Toast.LENGTH_SHORT).show();
            } else {
                student = new Student(idValue, nameStr, cgpaValue);

                if (helper.updateStudent(student)) {
                    Toast.makeText(context, "Student Updated Successfully !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, ViewAllStudents.class));
                    finish();
                } else {
                    Toast.makeText(context, "Student Update Failed !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(view -> {
            startActivity(new Intent(context, ViewAllStudents.class));
            finish();
        });
    }
}
