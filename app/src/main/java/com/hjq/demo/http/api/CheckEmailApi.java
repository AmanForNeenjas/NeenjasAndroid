package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class CheckEmailApi implements IRequestApi {

    @Override
    public String getApi() {
        return "index/checkemail";
    }

    /** email */
    private String mobile;



    public CheckEmailApi setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }


}