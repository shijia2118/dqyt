package com.hxjt.dqyt.app;

import static com.hxjt.dqyt.app.Constants.DEFAULT_IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.DEFAULT_PORT;
import static com.hxjt.dqyt.app.Constants.IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.PORT;

import android.content.Context;
import androidx.multidex.MultiDex;

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;
import com.hxjt.dqyt.base.BaseApplication;
import com.hxjt.dqyt.utils.SPUtil;

import org.simple.eventbus.EventBus;

public class App extends BaseApplication {

    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initEasySocket();

        EasySocket.getInstance().subscribeSocketAction(iSocketActionListener);
    }

    /**
     * 初始化EasySocket
     */
    private void initEasySocket() {

        String ip = SPUtil.getString(IP_ADDRESS,"");
        int port = (int) SPUtil.get(PORT,-1);

        if(ip.isEmpty()){
            ip = DEFAULT_IP_ADDRESS;
            SPUtil.putString(IP_ADDRESS,DEFAULT_IP_ADDRESS);
        }
        if(port == -1){
            port = DEFAULT_PORT;
            SPUtil.put(PORT,DEFAULT_PORT);
        }
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip,port))
                .setMaxReadBytes(1024*50)
                .build();

        // 初始化
        EasySocket.getInstance()
                .createConnection(options, this);// 创建一个socket连接
    }

    /**
     * 监听tcp
     */
    private final ISocketActionListener iSocketActionListener = new SocketActionListener() {
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            EventBus.getDefault().post(true, "connect_status");
        }

        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            EventBus.getDefault().post(false, "connect_status");
        }

        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            EventBus.getDefault().post(false, "connect_status");
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, byte[] readData) {
            super.onSocketResponse(socketAddress, readData);
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, String readData) {
            super.onSocketResponse(socketAddress, readData);
            LogUtil.d(socketAddress.getPort() + "端口" + "SocketActionListener收到数据-->" + readData);

            EventBus.getDefault().post(false, "connect_status");
        }
    };



    public static Context getContext() {
        return context;
    }


}
