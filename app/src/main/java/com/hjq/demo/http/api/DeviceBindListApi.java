package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

import java.util.Date;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 可进行拷贝的副本
 */
public final class DeviceBindListApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getmybinddevices";
    }

    private int page;
    private int limit;


    public DeviceBindListApi setPage(int page) {
        this.page = page;
        return this;
    }

    public DeviceBindListApi setLimit(int limit) {
        this.limit = limit;
        return this;
    }


}