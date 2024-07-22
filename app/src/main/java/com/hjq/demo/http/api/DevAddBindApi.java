package com.hjq.demo.http.api;

import com.hjq.demo.http.model.DeviceBind;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 添加设备绑定第二版
 */
public final class DevAddBindApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/adddevbindtwo";
    }

    private String name;
    private String nickname;
    private String macaddr;
    // dev_id = -1 为第一次绑定，
    private int dev_id;
    // uid = -1 为第一次绑定，
    private int uid;

    public DevAddBindApi setName(String name) {
        this.name = name;
        return this;
    }

    public DevAddBindApi setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public DevAddBindApi setMacaddr(String macaddr) {
        this.macaddr = macaddr;
        return this;
    }

    public DevAddBindApi setDev_id(int dev_id) {
        this.dev_id = dev_id;
        return this;
    }

    public DevAddBindApi setUid(int uid) {
        this.uid = uid;
        return this;
    }
}