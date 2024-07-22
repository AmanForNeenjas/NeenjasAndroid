package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户组
 */
public class GroupID implements Parcelable {
    protected String name;
    protected int groudid;
    protected int adminuid;

    public GroupID() {
    }

    protected GroupID(Parcel in) {
        name = in.readString();
        groudid = in.readInt();
        adminuid = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(groudid);
        dest.writeInt(adminuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupID> CREATOR = new Creator<GroupID>() {
        @Override
        public GroupID createFromParcel(Parcel in) {
            return new GroupID(in);
        }

        @Override
        public GroupID[] newArray(int size) {
            return new GroupID[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroudid() {
        return groudid;
    }

    public void setGroudid(int groudid) {
        this.groudid = groudid;
    }

    public int getAdminuid() {
        return adminuid;
    }

    public void setAdminuid(int adminuid) {
        this.adminuid = adminuid;
    }
}
