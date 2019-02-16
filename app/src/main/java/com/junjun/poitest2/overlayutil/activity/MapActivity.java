package com.junjun.poitest2.overlayutil.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.junjun.bean.BasicDriverBean;
import com.junjun.bean.CustomBean;
import com.junjun.bean.LocationBean;
import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.DrivingRouteOverlay;
import com.junjun.poitest2.overlayutil.IoHandler.MyIoHandler;
import com.junjun.poitest2.overlayutil.IoHandler.ReDriver;
import com.junjun.poitest2.overlayutil.IoHandler.ReLocation;
import com.junjun.poitest2.overlayutil.entity.JourneyEntity;
import com.junjun.poitest2.overlayutil.global.Data;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.mina.core.session.IoSession;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, OnGetRoutePlanResultListener, SensorEventListener, BaiduMap.OnMapStatusChangeListener {
    private MapView mMapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private IoSession session;
    private String phone;
    private BitmapDescriptor car;
    private static ArrayList<HashMap<String, Object>> drivers = new ArrayList<>();
    private RoutePlanSearch search = null;
    private Data data;

    private Double mLastX = 0.0;
    private int mCurrentDirection = 0;
    private boolean isFirstLoc = true;    //是否第一次定位
    private float mCurrentAccuracy;
    private double mCurrentLat = 0.0;//乘客当前的经纬度
    private double mCurrentLng = 0.0;

    private MyLocationData locData;
    private SensorManager mSensorManager;
    BitmapDescriptor mCurrentMarker = null;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private RoundedImageView roundImageView;
    private Button btnPersonalData;

    private double driverLat;
    private double driverLong;

    //private boolean isFirstLogin=true;

    private SlidingMenu mSlidingMenu;


    Handler handler=new Handler(){
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                /*baiduMap.addOverlay(new MarkerOptions().icon(car)
                        .position(new LatLng(driverLat,driverLong))
                        .title("司机")
                );*/
               // baiduMap.clear();
                MarkerOptions marker = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car))
                        .position(new LatLng(driverLat,driverLong))
                        .title("司机");
                baiduMap.addOverlay(marker);

            }else if (msg.what==2){
                LocationBean bean= (LocationBean) msg.obj;
                String distance=bean.getDistance();
                //double distance= DistanceUtil.getDistance(new LatLng(driverLat,driverLong),new LatLng(mCurrentLat,mCurrentLng));
                //String formatDistance=new DecimalFormat("0000.0").format(distance);
                tvDistance.setText(distance);
            }else if (msg.what==3){//乘客已上车
                tvYou.setText("您已上车！");
                //tvYou.setTextColor(R.color.yellow);
                //tvYou.setTextSize(R.dimen.word_size);

                tvDistance.setVisibility(View.INVISIBLE);
                tvWait.setVisibility(View.INVISIBLE);
            }else if (msg.what==4){//订单已结束
                rlMainMenu.setVisibility(View.VISIBLE);
                llDriverDetail.setVisibility(View.GONE);
                tvState.setText("空闲");
                btnEnd.setText("目的地");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        data = (Data) getApplication();
        //隐藏标题栏
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //初始化litepal数据库，生成表
        Connector.getDatabase();
        session = data.getSession();
        phone = data.getPhone();
        car = BitmapDescriptorFactory.fromResource(R.mipmap.car);

        search = RoutePlanSearch.newInstance();//初始化路线规划
        search.setOnGetRoutePlanResultListener(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        MyIoHandler ioHandler = (MyIoHandler) session.getHandler();
        ioHandler.setReLocationListener(new ReLocation() {
            @Override
            public void showDriver(LocationBean location) {
                /*Toast.makeText(getApplicationContext(),"接收到司机端定位信息," + "经度：" +
                        location.getLongitude() + ",纬度：" + location.getLatitude(),Toast.LENGTH_SHORT).show();*/
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("locationCar", location);
                message.setData(bundle);
                handlerLocationBean.sendMessage(message);
            }
        });
        ioHandler.setReDriverListener(new ReDriver() {
            @Override
            public void showDialog(final BasicDriverBean driver) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("driver", driver);
                message.setData(bundle);
                handlerDriverBean.sendMessage(message);
            }
        });

        new Thread() {
            @Override
            public void run() {
                session.write(0);
            }
        }.start();

        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);//隐藏缩放比例尺
        mMapView.showZoomControls(false);//隐藏放大缩小按钮
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        /*在此处添加显示当前位置的图标*/
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.arrow);
        //baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true
          //      , mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor));
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true
                , mCurrentMarker));

        //重新定位
        ReLocation(mCurrentLat,mCurrentLng);

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        initSlidingMenu();
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        search.destroy();
        session.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        ReLocation(mCurrentLat,mCurrentLng);
    }

    /**
     * 重新定位
     */
    private void ReLocation(double lat,double lng) {
        LatLng latLng=new LatLng(lat,lng);
        //MapStatus mapStatus = new MapStatus.Builder().zoom(16).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.setMapStatus(mapStatusUpdate);


    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        //取消传感器监听
        mSensorManager.unregisterListener(this);

        super.onStop();
    }

    private long firstPressTime;

    @Override
    public void onBackPressed() {
        //当前系统时间，即第二次按返回键的时间
        if (System.currentTimeMillis() - firstPressTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
            firstPressTime = System.currentTimeMillis();
        }
    }

    private TextView tvCity;
    private Button btnStart;
    private Button btnEnd;
    private TextView tvState;
    private Button btnSearch;
    private Button btnOrder;
    private Button btnMessage;
    private TextView tvOrders;
    private TextView tvSetting;
    private ImageView myLocation;
    private TextView tvMyWallet;
    private RelativeLayout rlMainMenu;//信息栏
    private LinearLayout llDriverDetail;//司机信息栏
    private ImageView ivCall;//司机信息栏中，给司机打电话按钮
    private TextView tvDistance;
    private TextView tvYou;
    private TextView tvWait;
    private void initView() {
        tvCity = (TextView) findViewById(R.id.tv_city);//

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        btnEnd = (Button) findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(this);

        tvState = (TextView) findViewById(R.id.tv_state);
        tvState.setText("空闲");

        btnSearch = (Button) findViewById(R.id.btn_search);//查看路线按钮
        btnSearch.setOnClickListener(this);

        btnOrder = (Button) findViewById(R.id.btn_order);
        btnOrder.setOnClickListener(this);

        myLocation=findViewById(R.id.my_location);//重定位图标
        myLocation.setOnClickListener(this);

        rlMainMenu=findViewById(R.id.rl_main_menu);//信息栏
        llDriverDetail=findViewById(R.id.ll_driver_detail);

        ivCall=findViewById(R.id.iv_call);
        ivCall.setOnClickListener(this);

        tvYou=findViewById(R.id.tv_you);
        tvDistance=findViewById(R.id.tv_distance);
        tvWait=findViewById(R.id.tv_wait_please);

        /*消息界面*/
        btnMessage=findViewById(R.id.btn_message);
        btnMessage.setOnClickListener(this);

        /*个人资料界面的布局初始化*/
        roundImageView=(RoundedImageView) findViewById(R.id.roundImageView);
        btnPersonalData=findViewById(R.id.btn_personal_data);
        btnPersonalData.setOnClickListener(this);

        tvOrders=findViewById(R.id.tv_orders);//订单
        tvOrders.setClickable(true);
        tvOrders.setOnClickListener(this);

        tvSetting=findViewById(R.id.tv_setting);//设置
        tvSetting.setOnClickListener(this);

        tvMyWallet=findViewById(R.id.tv_myWallet);//钱包
        tvMyWallet.setOnClickListener(this);
    }

    /**
     * 初始化侧边栏SlidingMenu
     */
    private void initSlidingMenu(){
        mSlidingMenu=new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT); //设置从左侧弹出/划出SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//设置取消手势滑动翻转侧边栏
        mSlidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);//绑定到哪一个activity对象
        mSlidingMenu.setMenu(R.layout.layout_left);
        mSlidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(60000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        //option.setAddrType("all");
        mLocationClient.setLocOption(option);
    }

    private int price;
    private boolean isScan = false;

    /**
     * View的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start://起点
                Intent intentStart = new Intent(MapActivity.this, SearcherActivity.class);
                intentStart.putExtra("isStartPort", true);
                startActivityForResult(intentStart, 0);
                break;
            case R.id.btn_message://点击查看消息页面3
                startActivity(new Intent(MapActivity.this,MessageActivity.class));
                break;
            case R.id.btn_end://终点
                Intent intentEnd = new Intent(MapActivity.this, SearcherActivity.class);
                intentEnd.putExtra("isStartPort", false);
                startActivityForResult(intentEnd, 1);
                break;
            case R.id.btn_search://搜索
                if (startLocation == null || endLocation == null) {
                    Toast.makeText(MapActivity.this, "请输入完整线路信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                PlanNode startNode = PlanNode.withLocation(startLocation);
                PlanNode endNode = PlanNode.withLocation(endLocation);
                /*添加驾驶路线功能*/
                DrivingRoutePlanOption option1 = new DrivingRoutePlanOption();
                option1.currentCity(tvCity.getText().toString());
                option1.from(startNode);
                option1.to(endNode);
                search.drivingSearch(option1);
                break;
            case R.id.my_location://重定位
                ReLocation(mCurrentLat,mCurrentLng);
                break;
            case R.id.btn_order://下单
                if (!isScan) {
                    Toast.makeText(MapActivity.this, "请先查看路线", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startName == null) {
                    startName = startAddress;
                }
                new Thread() {
                    @Override
                    public void run() {
                        session.write(new CustomBean(phone,mCurrentLat,mCurrentLng,startName, startLocation.latitude, startLocation.longitude,
                                endName, endLocation.latitude, endLocation.longitude, price));
                    }
                }.start();
                tvState.setText("等待司机接单，请稍候......");
                break;
            /*给司机打电话*/
            case R.id.iv_call:
                isToCallToDriver();
                break;
            /*侧边栏部分的按钮事件*/
            case R.id.btn_personal_data://弹出个人资料侧边栏
                    mSlidingMenu.toggle();
                    break;
            case R.id.tv_orders://查看个人历史订单
                startActivity(new Intent(getApplicationContext(),HistoryList.class));
                break;
            case R.id.tv_setting://设置
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                break;
            case R.id.tv_myWallet:
                startActivity(new Intent(getApplicationContext(),MyWalletActivity.class));
                break;

            default:
                break;
        }
    }

    //监听路线规划结果的接口
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
    }

    //公交线路规划
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            //DrivingRouteOverlay overlay=new DrivingRouteOverlay(baiduMap);
            overlay.setData(routeLine);
            overlay.addToMap();
            overlay.zoomToSpan();
            Log.e("MapActivity", "从起点到终点距离：" + routeLine.getDistance());

            /*计算从起点到终点的乘车价格*/
            price = (int) (routeLine.getDistance() * 0.003f);
            tvState.setText("路线规划成功！大约需要" + price + "元");

            isScan = true;
        }

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    /**
     * 显示一条驾车路线的overlay，定制RouteOverlay
     */
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_me_history_startpoint);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_me_history_finishpoint);
        }
    }


    //添加方向传感器，以根据手机方向改变定位方向
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //判断返回的传感器类型是不是方向传感器
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            //只获取x的值
            double x = sensorEvent.values[SensorManager.DATA_X];
            //为了防止经常性的更新
            if (Math.abs(x - mLastX) > 1.0) {
                mCurrentDirection = (int) x;
                locData = new MyLocationData.Builder().accuracy(mCurrentAccuracy)
                        .direction(mCurrentDirection)
                        .latitude(mCurrentLat)
                        .longitude(mCurrentLng)
                        .build();
                baiduMap.setMyLocationData(locData);

            }
            mLastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {


    }


    //接收到自己定位信息的处理函数
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //mapView销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            mCurrentAccuracy = location.getRadius();
            mCurrentLat = location.getLatitude();
            mCurrentLng = location.getLongitude();
            //将获取的location信息给百度map
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360，mLastX就是获取到的方向传感器传来的X轴数值
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locData);

            tvCity.setText(location.getCity());
            data.setCity(location.getCity());
            Log.e("MapActivity", "我现在的位置，城市：" + location.getCity() + "经度：" + location.getLongitude() +
                    ",纬度：" + location.getLatitude() + "，位置信息：" + location.getAddrStr() + ",地址：" + location.getAddress());
            btnStart.setText("我的位置");
            startAddress = location.getAddrStr();
            startLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                /*LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));*/
                ReLocation(location.getLatitude(),location.getLongitude());
            }
        }
    }

    //接收到司机端定位信息的Handler
    private HandlerLocationBean handlerLocationBean = new HandlerLocationBean();

    private class HandlerLocationBean extends Handler {
        /*接收到司机的定位信息--------------------------------------------------------------------*/
        @Override
        public void handleMessage(Message msg) {

            LocationBean bean = (LocationBean) msg.getData().getSerializable("locationCar");
            //driverLat=bean.getLatitude();
            //driverLong=bean.getLongitude();
            Message message=new Message();
            message.obj=bean;
            message.what=2;
            handler.sendMessage(message);
            //handler.sendEmptyMessage(1);//在地图上添加司机的图标
            //Toast.makeText(getApplicationContext(),"接收到司机端定位信息," + "经度：" + bean.getLongitude() + ",纬度：" + bean.getLatitude(),Toast.LENGTH_SHORT).show();
            //Log.e("MapActivity", "接收到司机端定位信息," + "经度：" + bean.getLongitude() + ",纬度：" + bean.getLatitude());
            //assert bean != null;
            //在纬度方向上增加0.01f的值，防止司机与乘客在同一点看不到司机位置

            MarkerOptions marker = new MarkerOptions()
                    .icon(car)
                    .position(new LatLng(bean.getLatitude(), bean.getLongitude()));
            marker.visible(true);
            boolean isExists = false;

            for (HashMap<String, Object> map : drivers) {
                if (map.get("phone").equals(bean.getPhoneDriver())) {
                    isExists = true;
                    ((MarkerOptions) map.get("marker")).position(new LatLng(bean.getLatitude(), bean.getLongitude()));
                    map.put("marker", marker);
                }
            }
            if (!isExists) {
                baiduMap.addOverlay(marker);
                Toast.makeText(getApplicationContext(),"添加overlay",Toast.LENGTH_SHORT).show();
                HashMap<String, Object> newMap = new HashMap<>();
                newMap.put("phone", bean.getPhoneDriver());
                newMap.put("marker", marker);
                drivers.add(newMap);
                //drivers.add(newMap);//添加两个司机图标
            }
           // baiduMap.clear();


        }
    }


    private String driverPhone;
    //接收到订单被抢的信息的Handler
    private HandlerDriverBean handlerDriverBean = new HandlerDriverBean();

    private class HandlerDriverBean extends Handler {

        /*接收到司机已接单的消息------------------------------------------------------------------*/
        @Override
        public void handleMessage(Message msg) {
            final BasicDriverBean bean = (BasicDriverBean) msg.getData().getSerializable("driver");
            Toast.makeText(getApplicationContext(),bean.getDriverName()+"已接单,车牌是："+bean.getPlateNumber(),
                                    Toast.LENGTH_SHORT).show();
            driverPhone=bean.getPhoneDriver();
            showDriverDetail();
            if (bean.isPassengerGetIn()){//从司机端，获取乘客是否已上车
                Toast.makeText(getApplicationContext(),"乘客已上车",Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(3);
                if (bean.isFinishOrder()){//从司机端获取，该订单已结束
                    handler.sendEmptyMessage(4);
                    Toast.makeText(getApplicationContext(),"订单已结束",Toast.LENGTH_SHORT).show();

                    /*将本次打车记录存储到本地数据库*/
                    long l=System.currentTimeMillis();
                    Date date=new Date(l);
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    if (startName != null && endName!=null){
                        JourneyEntity table=new JourneyEntity();
                        table.setJourneyTime(dateFormat.format(date));
                        table.setStartAddress(startName.toString());
                        table.setEndAddress(endName.toString());
                        table.setChecked(false);
                        table.setHowMuch((float) price);
                        table.setEvaluated(false);
                        table.setPay(false);
                        table.setStarNumbers(0);
                        table.save();
                        Log.e("MapActivity","litePal存储到本地,时间："+dateFormat.format(date)+"，起点："+startName+"，终点"+endName);
                }
            }else {//收到司机的信息，但是乘客还未上车
                //showDriverDetail();
                tvState.setText("司机已接单，订单进行中!");
            }


            //handler.sendEmptyMessage(1);//在地图上添加司机的图标
            //assert bean != null;
            /*if (!bean.getPhoneCustom().equals(phone)) {
                return;
            }*/

            //btnEnd.setText("目的地");


            }

        }
    }

    /**
     * 是否给司机打电话
     */
    private void isToCallToDriver(){
        AlertDialog dialog = new AlertDialog.Builder(MapActivity.this)
                .setCancelable(false)
                .setMessage("接单司机手机号是：\n" + driverPhone + "\n" +
                        "是否拨打电话？")
                .setPositiveButton("打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + driverPhone));
                        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MapActivity.this, "用户未启用拨打电话权限", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(intent);
                    }
                }).setNegativeButton("不打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        btnOrder.setClickable(false);
                    }
                }).create();
        dialog.show();
    }

    /**
     * 收到司机接单消息后，隐藏信息栏，显示司机信息栏
     */
    private void showDriverDetail(){
        rlMainMenu.setVisibility(View.GONE);
        llDriverDetail.setVisibility(View.VISIBLE);

    }

    //从SearcherActivity返回结果的处理函数
    private String startName;
    private String startAddress;
    private LatLng startLocation;
    private String endName;
    private String endAddress;
    private LatLng endLocation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HashMap<String, Object> map = (HashMap<String, Object>) data.getSerializableExtra("result");
        //起点位置搜索，返回的结果
        if (requestCode == 0) {
            startName = (String) map.get("name");
            startAddress = (String) map.get("address");
            startLocation = (LatLng) map.get("location");
            btnStart.setText(startName);
        } else if (requestCode == 1) {//终点位置搜索,返回的结果
            endName = (String) map.get("name");
            endAddress = (String) map.get("address");
            endLocation = (LatLng) map.get("location");
            btnEnd.setText("到"+endName);
        }
    }


}
