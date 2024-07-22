package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 修改密码
 */
public final class PasswordApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/password";
    }


    /** 旧密码 */
    private String oldpassword;
    /** 新密码 */
    private String newpassword;
    /** 再次密码 */
    private String surepassword;

    public PasswordApi setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
        return this;
    }

    public PasswordApi setNewpassword(String newpassword) {
        this.newpassword = newpassword;
        return this;
    }

    public PasswordApi setSurepassword(String surepassword) {
        this.surepassword = surepassword;
        return this;
    }
}