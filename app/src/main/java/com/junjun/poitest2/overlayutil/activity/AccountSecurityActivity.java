package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.otto.ActivityEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public class AccountSecurityActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rlChangePhoneNumber;
    private TextView tvPhoneNumber;
    private String newPhoneNumber;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_account_security);
        initView();

    }

    /**
     * 初始化布局
     */
    private void initView(){
        rlChangePhoneNumber=findViewById(R.id.rl_change_phoneNumber);
        rlChangePhoneNumber.setOnClickListener(this);

        tvBack=findViewById(R.id.tv_accountAndSecurity_back);
        tvBack.setOnClickListener(this);

        tvPhoneNumber=findViewById(R.id.tv_phoneNumber);

    }

    /**
     * 事件的订阅
     */
    @Subscribe
   public void onActivityEvent(ActivityEvent event){
       newPhoneNumber=event.getEvent();
        Log.e("收到更改手机号码页面传回的数据","新手机号码为"+newPhoneNumber.toString());
        tvPhoneNumber.setText(newPhoneNumber.toString());
   }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_change_phoneNumber:
                startActivity(new Intent(this,ChangePhoneNumberActivity.class));
                break;
            case R.id.tv_accountAndSecurity_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
