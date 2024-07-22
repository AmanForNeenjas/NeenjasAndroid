package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OtaPackage implements Parcelable {
    public int id;
    public int profileinfo_id;
    public String ptype;
    public String title;
    public String version;
    public String tag;
    public String url;
    public String filename;
    public int atach_id;
    public String checksumalgorithm;
    public String checksum;
    public String datasize;
    public String addtime;
    public String lastedittime;
    public int status;
    public String subversion;

    public OtaPackage() {


    }

    protected OtaPackage(Parcel in) {
        id = in.readInt();
        profileinfo_id = in.readInt();
        ptype = in.readString();
        title = in.readString();
        version = in.readString();
        tag = in.readString();
        url = in.readString();
        filename = in.readString();
        atach_id = in.readInt();
        checksumalgorithm = in.readString();
        checksum = in.readString();
        datasize = in.readString();
        addtime = in.readString();
        lastedittime = in.readString();
        status = in.readInt();
        subversion = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(profileinfo_id);
        dest.writeString(ptype);
        dest.writeString(title);
        dest.writeString(version);
        dest.writeString(tag);
        dest.writeString(url);
        dest.writeString(filename);
        dest.writeInt(atach_id);
        dest.writeString(checksumalgorithm);
        dest.writeString(checksum);
        dest.writeString(datasize);
        dest.writeString(addtime);
        dest.writeString(lastedittime);
        dest.writeInt(status);
        dest.writeString(subversion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OtaPackage> CREATOR = new Creator<OtaPackage>() {
        @Override
        public OtaPackage createFromParcel(Parcel in) {
            return new OtaPackage(in);
        }

        @Override
        public OtaPackage[] newArray(int size) {
            return new OtaPackage[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileinfo_id() {
        return profileinfo_id;
    }

    public void setProfileinfo_id(int profileinfo_id) {
        this.profileinfo_id = profileinfo_id;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDatasize() {
        return datasize;
    }

    public void setDatasize(String datasize) {
        this.datasize = datasize;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubversion() {
        return subversion;
    }

    public void setSubversion(String subversion) {
        this.subversion = subversion;
    }
}
