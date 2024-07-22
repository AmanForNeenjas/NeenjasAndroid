package com.hjq.demo.util.simplecache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.demo.app.BaseApplication;
import com.hjq.demo.bean.ConnectDev;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CacheUtil {
    private JSONArray jsonArray;
    private static CacheUtil cacheUtil;
    private  ACache mCache;


    public static CacheUtil getInstance(){
        if(cacheUtil ==null){
            synchronized (CacheUtil.class){
                if(cacheUtil == null){
                    cacheUtil = new CacheUtil();
                }
            }
            cacheUtil.mCache  = ACache.get(BaseApplication.getInstance());
        }
        return cacheUtil;
    }

    /**
     * save连接设备到缓存
     * @param list
     */
    public void save(List<ConnectDev> list){
        String json = new Gson().toJson(list);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
            mCache.put("connectdevlist",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读连接设备缓存
     *
     * @param
     */
    public List<ConnectDev>  read() {
        JSONArray testJsonArray = mCache.getAsJSONArray("connectdevlist");
        List<ConnectDev> list = new ArrayList<>();
        if (testJsonArray == null) {

            return list;
        }
        return (new Gson()).fromJson(String.valueOf(testJsonArray),new TypeToken<List<ConnectDev>>() {}.getType());
    }
}
