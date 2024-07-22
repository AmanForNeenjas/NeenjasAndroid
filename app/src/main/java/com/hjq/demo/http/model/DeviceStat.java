package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 存储设备读取的状态信息
 */
public class DeviceStat implements Parcelable {
    private String lowad1;
    private String lowad3;
    private String lowad8;
    private String lowad9;
    private String lowad11;
    private String lowad12;
    private String lowad14;
    private String lowad15;
    private String lowad16;
    private String lowad17;
    private String lowad21;
    private String lowad24;
    private String lowad26;
    private String lowad64;
    private String lowad65;
    private String lowad66;
    private String lowad77;
    private String lowad78;
    private String lowad79;
    private String lowad80;

    public String getLowad1() {
        return lowad1;
    }

    public void setLowad1(String lowad1) {
        this.lowad1 = lowad1;
    }

    public String getLowad3() {
        return lowad3;
    }

    public void setLowad3(String lowad3) {
        this.lowad3 = lowad3;
    }

    public String getLowad8() {
        return lowad8;
    }

    public void setLowad8(String lowad8) {
        this.lowad8 = lowad8;
    }

    public String getLowad9() {
        return lowad9;
    }

    public void setLowad9(String lowad9) {
        this.lowad9 = lowad9;
    }

    public String getLowad11() {
        return lowad11;
    }

    public void setLowad11(String lowad11) {
        this.lowad11 = lowad11;
    }

    public String getLowad12() {
        return lowad12;
    }

    public void setLowad12(String lowad12) {
        this.lowad12 = lowad12;
    }

    public String getLowad14() {
        return lowad14;
    }

    public void setLowad14(String lowad14) {
        this.lowad14 = lowad14;
    }

    public String getLowad15() {
        return lowad15;
    }

    public void setLowad15(String lowad15) {
        this.lowad15 = lowad15;
    }

    public String getLowad16() {
        return lowad16;
    }

    public void setLowad16(String lowad16) {
        this.lowad16 = lowad16;
    }

    public String getLowad17() {
        return lowad17;
    }

    public void setLowad17(String lowad17) {
        this.lowad17 = lowad17;
    }

    public String getLowad21() {
        return lowad21;
    }

    public void setLowad21(String lowad21) {
        this.lowad21 = lowad21;
    }

    public String getLowad24() {
        return lowad24;
    }

    public void setLowad24(String lowad24) {
        this.lowad24 = lowad24;
    }

    public String getLowad26() {
        return lowad26;
    }

    public void setLowad26(String lowad26) {
        this.lowad26 = lowad26;
    }

    public String getLowad64() {
        return lowad64;
    }

    public void setLowad64(String lowad64) {
        this.lowad64 = lowad64;
    }

    public String getLowad65() {
        return lowad65;
    }

    public void setLowad65(String lowad65) {
        this.lowad65 = lowad65;
    }

    public String getLowad66() {
        return lowad66;
    }

    public void setLowad66(String lowad66) {
        this.lowad66 = lowad66;
    }

    public String getLowad77() {
        return lowad77;
    }

    public void setLowad77(String lowad77) {
        this.lowad77 = lowad77;
    }

    public String getLowad78() {
        return lowad78;
    }

    public void setLowad78(String lowad78) {
        this.lowad78 = lowad78;
    }

    public String getLowad79() {
        return lowad79;
    }

    public void setLowad79(String lowad79) {
        this.lowad79 = lowad79;
    }

    public String getLowad80() {
        return lowad80;
    }

    public void setLowad80(String lowad80) {
        this.lowad80 = lowad80;
    }

    public DeviceStat() {
    }

    protected DeviceStat(Parcel in) {
        lowad1 = in.readString();
        lowad3 = in.readString();
        lowad8 = in.readString();
        lowad9 = in.readString();
        lowad11 = in.readString();
        lowad12 = in.readString();
        lowad14 = in.readString();
        lowad15 = in.readString();
        lowad16 = in.readString();
        lowad17 = in.readString();
        lowad21 = in.readString();
        lowad24 = in.readString();
        lowad26 = in.readString();
        lowad64 = in.readString();
        lowad65 = in.readString();
        lowad66 = in.readString();
        lowad77 = in.readString();
        lowad78 = in.readString();
        lowad79 = in.readString();
        lowad80 = in.readString();
    }

    public static final Creator<DeviceStat> CREATOR = new Creator<DeviceStat>() {
        @Override
        public DeviceStat createFromParcel(Parcel in) {
            return new DeviceStat(in);
        }

        @Override
        public DeviceStat[] newArray(int size) {
            return new DeviceStat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lowad1);
        parcel.writeString(lowad3);
        parcel.writeString(lowad8);
        parcel.writeString(lowad9);
        parcel.writeString(lowad11);
        parcel.writeString(lowad12);
        parcel.writeString(lowad14);
        parcel.writeString(lowad15);
        parcel.writeString(lowad16);
        parcel.writeString(lowad17);
        parcel.writeString(lowad21);
        parcel.writeString(lowad24);
        parcel.writeString(lowad26);
        parcel.writeString(lowad64);
        parcel.writeString(lowad65);
        parcel.writeString(lowad66);
        parcel.writeString(lowad77);
        parcel.writeString(lowad78);
        parcel.writeString(lowad79);
        parcel.writeString(lowad80);
    }
}
