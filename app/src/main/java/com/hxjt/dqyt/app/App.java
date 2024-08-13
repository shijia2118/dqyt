package com.hxjt.dqyt.app;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.DEFAULT_IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.DEFAULT_PORT;
import static com.hxjt.dqyt.app.Constants.IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.PORT;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.google.gson.Gson;
import com.hxjt.dqyt.BuildConfig;
import com.hxjt.dqyt.base.BaseApplication;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.bean.MyObjectBox;
import com.hxjt.dqyt.utils.DBUtils;
import com.hxjt.dqyt.utils.MessageAssembler;
import com.hxjt.dqyt.utils.SPUtil;

import org.simple.eventbus.EventBus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.objectbox.BoxStore;
import io.objectbox.android.Admin;

public class App extends BaseApplication {

    private static Context context;
    private static BoxStore mBoxStore;
    private MessageAssembler assembler = new MessageAssembler();

    private static final long INTERVAL = 3 * 60 * 1000; // 3分钟的间隔，单位为毫秒
    private Handler handler;
    private Runnable periodicTask;

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

        // 定义并启动定期执行任务的Handler和Runnable
        handler = new Handler();
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // 每3分钟执行一次的任务
                sendMessage_jdq();

                // 重新安排下次执行
                handler.postDelayed(this, INTERVAL);
            }
        };

        // 立即启动任务
        handler.post(periodicTask);

        mBoxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            // 添加调试
            boolean start=  new Admin(mBoxStore).start(this);
            Log.e("====start=======",start+"");
        }
        Log.d("App===", "Using ObjectBox " + BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")");

        // 注册一个全局的 Activity 生命周期回调
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                // 每次 Activity 被创建时，强制设置为横屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}
            @Override
            public void onActivityResumed(@NonNull Activity activity) {}
            @Override
            public void onActivityPaused(@NonNull Activity activity) {}
            @Override
            public void onActivityStopped(@NonNull Activity activity) {}
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });

    }

    private void sendMessage_jdq(){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","bsmio");
        map.put("DeviceCode","1");
        map.put("CmdType","13");
        map.put("PayloadJson","");
        map.put("jcqdz",null);

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);

        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

        EasySocket.getInstance().upMessage(jsonBytes);
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
            if(readData.startsWith("[djjshz_")) {
                assembler.processMessage(readData);
                if (assembler.isComplete) {
                    String result = assembler.getCompleteMessage();
                    if (result != null) {
                        DBUtils.insert(result);
                        assembler.reset();
                    }
                }
            } else {
                EventBus.getDefault().post(readData, RECEIVED_MESSAGE);
                DBUtils.insert(readData);
                DBUtils.delete(readData);
            }
        }
    };


    public static Context getContext() {
        return context;
    }


}
