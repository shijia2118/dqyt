package com.hxjt.dqyt.base;

public interface BaseView {
    /**
     * 显示dialog
     */
    void showLoading(String message);

    /**
     * 隐藏 dialog
     */

    void hideLoading();

    /**
     * 显示错误信息
     *
     * @param msg
     */
    void showError(String msg);

    /**
     * 错误码
     */
    void onErrorCode(int code);


}