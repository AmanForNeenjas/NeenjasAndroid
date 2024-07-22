package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 设置设备信息,入库
 */
public final class SetDeviceApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/setdevice";
    }

    int dtype_id;
    String dlabel;
    String macaddr;
    int profile_id;
    String dat;
    String sn;
    int dcotaid;
    int llcotaid;
    int pfcotaid;
    String truename;
    int status;
    String batterycells;
    String voltagelevel;
    int onecorecode;
    int twocorecode;
    String dcversion;
    String dcsubversion;
    String llcversion;
    String llcsubversion;
    String pfcversion;
    String pfcsubversion;
    String frequency;
    String bmsversion;

    public SetDeviceApi AddDevice(int dtype_id, String dlabel, String macaddr, int profile_id, String dat, int dcotaid, int llcotaid, int pfcotaid, String truename, int status, String batterycells, String voltagelevel, int onecorecode, int twocorecode, String dcversion, String dcsubversion, String llcversion, String llcsubversion, String pfcversion, String pfcsubversion, String frequency, String bmsversion, String sn) {
        this.dtype_id = dtype_id;
        this.dlabel = dlabel;
        this.macaddr = macaddr;
        this.profile_id = profile_id;
        this.dat = dat;
        this.dcotaid = dcotaid;
        this.llcotaid = llcotaid;
        this.pfcotaid = pfcotaid;
        this.truename = truename;
        this.status = status;
        this.batterycells = batterycells;
        this.voltagelevel = voltagelevel;
        this.onecorecode = onecorecode;
        this.twocorecode = twocorecode;
        this.dcversion = dcversion;
        this.dcsubversion = dcsubversion;
        this.llcversion = llcversion;
        this.llcsubversion = llcsubversion;
        this.pfcversion = pfcversion;
        this.pfcsubversion = pfcsubversion;
        this.frequency = frequency;
        this.bmsversion = bmsversion;
        this.sn = sn;
        return this;
    }

    public SetDeviceApi setDtype_id(int dtype_id) {
        this.dtype_id = dtype_id;
        return this;
    }

    public SetDeviceApi setDlabel(String dlabel) {
        this.dlabel = dlabel;
        return this;
    }

    public SetDeviceApi setMacaddr(String macaddr) {
        this.macaddr = macaddr;
        return this;
    }

    public SetDeviceApi setProfile_id(int profile_id) {
        this.profile_id = profile_id;
        return this;
    }

    public SetDeviceApi setDat(String dat) {
        this.dat = dat;
        return this;
    }

    public SetDeviceApi setDcotaid(int dcotaid) {
        this.dcotaid = dcotaid;
        return this;
    }

    public SetDeviceApi setLlcotaid(int llcotaid) {
        this.llcotaid = llcotaid;
        return this;
    }

    public SetDeviceApi setPfcotaid(int pfcotaid) {
        this.pfcotaid = pfcotaid;
        return this;
    }

    public SetDeviceApi setTruename(String truename) {
        this.truename = truename;
        return this;
    }

    public SetDeviceApi setStatus(int status) {
        this.status = status;
        return this;
    }

    public SetDeviceApi setBatterycells(String batterycells) {
        this.batterycells = batterycells;
        return this;
    }

    public SetDeviceApi setVoltagelevel(String voltagelevel) {
        this.voltagelevel = voltagelevel;
        return this;
    }

    public SetDeviceApi setOnecorecode(int onecorecode) {
        this.onecorecode = onecorecode;
        return this;
    }

    public SetDeviceApi setTwocorecode(int twocorecode) {
        this.twocorecode = twocorecode;
        return this;
    }

    public SetDeviceApi setDcversion(String dcversion) {
        this.dcversion = dcversion;
        return this;
    }

    public SetDeviceApi setDcsubversion(String dcsubversion) {
        this.dcsubversion = dcsubversion;
        return this;
    }

    public SetDeviceApi setLlcversion(String llcversion) {
        this.llcversion = llcversion;
        return this;
    }

    public SetDeviceApi setLlcsubversion(String llcsubversion) {
        this.llcsubversion = llcsubversion;
        return this;
    }

    public SetDeviceApi setPfcversion(String pfcversion) {
        this.pfcversion = pfcversion;
        return this;
    }

    public SetDeviceApi setPfcsubversion(String pfcsubversion) {
        this.pfcsubversion = pfcsubversion;
        return this;
    }

    public SetDeviceApi setFrequency(String frequency) {
        this.frequency = frequency;
        return this;
    }

    public SetDeviceApi setBmsversion(String bmsversion) {
        this.bmsversion = bmsversion;
        return this;
    }

}