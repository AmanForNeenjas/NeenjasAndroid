package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class ReadAddress implements Parcelable {
    private int id;
    /** 地址高位0x33 */
    private String highadd;
    /** 地址高位0x33/地址低位对应意义定义 */
    private String lowadd;
    /** 设备返回的值*/
    private String devval;
    /** 请求是否发送 */
    private boolean issend;
    /** 是否收到反馈  */
    private boolean result;



    public ReadAddress() {
    }

    public ReadAddress(int id, String highadd, String lowadd, String devval, boolean issend, boolean result) {
        this.id = id;
        this.highadd = highadd;
        this.lowadd = lowadd;
        this.devval = devval;
        this.issend = issend;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHighadd() {
        return highadd;
    }

    public void setHighadd(String highadd) {
        this.highadd = highadd;
    }

    public String getLowadd() {
        return lowadd;
    }

    public void setLowadd(String lowadd) {
        this.lowadd = lowadd;
    }

    public String getDevval() {
        return devval;
    }

    public void setDevval(String devval) {
        this.devval = devval;
    }

    public boolean isIssend() {
        return issend;
    }

    public void setIssend(boolean issend) {
        this.issend = issend;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    protected ReadAddress(Parcel in) {
        id = in.readInt();
        highadd = in.readString();
        lowadd = in.readString();
        devval = in.readString();
        issend = in.readByte() != 0;
        result = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(highadd);
        dest.writeString(lowadd);
        dest.writeString(devval);
        dest.writeByte((byte) (issend ? 1 : 0));
        dest.writeByte((byte) (result ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReadAddress> CREATOR = new Creator<ReadAddress>() {
        @Override
        public ReadAddress createFromParcel(Parcel in) {
            return new ReadAddress(in);
        }

        @Override
        public ReadAddress[] newArray(int size) {
            return new ReadAddress[size];
        }
    };
}
