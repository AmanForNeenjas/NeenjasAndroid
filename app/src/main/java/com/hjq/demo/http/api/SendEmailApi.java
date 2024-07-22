package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 用户登录
 */
public final class SendEmailApi implements IRequestApi {

    @Override
    public String getApi() {
        return "index/sendemail";
    }

    /** 手机号 */
    private String mobile;
    /** 请求名称 */
    private String title;
    /**国际化语言提示  【APP 注册校验码】：*/
    private String tipa;

    /**国际化语言提示  请注意保存，10分钟有效 */
    private String tipb;


    public SendEmailApi setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public SendEmailApi setTitle(String title) {
        this.title = title;
        return this;
    }

    public SendEmailApi setTipa(String tipa) {
        this.tipa = tipa;
        return this;
    }

    public SendEmailApi setTipb(String tipb) {
        this.tipb = tipb;
        return this;
    }
}