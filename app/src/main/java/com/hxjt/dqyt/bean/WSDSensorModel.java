package com.hxjt.dqyt.bean;

import java.util.Map;

public class WSDSensorModel {

    /**
     * Id : string
     * DeviceId : string
     * DeviceCode : string
     * DeviceName : string
     * CreateTime : string
     * UpdateTime : string
     * DeviceStatus : string
     * Td : string
     * Wd : string
     * Sd : string
     * SHowzd : string
     */

    private String Id;
    private String DeviceId;
    private String DeviceCode;
    private String DeviceName;
    private String CreateTime;
    private String UpdateTime;
    private String DeviceStatus;
    private String Td;
    private String Wd;
    private String Sd;
    private String SHowzd;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String DeviceId) {
        this.DeviceId = DeviceId;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String DeviceCode) {
        this.DeviceCode = DeviceCode;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName = DeviceName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public String getDeviceStatus() {
        return DeviceStatus;
    }

    public void setDeviceStatus(String DeviceStatus) {
        this.DeviceStatus = DeviceStatus;
    }

    public String getTd() {
        return Td;
    }

    public void setTd(String Td) {
        this.Td = Td;
    }

    public String getWd() {
        return Wd;
    }

    public void setWd(String Wd) {
        this.Wd = Wd;
    }

    public String getSd() {
        return Sd;
    }

    public void setSd(String Sd) {
        this.Sd = Sd;
    }

    public String getSHowzd() {
        return SHowzd;
    }

    public void setSHowzd(String SHowzd) {
        this.SHowzd = SHowzd;
    }

    public static WSDSensorModel fromMap(Map<String, String> map) {
        WSDSensorModel obj = new WSDSensorModel();
        obj.setId(map.get("Id"));
        obj.setDeviceId(map.get("DeviceId"));
        obj.setDeviceCode(map.get("DeviceCode"));
        obj.setDeviceName(map.get("DeviceName"));
        obj.setCreateTime(map.get("CreateTime"));
        obj.setUpdateTime(map.get("UpdateTime"));
        obj.setDeviceStatus(map.get("DeviceStatus"));
        obj.setTd(map.get("Td"));
        obj.setWd(map.get("Wd"));
        obj.setSd(map.get("Sd"));
        obj.setSHowzd(map.get("SHowzd"));
        return obj;
    }

}
