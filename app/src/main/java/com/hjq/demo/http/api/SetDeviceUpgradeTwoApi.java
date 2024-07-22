package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 提交设备升级日志表
 */
public final class SetDeviceUpgradeTwoApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/setdeviceupgradethree";
    }

    public SetDeviceUpgradeTwoApi setDevice_id(int device_id) {
        this.device_id = device_id;
        return this;

    }

    public SetDeviceUpgradeTwoApi setOtapackage_id(int otapackage_id) {
        this.otapackage_id = otapackage_id;
        return this;
    }

    public SetDeviceUpgradeTwoApi setStatus(int status) {
        this.status = status;
        return this;
    }

    public SetDeviceUpgradeTwoApi setOtapackage(String otapackage) {
        this.otapackage = otapackage;
        return this;
    }

    public SetDeviceUpgradeTwoApi setStarttime(String starttime) {
        this.starttime = starttime;
        return this;
    }

    public SetDeviceUpgradeTwoApi setFinishtime(String finishtime) {
        this.finishtime = finishtime;
        return this;
    }

    int device_id;
    int otapackage_id;
    int status;
    int bind_id;
    String macaddr;
    String otapackage;
    String starttime;

    public SetDeviceUpgradeTwoApi setAg_id(int ag_id) {
        this.ag_id = ag_id;
        return this;
    }

    String finishtime;
    String tag;
    int ag_id; //增加代理信息20231021

    public SetDeviceUpgradeTwoApi setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public SetDeviceUpgradeTwoApi setBind_id(int bind_id) {
        this.bind_id = bind_id;
        return this;
    }

    public SetDeviceUpgradeTwoApi setMacaddr(String macaddr) {
        this.macaddr = macaddr;
        return this;
    }
}