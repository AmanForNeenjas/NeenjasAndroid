package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 注销用户
 */
public final class UnRegisterApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/closeacount";
    }

    /** 旧手机号验证码（没有绑定情况下可不传） */
    //private String preCode;

    /** 新手机号 */
    //private String nickname;

    //private String code; /** 新手机号验证码 */
    /*
    public PhoneApi setPreCode(String preCode) {
        this.preCode = preCode;
        return this;
    }
    public PhoneApi setCode(String code) {
        this.code = code;
        return this;
    }
    public UnRegisterApi setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }*/


}