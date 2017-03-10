package com.glory.upgrade.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.glory.upgrade.R;
import com.glory.upgrade.utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkVersion(this,true);
    }
}
