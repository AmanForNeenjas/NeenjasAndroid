package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取用户信息
 */
public final class UserInfoByAutoLoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/infoautologin";
    }

    private String type;

    public UserInfoByAutoLoginApi setType(String type) {
        this.type = type;
        return this;
    }


}