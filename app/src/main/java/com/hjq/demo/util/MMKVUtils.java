package com.hjq.demo.util;

import android.content.Context;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

public class MMKVUtils {
    // 网络状态 networkstatus
    public final static String NETWORKSTATUS = "networkstatus";
    public final static String CACHID = "dms_app_id";
    public final static String DEVICEBINDLIST = "devicebindlist";
    public static final String TOKEN = "token";
    public static final String USERINFO = "userinfo";
    public static final String AUTOLOGIN = "autulogin";
    public static final String FIRSTLOGIN = "firstLogin";
    public static final String GROUPID = "groupid";
    public static final String GROUPUSERLIST = "groupuserlist";
    public static final String READADDRESSLIST = "readaddresslist";
    public static final String APPSETINFO = "appsetinfo";
    // 已经绑定的设备List
    public static final String DEVICEBINDMACLIST = "devicebindmaclist";
    public static final String APPDEVRAW = "devicerawdata";
    public static final String DEVRAWWRITEDATE = "devicerawwritedate";

    /*
     *作者:赵星海
     *时间:18/06/08 09:51
     *用途:增改sp的数组  name-自定义集合的名称 只存储20个记录
     * https://blog.csdn.net/qq_39731011/article/details/120046721
     */
    public static <T> Boolean setArray(MMKV kv, Context mContext, List<T> list, String name) {
        //MMKV kv = MMKV.defaultMMKV();
        if (list == null || list.size() == 0) { //清空
            kv.putInt(name + "size", 0);
            int size = kv.getInt(name + "size", 0);
            for (int i = 0; i < size; i++) {
                if (kv.getString(name + i, null) != null) {
                    kv.remove(name + i);
                }
            }
        } else {
            kv.putInt(name + "size", list.size());
            if (list.size() > 20) {
                list.remove(0);   //只保留后20条记录
            }
            for (int i = 0; i < list.size(); i++) {
                kv.remove(name + i);
                kv.remove(new Gson().toJson(list.get(i)));//删除重复数据 先删后加
                kv.putString(name + i, new Gson().toJson(list.get(i)));
            }
        }
        return kv.commit();
    }

    /*
     *作者:赵星海
     *时间:18/06/08 09:51 读取所有的记录
     *用途:加载sp的数组 name-自定义集合的名称
     */
    public static <T> List<T> getArray(MMKV kv,  Context mContext, String name, T bean) {
        //MMKV kv = MMKV.defaultMMKV();
        List<T> list = new ArrayList<T>();
        int size = kv.getInt(name + "size", 0);
        for (int i = 0; i < size; i++) {
            if (kv.getString(name + i, null) != null) {
                try {
                    list.add((T) new Gson().fromJson(kv.getString(name + i, null), bean.getClass()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return list;
    }

    /**
     * 设置所有数据
     * @param kv
     * @param mContext
     * @param list
     * @param name
     * @param <T>
     * @return
     */
    public static <T> Boolean setAllArray(MMKV kv, Context mContext, List<T> list, String name) {
        //MMKV kv = MMKV.defaultMMKV();
        if (list == null || list.size() == 0) { //清空
            kv.putInt(name + "size", 0);
            int size = kv.getInt(name + "size", 0);
            for (int i = 0; i < size; i++) {
                if (kv.getString(name + i, null) != null) {
                    kv.remove(name + i);
                }
            }
        } else {
            kv.putInt(name + "size", list.size());
            for (int i = 0; i < list.size(); i++) {
                kv.remove(name + i);
                kv.remove(new Gson().toJson(list.get(i)));//删除重复数据 先删后加
                kv.putString(name + i, new Gson().toJson(list.get(i)));
            }
        }
        return kv.commit();
    }

    /**
     * 分页读取数据
     * @param kv
     * @param mContext
     * @param name
     * @param bean
     * @param page
     * @param limit
     * @param <T>
     * @return
     */
    public static <T> List<T> getPageArray(MMKV kv,  Context mContext, String name, T bean, int page, int limit) {
        //MMKV kv = MMKV.defaultMMKV();
        List<T> list = new ArrayList<T>();
        int size = kv.getInt(name + "size", 0);
        if((((page-1)*limit)+1)>size){
            return list;
        } else if(page*limit+1 > size){
            for (int i = size-1; i > ((page-1)*limit)-1; i--) {
                if (kv.getString(name + i, null) != null) {
                    try {
                        list.add((T) new Gson().fromJson(kv.getString(name + i, null), bean.getClass()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }else{
            for (int i = (page*limit)-1; i > ((page-1)*limit)-1 ; i--) {
                if (kv.getString(name + i, null) != null) {
                    try {
                        list.add((T) new Gson().fromJson(kv.getString(name + i, null), bean.getClass()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        return list;
    }

    /**
     * 读取列表记录个数
     * @param kv
     * @param mContext
     * @param name
     * @return
     */
    public static int getArrayTotal(MMKV kv,  Context mContext, String name)
    {
        int size = kv.getInt(name + "size", 0);
        return size;
    }

    /**
     * 在列中添加数据项目
     * @param kv
     * @param mContext
     * @param item
     * @param name
     * @param <T>
     * @return
     */
    public static <T> Boolean AddInArray(MMKV kv, Context mContext, T item, String name) {
        //MMKV kv = MMKV.defaultMMKV();
        if(item != null) {
            int size = kv.getInt(name + "size", 0);
            kv.putInt(name + "size", size + 1);
            kv.putString(name + size, new Gson().toJson(item));
            return kv.commit();

        }
        return false;
    }

}
