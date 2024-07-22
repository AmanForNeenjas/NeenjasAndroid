package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取APP升级包
 */
public final class GetAppUpgradePackageApi implements IRequestApi {

    @Override
    public String getApi() {
        return "common/getappupgradepackage";
    }

    int apptype;
    int versionid;
    int versionmini;
    String tag;
    String versioncode;
    String phoneid;

    public GetAppUpgradePackageApi setVersioncode(String versioncode) {
        this.versioncode = versioncode;
        return this;
    }

    public GetAppUpgradePackageApi setPhoneid(String phoneid) {
        this.phoneid = phoneid;
        return this;
    }

    public GetAppUpgradePackageApi setApptype(int apptype) {
        this.apptype = apptype;
        return this;
    }

    public GetAppUpgradePackageApi setVersionid(int versionid) {
        this.versionid = versionid;
        return this;
    }

    public GetAppUpgradePackageApi setVersionmini(int versionmini) {
        this.versionmini = versionmini;
        return this;
    }

    public GetAppUpgradePackageApi setTag(String tag) {
        this.tag = tag;
        return this;
    }
}