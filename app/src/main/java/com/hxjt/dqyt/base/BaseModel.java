package com.hxjt.dqyt.base;

import java.io.Serializable;

public class BaseModel<T> implements Serializable {
    public static final int CODE_SUCCESS=0;
    public static final int CODE_ERROR=-1;

    protected int Code;
    protected String Message;
    protected int TotalCount;
    protected  boolean Success;

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