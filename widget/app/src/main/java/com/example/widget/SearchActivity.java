package com.example.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends menuOptions {

    SearchView search;
    ListView list;
    List<Student> students;
    int idValue;
    String nameStr;
    float cgpaValue;
    SqlHelper helper;
    TextView name, cgpa;
    CustomAdapter adapter;
    Context context;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        students = new ArrayList<>();
        helper = new SqlHelper(this);
        context = this;
        Cursor cursor = helper.getAllStudents();
        list = findViewById(R.id.listView);

        while(cursor.moveToNext()) {
            idValue = cursor.getInt(cursor.getColumnIndex("Id"));
            nameStr = cursor.getString(cursor.getColumnIndex("Name"));
            cgpaValue = cursor.getFloat(cursor.getColumnIndex("Cgpa"));

            students.add(new Student(idValue, nameStr, cgpaValue));
        }

        adapter = new CustomAdapter(this, R.layout.item_layout, students);
        search = findViewById(R.id.searchView);
        list = findViewById(R.id.listView);
        list.setAdapter(adapter);
        adapter.getFilter().filter("");

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Student p = (Student) adapterView.getItemAtPosition(i);

            name = dialog.findViewById(R.id.nameDialog);
            cgpa = dialog.findViewById(R.id.mrpDialog);

            name.setText(p.getName());
            cgpa.setText("" + p.getCgpa());


            Button editButton = dialog.findViewById(R.id.editDialogBtn);
            editButton.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", p.getId());
                startActivity(intent);
            });

            dialog.show();
        });
    }
}