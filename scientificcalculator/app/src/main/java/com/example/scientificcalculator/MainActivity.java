package com.example.scientificcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button zero, one, two, three, four, five, six, seven, eight, nine, add, sub, mul, div, equal, clear;
    private Button log, in, mod, modulus, square, root, pi, cube, reci, sign, e, dot;
    private TextView result, info;
    private final char addition='+', subtraction='-', multiplication='*', division='/', equ=0, modulo='|';
    private double val1=Double.NaN, val2;
    private char action='x';

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();

        zero.setOnClickListener(v -> info.setText(info.getText().toString() + "0"));
        one.setOnClickListener(v -> info.setText(info.getText().toString() + "1"));
        two.setOnClickListener(v -> info.setText(info.getText().toString() + "2"));
        three.setOnClickListener(v -> info.setText(info.getText().toString() + "3"));
        four.setOnClickListener(v -> info.setText(info.getText().toString() + "4"));
        five.setOnClickListener(v -> info.setText(info.getText().toString() + "5"));
        six.setOnClickListener(v -> info.setText(info.getText().toString() + "6"));
        seven.setOnClickListener(v -> info.setText(info.getText().toString() + "7"));
        eight.setOnClickListener(v -> info.setText(info.getText().toString() + "8"));
        nine.setOnClickListener(v -> info.setText(info.getText().toString() + "9"));
        dot.setOnClickListener(v -> info.setText(info.getText().toString() + "."));
        sign.setOnClickListener(v -> info.setText(info.getText().toString() + "-"));
        pi.setOnClickListener(v -> info.setText(info.getText().toString() + "3.14"));
        e.setOnClickListener(v -> info.setText(info.getText().toString() + "2.71"));
        add.setOnClickListener(v -> {
            compute();
            action=addition;
            if (!Double.isNaN(val1)) result.setText(val1 +" + ");
            info.setText(null);
        });
        sub.setOnClickListener(v -> {
            compute();
            action=subtraction;
            if (!Double.isNaN(val1)) result.setText(val1 +" - ");
            info.setText(null);
        });
        mul.setOnClickListener(v -> {
            compute();
            action=multiplication;
            if (!Double.isNaN(val1)) result.setText(val1 +" x ");
            info.setText(null);
        });
        div.setOnClickListener(v -> {
            compute();
            action=division;
            if (!Double.isNaN(val1)) result.setText(val1 +" / ");
            info.setText(null);
        });
        mod.setOnClickListener(v -> {
            compute();
            action=modulo;
            if (!Double.isNaN(val1)) result.setText(val1+" mod ");
            info.setText(null);
        });
        clear.setOnClickListener(v -> {
            if(info.getText().length()>0) {
                CharSequence name=info.getText().toString();
                info.setText(name.subSequence(0,name.length()-1));
            }
            else {
                val1=Double.NaN;
                val2=Double.NaN;
                info.setText(null);
                result.setText(null);
            }
        });
        equal.setOnClickListener(v -> {
            compute();
            action=equ;
            if (!Double.isNaN(val1) && !Double.isNaN(val2)) result.setText(result.getText().toString()+ val2 +" = "+ val1);
            info.setText(null);
        });
        log.setOnClickListener(v -> {
            action='L';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        in.setOnClickListener(v -> {
            action='I';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        modulus.setOnClickListener(v -> {
            action='M';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        square.setOnClickListener(v -> {
            action='S';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        root.setOnClickListener(v -> {
            action='r';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        cube.setOnClickListener(v -> {
            action='C';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
        reci.setOnClickListener(v -> {
            action='R';
            compute();
            if (!Double.isNaN(val1)) result.setText(String.valueOf(val1));
            info.setText(null);
        });
    }

    private void setupUIViews() {

        zero=findViewById(R.id.zero);
        one=findViewById(R.id.one);
        two=findViewById(R.id.two);
        three=findViewById(R.id.three);
        four=findViewById(R.id.four);
        five=findViewById(R.id.five);
        six=findViewById(R.id.six);
        seven=findViewById(R.id.seven);
        eight=findViewById(R.id.eight);
        nine=findViewById(R.id.nine);
        dot=findViewById(R.id.dot);
        cube=findViewById(R.id.cube);
        e=findViewById(R.id.e);
        sign=findViewById(R.id.sign);
        reci=findViewById(R.id.reciprocal);
        pi=findViewById(R.id.pi);
        root=findViewById(R.id.sqrt);
        square=findViewById(R.id.square);
        modulus=findViewById(R.id.modulus);
        mod=findViewById(R.id.mod);
        in=findViewById(R.id.In);
        log=findViewById(R.id.log);
        add=findViewById(R.id.plus);
        sub=findViewById(R.id.minus);
        mul=findViewById(R.id.mul);
        div=findViewById(R.id.div);
        equal=findViewById(R.id.equal);
        clear=findViewById(R.id.clear);
        result=findViewById(R.id.result);
        info=findViewById(R.id.info);
    }

    private void compute() {

        if (!Double.isNaN(val1)) {
            try {
                val2 = Double.parseDouble(info.getText().toString());
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(),"Invalid second value !",Toast.LENGTH_SHORT).show();
                return;
            }

            switch (action) {
                case addition:
                    val1 = val1 + val2;
                    break;
                case subtraction:
                    val1 = val1 - val2;
                    break;
                case multiplication:
                    val1 = val1 * val2;
                    break;
                case division:
                    val1 = val1 / val2;
                    break;
                case modulo:
                    val1 = val1 % val2;
                    break;
                case equ:
                    break;
            }
            action='x';
        } else {
            try {
                val1 = Double.parseDouble(info.getText().toString());
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Invalid value !", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (action) {
                case 'L':
                    val1 = Math.log10(val1);
                    break;
                case 'I':
                    val1 = Math.log(val1);
                    break;
                case 'S':
                    val1 = val1 * val1;
                    break;
                case 'r':
                    val1 = Math.sqrt(val1);
                    break;
                case 'R':
                    val1 = 1 / val1;
                    break;
                case 'C':
                    val1 = val1 * val1 * val1;
                    break;
                case 'M':
                    if (val1 < 0)
                        val1 = -1 * val1;
                    break;
            }
        }

    }

}