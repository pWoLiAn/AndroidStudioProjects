package com.example.widget;

public class Student {
    int id;
    String name;
    float cgpa;

    Student(int Id, String Name, float Cgpa) {
        this.id=Id;
        this.name=Name;
        this.cgpa=Cgpa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public float getCgpa() {
        return cgpa;
    }

    public void setCgpa(float cgpa) {
        this.cgpa=cgpa;
    }
}
