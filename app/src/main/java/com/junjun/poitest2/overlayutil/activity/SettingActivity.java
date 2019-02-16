package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.switcher.Switch;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvBack;
    private RelativeLayout accountAndSecurity;
    private RelativeLayout rlAboutCar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView(){
        Switch switch1=findViewById(R.id.switch_changer);
        switch1.setChecked(true);

        tvBack=findViewById(R.id.tv_back1);
        tvBack.setOnClickListener(this);

        accountAndSecurity=findViewById(R.id.rl_accountAndSecurity);
        accountAndSecurity.setOnClickListener(this);

        rlAboutCar=findViewById(R.id.rl_about_car);
        rlAboutCar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back1:
                this.finish();
                break;
            case R.id.rl_accountAndSecurity:
                startActivity(new Intent(getApplicationContext(),AccountSecurityActivity.class));
                break;
            case R.id.rl_about_car:
                startActivity(new Intent(getApplicationContext(),AboutCarActivity.class));
                break;
            default:
                break;

        }
    }
}
