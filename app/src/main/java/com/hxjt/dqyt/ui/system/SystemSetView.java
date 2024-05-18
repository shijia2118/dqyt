package com.hxjt.dqyt.ui.system;

import com.hxjt.dqyt.base.BaseView;

public interface SystemSetView extends BaseView {

    void addDeviceSuccess(String message);
    void addDeviceFailed(String message);

}