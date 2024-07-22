package com.hjq.demo.http.model;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : 两个列表选项目
 */
public class HttpListSelectTwoData<T> extends HttpData<HttpListSelectTwoData.ListBean<T>> {

    public static class ListBean<T> {


        /** 数据 */
        private List<T> itema;

        private List<T> itemb;

        public List<T> getItema() {
            return itema;
        }

        public List<T> getItemb() {
            return itemb;
        }
    }
}