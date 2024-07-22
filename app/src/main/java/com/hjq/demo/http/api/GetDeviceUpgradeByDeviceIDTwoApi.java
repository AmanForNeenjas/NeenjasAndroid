package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取升级包文件
 */
public final class GetDeviceUpgradeByDeviceIDTwoApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getdeviceupgradebydeviceidtwo";
    }


    private int device_id;
    private int bind_id;
    private String macaddr;


    public GetDeviceUpgradeByDeviceIDTwoApi setDeviceId(int device_id) {
        this.device_id = device_id;
        return this;
    }

    public GetDeviceUpgradeByDeviceIDTwoApi setBind_id(int bind_id) {
        this.bind_id = bind_id;
        return this;
    }

    public GetDeviceUpgradeByDeviceIDTwoApi setMacaddr(String macaddr) {
        this.macaddr = macaddr;
        return this;
    }
}