package com.example.imagegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView audiTv, benzTv, citroenTv, chevroletTv, toyotaTv, renaultTv;
    ImageView audiIv, benzIv, citroenIv, chevroletIv, toyotaIv, renaultIv;
    Button submitBtn;
    int i=0, t=0, f=0;
    SharedPreferences sp;
    String last1, last2, last3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        audiTv=findViewById(R.id.audiText);
        benzTv=findViewById(R.id.benzText);
        citroenTv=findViewById(R.id.citroenText);
        chevroletTv=findViewById(R.id.chevroletText);
        toyotaTv=findViewById(R.id.toyotaText);
        renaultTv=findViewById(R.id.renaultText);

        audiIv=findViewById(R.id.audiImg);
        benzIv=findViewById(R.id.benzImg);
        citroenIv=findViewById(R.id.citroenImg);
        chevroletIv=findViewById(R.id.chevroletImg);
        toyotaIv=findViewById(R.id.toyotaImg);
        renaultIv=findViewById(R.id.renaultImg);

        submitBtn=findViewById(R.id.submit);

        audiTv.setOnTouchListener(new ChoiceTouchListener());
        benzTv.setOnTouchListener(new ChoiceTouchListener());
        citroenTv.setOnTouchListener(new ChoiceTouchListener());
        chevroletTv.setOnTouchListener(new ChoiceTouchListener());
        toyotaTv.setOnTouchListener(new ChoiceTouchListener());
        renaultTv.setOnTouchListener(new ChoiceTouchListener());

        audiIv.setOnDragListener(new ChoiceDragListener());
        benzIv.setOnDragListener(new ChoiceDragListener());
        citroenIv.setOnDragListener(new ChoiceDragListener());
        chevroletIv.setOnDragListener(new ChoiceDragListener());
        toyotaIv.setOnDragListener(new ChoiceDragListener());
        renaultIv.setOnDragListener(new ChoiceDragListener());

        submitBtn.setOnClickListener(v -> {

            if (t<6) {
                Toast.makeText(getApplicationContext(),"Match ALL images!!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(f==1) {
                Toast.makeText(getApplicationContext(), "Restart the game!!", Toast.LENGTH_SHORT).show();
                return;
            }

            f=1;

            NotificationManager mNotificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID=null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CHANNEL_ID = "Channel_1";
                CharSequence name = "Channel";
                String Description = "My channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            Spannable sb = new SpannableString("GAME RESULT!!!");
            sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            assert CHANNEL_ID != null;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.notif);
            mBuilder.setContentTitle(sb);

            if (i == 6) {
                mBuilder.setContentText("Game Won");
                last3= last2;
                last2= last1;
                last1= "Won";
            }
            else {
                mBuilder.setContentText("Game Lost");
                last3= last2;
                last2= last1;
                last1= "Lost";
            }

            saveData();

            Intent resultIntent = new Intent(this, ResultsActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(ResultsActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotificationManager.notify(1, mBuilder.build());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                finishAffinity();
                System.exit(0);
                break;
            case R.id.restart:
                finish();
                startActivity(getIntent());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final class ChoiceTouchListener implements View.OnTouchListener {
        @SuppressLint({"NewApi", "ClickableViewAccessibility"})
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    @SuppressLint("NewApi")
    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            if (event.getAction() == DragEvent.ACTION_DROP) {

                t++;

                View view = (View) event.getLocalState();

                ImageView dropTarget = (ImageView) v;
                TextView dropped = (TextView) view;

                String logo = (String) dropTarget.getTag();
                String txt = dropped.getText().toString();

                view.setVisibility(View.INVISIBLE);

                Object tag = dropTarget.getTag();

                if (tag == null) {

                    view.setVisibility(View.VISIBLE);
                }

                if (logo.equals(txt)) i++;

                dropTarget.setOnDragListener(null);

            }

            return true;
        }
    }

    public void saveData() {

        sp= getSharedPreferences("MySP", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sp.edit();
        myEdit.putString("value1", last1);
        myEdit.putString("value2", last2);
        myEdit.putString("value3", last3);
        myEdit.apply();
    }

    public void loadData() {

        sp= getSharedPreferences("MySP", Context.MODE_PRIVATE);
        last1= sp.getString("value1", "");
        last2= sp.getString("value2", "");
        last3= sp.getString("value3", "");
    }


}