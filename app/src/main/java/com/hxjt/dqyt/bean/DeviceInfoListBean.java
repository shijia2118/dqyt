package com.hxjt.dqyt.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DeviceInfoListBean {

    /**
     * deviceNo : 6C4B907319BE
     * addrList_wsdcgq : [{"chl":1,"dev_type":"wsdcgq","addr":"123","name":"test001"},{"chl":1,"dev_type":"wsdcgq","addr":"111","name":"test_002"}]
     * addrList_zscgq : []
     * addrList_ywcgq : []
     * addrList_sjcgq : []
     * addrList_clzscgq : []
     * addrList_zdjccgq : []
     * addrList_dlq : []
     * addrList_Bpq : []
     * addrList_Ymcsy : []
     */

    private String deviceNo;
    private List<DeviceInfoBean> AddrList_wsdcgq;
    private List<DeviceInfoBean> AddrList_zscgq;
    private List<DeviceInfoBean> AddrList_ywcgq;
    private List<DeviceInfoBean> AddrList_sjcgq;
    private List<DeviceInfoBean> AddrList_clzscgq;
    private List<DeviceInfoBean> AddrList_zdjccgq;
    private List<DeviceInfoBean> AddrList_dlq;
    private List<DeviceInfoBean> AddrList_Bpq;
    private List<DeviceInfoBean> AddrList_Ymcsy;
    private List<DeviceInfoBean> AddrList_sk645;
    private List<DeviceInfoBean> AddrList_jcq;

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public List<DeviceInfoBean> getAddrList_wsdcgq() {
        return AddrList_wsdcgq;
    }

    public void setAddrList_wsdcgq(List<DeviceInfoBean> AddrList_wsdcgq) {
        this.AddrList_wsdcgq = AddrList_wsdcgq;
    }

    public List<DeviceInfoBean> getAddrList_Bpq() {
        return AddrList_Bpq;
    }

    public void setAddrList_Bpq(List<DeviceInfoBean> AddrList_Bpq) {
        this.AddrList_Bpq = AddrList_Bpq;
    }

    public List<DeviceInfoBean> getAddrList_clzscgq() {
        return AddrList_clzscgq;
    }

    public void setAddrList_clzscgq(List<DeviceInfoBean> AddrList_clzscgq) {
        this.AddrList_clzscgq = AddrList_clzscgq;
    }

    public List<DeviceInfoBean> getAddrList_dlq() {
        return AddrList_dlq;
    }

    public void setAddrList_dlq(List<DeviceInfoBean> AddrList_dlq) {
        this.AddrList_dlq = AddrList_dlq;
    }

    public List<DeviceInfoBean> getAddrList_sjcgq() {
        return AddrList_sjcgq;
    }

    public void setAddrList_sjcgq(List<DeviceInfoBean> AddrList_sjcgq) {
        this.AddrList_sjcgq = AddrList_sjcgq;
    }

    public List<DeviceInfoBean> getAddrList_Ymcsy() {
        return AddrList_Ymcsy;
    }

    public void setAddrList_Ymcsy(List<DeviceInfoBean> AddrList_Ymcsy) {
        this.AddrList_Ymcsy = AddrList_Ymcsy;
    }

    public List<DeviceInfoBean> getAddrList_ywcgq() {
        return AddrList_ywcgq;
    }

    public void setAddrList_ywcgq(List<DeviceInfoBean> AddrList_ywcgq) {
        this.AddrList_ywcgq = AddrList_ywcgq;
    }

    public List<DeviceInfoBean> getAddrList_zdjccgq() {
        return AddrList_zdjccgq;
    }

    public void setAddrList_zdjccgq(List<DeviceInfoBean> AddrList_zdjccgq) {
        this.AddrList_zdjccgq = AddrList_zdjccgq;
    }

    public List<DeviceInfoBean> getAddrList_zscgq() {
        return AddrList_zscgq;
    }

    public void setAddrList_zscgq(List<DeviceInfoBean> AddrList_zscgq) {
        this.AddrList_zscgq = AddrList_zscgq;
    }

    public List<DeviceInfoBean> getAddrList_sk645() {
        return AddrList_sk645;
    }

    public void setAddrList_sk645(List<DeviceInfoBean> addrList_sk645) {
        AddrList_sk645 = addrList_sk645;
    }

    public List<DeviceInfoBean> getAddrList_jcq() {
        return AddrList_jcq;
    }

    public void setAddrList_jcq(List<DeviceInfoBean> addrList_jcq) {
        AddrList_jcq = addrList_jcq;
    }

    public Map<String, Object> toMap() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static DeviceInfoListBean fromMap(Map<String, Object> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return gson.fromJson(json, DeviceInfoListBean.class);
    }
}
