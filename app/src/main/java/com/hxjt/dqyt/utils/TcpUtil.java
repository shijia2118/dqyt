package com.hxjt.dqyt.utils;

import com.easysocket.EasySocket;
import com.google.gson.Gson;
import com.hxjt.dqyt.bean.DeviceInfoBean;

import java.nio.charset.StandardCharsets;
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

        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

        EasySocket.getInstance().upMessage(jsonBytes);
    }

    public void deleteDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","101");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);

        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        EasySocket.getInstance().upMessage(jsonBytes);
    }

    public void editDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","102");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);

        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        EasySocket.getInstance().upMessage(jsonBytes);
    }

    public void addDevice(DeviceInfoBean deviceInfoBean){
        Map<String,Object> map = new HashMap<>();
        map.put("DeviceType","");
        map.put("DeviceCode","");
        map.put("CmdType","100");
        map.put("PayloadJson",deviceInfoBean.toJson());

        Gson gson = new Gson();
        String jsonString = gson.toJson(map);
        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        EasySocket.getInstance().upMessage(jsonBytes);
    }

}
