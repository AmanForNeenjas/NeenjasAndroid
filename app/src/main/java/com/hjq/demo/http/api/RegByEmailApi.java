package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class RegByEmailApi implements IRequestApi {

    @Override
    public String getApi() {
        return "index/regbyemail";
    }

    /** 手机号 */
    private String mobile;
    /** 登录密码 */
    private String password;



    /** 校验码*/
    private String code;

    public RegByEmailApi setPhone(String phone) {
        this.mobile= phone;
        return this;
    }

    public RegByEmailApi setPassword(String password) {
        this.password = password;
        return this;
    }

    public RegByEmailApi setCode(String code) {
        this.code = code;
        return this;
    }

    public final static class Bean {

        private String token;


        public String getToken() {
            return token;
        }


    }
}