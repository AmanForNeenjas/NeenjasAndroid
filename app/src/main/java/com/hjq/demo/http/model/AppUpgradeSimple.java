package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * App升级包文件
 */
public class AppUpgradeSimple implements Parcelable {
    public String vcode;
    public String vpoint;
    public String vurl;
    public String checksum;

    protected AppUpgradeSimple(Parcel in) {
        vcode = in.readString();
        vpoint = in.readString();
        vurl = in.readString();
        checksum = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vcode);
        dest.writeString(vpoint);
        dest.writeString(vurl);
        dest.writeString(checksum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppUpgradeSimple> CREATOR = new Creator<AppUpgradeSimple>() {
        @Override
        public AppUpgradeSimple createFromParcel(Parcel in) {
            return new AppUpgradeSimple(in);
        }

        @Override
        public AppUpgradeSimple[] newArray(int size) {
            return new AppUpgradeSimple[size];
        }
    };

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVpoint() {
        return vpoint;
    }

    public void setVpoint(String vpoint) {
        this.vpoint = vpoint;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
