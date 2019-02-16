package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.junjun.bean.CodeBean;
import com.junjun.bean.GetCodeBean;
import com.junjun.bean.LoginBean;
import com.junjun.bean.LoginResultBean;
import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.IoHandler.MyIoHandler;
import com.junjun.poitest2.overlayutil.IoHandler.ReCode;
import com.junjun.poitest2.overlayutil.IoHandler.ReLoginResult;
import com.junjun.poitest2.overlayutil.global.Data;
import com.junjun.poitest2.overlayutil.service.SocketService;

import org.apache.mina.core.session.IoSession;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean isFirstLogin=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_login);
        Intent intent = new Intent(LoginActivity.this,SocketService.class);
        startService(intent);
        initSharedPreference();
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }


    private EditText etPhone;
    private EditText etCode;
    private Button btnGetCode;
    private Button btnLogin;
    private void initView(){
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCode = (EditText) findViewById(R.id.et_code);
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        btnGetCode.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    private void initSharedPreference(){
        SharedPreferences preferences = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.contains("token")){
            editor.putBoolean("token", false);
            editor.apply();
        }
        if (preferences.getBoolean("token",false)){
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.putExtra("phone",preferences.getString("phone",""));
            startActivity(intent);
        }
    }

    private boolean isPhone(String phone){
        boolean isPhone = false;
        String YD = "^1((3[4-9])|(5[012789])|(8[2378])|(47))[0-9]{8}$";
        String LT = "^1((3[0-2])|(5[56])|(8[56]))[0-9]{8}$";
        String DX = "^1((33)|(53)|(8[09]))[0-9]{8}$";
        if (phone.matches(YD)||phone.matches(LT)||phone.matches(DX)) {
            isPhone = true;
        }
        return isPhone;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_code :
                if (!isPhone(etPhone.getText().toString())){
                    Toast.makeText(LoginActivity.this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
                }else {
                    getCode();
                }
                break;
            case R.id.btn_login :
                if (isPhone(etPhone.getText().toString()) && !etCode.getText().toString().equals("")){
                    login();
                }
                break;
        }
    }

    private void getCode(){
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                CodeBean code = (CodeBean) msg.getData().getSerializable("code");
                assert code != null;
                Toast.makeText(LoginActivity.this, "验证码是："+code.getCode(), Toast.LENGTH_SHORT).show();
            }
        };

        final IoSession session = ((Data) getApplication()).getSession();
        MyIoHandler ioHandler = (MyIoHandler)session.getHandler();
        ioHandler.setReCodeListener(new ReCode() {
            @Override
            public void showCode(CodeBean code) {
                Message msg = new Message();
                Bundle b = new Bundle();
                Log.e("LoginActivity,","服务器返回验证码："+code.getCode());
                b.putSerializable("code",code);
                msg.setData(b);
                h.sendMessage(msg);
            }
        });
        new Thread(){
            @Override
            public void run() {
                session.write(new GetCodeBean(etPhone.getText().toString()));
            }
        }.start();
    }

    private void login(){
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                LoginResultBean result = (LoginResultBean) msg.getData().getSerializable("result");
                assert result != null;
                if (result.isSuccess()){
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    ((Data) getApplication()).setPhone(etPhone.getText().toString());
                    startActivity(new Intent(LoginActivity.this, MapActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this,"验证码输入错误，请重新输入",Toast.LENGTH_SHORT).show();
                }
            }
        };

        final IoSession session = ((Data) getApplication()).getSession();
        MyIoHandler ioHandler = (MyIoHandler)session.getHandler();
        ioHandler.setReLoginResultListener(new ReLoginResult() {
            @Override
            public void showLoginResult(LoginResultBean result) {
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putSerializable("result", result);
                msg.setData(b);
                h.sendMessage(msg);
            }
        });

        new Thread(){
            @Override
            public void run() {
                session.write(new LoginBean(true, etPhone.getText().toString(), etCode.getText().toString()));
            }
        }.start();
    }
}