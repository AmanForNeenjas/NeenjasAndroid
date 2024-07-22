package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 解除设备绑定
 */
public final class DelDevBindByIdApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/deldevbindbyid";
    }

    private int id;
    public DelDevBindByIdApi setId(int id) {
        this.id = id;
        return this;
    }


}