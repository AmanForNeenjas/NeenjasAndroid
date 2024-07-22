package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取升级包文件
 */
public final class GetDeviceByID implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getdevicebyid";
    }

    /** 手机号 */
    private int id;

    public GetDeviceByID setTag(int id) {
        this.id = id;
        return this;
    }
}