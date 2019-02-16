package com.junjun.poitest2.overlayutil.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class PersonalData extends DataSupport {

    private int id;
    private String userName;
    private String phoneNumber;
    private String password;
    private String nickName;
    private String gender;
    private String year;
    private String business;
    private String company;
    private String profession;
    private String personalSign;
    private boolean realNameConfirmed;
    private boolean carOwnerConfirmed;

    //无参构造器
    public PersonalData(){super();}

    public PersonalData(int id, String userName, String phoneNumber, String password, String nickName, String gender,
                        String year, String business, String company, String profession, String personalSign,
                        boolean isRealNameConfirmed, boolean isCarOwnerConfirmed){
        super();
        this.id=id;
        this.userName=userName;
        this.phoneNumber=phoneNumber;
        this.password=password;
        this.nickName=nickName;
        this.gender=gender;
        this.year=year;
        this.business=business;
        this.company=company;
        this.profession=profession;
        this.personalSign=personalSign;
        this.realNameConfirmed=isRealNameConfirmed;
        this.carOwnerConfirmed=isCarOwnerConfirmed;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPersonalSign() {
        return personalSign;
    }

    public void setPersonalSign(String personalSign) {
        this.personalSign = personalSign;
    }

    public boolean isRealNameConfirmed() {
        return realNameConfirmed;
    }

    public void setRealNameConfirmed(boolean realNameConfirmed) {
        this.realNameConfirmed = realNameConfirmed;
    }

    public boolean isCarOwnerConfirmed() {
        return carOwnerConfirmed;
    }

    public void setCarOwnerConfirmed(boolean carOwnerConfirmed) {
        this.carOwnerConfirmed = carOwnerConfirmed;
    }





}
