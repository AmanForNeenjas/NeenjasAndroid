package com.hjq.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AppUpgrade implements Parcelable {
    public int id;
    public int app_type;
    public int version_id;
    public int version_mini;
    public String version_code;
    public int status;
    public String apk_url;
    public String filename;
    public int atach_id;
    public String url;
    public String tag;
    public String checksumalgorithm;
    public String checksum;
    public String upgrade_point;
    public String addtime;
    public String lastedittime;

    protected AppUpgrade(Parcel in) {
        id = in.readInt();
        app_type = in.readInt();
        version_id = in.readInt();
        version_mini = in.readInt();
        version_code = in.readString();
        status = in.readInt();
        apk_url = in.readString();
        filename = in.readString();
        atach_id = in.readInt();
        url = in.readString();
        tag = in.readString();
        checksumalgorithm = in.readString();
        checksum = in.readString();
        upgrade_point = in.readString();
        addtime = in.readString();
        lastedittime = in.readString();
    }

    public static final Creator<AppUpgrade> CREATOR = new Creator<AppUpgrade>() {
        @Override
        public AppUpgrade createFromParcel(Parcel in) {
            return new AppUpgrade(in);
        }

        @Override
        public AppUpgrade[] newArray(int size) {
            return new AppUpgrade[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApp_type() {
        return app_type;
    }

    public void setApp_type(int app_type) {
        this.app_type = app_type;
    }

    public int getVersion_id() {
        return version_id;
    }

    public void setVersion_id(int version_id) {
        this.version_id = version_id;
    }

    public int getVersion_mini() {
        return version_mini;
    }

    public void setVersion_mini(int version_mini) {
        this.version_mini = version_mini;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getAtach_id() {
        return atach_id;
    }

    public void setAtach_id(int atach_id) {
        this.atach_id = atach_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChecksumalgorithm() {
        return checksumalgorithm;
    }

    public void setChecksumalgorithm(String checksumalgorithm) {
        this.checksumalgorithm = checksumalgorithm;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getUpgrade_point() {
        return upgrade_point;
    }

    public void setUpgrade_point(String upgrade_point) {
        this.upgrade_point = upgrade_point;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(app_type);
        parcel.writeInt(version_id);
        parcel.writeInt(version_mini);
        parcel.writeString(version_code);
        parcel.writeInt(status);
        parcel.writeString(apk_url);
        parcel.writeString(filename);
        parcel.writeInt(atach_id);
        parcel.writeString(url);
        parcel.writeString(tag);
        parcel.writeString(checksumalgorithm);
        parcel.writeString(checksum);
        parcel.writeString(upgrade_point);
        parcel.writeString(addtime);
        parcel.writeString(lastedittime);
    }
}
