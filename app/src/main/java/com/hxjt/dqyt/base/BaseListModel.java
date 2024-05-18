package com.hxjt.dqyt.base;

import java.io.Serializable;

public class BaseListModel<T> implements Serializable {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;

    private int Code;
    private String Message;
    private int TotalCount;
    private boolean Success;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }


    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        this.Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }


}