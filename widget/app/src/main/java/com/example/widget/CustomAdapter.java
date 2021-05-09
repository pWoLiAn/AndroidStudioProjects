package com.example.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Student> {

    Context c;
    List <Student> students;
    int r;
    TextView id, name, cgpa;

    public CustomAdapter( Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        c=context;
        r=resource;
        students=new ArrayList<>(objects);
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int idValue=getItem(position).getId();
        String nameValue=getItem(position).getName();
        float cgpaValue=getItem(position).getCgpa();

        LayoutInflater inflater = LayoutInflater.from(c);
        convertView = inflater.inflate(r, parent, false);

        id=convertView.findViewById(R.id.idTv);
        name=convertView.findViewById(R.id.nameTv);
        cgpa=convertView.findViewById(R.id.cgpaTv);

        id.setText(""+idValue);
        name.setText(nameValue);
        cgpa.setText(""+cgpaValue);

        return convertView;
    }

    private final Filter productFilter =  new Filter() {

        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults result = new FilterResults();
            ArrayList<Student> studentsOne=new ArrayList<>();

            String filterPattern = constraint.toString().toLowerCase().trim();

            if (!filterPattern.isEmpty()) {
                for (Student stud : students) {
                    if (stud.getName().toLowerCase().contains(filterPattern)) {
                        studentsOne.add(stud);
                    }
                }
            }
            result.values = studentsOne;
            result.count=studentsOne.size();

            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List<Student>) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Student)resultValue).getName();
        }
    };

    @NonNull
    @Override
    public Filter getFilter() {
        return productFilter;
    }
}
