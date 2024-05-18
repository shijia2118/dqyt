package com.hxjt.dqyt.ui.system;

import com.hxjt.dqyt.base.BaseModuleInstead;
import com.hxjt.dqyt.base.BasePresenter;

import java.util.Map;

import io.reactivex.observers.DisposableObserver;


public class SystemSetPresenter extends BasePresenter<SystemSetView> {
    public SystemSetPresenter(SystemSetView baseView) {
        super(baseView);
    }

    /**
     * 添加设备
     * @param map
     */
    public void addDevice(Map<String,Object> map){
        DisposableObserver<BaseModuleInstead<String>> disposableObserver = new DisposableObserver<BaseModuleInstead<String>>() {
            @Override
            public void onNext(BaseModuleInstead<String> fenZuOperationBean) {
                if (fenZuOperationBean != null && fenZuOperationBean.isSuccess()) {
                    baseView.addDeviceSuccess(fenZuOperationBean.getMessage());
                } else {
                    baseView.addDeviceFailed(fenZuOperationBean.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                baseView.addDeviceFailed(e.toString());
            }

            @Override
            public void onComplete() {

            }
        };
        addDisposable(apiServer.addDevice(map), disposableObserver);
    }

}