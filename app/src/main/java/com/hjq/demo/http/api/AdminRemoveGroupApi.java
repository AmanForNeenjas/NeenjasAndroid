package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 组管理员移除组，首先要判断组成员只有组管理员一个，才能删除,不然提示先删除其他组成员
 */
public final class AdminRemoveGroupApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/adminremovegroup";
    }

}