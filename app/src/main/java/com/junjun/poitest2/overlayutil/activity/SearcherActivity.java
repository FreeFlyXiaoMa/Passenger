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
import android.widget.LinearLayout;
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
    private LinearLayout search_history;
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

        search_history=findViewById(R.id.keyword_search_history);
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
        for (int i = 0; i < list.size(); i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("name", list.get(i).name);
            map.put("address", list.get(i).address);
            map.put("location", list.get(i).location);
            arrayList.add(map);
        }
        /*search_history.setVisibility(View.GONE);
        HashMap<String,Object> map1 = new HashMap<>();
        map1.put("name", "北方工业大学");
        map1.put("address","北京市石景山区晋元庄路5号");
        arrayList.add(map1);
        HashMap<String,Object> map2 = new HashMap<>();
        map2.put("name", "北京邮电大学");
        map2.put("address","北京市海淀区北土城路5号");
        arrayList.add(map2);
        HashMap<String,Object> map3 = new HashMap<>();
        map3.put("name", "北京大学");
        map3.put("address","北京市海淀区成府路205号");
        arrayList.add(map3);
        HashMap<String,Object> map4 = new HashMap<>();
        map4.put("name", "北京市");
        map4.put("address","北京市");
        arrayList.add(map4);
        HashMap<String,Object> map5 = new HashMap<>();
        map5.put("name", "北京市国际饭店");
        map5.put("address","北京市东城区建国门大街甲9号");
        arrayList.add(map5);
        HashMap<String,Object> map6 = new HashMap<>();
        map6.put("name", "北京市植物园");
        map6.put("address","北京市海淀区香山南路");
        arrayList.add(map6);
        HashMap<String,Object> map7 = new HashMap<>();
        map7.put("name", "北京市旧机动车交易市场");
        map7.put("address","北京市丰台区南四环西路123号");
        arrayList.add(map7);
        HashMap<String,Object> map8 = new HashMap<>();
        map8.put("name", "北京市西郊宾馆");
        map8.put("address","北京市海淀区王庄路18号");
        arrayList.add(map8);
        HashMap<String,Object> map9 = new HashMap<>();
        map9.put("name", "北京锡华商务酒店");
        map9.put("address","北京市海淀区颐和园路12号");
        arrayList.add(map9);
        HashMap<String,Object> map10 = new HashMap<>();
        map10.put("name", "北京市朝阳公园");
        map10.put("address","北京市朝阳区朝阳公园南路1号");
        arrayList.add(map10);*/

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
