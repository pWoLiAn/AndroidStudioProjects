package com.example.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends menuOptions {

    EditText id, name, cgpa;
    String idStr, nameStr, cgpaStr;
    Button add;
    SqlHelper helper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id=findViewById(R.id.idEt);
        name=findViewById(R.id.nameEt);
        cgpa=findViewById(R.id.cgpaEt);
        add=findViewById(R.id.addBtn);
        context=this;
        helper=new SqlHelper(context);

        add.setOnClickListener(v -> {
            idStr=id.getText().toString();
            nameStr=name.getText().toString();
            cgpaStr=cgpa.getText().toString();

            if(idStr.isEmpty() || nameStr.isEmpty() || cgpaStr.isEmpty()) {
                Toast.makeText(context,"Some fields are empty !",Toast.LENGTH_SHORT).show();
            }

            else {
                Student student=new Student(Integer.parseInt(idStr), nameStr, Float.parseFloat(cgpaStr));
                if(helper.insertStudent(student)) {
                    Toast.makeText(context,"Student inserted successfully !",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, ViewAllStudents.class));
                }
                else {
                    Toast.makeText(context,"Student insert failed !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}