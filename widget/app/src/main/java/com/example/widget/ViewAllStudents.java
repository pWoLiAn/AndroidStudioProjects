package com.example.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewAllStudents extends menuOptions {

    ListView listView;
    CustomAdapter adapter;
    List<Student> students;
    SqlHelper helper;
    int id;
    String name;
    float cgpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_students);

        listView=findViewById(R.id.showAll);
        students=new ArrayList<>();
        helper=new SqlHelper(this);
        Cursor cursor=helper.getAllStudents();

        while(cursor.moveToNext()) {
            id=cursor.getInt(cursor.getColumnIndex("Id"));
            name=cursor.getString(cursor.getColumnIndex("Name"));
            cgpa=cursor.getFloat(cursor.getColumnIndex("Cgpa"));

            students.add(new Student(id, name, cgpa));
        }

        adapter=new CustomAdapter(this, R.layout.item_layout, students);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}