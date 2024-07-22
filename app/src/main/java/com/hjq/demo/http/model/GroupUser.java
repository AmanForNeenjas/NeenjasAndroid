package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 组用户
 */
public class GroupUser implements Parcelable {
    protected int uid;
    protected String nickname;
    protected String ava;
    protected String mobile;
    protected String countryno;
    protected int groudid;
    protected String email;
    protected boolean isadmin;

    public GroupUser() {
    }

    protected GroupUser(Parcel in) {
        uid = in.readInt();
        nickname = in.readString();
        ava = in.readString();
        mobile = in.readString();
        countryno = in.readString();
        groudid = in.readInt();
        email = in.readString();
        isadmin = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(nickname);
        dest.writeString(ava);
        dest.writeString(mobile);
        dest.writeString(countryno);
        dest.writeInt(groudid);
        dest.writeString(email);
        dest.writeByte((byte) (isadmin ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupUser> CREATOR = new Creator<GroupUser>() {
        @Override
        public GroupUser createFromParcel(Parcel in) {
            return new GroupUser(in);
        }

        @Override
        public GroupUser[] newArray(int size) {
            return new GroupUser[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryno() {
        return countryno;
    }

    public void setCountryno(String countryno) {
        this.countryno = countryno;
    }

    public int getGroudid() {
        return groudid;
    }

    public void setGroudid(int groudid) {
        this.groudid = groudid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }
}
