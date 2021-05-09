package com.example.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Objects;

public class Activity1 extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sb,dt;
    TextView txt;
    Button back,ring;
    String mRingUri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        txt=findViewById(R.id.detail);
        back=findViewById(R.id.back);
        sb=findViewById(R.id.sb);
        dt=findViewById(R.id.dt);
        ring=findViewById(R.id.ringtone);

        int curd=getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (curd==Configuration.UI_MODE_NIGHT_YES)
            dt.setChecked(true);

        sb.setOnClickListener(v -> hideStatusBar(sb.isChecked()));

        dt.setOnClickListener(v -> setDarkTheme(dt.isChecked()));

        ring.setOnClickListener(v -> {
            final Uri currentTone=
                    RingtoneManager.getActualDefaultRingtoneUri(Activity1.this,
                    RingtoneManager.TYPE_ALARM);
            Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
            if (mRingUri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(mRingUri));
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) currentTone);
            }
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent,999);
        });

        SeekBar seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Context context = getApplicationContext();

                boolean canWriteSettings = Settings.System.canWrite(context);

                if(canWriteSettings) {
                    int screenBrightnessValue = i*255/100;
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);
                }
                else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        back.setOnClickListener(v -> finish());
    }

    public void hideStatusBar(boolean enable) {

        View decorView = getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        int def=View.SYSTEM_UI_FLAG_VISIBLE;
        if (enable) {
            decorView.setSystemUiVisibility(uiOptions);
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
        else {
            decorView.setSystemUiVisibility(def);
            Objects.requireNonNull(getSupportActionBar()).show();
        }
    }

    public void setDarkTheme(boolean enable) {

        if (enable)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            txt.setText("From :" + uri.getPath());
            SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ringtone", uri.toString());
            editor.apply();
            mRingUri = uri.toString();
        }
    }
}