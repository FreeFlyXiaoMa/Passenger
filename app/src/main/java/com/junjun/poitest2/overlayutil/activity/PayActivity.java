package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.keyboard.popwindow.SelectPopupWindow;

public class PayActivity extends AppCompatActivity implements View.OnClickListener,SelectPopupWindow.OnPopWindowClickListener{
    private SelectPopupWindow menuWindow;
    private Button btnPay;
    private int RESULT_CODE=1;
    float howMuch;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                tvHowMuch.setText(""+howMuch);
                tvNeedHowMuch.setText(howMuch+"");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_pay_detail);
        initView();

        Intent intent=getIntent();
        howMuch=intent.getFloatExtra("howMuch",0);
        handler.sendEmptyMessage(1);
    }


    private ImageView closeOne;
    private TextView tvHowMuch;
    private TextView tvNeedHowMuch;
    private void initView(){
        btnPay=findViewById(R.id.btn_confirm_pay);//确认支付
        btnPay.setOnClickListener(this);

        closeOne=findViewById(R.id.close_one);
        closeOne.setOnClickListener(this);

        tvHowMuch=findViewById(R.id.tv_order_howMuch);
        tvNeedHowMuch=findViewById(R.id.tv_need_pay_price);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_confirm_pay:
                initPayKeyboard();
                break;
            case R.id.close_one:
                this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化支付键盘
     */
    private void initPayKeyboard(){
        menuWindow = new SelectPopupWindow(this, this);
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int winHeight = getWindow().getDecorView().getHeight();
        menuWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, winHeight - rect.bottom);

    }

    @Override
    public void onPopWindowClickListener(String psw, boolean complete) {
        if(complete){
            if (psw.toString().equals("000000")){
                Toast.makeText(this, "密码正确"+psw, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("paySucceed",true);
                this.setResult(RESULT_CODE,intent);
                this.finish();
            }else {
                Toast.makeText(this, "密码不正确，请重新支付"+psw, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
