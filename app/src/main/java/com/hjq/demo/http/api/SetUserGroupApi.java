package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 设置用户组名
 */
public final class SetUserGroupApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/editgroup";
    }


    /** 组名 */
    private String groupname;


    public SetUserGroupApi setGroupname(String groupname) {
        this.groupname = groupname;
        return this;
    }
}