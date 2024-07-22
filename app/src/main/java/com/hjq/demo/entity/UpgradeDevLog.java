package com.hjq.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 所有板卡最新升级记录
 */
public class UpgradeDevLog implements Parcelable {
    public UpgradeLogSimple dcitem;
    public UpgradeLogSimple llcitem;
    public UpgradeLogSimple pfcitem;


    protected UpgradeDevLog(Parcel in) {
        dcitem = in.readParcelable(UpgradeLogSimple.class.getClassLoader());
        llcitem = in.readParcelable(UpgradeLogSimple.class.getClassLoader());
        pfcitem = in.readParcelable(UpgradeLogSimple.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(dcitem, flags);
        dest.writeParcelable(llcitem, flags);
        dest.writeParcelable(pfcitem, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpgradeDevLog> CREATOR = new Creator<UpgradeDevLog>() {
        @Override
        public UpgradeDevLog createFromParcel(Parcel in) {
            return new UpgradeDevLog(in);
        }

        @Override
        public UpgradeDevLog[] newArray(int size) {
            return new UpgradeDevLog[size];
        }
    };
}
