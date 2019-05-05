package com.junjun.poitest2.overlayutil.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.junjun.poitest2.R;
import com.junjun.poitest2.overlayutil.global.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearcherActivity extends AppCompatActivity implements TextWatcher, OnGetPoiSearchResultListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private PoiSearch mPoiSearch;
    private String city;
    private boolean isStartPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        Data data = (Data) getApplication();
        city = data.getCity();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_searcher);

        isStartPort = getIntent().getBooleanExtra("isStartPort", true);

        initView();
    }

    private EditText etSearcher;
    private ImageView ivCancel;
    private ListView lvResult;

    private void initView(){
        etSearcher = (EditText) findViewById(R.id.et_searcher);
        if (isStartPort){
            etSearcher.setHint("请输入出发地");
        }else {
            etSearcher.setHint("请输入目的地");
        }
        etSearcher.addTextChangedListener(this);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(this);
        lvResult = (ListView) findViewById(R.id.lvResult);
        lvResult.setOnItemClickListener(this);
    }

    //监听etSearcher文本变化的接口
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null){
            return;
        }
        PoiCitySearchOption option = new PoiCitySearchOption()
                .city(city)
                .keyword(s.toString())
                .pageCapacity(10);
        mPoiSearch.searchInCity(option);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //监听POI检索结果的接口
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        List<PoiInfo> list = poiResult.getAllPoi();
        if (list == null){
            return;
        }
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        //关键字搜索
        /*for (int i = 0; i < list.size(); i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("name", list.get(i).name);
            map.put("address", list.get(i).address);
            map.put("location", list.get(i).location);
            arrayList.add(map);
        }*/
        for(int i=0;i<10;i++){



        }

        SimpleAdapter adapter = new SimpleAdapter(SearcherActivity.this, arrayList, R.layout.list_item,
                new String[]{"name", "address"}, new int[]{R.id.tv_result_name, R.id.tv_result_address});
        lvResult.setAdapter(adapter);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    //lvResult列表项点击事件的接口
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
        Intent intent = new Intent(SearcherActivity.this, MapActivity.class);
        intent.putExtra("result", map);
        if (isStartPort){
            setResult(RESULT_OK, intent);
        }else {
            setResult(RESULT_OK, intent);
        }
        this.finish();
    }

    @Override
    public void onClick(View v) {
        etSearcher.setText("");
    }

    private long firstPressTime;
    @Override
    public void onBackPressed() {
        //当前系统时间，即第二次按返回键的时间
        if (System.currentTimeMillis() - firstPressTime < 2000) {
            startActivity(new Intent(getApplicationContext(),MapActivity.class));
        } else {
            Toast.makeText(this, "再点一次返回", Toast.LENGTH_SHORT).show();
            firstPressTime = System.currentTimeMillis();
        }
    }
}
