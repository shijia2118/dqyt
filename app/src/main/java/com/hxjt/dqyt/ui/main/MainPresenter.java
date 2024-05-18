package com.hxjt.dqyt.ui.main;

import com.hxjt.dqyt.base.BaseModuleInstead;
import com.hxjt.dqyt.base.BasePresenter;
import com.hxjt.dqyt.bean.DeviceInfoListBean;

import io.reactivex.observers.DisposableObserver;

public class MainPresenter extends BasePresenter<MainView> {
    public MainPresenter(MainView baseView) {
        super(baseView);
    }

    /**
     * 获取设备列表
     */
    public void getAllDevice() {
        DisposableObserver<BaseModuleInstead<DeviceInfoListBean>> disposableObserver = new DisposableObserver<BaseModuleInstead<DeviceInfoListBean>>() {
            @Override
            public void onNext(BaseModuleInstead<DeviceInfoListBean> stringBaseModuleInstead) {
                if (stringBaseModuleInstead != null && stringBaseModuleInstead.isSuccess()) {
                    baseView.getAllDeviceSuccess(stringBaseModuleInstead.getData());
                } else {
                    baseView.getAllDeviceFailed(stringBaseModuleInstead.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                baseView.getAllDeviceFailed(e.toString());
            }

            @Override
            public void onComplete() {

            }
        };
        addDisposable(apiServer.getAllDevice(),disposableObserver);
    }

}