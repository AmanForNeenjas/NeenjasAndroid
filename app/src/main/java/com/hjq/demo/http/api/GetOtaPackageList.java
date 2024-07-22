package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取升级包文件
 */
public final class GetOtaPackageList implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getotapackagelistthree";
    }

    /** 手机号 */
    private String tag;
    private String chipcode;
    private String batterycell;
    private String voltagelevel;

    public GetOtaPackageList setDevname(String devname) {
        this.devname = devname;
        return  this;
    }

    //设备型号MATE-2000 META-2000 PRO
    private String devname;

    public GetOtaPackageList setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public GetOtaPackageList  setChipcode(String chipcode) {
        this.chipcode = chipcode;
        return this;
    }

    public GetOtaPackageList  setBatterycell(String batterycell) {
        this.batterycell = batterycell;
        return this;
    }

    public GetOtaPackageList  setVoltagelevel(String voltagelevel) {
        this.voltagelevel = voltagelevel;
        return this;
    }
}