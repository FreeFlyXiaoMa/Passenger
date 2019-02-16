package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.junjun.bean.OrderBean;
import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.entity.JourneyEntity;
import com.junjun.poitest2.overlayutil.global.Data;
import com.junjun.poitest2.overlayutil.ratingbarView.RatingBar;

import org.apache.mina.core.session.IoSession;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class EvaluateActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingChangeListener {

    private TextView tvBack;
    private RatingBar ratingBar;
    private int currentCount;
    private Button btnEvaluate;
    private boolean isPay;
    private boolean isEvaluate;
    private float howMuch;
    private int starNumbers;
    private int id;
    private TextView tvPayNow;
    private TextView tvPrice;
    private TextView tvEvaluatePlease;
    private JourneyEntity table;
    //private float price;
    private int REQUEST_CODE=0;//请求码

    /**
     * 异步更新UI
     */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                tvPayNow.setText("已支付");
                tvPayNow.setClickable(false);
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar=getSupportActionBar();
        bar.hide();
        Connector.getDatabase();
        setContentView(R.layout.activity_evaluate);
        /*从上一activity获取id、起点、终点等信息*/
        getData();

        initView();

        if (isPay){
            tvPayNow.setText("已支付");
            tvPayNow.setClickable(false);
        }
        if (isEvaluate){
            tvEvaluatePlease.setText("您对这次的评价");
            ratingBar.setCount(starNumbers);
            btnEvaluate.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 初始化布局
     */
    private void initView(){
        tvBack=findViewById(R.id.tv_back_back_back);
        tvBack.setOnClickListener(this);

        ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setOnRatingChangeListener(this);

        btnEvaluate=findViewById(R.id.btn_evaluate);
        btnEvaluate.setOnClickListener(this);

        tvPayNow=findViewById(R.id.tv_pay_now);
        tvPayNow.setOnClickListener(this);

        tvPrice=findViewById(R.id.tv_price);

        tvEvaluatePlease=findViewById(R.id.tv_evaluate_please);

        //主线程操作---谨慎！！
        tvPrice.setText("￥"+howMuch);

    }
    //评价
    @Override
    public void onChange(RatingBar ratingBar, int preCount, int curCount) {
        Toast.makeText(this,curCount+"星评价",Toast.LENGTH_SHORT).show();
        currentCount=curCount;
    }

    //获取数据
    private IoSession session;
    private Data data;
    private void getData(){
        data=(Data)getApplication();
        session=data.getSession();

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        id=bundle.getInt("id");
        //price=bundle.getFloat("howmuch");
        table=DataSupport.find(JourneyEntity.class,id);
        starNumbers=table.getStarNumbers();
        howMuch=table.getHowMuch();
        isEvaluate=table.isEvaluated();
        isPay=table.isPay();
        //table.getEndAddress();
        Toast.makeText(this,"该条订单价格为："+howMuch,Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击事件处理
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back_back_back:
                this.finish();
                break;
            case R.id.btn_evaluate://提交评价
                if(isPay){
                    /*存入数据库*/
                    table.setPay(true);
                    table.setStarNumbers(currentCount);
                    table.setEvaluated(true);
                    table.setHowMuch(howMuch);
                    table.update(id);
                    new Thread(){
                        @Override
                        public void run() {
                            /*session.write(new CustomBean(data.getPhone(),0.0,0.0,"",
                                    0.0,0.0,"",0.0,0.0,0,true,
                                    true,starNumbers));*/
                            session.write(new OrderBean("", (int) howMuch,true,true,currentCount));
                        }
                    }.start();
                    this.finish();
                }else {
                    Toast.makeText(this,"提交评价之前，请先付费",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_pay_now://立即付费
                Intent intent=new Intent(getApplicationContext(),PayActivity.class);
                intent.putExtra("howMuch",howMuch);

                startActivityForResult(intent,REQUEST_CODE);
                //startActivity(new Intent(getApplicationContext(),PayActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==1){
            boolean paySucceed=data.getBooleanExtra("paySucceed",false);
            //boolean paySucceed=data.getBooleanExtra("paySycceed",false);
            Log.e("EvaluateActivity","收到,paySucceed="+paySucceed);
            if (paySucceed){
                isPay=paySucceed;
                handler.sendEmptyMessage(1);
            }
        }



        }

}
