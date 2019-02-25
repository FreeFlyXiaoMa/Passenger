package com.junjun.poitest2.overlayutil.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.junjun.poitest2.overlayutil.IoHandler.MyIoHandler;
import com.junjun.poitest2.overlayutil.global.Data;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class SocketService extends Service {
    public SocketService() {
    }

    private InetSocketAddress remoteAddress = new InetSocketAddress("10.32.34.143",9976);
    @Override
    public void onCreate() {
        new Thread(){
            @Override
            public void run() {
                NioSocketConnector connector = new NioSocketConnector();
                connector.setConnectTimeoutMillis(3000);
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
                connector.setHandler(new MyIoHandler());

                ConnectFuture future = connector.connect(remoteAddress);
                future.awaitUninterruptibly();
                ((Data)getApplication()).setSession(future.getSession());
                Log.e("SocketService","开始连接服务端");
            }
        }.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}