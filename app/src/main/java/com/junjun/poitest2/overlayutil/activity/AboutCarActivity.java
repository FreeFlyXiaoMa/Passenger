package com.junjun.poitest2.overlayutil.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.junjun.poitest2.R;

/**
 * Created by Administrator on 2018/7/23 0023.
 */

public class AboutCarActivity extends AppCompatActivity {

    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_about_car);

        tvBack=findViewById(R.id.tv_back3);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
