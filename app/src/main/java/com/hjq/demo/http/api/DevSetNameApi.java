package com.hjq.demo.http.api;

import com.hjq.demo.http.model.DeviceBind;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 设置设备昵称
 */
public final class DevSetNameApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/editdevnickname";
    }

    private int id;
    private String nickname;

    public DevSetNameApi setId(int id) {
        this.id = id;
        return this;
    }

    public DevSetNameApi setNickname(String nickname) {

        this.nickname = nickname;
        return this;
    }
}