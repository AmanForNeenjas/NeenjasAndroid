package com.hjq.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一次板书升级记录
 */
public class UpgradeLogSimple implements Parcelable {

    public int id;
    public String tag;
    public String finishtime;


    protected UpgradeLogSimple(Parcel in) {
        id = in.readInt();
        tag = in.readString();
        finishtime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tag);
        dest.writeString(finishtime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpgradeLogSimple> CREATOR = new Creator<UpgradeLogSimple>() {
        @Override
        public UpgradeLogSimple createFromParcel(Parcel in) {
            return new UpgradeLogSimple(in);
        }

        @Override
        public UpgradeLogSimple[] newArray(int size) {
            return new UpgradeLogSimple[size];
        }
    };
}
