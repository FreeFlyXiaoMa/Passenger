package com.junjun.poitest2.overlayutil.global;

import org.apache.mina.core.session.IoSession;
import org.litepal.LitePalApplication;

public class Data extends LitePalApplication{
    private String phone;
    private IoSession session;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }
}
