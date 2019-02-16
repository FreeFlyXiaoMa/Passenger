package com.junjun.poitest2.overlayutil.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/7/6 0006.
 */

public class JourneyEntity extends DataSupport {

    private int id;
    private String journeyTime;
    private String startAddress;
    private String endAddress;
    private boolean isChecked;
    private boolean isEvaluated;//是否评价
    private boolean isPay;//是否付款
    private float howMuch;//价格多少
    private int starNumbers;//几星


    public JourneyEntity(){

    }

    //构造器
    private JourneyEntity(int id,String journeyTime,String startAddress,String endAddress,boolean isChecked,boolean isEvaluated
                        ,boolean isPay,float howMuch,int starNumbers){
        super();
        this.id=id;
        this.journeyTime=journeyTime;
        this.startAddress=startAddress;
        this.endAddress=endAddress;
        this.isChecked=isChecked;
        this.isEvaluated=isEvaluated;
        this.isPay=isPay;
        this.howMuch=howMuch;
        this.starNumbers=starNumbers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public float getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(float howMuch) {
        this.howMuch = howMuch;
    }

    public int getStarNumbers() {
        return starNumbers;
    }

    public void setStarNumbers(int starNumbers) {
        this.starNumbers = starNumbers;
    }

}
