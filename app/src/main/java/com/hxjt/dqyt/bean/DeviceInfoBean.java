package com.hxjt.dqyt.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Map;

public class DeviceInfoBean implements Parcelable {

    private int chl;
    private String dev_type;
    private String addr;
    private String name;

    public int getChl() {
        return chl;
    }

    public void setChl(int chl) {
        this.chl = chl;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 实现Parcelable.Creator接口
    public static final Creator<DeviceInfoBean> CREATOR = new Creator<DeviceInfoBean>() {
        @Override
        public DeviceInfoBean createFromParcel(Parcel in) {
            return new DeviceInfoBean(in);
        }

        @Override
        public DeviceInfoBean[] newArray(int size) {
            return new DeviceInfoBean[size];
        }
    };

    // Parcelable构造器
    protected DeviceInfoBean(Parcel in) {
        chl = in.readInt();
        dev_type = in.readString();
        addr = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chl);
        dest.writeString(dev_type);
        dest.writeString(addr);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static DeviceInfoBean fromMap(Map<String, Object> map) {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        return gson.fromJson(json, DeviceInfoBean.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

