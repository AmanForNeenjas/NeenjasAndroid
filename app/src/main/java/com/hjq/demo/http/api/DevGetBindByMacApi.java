package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 根据MAC地址查询设备绑定
 */
public final class DevGetBindByMacApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getdevbindbymac";
    }

    private String macaddr;

    public DevGetBindByMacApi setMacaddr(String macaddr) {
        this.macaddr = macaddr;
        return this;
    }


}