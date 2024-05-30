package com.hxjt.dqyt.app;

import static com.hxjt.dqyt.app.Constants.DEFAULT_IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.DEFAULT_PORT;
import static com.hxjt.dqyt.app.Constants.IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.PORT;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.multidex.MultiDex;

import com.hxjt.dqyt.base.ApiServer;
import com.hxjt.dqyt.base.BaseApplication;
import com.hxjt.dqyt.utils.OkHttpConfig;
import com.hxjt.dqyt.utils.RxHttpUtils;
import com.hxjt.dqyt.utils.SPUtil;
import com.hxjt.dqyt.utils.TcpClient;
import com.hxjt.dqyt.utils.cookie.store.SPCookieStore;
import com.hxjt.dqyt.utils.interfaces.BuildHeadersListener;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

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
        initRxHttpUtils();

        String ip = SPUtil.getString(IP_ADDRESS,"");
        String port = SPUtil.getString(PORT,"");
        if(ip.isEmpty()){
            SPUtil.putString(IP_ADDRESS,DEFAULT_IP_ADDRESS);
        }
        if(port.isEmpty()){
            SPUtil.putString(PORT,DEFAULT_PORT);
        }

        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                boolean isTcpConnected = TcpClient.getInstance().isConnected();
                if(!isTcpConnected){
                    TcpClient.getInstance().connectToServer();
                    TcpClient.getInstance().startMessageReceiver();

                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                TcpClient.getInstance().close();
            }
        });
    }



    public static Context getContext() {
        return context;
    }

    /**
     * 全局请求的统一配置（以下配置根据自身情况选择性的配置即可）
     */
    private void initRxHttpUtils() {

        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                //自定义factory的用法
                //.setCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.setConverterFactory(ScalarsConverterFactory.create(),GsonConverterFactory.create(GsonAdapter.buildGson()))
                //配置全局baseUrl
                .setBaseUrl(ApiServer.host)
                //开启全局配置
                .setOkClient(createOkHttp());
    }

    private OkHttpClient createOkHttp() {
        OkHttpClient okHttpClient = new OkHttpConfig
                .Builder(this)
                //添加公共请求头
                .setHeaders(new BuildHeadersListener() {
                    @Override
                    public Map<String, String> buildHeaders() {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("content-type", "application/json; charset=UTF-8");
                        //hashMap.put("X-Token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJVc2VySWQiOjExMzAxNzA5MTY4NzQwOTY2NDAsIlVzZXJDb2RlIjoiMTMwOTM3NjU5MDgiLCJVc2VyTmFtZSI6ImFzb25nIiwiTXBPcGVuSUQiOiJvZnp6bTVUTE1Mb3E2ejQxeUYwdS1vb2tGbWpNIn0.-azjveKVg5SVvm7GI8Lowbv8qHvEQqFsJ55yrnZSfWM");
                        return hashMap;
                    }
                })
                //添加自定义拦截器
                //.setAddInterceptor()
                //开启缓存策略(默认false)
                //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
                //2、在没有网络的时候，去读缓存中的数据。
                .setCache(true)
                .setHasNetCacheTime(10)//默认有网络时候缓存60秒
                //全局持久话cookie,保存到内存（new MemoryCookieStore()）或者保存到本地（new SPCookieStore(this)）
                //不设置的话，默认不对cookie做处理
                .setCookieType(new SPCookieStore(this))
                //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
                //.setAddInterceptor(null)
                //全局ssl证书认证
                //1、信任所有证书,不安全有风险（默认信任所有证书）
                //.setSslSocketFactory()
                //2、使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(cerInputStream)
                //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
                //设置Hostname校验规则，默认实现返回true，需要时候传入相应校验规则即可
                //.setHostnameVerifier(null)
                //全局超时配置
                .setReadTimeout(10)
                //全局超时配置
                .setWriteTimeout(10)
                //全局超时配置
                .setConnectTimeout(10)
                //全局是否打开请求log日志
                .build();

        return okHttpClient;
    }
}
