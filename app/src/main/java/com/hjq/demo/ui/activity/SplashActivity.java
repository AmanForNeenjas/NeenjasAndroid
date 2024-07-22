package com.hjq.demo.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.AppSetInfoApi;
import com.hjq.demo.http.api.DeviceBindMacListApi;
import com.hjq.demo.http.api.UserInfoApi;
import com.hjq.demo.http.api.UserInfoByAutoLoginApi;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.dialog.AgreementDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.language.MultiLanguages;
import com.hjq.widget.view.SlantedTextView;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 闪屏界面
 */
public final class SplashActivity extends AppActivity {

    private LottieAnimationView mLottieView;
    private SlantedTextView mDebugView;
    private Boolean isfirstLogin;
    private Boolean networkOk;
    private String token;
    private Boolean isautoLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.splash_activity;
    }

    @Override
    protected void initView() {
        mLottieView = findViewById(R.id.lav_splash_lottie);
        //mDebugView = findViewById(R.id.iv_splash_debug);
        // 设置动画监听
        mLottieView.addAnimatorListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mLottieView.removeAnimatorListener(this);
                firstLogin();
                //finish();
            }
        });
    }


    @Override
    protected void initData() {
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        /*mDebugView.setText(AppConfig.getBuildType().toUpperCase());
        if (AppConfig.isDebug()) {
            mDebugView.setVisibility(View.VISIBLE);
        } else {
            mDebugView.setVisibility(View.INVISIBLE);
        }*/
        //// EasyLog.print("======================== getLanguage " + MultiLanguages.getAppLanguage().getLanguage());;
        // 是否是第一次登录
        isfirstLogin = mMmkv.getBoolean(MMKVUtils.FIRSTLOGIN, true);
        networkOk =mMmkv.getBoolean(MMKVUtils.NETWORKSTATUS, false);

        EasyHttp.post(this)
                .api(new AppSetInfoApi())
                .request(new HttpCallback<HttpData<AppSetInfo>>(this) {

                    @Override
                    public void onSucceed(HttpData<AppSetInfo> data) {
                        if(data.getCode() == 200){
                            //EasyLog.print("======================== getLanguage " + data.getData().appcopy);
                           // EasyLog.print("======================== getLanguage " + data.getData().globaltest);
                            mMmkv.encode(MMKVUtils.APPSETINFO, data.getData());
                        }
                    }
                });
        token = mMmkv.decodeString(MMKVUtils.TOKEN,"");
        isautoLogin = mMmkv.getBoolean(MMKVUtils.AUTOLOGIN, true);

        if(token.isEmpty()){
            return;
        }
        if(networkOk){
            // 刷新用户信息
            EasyHttp.post(this)
                    .api(new UserInfoByAutoLoginApi()
                            .setType("android"))
                    .request(new HttpCallback<HttpData<UserInfo>>(this) {

                        @Override
                        public void onSucceed(HttpData<UserInfo> data) {
                            if(data.getCode() == 200){
                                MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                mMmkv.encode(MMKVUtils.USERINFO, data.getData());
                                //mMmkv = null;
                            }
                        }
                    });
            EasyHttp.post(this)
                    .api(new DeviceBindMacListApi())
                    .request(new HttpCallback<HttpListData<String>>(this) {

                        @Override
                        public void onSucceed(HttpListData<String> data) {
                            if(data.getCode() == 200){
                                HashSet<String> macset = new HashSet<>(data.getData().getItems());
                                MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                mMmkv.encode(MMKVUtils.DEVICEBINDMACLIST, macset);
                                //mMmkv = null;
                            }
                        }
                    });
        }

    }

    /**
     * 第一次登录
     */
    private void firstLogin(){

        if(isfirstLogin == true){
            new AgreementDialog.Builder(getActivity())
                    .setTitle(R.string.userAndPrivacyAgreement)
                    // 标题可以不用填写
                    // 确定按钮文本
                    .setWidth(900)
                    .setConfirm(getString(R.string.agree))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.refuse))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new AgreementDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            mMmkv.putBoolean(MMKVUtils.FIRSTLOGIN, false);
                            //mMmkv = null;
                            token = "";
                            enterSystem();
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.userrefuse);
                            finish();
                        }

                        @Override
                        public void onClickUserAgree(BaseDialog dialog) {
                            //int defaultNightMode = AppCompatDelegate.getDefaultNightMode();

                            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                                // light
                                //if(defaultNightMode == Configuration.UI_MODE_NIGHT_NO){
                                    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_zh.html");
                                //}else{
                                //    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_zh.html");
                                //}

                            }else{
                                //if(defaultNightMode == Configuration.UI_MODE_NIGHT_NO){
                                    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_en.html");
                                //}else{
                                //    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_en.html");
                               // }

                            }

                        }

                        @Override
                        public void onClickPrivacyAgree(BaseDialog dialog) {

                            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_zh.html");
                            }else{
                                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_en.html");
                            }
                        }
                    })
                    .show();
        }else{
            enterSystem();
        }
    }

    /**
     * 进入系统初始化工作及跳转
     */
    private void enterSystem(){

        if(token.isEmpty()){
            LoginActivity.start(this,"", "");
        }else{
            // 自动登录
            if(isautoLogin){
                HomeActivity.start(getContext());
            } else{
                LoginActivity.start(this,"", "");
            }
        }
        finish();
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 隐藏状态栏和导航栏
                .hideBar(BarHide.FLAG_HIDE_BAR);
    }



    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }

    @Override
    protected void initActivity() {
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (intent != null && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && Intent.ACTION_MAIN.equals(intent.getAction())) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish();
                return;
            }
        }
        super.initActivity();
    }

    @Deprecated
    @Override
    protected void onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy();
    }
}