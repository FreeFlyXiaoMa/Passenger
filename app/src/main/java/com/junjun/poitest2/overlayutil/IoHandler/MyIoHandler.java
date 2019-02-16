package com.junjun.poitest2.overlayutil.IoHandler;

import android.util.Log;

import com.junjun.bean.BasicDriverBean;
import com.junjun.bean.CodeBean;
import com.junjun.bean.LocationBean;
import com.junjun.bean.LoginResultBean;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MyIoHandler implements IoHandler {

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message.getClass() == CodeBean.class) {
            CodeBean code = (CodeBean) message;
            reCode.showCode(code);
        } else if (message.getClass() == LoginResultBean.class) {
            LoginResultBean result = (LoginResultBean)message;
            reLoginResult.showLoginResult(result);
        } else if (message.getClass() == LocationBean.class) {
            LocationBean location = (LocationBean)message;
            Log.e("MyIoHandler","接收到司机的定位信息"+location.getPhoneDriver());
            reLocation.showDriver(location);
        } else if (message.getClass() == BasicDriverBean.class) {
            BasicDriverBean driver = (BasicDriverBean)message;
            reDriver.showDialog(driver);
        }
        /*数据库存储司机信息*/
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {

    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {

    }

    //接口们
    private ReCode reCode;
    public void setReCodeListener(ReCode r) {
        this.reCode = r;
    }

    private ReLoginResult reLoginResult;
    public void setReLoginResultListener(ReLoginResult r){
        this.reLoginResult = r;
    }

    private ReLocation reLocation;
    public void setReLocationListener(ReLocation r){
        this.reLocation = r;
    }

    private ReDriver reDriver;
    public void setReDriverListener(ReDriver r){
        this.reDriver = r;
    }
}
