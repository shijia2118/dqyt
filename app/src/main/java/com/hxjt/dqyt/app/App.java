package com.hxjt.dqyt.app;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.DEFAULT_IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.DEFAULT_PORT;
import static com.hxjt.dqyt.app.Constants.IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.PORT;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.hxjt.dqyt.BuildConfig;
import com.hxjt.dqyt.base.BaseApplication;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.bean.MyObjectBox;
import com.hxjt.dqyt.utils.DBUtils;
import com.hxjt.dqyt.utils.SPUtil;

import org.simple.eventbus.EventBus;

import io.objectbox.BoxStore;
import io.objectbox.android.Admin;

public class App extends BaseApplication {

    private static Context context;
    private static BoxStore mBoxStore;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        context = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initEasySocket();
        EasySocket.getInstance().subscribeSocketAction(iSocketActionListener);

        mBoxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            // 添加调试
            boolean start=  new Admin(mBoxStore).start(this);
            Log.e("====start=======",start+"");
        }
        Log.d("App===", "Using ObjectBox " + BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")");

    }

    public static BoxStore getBoxStore() {
        return mBoxStore;
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 初始化EasySocket
     */
    private void initEasySocket() {

        String ip = SPUtil.getString(IP_ADDRESS,"");
        String port = SPUtil.getString(PORT,"");

        if(ip.isEmpty()){
            ip = DEFAULT_IP_ADDRESS;
            SPUtil.putString(IP_ADDRESS,DEFAULT_IP_ADDRESS);
        }
        if(port.isEmpty()){
            port = DEFAULT_PORT;
            SPUtil.putString(PORT,DEFAULT_PORT);
        }
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(ip,Integer.parseInt(port)))
                .setMaxReadBytes(1024*1024*10)
                .setMaxWriteBytes(1024*1024*10)
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
            EventBus.getDefault().post(true, CONNECTION_CHANGED);
        }

        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            EventBus.getDefault().post(false, CONNECTION_CHANGED);
        }

        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            EventBus.getDefault().post(false, CONNECTION_CHANGED);
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
            EventBus.getDefault().post(readData, RECEIVED_MESSAGE);
            DBUtils.insert(readData);
            DBUtils.delete(readData);
        }
    };


    public static Context getContext() {
        return context;
    }


}
