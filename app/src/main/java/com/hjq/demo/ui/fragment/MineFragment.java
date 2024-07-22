package com.hjq.demo.ui.fragment;


import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.TitleBarFragment;

import com.hjq.demo.http.api.GetAppUpgradePackageApi;
import com.hjq.demo.http.api.GetAppUpgradePackageSimpleApi;

import com.hjq.demo.http.model.AppUpgradeSimple;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.UserInfo;

import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.activity.AboutActivity;
import com.hjq.demo.ui.activity.BrowserActivity;


import com.hjq.demo.ui.activity.HelpCenterActivity;
import com.hjq.demo.ui.activity.HomeActivity;


import com.hjq.demo.ui.activity.MyQrcodeActivity;



import com.hjq.demo.ui.activity.RegisterActivity;
import com.hjq.demo.ui.activity.SettingActivity;
import com.hjq.demo.ui.activity.SplashActivity;
import com.hjq.demo.ui.activity.StatusActivity;

import com.hjq.demo.ui.activity.VideoSelectActivity;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.demo.util.CommonUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.language.MultiLanguages;
import com.hjq.shape.view.ShapeTextView;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.hjq.widget.view.DrawableTextView;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 我的 Fragment
 */
public final class MineFragment extends TitleBarFragment<HomeActivity> {

    private UserInfo userInfo;
    private DrawableTextView mUidView;
    private TextView mNicknameView;
    private ImageView mAvaCornerView;
    private ShapeTextView mHasnewrefreshView;
    //private AppUpgradeSimple appUpgrade;
    //private int appcount;
    private RoundTopSettingBar mQrCodeView;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_fragment;
    }

    @Override
    protected void initView() {
        mUidView = findViewById(R.id.dtv_uid);
        mNicknameView = findViewById(R.id.tv_nickname);
        mAvaCornerView = findViewById(R.id.iv_person_data_avatar);
        mHasnewrefreshView = findViewById(R.id.st_hasnewrefresh);
        mQrCodeView = findViewById(R.id.sb_myqrcode);
        setOnClickListener(R.id.iv_person_data_avatar, R.id.dtv_uid,R.id.tv_nickname, R.id.dtv_right, R.id.sb_language, R.id.sb_help, R.id.sb_checkrefresh,R.id.sb_setting_agreement,
                R.id.sb_about, R.id.sb_myqrcode);
        //ShapeTextView shapeTextView = findViewById(R.id.st_hasnewrefresh);
        //if (1 > AppConfig.getVersionCode()) {
        //    shapeTextView.setVisibility(View.VISIBLE);
        //}
        //appcount = 0;
        //appUpgrade = null;

    }


    @Override
    protected void initData() {
        // 显示圆角的 ImageView
        GlideApp.with(this)
                .load(R.drawable.bg5)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_10))))
                .into(mAvaCornerView);
        getAppUpgrade();
        //showUid();
    }

    private void showUid(){
        userInfo = ((HomeActivity)getActivity()).getUserInfo();
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setLogined(false);
            userInfo.setEmail("");
            userInfo.setMobile("");
            userInfo.setNickname(getString(R.string.guest));
            userInfo.setUid(0);
        }
        String uid = "";
        if(!userInfo.getEmail().isEmpty()){
            uid = userInfo.getEmail();
        }
        else if(!userInfo.getMobile().isEmpty()){
            uid = userInfo.getMobile();
        }
        mUidView.setText(uid);
        // EasyLog.print("========================1 newname" + userInfo.getNickname());
        mNicknameView.setText(userInfo.getNickname());
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_person_data_avatar) {
            if(userInfo.isLogined()){
                //startActivity(SettingActivity.class);
                startSetting();

            } else {
                toast(R.string.login_tip);
            }


        } else if(viewId == R.id.dtv_uid){
            if(userInfo.isLogined()){
                //startActivity(SettingActivity.class);
                startSetting();
            } else {
                toast(R.string.login_tip);
            }
        }else if(viewId == R.id.tv_nickname){
            if(userInfo.isLogined()){
                //startActivity(SettingActivity.class);
                startSetting();
            } else {
                toast(R.string.login_tip);
            }
        } else if(viewId == R.id.dtv_right){
            if(userInfo.isLogined()){
                //startActivity(SettingActivity.class);
                startSetting();
            } else {
                toast(R.string.login_tip);
            }
        } else if(viewId == R.id.sb_language){

            // 底部选择框
            new MenuDialog.Builder(getContext())
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(R.string.bysys, R.string.setting_language_en,R.string.setting_language_ukr,R.string.setting_language_simple)
                    .setListener((MenuDialog.OnListener<String>) (dialog, position, string) -> {
                        //toast("选择语言：" + string);
                        //toast("选择：position" + position);
                        setLang(position);
                        //mLanguageView.setRightText(string);
                        //BrowserActivity.start(getActivity(), "https://github.com/getActivity/MultiLanguages");
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                    .show();
        } else if(viewId == R.id.sb_help){
            startActivity(HelpCenterActivity.class);
        } else if(viewId == R.id.sb_checkrefresh){
            //toast(R.string.update_no_update);
            // 本地的版本码和服务器的进行比较
            getAppUpgradeStart();

        } else if(viewId == R.id.sb_setting_agreement){
            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_zh.html");
            }else{
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_en.html");
            }
        } else if(viewId == R.id.sb_about){
            startActivity(AboutActivity.class);
        } else if (viewId == R.id.sb_myqrcode){
            // 显示我的二维码
            if(userInfo.isLogined()){
                MyQrcodeActivity.start(getContext(), userInfo.getUid(),userInfo.getEmail());
            } else {
                toast(R.string.login_tip);
            }
        }


    }

    private void setLang(int lang){
        // 是否需要重启
        boolean restart;
        // toast("选择：lang" + lang);
        switch (lang) {
            // 跟随系统
            case 0:
                //toast("选择：0" );
                restart = MultiLanguages.clearAppLanguage(getContext());
                break;
            // 英语
            case 1:
                //toast("选择：1" );
                restart = MultiLanguages.setAppLanguage(getContext(), Locale.ENGLISH);
                break;
            // 简体中文
            case 2:
                //toast("选择：2" );
                //restart = MultiLanguages.setAppLanguage(getContext(), Locale.FRANCE);
                restart = MultiLanguages.setAppLanguage(getContext(), new Locale("uk","UA"));
                //restart = MultiLanguages.setAppLanguage(getContext(), new Locale("uk"));
                break;
            case 3:
                //toast("选择：2" );
                //restart = MultiLanguages.setAppLanguage(getContext(), new Locale("uk"));
                restart = MultiLanguages.setAppLanguage(getContext(),Locale.CHINA);
                break;
            default:
                restart = false;
                break;
        }
        //toast("选择：8" );
        if (restart) {
            toast("选择：restart" + (restart?"true":"false"));
            // 我们可以充分运用 Activity 跳转动画，在跳转的时候设置一个渐变的效果
            //((HomeActivity)getActivity()).reStart();
            toast(R.string.setting_language_success);
            //ActivityManager.getInstance().finishAllActivities();
        }
    }

    /**
     * 监测昵称是滞修改成功
     */
    private void startSetting(){
        // 跳转到设置界面
        SettingActivity.start((HomeActivity)getActivity(),
                 (newname) -> {
                     //mNicknameView.setText(newname);
                     ((HomeActivity)getActivity()).setUserInfo(newname);
                     //// EasyLog.print("========================2 newname" + newname);
                    //onClick(mCommitView);
                });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHasnewrefreshView.setVisibility(View.GONE);
        showUid();
        //getAppUpgrade();
    }

    private  void getAppUpgrade(){
        /*
        { key: 0, label: 'android' },
        { key: 1, label: 'iphone' },
        { key: 2, label: 'ipad' }
        tagOptions: [
        { key: 'global', label: '国际' },
        { key: 'local', label: '国内' },
        { key: 'test', label: '测试' }
      ],
         */
        String versioncode = AppConfig.getVersionName();
        // EasyLog.print("======================== versioncode" + versioncode);
        String[]  arr = versioncode.split("\\.");
        int versionid = 0;
        int versionmini = 0;
        if(arr.length ==2){
            try{

                versionid =Integer.parseInt(arr[0]);
                versionmini = Integer.parseInt(arr[1]);
                // EasyLog.print("======================== versionmini" + versionmini);
            }catch (Exception e){

            }

        }else{
            try{
                versionid =Integer.parseInt(getString(R.string.versionid));
                versionmini = Integer.parseInt(getString(R.string.versionmini));
            }catch (Exception e){

            }
        }
        String phoneid = "";
        // 测试版，正式版要修改的
        EasyHttp.post(this)
                .api(new GetAppUpgradePackageApi()
                        .setApptype(0)
                        .setPhoneid(phoneid)
                        .setTag("global")
                        //.setTag("test")
                        .setVersioncode(versioncode)
                        .setVersionid(versionid)
                        .setVersionmini(versionmini)
                )
                .request(new HttpCallback<HttpData<AppUpgradeSimple>>(this) {

                    @Override
                    public void onSucceed(HttpData<AppUpgradeSimple> data) {
                        if(data.getCode() == 200){
                            if(data.getCount() >0){
                                //appcount = 1;
                                //appUpgrade = data.getData();
                                //toast(data.getData().checksum + " " + appcount + appUpgrade.vpoint);
                                mHasnewrefreshView.setVisibility(View.VISIBLE);
                            }

                        }else{
                            //appcount = 0;
                            //appUpgrade= null;
                            mHasnewrefreshView.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        //appcount = 0;
                        //appUpgrade= null;
                        mHasnewrefreshView.setVisibility(View.GONE);
                    }
                });
    }
    private  void getAppUpgradeStart(){
        /*
        { key: 0, label: 'android' },
        { key: 1, label: 'iphone' },
        { key: 2, label: 'ipad' }
        tagOptions: [
        { key: 'global', label: '国际' },
        { key: 'local', label: '国内' },
        { key: 'test', label: '测试' }
      ],
         */
        String versioncode = AppConfig.getVersionName();
        // EasyLog.print("======================== versioncode" + versioncode);
        String[]  arr = versioncode.split("\\.");
        int versionid = 0;
        int versionmini = 0;
        if(arr.length ==2){
            try{

                versionid =Integer.parseInt(arr[0]);
                versionmini = Integer.parseInt(arr[1]);
                // EasyLog.print("======================== versionmini" + versionmini);
            }catch (Exception e){

            }

        }else{
            try{
                versionid =Integer.parseInt(getString(R.string.versionid));
                versionmini = Integer.parseInt(getString(R.string.versionmini));
            }catch (Exception e){

            }
        }
        String phoneid = "";
        // 测试版，正式版要修改的
        EasyHttp.post(this)
                .api(new GetAppUpgradePackageSimpleApi()
                        .setApptype(0)
                        .setPhoneid(phoneid)
                        .setTag("global")
                        //.setTag("test")
                        .setVersioncode(versioncode)
                        .setVersionid(versionid)
                        .setVersionmini(versionmini)
                )
                .request(new HttpCallback<HttpData<AppUpgradeSimple>>(this)  {

                    @Override
                    public void onSucceed(HttpData<AppUpgradeSimple> data) {
                        if(data.getCode() == 200){
                            if(data.getCount() >0){
                                  if ( (!CommonUtil.isEmptyIfStr(data.getData().vurl)) && (!CommonUtil.isEmptyIfStr(data.getData().checksum))) {
                                      // google play版本
                                      //toast(R.string.update_no_update_googleplay);
                                      // 非google play版本 自己安装

                                        new UpdateDialog.Builder(getContext())
                                                .setVersionName("V"+ data.getData().vcode+".0")
                                                .setForceUpdate(false)
                                                .setUpdateLog(data.getData().vpoint)
                                                .setDownloadUrl(data.getData().vurl)
                                                .setFileMd5(data.getData().checksum)
                                                .show();


                                   } else {
                                        toast(R.string.update_no_update);
                                    }
                                //}
                            }

                        }else{
                            toast(R.string.update_no_update);
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        toast(R.string.update_no_update);
                    }
                });
    }
}