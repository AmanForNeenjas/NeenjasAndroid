package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 组管理员删除组成员
 */
public final class AdminDelGroupUserApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/admindelgroupuser";
    }
    public AdminDelGroupUserApi setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    /** 用户ID */
    private String userid;
}