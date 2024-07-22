package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class FindpwdByEmailApi implements IRequestApi {

    @Override
    public String getApi() {
        return "index/findpwdbyemail";
    }

    /** 手机号 */
    private String mobile;
    /** 校验码*/
    private String code;
    public FindpwdByEmailApi setPhone(String phone) {
        this.mobile= phone;
        return this;
    }

    public FindpwdByEmailApi setCode(String code) {
        this.code = code;
        return this;
    }

    /** 新密码 */
    private String newpassword;
    /** 再次密码 */
    private String surepassword;


    public FindpwdByEmailApi setNewpassword(String newpassword) {
        this.newpassword = newpassword;
        return this;
    }

    public FindpwdByEmailApi setSurepassword(String surepassword) {
        this.surepassword = surepassword;
        return this;
    }


}