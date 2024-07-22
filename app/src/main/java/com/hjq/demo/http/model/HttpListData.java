package com.hjq.demo.http.model;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : 统一接口列表数据结构
 */
public class HttpListData<T> extends HttpData<HttpListData.ListBean<T>> {

    public static class ListBean<T> {

        /** 当前页码 */
        private int page;
        /** 页大小 */
        private int limit;
        /** 总数量 */
        private int total;
        /** 数据 */
        private List<T> items;

        /**
         * 判断是否是最后一页
         */
        public boolean isLastPage() {
            return Math.ceil((float) total / limit) <= page;
        }

        public int getTotal() {
            return total;
        }

        public int getPage() {
            return page;
        }

        public int getLimit() {
            return limit;
        }

        public List<T> getItems() {
            return items;
        }
    }
}