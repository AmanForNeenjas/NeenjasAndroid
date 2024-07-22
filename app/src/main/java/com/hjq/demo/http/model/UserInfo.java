package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hjq.demo.R;

/**
 * 用户信息
 */
public class UserInfo implements Parcelable {
    protected int uid;
    protected boolean logined;
    protected boolean buyed ;
    protected String vipedntime;
    protected boolean bindwx;
    protected String nickname;
    protected String ava;
    protected String mobile;
    protected String countryno;
    protected int groudid;
    protected String email;
    protected boolean isautologin;
    protected boolean isupgrade;
    protected boolean ipupdev;


    public UserInfo() {
        uid = 0;
        logined = false;
        buyed = false;
        vipedntime = "";
        bindwx = false;
        nickname = "";
        ava = "";
        mobile = "";
        countryno = "";
        groudid = 0;
        email = "";
        isautologin = false;
        isupgrade = false;
        ipupdev = false;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isLogined() {
        return logined;
    }

    public void setLogined(boolean logined) {
        this.logined = logined;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public void setBuyed(boolean buyed) {
        this.buyed = buyed;
    }

    public String getVipedntime() {
        return vipedntime;
    }

    public void setVipedntime(String vipedntime) {
        this.vipedntime = vipedntime;
    }

    public boolean isBindwx() {
        return bindwx;
    }

    public void setBindwx(boolean bindwx) {
        this.bindwx = bindwx;
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

    public boolean isIsautologin() {
        return isautologin;
    }

    public void setIsautologin(boolean isautologin) {
        this.isautologin = isautologin;
    }

    public boolean isIsupgrade() {
        return isupgrade;
    }

    public void setIsupgrade(boolean isupgrade) {
        this.isupgrade = isupgrade;
    }

    public boolean isIpupdev() {
        return ipupdev;
    }

    public void setIpupdev(boolean ipupdev) {
        this.ipupdev = ipupdev;
    }

    protected UserInfo(Parcel in) {
        uid = in.readInt();
        logined = in.readByte() != 0;
        buyed = in.readByte() != 0;
        vipedntime = in.readString();
        bindwx = in.readByte() != 0;
        nickname = in.readString();
        ava = in.readString();
        mobile = in.readString();
        countryno = in.readString();
        groudid = in.readInt();
        email = in.readString();
        isautologin = in.readByte() != 0;
        isupgrade = in.readByte() != 0;
        ipupdev = in.readByte() != 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeByte((byte) (logined ? 1 : 0));
        parcel.writeByte((byte) (buyed ? 1 : 0));
        parcel.writeString(vipedntime);
        parcel.writeByte((byte) (bindwx ? 1 : 0));
        parcel.writeString(nickname);
        parcel.writeString(ava);
        parcel.writeString(mobile);
        parcel.writeString(countryno);
        parcel.writeInt(groudid);
        parcel.writeString(email);
        parcel.writeByte((byte) (isautologin ? 1 : 0));
        parcel.writeByte((byte) (isupgrade ? 1 : 0));
        parcel.writeByte((byte) (ipupdev ? 1 : 0));
    }
}
