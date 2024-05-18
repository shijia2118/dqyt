package com.hxjt.dqyt.base;

import static com.hxjt.dqyt.base.ApiServer.host;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiRetrofit {

    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private OkHttpClient client;
    private ApiServer apiServer;
    private String TAG = "ApiRetrofit";

    /**
     * 请求访问quest
     * response拦截器
     */
    private Interceptor interceptor = chain -> {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        // 获取请求参数并保存
        String requestBody = "";
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            requestBody = buffer.readUtf8();
        }

        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MediaType mediaType = response.body().contentType();
        String content = response.body().string();

        // 格式化 JSON 字符串
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(content);
        String formattedContent = gson.toJson(jsonElement);

        Log.d("", "\n");
        Log.d("", "\n");
        Log.d("", "\n");
        Log.e(TAG, "----------Request Start----------------");
        Log.e(TAG, "| " + request.toString() + "\n"+"请求头:"+request.headers().toString() + "\n"+"请求体:" + requestBody);
        logWithLineBreak(TAG,content);
        Log.e(TAG, "----------Request End:" + duration + "毫秒----------");
        Log.d("", "\n");
        Log.d("", "\n");
        Log.d("", "\n");

        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build();
    };

    private void logWithLineBreak(String tag, String content) {
        if (content.length() <= 800) {
            Log.e(tag, "响应内容:"+content);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            int startIndex = 0;

            while (startIndex < content.length()) {
                int endIndex = Math.min(startIndex + 800, content.length());
                String subContent = content.substring(startIndex, endIndex);
                stringBuilder.append(subContent).append(System.lineSeparator());
                startIndex = endIndex;
            }
            Log.e(tag, "响应内容:"+stringBuilder.toString());
        }
    }


    public ApiRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> {
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                //添加log拦截器
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

        apiServer = retrofit.create(ApiServer.class);
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public ApiServer getApiService() {
        return apiServer;
    }

}