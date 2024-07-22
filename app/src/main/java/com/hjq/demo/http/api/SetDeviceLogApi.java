package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 提交设备日志表
 */
public final class SetDeviceLogApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/setdevicelogtwo";
    }

    int device_id;
    String customertitle;
    int status;
    String charging;
    int infotype;
    String ip;
    String rawdata;
    String errors;
    int ag_id;

    public SetDeviceLogApi setAg_id(int ag_id) {
        this.ag_id = ag_id;
        return this;
    }

    public SetDeviceLogApi setErrors(String errors) {
        this.errors = errors;
        return this;
    }

    public SetDeviceLogApi setAlerts(String alerts) {
        this.alerts = alerts;
        return this;
    }

    String alerts;



    public SetDeviceLogApi setDevice_id(int device_id) {
        this.device_id = device_id;
        return this;
    }

    public SetDeviceLogApi setCustomertitle(String customertitle) {
        this.customertitle = customertitle;
        return this;
    }

    public SetDeviceLogApi setStatus(int status) {
        this.status = status;
        return this;
    }

    public SetDeviceLogApi setCharging(String charging) {
        this.charging = charging;
        return this;
    }

    public SetDeviceLogApi setInfotype(int infotype) {
        this.infotype = infotype;
        return this;
    }

    public SetDeviceLogApi setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public SetDeviceLogApi setRawdata(String rawdata) {
        this.rawdata = rawdata;
        return this;
    }




    
    
}