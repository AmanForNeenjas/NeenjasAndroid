package com.hjq.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;



public class UpgradeLog implements Parcelable {

    public int id;
    public int device_id;
    public int user_id;
    public int otapackage_id;
    public String tag;
    public int status;
    public String addtime;
    public String starttime;
    public String finishtime;

    protected UpgradeLog(Parcel in) {
        id = in.readInt();
        device_id = in.readInt();
        user_id = in.readInt();
        otapackage_id = in.readInt();
        tag = in.readString();
        status = in.readInt();
        addtime = in.readString();
        starttime = in.readString();
        finishtime = in.readString();
    }

    public static final Creator<UpgradeLog> CREATOR = new Creator<UpgradeLog>() {
        @Override
        public UpgradeLog createFromParcel(Parcel in) {
            return new UpgradeLog(in);
        }

        @Override
        public UpgradeLog[] newArray(int size) {
            return new UpgradeLog[size];
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOtapackage_id() {
        return otapackage_id;
    }

    public void setOtapackage_id(int otapackage_id) {
        this.otapackage_id = otapackage_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(device_id);
        parcel.writeInt(user_id);
        parcel.writeInt(otapackage_id);
        parcel.writeString(tag);
        parcel.writeInt(status);
        parcel.writeString(addtime);
        parcel.writeString(starttime);
        parcel.writeString(finishtime);
    }
}
