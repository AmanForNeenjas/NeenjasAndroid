package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 设备绑定表
 */
public class DeviceBind implements Parcelable {
    private int id;
    private int device_id;
    private int puser_id;
    private int user_id;
    private String devicename;
    private String devnickname;
    private String macaddr;
    private String implus;
    private String addtime;
    private String lastedittime;
    private int status;
    private int ag_id;




    public DeviceBind() {

    }

    protected DeviceBind(Parcel in) {
        id = in.readInt();
        device_id = in.readInt();
        puser_id = in.readInt();
        user_id = in.readInt();
        devicename = in.readString();
        devnickname = in.readString();
        macaddr = in.readString();
        implus = in.readString();
        addtime = in.readString();
        lastedittime = in.readString();
        status = in.readInt();
        ag_id = in.readInt();
    }

    public static final Creator<DeviceBind> CREATOR = new Creator<DeviceBind>() {
        @Override
        public DeviceBind createFromParcel(Parcel in) {
            return new DeviceBind(in);
        }

        @Override
        public DeviceBind[] newArray(int size) {
            return new DeviceBind[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public int getPuser_id() {
        return puser_id;
    }

    public void setPuser_id(int puser_id) {
        this.puser_id = puser_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getDevnickname() {
        return devnickname;
    }

    public void setDevnickname(String devnickname) {
        this.devnickname = devnickname;
    }

    public String getMacaddr() {
        return macaddr;
    }

    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
    }

    public String getImplus() {
        return implus;
    }

    public void setImplus(String implus) {
        this.implus = implus;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getLastedittime() {
        return lastedittime;
    }

    public void setLastedittime(String lastedittime) {
        this.lastedittime = lastedittime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getAg_id() {
        return ag_id;
    }

    public void setAg_id(int ag_id) {
        this.ag_id = ag_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeInt(device_id);
        parcel.writeInt(puser_id);
        parcel.writeInt(user_id);
        parcel.writeString(devicename);
        parcel.writeString(devnickname);
        parcel.writeString(macaddr);
        parcel.writeString(implus);
        parcel.writeString(addtime);
        parcel.writeString(lastedittime);
        parcel.writeInt(status);
        parcel.writeInt(ag_id);
    }


}
