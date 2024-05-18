package com.hxjt.dqyt.base;


import com.hxjt.dqyt.bean.DeviceInfoListBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiServer {

    String host = "http://192.168.1.100:9000/"; //开发板
//    String host = "http://192.168.1.13:9000/";  //外网 测试用

    /// 添加设备
    @POST("api/LinuxBoard/AddDevice")
    Observable<BaseModuleInstead<String>> addDevice(@Body Map<String, Object> map);

    /// 获取设备列表
    @GET("api/LinuxBoard/GetAllDevice")
    Observable<BaseModuleInstead<DeviceInfoListBean>> getAllDevice();

    /// 修改设备名称
    @POST("api/LinuxBoard/ModifyDevice")
    Observable<BaseModuleInstead<String>> modifyDeviceName(@Body Map<String, Object> map);

    /// 删除设备
    @POST("api/LinuxBoard/DelDevice")
    Observable<BaseModuleInstead<String>> deleteDevice(@Body Map<String, Object> map);




}