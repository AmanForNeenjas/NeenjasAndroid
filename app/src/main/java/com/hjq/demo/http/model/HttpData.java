package com.hjq.demo.http.model;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 统一接口数据结构
 */
public class HttpData<T> {

    /** 返回码 */
    private int code;
    /** 提示语 */
    private String msg;
    /** 数据 */
    private T data;

    private int count ;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    /**
     * 是否请求成功
     */
    public boolean isRequestSucceed() {
        return code == 200;
    }

    /**
     * 是否 Token 失效
     */
    public boolean isTokenFailure() {
        return code == 1001;
    }
}