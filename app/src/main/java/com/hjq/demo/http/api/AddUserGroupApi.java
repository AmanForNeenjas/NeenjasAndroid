package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 增加用户组
 */
public final class AddUserGroupApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/addgroup";
    }


    /** 组名 */
    private String groupname;


    public AddUserGroupApi setGroupname(String groupname) {
        this.groupname = groupname;
        return this;
    }
}