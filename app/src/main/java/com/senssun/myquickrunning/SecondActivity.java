package com.senssun.myquickrunning;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP_MR1){
            SignalStrengthsHandler.getInstance(this);
        }
    }
}
