package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取APP升级包
 */
public final class GetAppUpgradePackageSimpleApi implements IRequestApi {

    @Override
    public String getApi() {
        return "common/getappupgradepackagesimple";
    }

    int apptype;
    int versionid;
    int versionmini;
    String tag;
    String versioncode;
    String phoneid;

    public GetAppUpgradePackageSimpleApi setVersioncode(String versioncode) {
        this.versioncode = versioncode;
        return this;
    }

    public GetAppUpgradePackageSimpleApi setPhoneid(String phoneid) {
        this.phoneid = phoneid;
        return this;
    }

    public GetAppUpgradePackageSimpleApi setApptype(int apptype) {
        this.apptype = apptype;
        return this;
    }

    public GetAppUpgradePackageSimpleApi setVersionid(int versionid) {
        this.versionid = versionid;
        return this;
    }

    public GetAppUpgradePackageSimpleApi setVersionmini(int versionmini) {
        this.versionmini = versionmini;
        return this;
    }

    public GetAppUpgradePackageSimpleApi setTag(String tag) {
        this.tag = tag;
        return this;
    }
}