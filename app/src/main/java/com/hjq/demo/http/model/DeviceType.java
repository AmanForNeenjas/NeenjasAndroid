package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceType implements Parcelable {
    private int id;
    private String title;
    private String thumbpic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbpic() {
        return thumbpic;
    }

    public void setThumbpic(String thumbpic) {
        this.thumbpic = thumbpic;
    }

    public DeviceType() {
    }

    protected DeviceType(Parcel in) {
        id = in.readInt();
        title = in.readString();
        thumbpic = in.readString();
    }

    public static final Creator<DeviceType> CREATOR = new Creator<DeviceType>() {
        @Override
        public DeviceType createFromParcel(Parcel in) {
            return new DeviceType(in);
        }

        @Override
        public DeviceType[] newArray(int size) {
            return new DeviceType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(thumbpic);
    }
}
