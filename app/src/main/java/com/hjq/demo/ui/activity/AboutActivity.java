package com.hjq.demo.ui.activity;

import androidx.appcompat.widget.AppCompatTextView;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyLog;
import com.tencent.mmkv.MMKV;
/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 关于界面
 */
public final class AboutActivity extends AppActivity {
    private AppCompatTextView mAppVersionView;
    private AppCompatTextView mAppcopyrightView;

    @Override
    protected int getLayoutId() {
        return R.layout.about_activity;
    }

    @Override
    protected void initView() {

        mAppVersionView = findViewById(R.id.tv_app_version);
        mAppcopyrightView = findViewById(R.id.tx_copyright);
    }

    @Override
    protected void initData() {
        //String versioncode = AppConfig.getVersionName();
        //String versioncode = AppConfig.getVersionName();
        mAppVersionView.setText( getString(R.string.versionid) +"."+getString(R.string.versionmini)+".0");
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);
        if(appSetInfo != null){
            // EasyLog.print("======================== appSetInfo.appcopy-> " + appSetInfo.appcopy);
            mAppcopyrightView.setText(appSetInfo.appcopy);
        }

    }
}