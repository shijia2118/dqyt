package com.hxjt.dqyt.ui.detail;

import com.hxjt.dqyt.base.BaseModuleInstead;
import com.hxjt.dqyt.base.BasePresenter;

import java.util.Map;

import io.reactivex.observers.DisposableObserver;

public class DeviceDetailPresenter extends BasePresenter<DeviceDetailView> {
    public DeviceDetailPresenter(DeviceDetailView baseView) {
        super(baseView);
    }

    /**
     * 修改设备
     * @param map
     */
    public void modifyDevice(Map<String, Object> map,String newName){
        addDisposable(apiServer.modifyDeviceName(map), new DisposableObserver<BaseModuleInstead<String>>() {
            @Override
            public void onNext(BaseModuleInstead<String> stringBaseModuleInstead) {
                if (stringBaseModuleInstead != null && stringBaseModuleInstead.isSuccess()) {
                    baseView.modifyDeviceNameSuccess(newName);
                } else {
                    if(stringBaseModuleInstead != null && stringBaseModuleInstead.getMessage() != null){
                        baseView.modifyDeviceNameFailed(stringBaseModuleInstead.getMessage());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if(e.getMessage() != null)
                baseView.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 删除设备
     * @param map
     */
    public void deleteDevice(Map<String, Object> map){
        addDisposable(apiServer.deleteDevice(map), new DisposableObserver<BaseModuleInstead<String>>() {
            @Override
            public void onNext(BaseModuleInstead<String> stringBaseModuleInstead) {
                if (stringBaseModuleInstead != null && stringBaseModuleInstead.isSuccess()) {
                    baseView.onDeleteDeviceSuccess(stringBaseModuleInstead.getMessage());
                } else {
                    if(stringBaseModuleInstead != null && stringBaseModuleInstead.getMessage() != null){
                        baseView.onDeleteDeviceFailed(stringBaseModuleInstead.getMessage());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if(e.getMessage() != null)
                    baseView.onError(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


}