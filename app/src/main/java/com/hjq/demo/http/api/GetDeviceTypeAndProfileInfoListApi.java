package com.hjq.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 获取设备型号及设备版本表列表
 */
public final class GetDeviceTypeAndProfileInfoListApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getdevicetypeandprofileinfolist";
    }




}