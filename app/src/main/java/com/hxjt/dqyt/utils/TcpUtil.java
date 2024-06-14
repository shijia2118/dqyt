package com.hxjt.dqyt.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hxjt.dqyt.bean.DeviceInfoBean;

import java.util.HashMap;
import java.util.Map;

public class TcpUtil {

    public void getAllDevices(){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","103");
        map.put("PayloadJson","");

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        TcpClient.getInstance().sendMessage(jsonString);


    }

    public void deleteDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","101");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        new Thread(() -> {
            TcpClient.getInstance().sendMessage(jsonString);
        }).start();
    }

    public void editDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","102");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        new Thread(() -> {
            TcpClient.getInstance().sendMessage(jsonString);
        }).start();
    }

    public void addDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","100");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        TcpClient.getInstance().sendMessage(jsonString);
    }

    public void setStaticIp(String ip,String wgCode){

        Map<String,Object> payloadMap = new HashMap<>();
        payloadMap.put("ip",ip);
        payloadMap.put("wg",wgCode);
        Gson gson = new Gson();
        String payloadJson = gson.toJson(payloadMap);

        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","1002");
        map.put("PayloadJson",payloadJson);
        map.put("jcqdz","");

        String jsonString = gson.toJson(map);
        TcpClient.getInstance().sendMessage(jsonString);
    }

    /**
     * 获取设备历史数据列表
     * @param params
     */
    public void getDeviceHistoryDataList(@NonNull Map<String,Object> params){
        String deviceType = (String) params.get("DeviceType");
        String deviceCode = (String) params.get("DeviceCode");

        if(deviceType == null || deviceCode == null){
            throw new IllegalArgumentException("DeviceType and DeviceCode cannot be null");
        }

        String dataType = (String) params.get("DataType");
        if(dataType==null) dataType = "0";

        String pageIndex = (String) params.get("PageIndex");
        String pageSize = (String) params.get("PageSize");

        if(pageIndex==null) pageIndex = "1";
        if(pageSize==null) pageSize = "10";

        String startDt = (String) params.get("StartDt");
        String endDt = (String) params.get("EndDt");

        Map<String,Object> payloadMap = new HashMap<>();
        payloadMap.put("DataType",dataType);
        payloadMap.put("PageIndex",pageIndex);
        payloadMap.put("PageSize",pageSize);
        payloadMap.put("StartDt",startDt);
        payloadMap.put("EndDt",endDt);
        Gson gson = new Gson();
        String payloadJson = gson.toJson(payloadMap);

        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType",deviceType);
        map.put("DeviceCode",deviceCode);
        map.put("CmdType","104");
        map.put("PayloadJson",payloadJson);

        String jsonString = gson.toJson(map);
        TcpClient.getInstance().sendMessage(jsonString);
    }

}
