package com.example.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class menuOptions extends AppCompatActivity {

    Context context;
    EditText text;
    Button button;
    SqlHelper helper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        context = this;
        helper = new SqlHelper(context);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.showAllItem:
                startActivity(new Intent(this, ViewAllStudents.class));
                break;
            case R.id.searchItem:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.editItem:
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_two_layout);
                dialog.setCancelable(true);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                text = dialog.findViewById(R.id.studIdTb);
                button = dialog.findViewById(R.id.findProductBtn);

                button.setOnClickListener(view -> {
                    String textStr = text.getText().toString();
                    if(textStr.isEmpty()) {
                        Toast.makeText(context, "Enter a product ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Cursor cursor = helper.getSingleStudent(Integer.parseInt(textStr));
                    if(cursor.getCount() == 0) {
                        Toast.makeText(context, "Product Does Not Exist", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(context, UpdateActivity.class);
                    intent.putExtra("id", Integer.parseInt(textStr));
                    startActivity(intent);
                });

                dialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
