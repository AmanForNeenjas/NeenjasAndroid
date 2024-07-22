package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 设置APP安装版本信息
 */
public final class SetAppInstallApi implements IRequestApi {

    @Override
    public String getApi() {
        return "common/setappinstall";
    }

    int apptype;
    String versioncode;
    String phoneid;
    String tag;


    public SetAppInstallApi setApptype(int apptype) {
        this.apptype = apptype;
        return this;
    }

    public SetAppInstallApi setVersioncode(String versioncode) {
        this.versioncode = versioncode;
        return this;
    }

    public SetAppInstallApi setPhoneid(String phoneid) {
        this.phoneid = phoneid;
        return this;
    }

    public SetAppInstallApi setTag(String tag) {
        this.tag = tag;
        return this;
    }
}