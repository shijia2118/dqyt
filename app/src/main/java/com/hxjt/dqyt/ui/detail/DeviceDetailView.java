package com.hxjt.dqyt.ui.detail;

import com.hxjt.dqyt.base.BaseView;

public interface DeviceDetailView extends BaseView {
    void modifyDeviceNameSuccess(String message);
    void modifyDeviceNameFailed(String message);

    void onDeleteDeviceSuccess(String message);
    void onDeleteDeviceFailed(String message);

    void onError(String errorMsg);
}