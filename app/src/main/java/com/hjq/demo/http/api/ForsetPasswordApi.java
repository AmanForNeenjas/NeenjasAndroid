package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 修改密码
 */
public final class ForsetPasswordApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/password";
    }



    /** 新密码 */
    private String newpassword;
    /** 再次密码 */
    private String surepassword;


    public ForsetPasswordApi setNewpassword(String newpassword) {
        this.newpassword = newpassword;
        return this;
    }

    public ForsetPasswordApi setSurepassword(String surepassword) {
        this.surepassword = surepassword;
        return this;
    }
}