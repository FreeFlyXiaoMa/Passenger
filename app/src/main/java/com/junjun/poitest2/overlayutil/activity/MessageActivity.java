package com.junjun.poitest2.overlayutil.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.junjun.poitest2.R;

public class MessageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_message);
    }
}
