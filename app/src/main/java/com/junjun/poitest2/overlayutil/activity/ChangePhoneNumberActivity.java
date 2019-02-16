package com.junjun.poitest2.overlayutil.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.entity.PersonalData;
import com.junjun.poitest2.overlayutil.otto.ActivityEvent;
import com.junjun.poitest2.overlayutil.otto.BusProvider;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class ChangePhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etInputNewNumber;
    private Button btnConfirmChange;
    private TextView tvCurrentNumber;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_change_phonenumber);
        //otto的注册
        BusProvider.getInstance().register(this);
        initView();

    }

    /**
     * 初始化布局
     */
    private void initView(){
        etInputNewNumber=findViewById(R.id.et_input_newPhoneNumber);

        btnConfirmChange=findViewById(R.id.btn_confirm_change);
        btnConfirmChange.setOnClickListener(this);

        tvCurrentNumber=findViewById(R.id.tv_current_phonenumber);

        tvBack=findViewById(R.id.tv_changephonenumber_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.e("确认更换之前作出判断","手机号码是否为空，同时为11位");
        if (etInputNewNumber.getText().toString() != null && etInputNewNumber.getText().toString().length() ==11){
            //更新手机号码
            BusProvider.getInstance().post(new ActivityEvent(etInputNewNumber.getText().toString()));
            tvCurrentNumber.setText("您当前手机号为"+etInputNewNumber.getText().toString());
            PersonalData personalDataTable=new PersonalData();
            personalDataTable.setPhoneNumber(etInputNewNumber.getText().toString());
            personalDataTable.update(1);
            int id=personalDataTable.getId();
            Log.e("手机号码更新成功，并存储在数据库","新手机号码为"+etInputNewNumber.getText().toString()+",id为"+String.valueOf(id));
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);//取消Bus的注册
    }

}
