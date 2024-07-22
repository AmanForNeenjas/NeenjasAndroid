package com.hjq.demo.http.interceptor;

import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.tencent.mmkv.MMKV;

/**
 *  签名拦截器，加TOKEN
 */
public class SignInterceptor implements IRequestInterceptor {


    @Override
    public void interceptArguments(IRequestApi api, HttpParams params, HttpHeaders headers) {
        MMKV mMmkv =MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        String token = mMmkv.decodeString(MMKVUtils.TOKEN,"");
        // EasyLog.print("========================88SignInterceptor" + token);
        if(!token.isEmpty()){
            params.put("token", token);
        }
        //mMmkv = null;
    }
}
