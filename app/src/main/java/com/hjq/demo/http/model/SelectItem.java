package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectItem implements Parcelable {
    public int id;
    public String name;

    public SelectItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected SelectItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<SelectItem> CREATOR = new Creator<SelectItem>() {
        @Override
        public SelectItem createFromParcel(Parcel in) {
            return new SelectItem(in);
        }

        @Override
        public SelectItem[] newArray(int size) {
            return new SelectItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }
}
