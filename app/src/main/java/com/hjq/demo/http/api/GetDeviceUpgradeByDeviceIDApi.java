package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取升级包文件
 */
public final class GetDeviceUpgradeByDeviceIDApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getdeviceupgradebydeviceid";
    }


    private int device_id;

    public GetDeviceUpgradeByDeviceIDApi setDeviceId(int device_id) {
        this.device_id = device_id;
        return this;
    }
}