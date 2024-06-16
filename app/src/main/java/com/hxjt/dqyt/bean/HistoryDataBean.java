package com.hxjt.dqyt.bean;

import com.google.gson.Gson;

public class HistoryDataBean {

    /**
     * Id : 202
     * DeviceType : 2
     * DeviceData : {"Id":null,"DeviceId":"FC0FE737C3B5","DeviceCode":"2","DeviceName":"温湿度设备","CreateTime":"2024-06-14 13:53:57","UpdateTime":null,"DeviceStatus":null,"Td":"3","Wd":"26.8","Sd":"59.2","SHowzd":"26.8","TcpCmdType":"wsdcgq","TcpDataType":null}
     * DataType : 1
     * CreateTimeStr : 2024-06-14 13:53:57
     * CreateTime : 2024-06-14 13:53:57.242
     */

    private String Id;
    private String DeviceType;
    private String DeviceData;
    private int DataType;
    private String CreateTimeStr;
    private String CreateTime;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String DeviceType) {
        this.DeviceType = DeviceType;
    }

    public String getDeviceData() {
        return DeviceData;
    }

    public void setDeviceData(String DeviceData) {
        this.DeviceData = DeviceData;
    }

    public int getDataType() {
        return DataType;
    }

    public void setDataType(int DataType) {
        this.DataType = DataType;
    }

    public String getCreateTimeStr() {
        return CreateTimeStr;
    }

    public void setCreateTimeStr(String CreateTimeStr) {
        this.CreateTimeStr = CreateTimeStr;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String toJson() {
        Gson gson = new Gson();
        try {
            return gson.toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "数据解析异常";
        }
    }
}
