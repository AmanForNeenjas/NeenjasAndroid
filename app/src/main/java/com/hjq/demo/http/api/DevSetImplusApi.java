package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 设置设备冲电比
 */
public final class DevSetImplusApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/editdevbindimplus";
    }

    private int id;
    private String implus;

    public DevSetImplusApi setId(int id) {
        this.id = id;
        return this;
    }

    public DevSetImplusApi setImplus(String implus) {

        this.implus = implus;
        return this;
    }
}