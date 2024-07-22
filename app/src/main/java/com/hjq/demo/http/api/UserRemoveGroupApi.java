package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 组成员移除组
 */
public final class UserRemoveGroupApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/userremovegroup";
    }

}