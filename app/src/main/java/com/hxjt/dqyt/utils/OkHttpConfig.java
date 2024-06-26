package com.hxjt.dqyt.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hxjt.dqyt.base.interceptor.HeaderInterceptor;
import com.hxjt.dqyt.base.interceptor.NetCacheInterceptor;
import com.hxjt.dqyt.base.interceptor.NoNetCacheInterceptor;
import com.hxjt.dqyt.base.interceptor.RxHttpLogger;
import com.hxjt.dqyt.utils.cookie.CookieJarImpl;
import com.hxjt.dqyt.utils.cookie.store.CookieStore;
import com.hxjt.dqyt.utils.interfaces.BuildHeadersListener;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * <pre>
 *      @author : Allen
 *      e-mail  : lygttpod@163.com
 *      date    : 2018/05/29
 *      desc    : 统一OkHttp配置信息
 *      version : 1.0
 * </pre>
 */
public class OkHttpConfig {


    private static String defaultCachePath;
    private static final long defaultCacheSize = 1024 * 1024 * 100;
    private static final long defaultTimeout = 10;


    private static OkHttpConfig instance;

    private static OkHttpClient.Builder okHttpClientBuilder;

    private static OkHttpClient okHttpClient;

    public OkHttpConfig() {
        okHttpClientBuilder = new OkHttpClient.Builder();
    }

    public static OkHttpConfig getInstance() {

        if (instance == null) {
            synchronized (OkHttpConfig.class) {
                if (instance == null) {
                    instance = new OkHttpConfig();
                }
            }

        }
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            return okHttpClientBuilder.build();
        } else {
            return okHttpClient;
        }
    }

    public static class Builder {
        public Context context;
        private boolean isDebug;
        private boolean isCache;
        private int cacheTime = 60;
        private int noNetCacheTime = 10;
        private String cachePath;
        private long cacheMaxSize;
        private CookieStore cookieStore;
        private long readTimeout;
        private long writeTimeout;
        private long connectTimeout;
        private InputStream bksFile;
        private String password;
        private InputStream[] certificates;
        private Interceptor[] interceptors;
        private BuildHeadersListener buildHeadersListener;
        private HostnameVerifier hostnameVerifier;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setHeaders(BuildHeadersListener buildHeadersListener) {
            this.buildHeadersListener = buildHeadersListener;
            return this;
        }

        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Builder setCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Builder setHasNetCacheTime(int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public Builder setNoNetCacheTime(int noNetCacheTime) {
            this.noNetCacheTime = noNetCacheTime;
            return this;
        }

        public Builder setCachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public Builder setCacheMaxSize(long cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        public Builder setCookieType(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
            return this;
        }

        public Builder setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setAddInterceptor(Interceptor... interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public Builder setSslSocketFactory(InputStream... certificates) {
            this.certificates = certificates;
            return this;
        }

        public Builder setSslSocketFactory(InputStream bksFile, String password, InputStream... certificates) {
            this.bksFile = bksFile;
            this.password = password;
            this.certificates = certificates;
            return this;
        }

        public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }


        public OkHttpClient build() {

            OkHttpConfig.getInstance();

            setCookieConfig();
            setCacheConfig();
            setHeadersConfig();
            setSslConfig();
            setHostnameVerifier();
            addInterceptors();
            setTimeout();
            setDebugConfig();

            okHttpClient = okHttpClientBuilder.build();
            return okHttpClient;
        }

        private void addInterceptors() {
            if (null != interceptors) {
                for (Interceptor interceptor : interceptors) {
                    okHttpClientBuilder.addInterceptor(interceptor);
                }
            }
        }

        /**
         * 配置开发环境
         */
        private void setDebugConfig() {
            if (isDebug) {
                HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new RxHttpLogger());
                logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(logInterceptor);
            }
        }


        /**
         * 配置headers
         */
        private void setHeadersConfig() {
            if (buildHeadersListener != null) {
                okHttpClientBuilder.addInterceptor(new HeaderInterceptor() {
                    @Override
                    public Map<String, String> buildHeaders() {
                        return buildHeadersListener.buildHeaders();
                    }
                });
            }

        }

        /**
         * 配饰cookie保存到sp文件中
         */
        private void setCookieConfig() {
            if (null != cookieStore) {
                okHttpClientBuilder.cookieJar(new CookieJarImpl(cookieStore));
            }
        }

        /**
         * 配置缓存
         */
        private void setCacheConfig() {
            File externalCacheDir = context.getExternalCacheDir();
            if (null == externalCacheDir) {
                return;
            }
            defaultCachePath = externalCacheDir.getPath() + "/RxHttpCacheData";
            if (isCache) {
                Cache cache;
                if (!TextUtils.isEmpty(cachePath) && cacheMaxSize > 0) {
                    cache = new Cache(new File(cachePath), cacheMaxSize);
                } else {
                    cache = new Cache(new File(defaultCachePath), defaultCacheSize);
                }

                okHttpClientBuilder
                        .cache(cache)
                        .addInterceptor(new NoNetCacheInterceptor(noNetCacheTime))
                        .addNetworkInterceptor(new NetCacheInterceptor(cacheTime));
            }
        }

        /**
         * 配置超时信息
         */
        private void setTimeout() {
            okHttpClientBuilder.readTimeout(readTimeout == 0 ? defaultTimeout : readTimeout, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(writeTimeout == 0 ? defaultTimeout : writeTimeout, TimeUnit.SECONDS);
            okHttpClientBuilder.connectTimeout(connectTimeout == 0 ? defaultTimeout : connectTimeout, TimeUnit.SECONDS);
            okHttpClientBuilder.retryOnConnectionFailure(true);
        }

        /**
         * 配置证书
         */
        private void setSslConfig() {
            SSLUtils.SSLParams sslParams = null;

            if (null == certificates) {
                //信任所有证书,不安全有风险
                sslParams = SSLUtils.getSslSocketFactory();
            } else {
                if (null != bksFile && !TextUtils.isEmpty(password)) {
                    //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(bksFile, password, certificates);
                } else {
                    //使用预埋证书，校验服务端证书（自签名证书）
                    sslParams = SSLUtils.getSslSocketFactory(certificates);
                }
            }

            okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        }

        private void setHostnameVerifier() {
            if (null == hostnameVerifier) {
//                okHttpClientBuilder.hostnameVerifier(SSLUtils.UnSafeHostnameVerifier);
            } else {
                okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
            }
        }
    }
}
