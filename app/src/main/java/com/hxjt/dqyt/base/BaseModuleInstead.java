package com.hxjt.dqyt.base;

import java.io.Serializable;

public class BaseModuleInstead<T> implements Serializable {
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -1;
    private T data;
    protected int code;
    protected String message;
    protected int totalCount;
    protected boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
