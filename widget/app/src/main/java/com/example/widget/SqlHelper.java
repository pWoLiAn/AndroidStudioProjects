package com.example.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    ContentValues cv;

    public SqlHelper(Context context) {
        super(context, "AppData.db", null, 1);
        db=getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Students(Id INTEGER primary key, Name TEXT, Cgpa FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Products");
    }

    public boolean insertStudent(Student student) {
        cv=new ContentValues();
        cv.put("Id", student.getId());
        cv.put("Name", student.getName());
        cv.put("Cgpa", student.getCgpa());

        return db.insert("Students", null, cv) >= 0;
    }

    public boolean updateStudent(Student student) {
        cv=new ContentValues();
        cv.put("Name", student.getName());
        cv.put("Cgpa", student.getCgpa());

        return db.update("Students", cv, "Id=?", new String[]{"" + student.getId()}) >= 0;
    }

    public Cursor getAllStudents() {
        return db.rawQuery("Select * from Students", null);
    }

    public Cursor getSingleStudent(int Id) {
        return db.rawQuery("Select * from Students where Id=?", new String[]{""+Id});
    }
}
