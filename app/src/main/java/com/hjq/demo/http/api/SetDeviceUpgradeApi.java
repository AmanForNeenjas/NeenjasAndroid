package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 提交设备升级日志表
 */
public final class SetDeviceUpgradeApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/setdeviceupgrade";
    }

    public SetDeviceUpgradeApi setDevice_id(int device_id) {
        this.device_id = device_id;
        return this;

    }

    public SetDeviceUpgradeApi setOtapackage_id(int otapackage_id) {
        this.otapackage_id = otapackage_id;
        return this;
    }

    public SetDeviceUpgradeApi setStatus(int status) {
        this.status = status;
        return this;
    }

    public SetDeviceUpgradeApi setOtapackage(String otapackage) {
        this.otapackage = otapackage;
        return this;
    }

    public SetDeviceUpgradeApi setStarttime(String starttime) {
        this.starttime = starttime;
        return this;
    }

    public SetDeviceUpgradeApi setFinishtime(String finishtime) {
        this.finishtime = finishtime;
        return this;
    }

    int device_id;
    int otapackage_id;
    int status;
    String otapackage;
    String starttime;
    String finishtime;
    String tag;

    public SetDeviceUpgradeApi setTag(String tag) {
        this.tag = tag;
        return this;
    }
}