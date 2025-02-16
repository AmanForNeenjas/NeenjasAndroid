package com.hjq.demo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.hjq.bar.TitleBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.interceptor.SignInterceptor;
import com.hjq.demo.http.model.RequestHandler;
import com.hjq.demo.http.model.RequestServer;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.other.CrashHandler;
import com.hjq.demo.other.DebugLoggerTree;
import com.hjq.demo.other.MaterialHeader;
import com.hjq.demo.other.SmartBallPulseFooter;
import com.hjq.demo.other.TitleBarStyle;
import com.hjq.demo.other.ToastLogInterceptor;
import com.hjq.demo.other.ToastStyle;
import com.hjq.demo.other.WhiteSetToastStyle;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.language.MultiLanguages;
import com.hjq.language.OnLanguageListener;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
//import com.hjq.umeng.UmengClient;
import com.hjq.toast.style.WhiteToastStyle;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.Method;
import java.util.Locale;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 应用入口
 */
public final class BaseApplication extends Application {
    private static BaseApplication mApplication;
    private FscBleCentralApi sFscBleApi;

    @Log("启动耗时")
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // 初始化蓝牙
        sFscBleApi = FscBleCentralApiImp.getInstance(this);
        sFscBleApi.initialize();
        //sFscBleApi.isShowLog(AppConfig.isLogEnable());
        sFscBleApi.isShowLog(false);
        // 初始化语种切换框架
        MultiLanguages.init(this);
        // 设置语种变化监听器
        /*MultiLanguages.setOnLanguageListener(new OnLanguageListener() {

            @Override
            public void onAppLocaleChange(Locale oldLocale, Locale newLocale) {
                Log.d("MultiLanguages", "监听到应用切换了语种，旧语种：" + oldLocale + "，新语种：" + newLocale);
            }

            @Override
            public void onSystemLocaleChange(Locale oldLocale, Locale newLocale) {
                Log.d("MultiLanguages", "监听到系统切换了语种，旧语种：" + oldLocale + "，新语种：" + newLocale + "，是否跟随系统：" + MultiLanguages.isSystemLanguage());
            }
        });*/
        initSdk(this);

    }

    @Override
    protected void attachBaseContext(Context base) {

        //super.attachBaseContext(base);
        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(base));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        GlideApp.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp.get(this).onTrimMemory(level);
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSdk(Application application) {
        // 设置标题栏初始化器
        TitleBar.setDefaultStyle(new TitleBarStyle());

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((cx, layout) ->
                new MaterialHeader(application).setColorSchemeColors(ContextCompat.getColor(application, R.color.common_accent_color)));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((cx, layout) -> new SmartBallPulseFooter(application));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((cx, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });


        int defaultNightMode = mApplication.getResources().getConfiguration().uiMode &  Configuration.UI_MODE_NIGHT_MASK;

        //// EasyLog.print("========================defaultNightMode : "  +defaultNightMode );
        //// EasyLog.print("========================UI_MODE_NIGHT_NO : "  +Configuration.UI_MODE_NIGHT_NO );
        // 获取默认的模式
        //int defaultNightMode =AppCompatDelegate.getDefaultNightMode() & Configuration.UI_MODE_NIGHT_MASK;
        if(defaultNightMode == Configuration.UI_MODE_NIGHT_NO){
            // 初始化吐司
            ToastUtils.init(application, new ToastStyle());

        }else{
            ToastUtils.init(application, new WhiteSetToastStyle());

        }

        // 设置调试模式
        ToastUtils.setDebugMode(AppConfig.isDebug());
        // 设置 Toast 拦截器
        ToastUtils.setInterceptor(new ToastLogInterceptor());

        // 本地异常捕捉
        CrashHandler.register(application);

        // 友盟统计、登录、分享 SDK
        //UmengClient.init(application, AppConfig.isLogEnable());

        // Bugly 异常捕捉
        CrashReport.initCrashReport(application, AppConfig.getBuglyId(), AppConfig.isDebug());

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(application);

        // MMKV 初始化
        MMKV.initialize(application);

        // 网络请求框架初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(new RequestServer())
                // 设置请求处理策略
                .setHandler(new RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(0)
                //.setInterceptor((api, params, headers) -> {
                    // 添加全局请求头
                    //headers.put("token", "66666666666");
                    //headers.put("deviceOaid", UmengClient.getDeviceOaid());
                    //headers.put("versionName", AppConfig.getVersionName());
                    //headers.put("versionCode", String.valueOf(AppConfig.getVersionCode()));
                    // 添加全局请求参数
                    // params.put("6666666", "6666666");
                //})
                // 添加token
                .setInterceptor(new SignInterceptor())
                .into();

        // 设置 Json 解析容错监听
        GsonFactory.setJsonCallback((typeToken, fieldName, jsonToken) -> {
            // 上报到 Bugly 错误列表
            CrashReport.postCatchedException(new IllegalArgumentException(
                    "类型解析异常：" + typeToken + "#" + fieldName + "，后台返回的类型为：" + jsonToken));
        });

        // 初始化日志打印
        if (AppConfig.isLogEnable()) {
            Timber.plant(new DebugLoggerTree());
        }

        // 注册网络状态变化监听
        ConnectivityManager connectivityManager = ContextCompat.getSystemService(application, ConnectivityManager.class);
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    Activity topActivity = ActivityManager.getInstance().getTopActivity();
                    if (!(topActivity instanceof LifecycleOwner)) {
                        return;
                    }

                    LifecycleOwner lifecycleOwner = ((LifecycleOwner) topActivity);
                    if (lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
                        return;
                    }
                    // 网络状态 networkstatus
                    MMKV.mmkvWithID(MMKVUtils.CACHID).putBoolean(MMKVUtils.NETWORKSTATUS, false);
                    ToastUtils.show(R.string.common_network_error);
                }

                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    MMKV.mmkvWithID(MMKVUtils.CACHID).putBoolean(MMKVUtils.NETWORKSTATUS, true);
                }
            });
        }


    }
    @Override
    public void onTerminate() {
        MMKV.onExit();
        if(sFscBleApi!=null){
            sFscBleApi=null;
        }
        super.onTerminate();
    }

    public static Application getInstance(){
        if (mApplication == null) {
            try {
                Class clazz = Class.forName("android.app.ActivityThread");
                Method method = clazz.getMethod("currentApplication", new Class[]{});
                Object obj = method.invoke(null, new Object[]{});
                if (obj != null && obj instanceof BaseApplication) {
                    mApplication = (BaseApplication) obj;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mApplication;
    }
}