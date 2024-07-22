package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 可进行拷贝的副本
 */
public final class DeviceBindMacListApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getmybinddevicesmacs";
    }
}