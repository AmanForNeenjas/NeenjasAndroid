package com.hjq.demo.http.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AppSetInfo implements Parcelable {
    public String appname;
    public String appurl;
    public String appcopy;
    public String globaltest; //全局测试
    public String shownew;//显示最新消息是否提醒20231026添加
    public List<String> allowmac;
    public List<String> dismac;
    public List<String>  blenames;
    public List<String>  batterycells;
    public List<String>  voltagelevel;


    protected AppSetInfo(Parcel in) {
        appname = in.readString();
        appurl = in.readString();
        appcopy = in.readString();
        globaltest = in.readString();
        shownew = in.readString();
        allowmac = in.createStringArrayList();
        dismac = in.createStringArrayList();
        blenames = in.createStringArrayList();
        batterycells = in.createStringArrayList();
        voltagelevel = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appname);
        dest.writeString(appurl);
        dest.writeString(appcopy);
        dest.writeString(globaltest);
        dest.writeString(shownew);
        dest.writeStringList(allowmac);
        dest.writeStringList(dismac);
        dest.writeStringList(blenames);
        dest.writeStringList(batterycells);
        dest.writeStringList(voltagelevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppSetInfo> CREATOR = new Creator<AppSetInfo>() {
        @Override
        public AppSetInfo createFromParcel(Parcel in) {
            return new AppSetInfo(in);
        }

        @Override
        public AppSetInfo[] newArray(int size) {
            return new AppSetInfo[size];
        }
    };
}
