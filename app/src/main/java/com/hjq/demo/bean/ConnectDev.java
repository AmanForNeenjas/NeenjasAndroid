package com.hjq.demo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ConnectDev implements Parcelable {
    public String id;
    public String user_id;
    public String name;
    public String dtype;
    public String dtype_id;
    public String macaddr;
    public String ag_id;
    public String dlabel;
    public String profile_id;
    public String dat;
    public String firmware_id;
    public String software_id;
    public String external_id;
    public String firmware_name;
    public String software_name;
    public String external_name;
    public String status;
    public String customertitle;
    public String address;
    public String latitude;
    public String longitude;
    public String impluse;

    public ConnectDev() {
    }

    protected ConnectDev(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        name = in.readString();
        dtype = in.readString();
        dtype_id = in.readString();
        macaddr = in.readString();
        ag_id = in.readString();
        dlabel = in.readString();
        profile_id = in.readString();
        dat = in.readString();
        firmware_id = in.readString();
        software_id = in.readString();
        external_id = in.readString();
        firmware_name = in.readString();
        software_name = in.readString();
        external_name = in.readString();
        status = in.readString();
        customertitle = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        impluse = in.readString();
    }

    public static final Creator<ConnectDev> CREATOR = new Creator<ConnectDev>() {
        @Override
        public ConnectDev createFromParcel(Parcel in) {
            return new ConnectDev(in);
        }

        @Override
        public ConnectDev[] newArray(int size) {
            return new ConnectDev[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString (id);
        parcel.writeString(user_id);
        parcel.writeString(name);
        parcel.writeString(dtype);
        parcel.writeString(dtype_id);
        parcel.writeString(macaddr);
        parcel.writeString (ag_id);
        parcel.writeString (dlabel);
        parcel.writeString (profile_id);
        parcel.writeString (dat);
        parcel.writeString (firmware_id);
        parcel.writeString (software_id);
        parcel.writeString (external_id);
        parcel.writeString (firmware_name);
        parcel.writeString (software_name);
        parcel.writeString (external_name);
        parcel.writeString (status);
        parcel.writeString (customertitle);
        parcel.writeString (address);
        parcel.writeString (latitude);
        parcel.writeString (longitude);
        parcel.writeString (impluse);

    }
}
