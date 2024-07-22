package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.api.NickNameApi;
import com.hjq.demo.http.api.PasswordApi;
import com.hjq.demo.http.api.PhoneApi;
import com.hjq.demo.http.api.SetAutoLoginApi;
import com.hjq.demo.http.api.UnRegisterApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.manager.CacheDataManager;
import com.hjq.demo.manager.ThreadPoolManager;
import com.hjq.demo.other.AppConfig;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.ui.dialog.PasswordDialog;
import com.hjq.demo.ui.dialog.PhoneDialog;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.ui.dialog.UpdateDialog;
import com.hjq.demo.util.CommonUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.RoundBottomSettingBar;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;
import com.tencent.mmkv.MMKV;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设置界面
 */
public final class SettingActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

    private static final String INTENT_KEY_USERINFO = "userinfo";
    private static final String INTENT_KEY_NICKNAME = "nickname";

    private RoundTopSettingBar mNickNameView;
    private SettingBar mPhoneView;
    private RoundBottomSettingBar mPasswordView;
    private SettingBar mCleanCacheView;
    private SwitchButton mAutoSwitchView;
    private SettingBar mCloseAcountView;
    private String newname;
    private UserInfo userInfo;


    public static void start(BaseActivity activity,  OnSetNickNameListener  listener) {
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_NICKNAME));
            } else {
                listener.onCancel();
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.setting_activity;
    }

    @Override
    protected void initView() {
        mNickNameView = findViewById(R.id.sb_setting_nickname);
        mPhoneView = findViewById(R.id.sb_setting_phone);
        mPasswordView = findViewById(R.id.sb_setting_password);
        mCleanCacheView = findViewById(R.id.sb_setting_cache);
        mAutoSwitchView = findViewById(R.id.sb_setting_switch);
        mCloseAcountView = findViewById(R.id.sb_close_count);


        // 设置切换按钮的监听



        setOnClickListener(R.id.sb_setting_nickname,  R.id.sb_setting_phone,
                R.id.sb_setting_password,
                R.id.sb_setting_cache, R.id.sb_setting_auto,R.id.sb_close_count, R.id.sb_setting_exit);
        getTitleBar().getLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newname.isEmpty()){
                    postDelayed(() -> {

                        setResult(RESULT_OK, new Intent()
                                .putExtra(INTENT_KEY_NICKNAME, newname)
                        );
                        finish();
                    }, 1000);
                }
                else{
                    postDelayed(() -> {
                        setResult(RESULT_CANCELED);
                        finish();
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void initData() {

        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
        newname = "";
        // 获取应用缓存大小
        mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

        mNickNameView.setRightText(userInfo.getNickname());
        mPhoneView.setRightText(userInfo.getCountryno() + "-" + userInfo.getMobile());
        //mAutoSwitchView.setChecked(userInfo.isIsautologin());
        if(userInfo.isIsautologin()){
            mAutoSwitchView.setChecked(true);
        }else {
            mAutoSwitchView.setChecked(false);
        }
        mAutoSwitchView.setOnCheckedChangeListener(this);
        //toast(userInfo.isLogined());
        //mPasswordView.setRightText();
    }



    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.sb_setting_nickname) {
            // 输入对话框
            new InputDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(R.string.setting_nickname)
                    .setWidth(900)
                    // 内容可以不用填写
                    .setContent("")
                    // 提示可以不用填写
                    .setHint(R.string.setting_nickname_tip)
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new InputDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog, String content) {
                            newname = content;
                            setNickName();
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.common_cancel);
                        }
                    })
                    .show();

        }  else if (viewId == R.id.sb_setting_phone) {
            // 输入对话框
            new PhoneDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(R.string.setting_phone)
                    .setWidth(900)
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new PhoneDialog.OnListener() {


                        @Override
                        public void onConfirm(BaseDialog dialog, String countryno, String phone) {
                            setPhone(countryno, phone);
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            PhoneDialog.OnListener.super.onCancel(dialog);
                        }
                    })
                    .show();


        } else if (viewId == R.id.sb_setting_password) {
            PasswordSetActivity.start(getContext());
            /*new PasswordDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(R.string.setting_password)
                    .setWidth(900)
                    .setYOffset(800)
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new PasswordDialog.OnListener() {
                        @Override
                        public void onConfirm(BaseDialog dialog, String oldpwd, String newpwd, String surepwd) {
                            if(!newpwd.equals(surepwd)){
                                toast(R.string.password_reset_input_error);
                            }else{
                                if(oldpwd.equals(newpwd)){
                                    toast(R.string.password_reset_input_error);
                                }
                            }
                            setPassword(oldpwd, newpwd, surepwd);
                        }
                        @Override
                        public void onCancel(BaseDialog dialog) {
                            PasswordDialog.OnListener.super.onCancel(dialog);
                        }
                    })
                    .show();
            */

        } else if (viewId == R.id.sb_close_count) {
            // 关闭账号
            new MessageDialog.Builder(getActivity())
                    // 标题可以不用填写
                    .setTitle(R.string.close_count)
                    // 内容必须要填写
                    .setMessage(R.string.close_count_tip)
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new MessageDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog) {
                            // 去注销
                            closeAcount();

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {


                        }
                    })
                    .show();

        } else if (viewId == R.id.sb_setting_switch) {

            // 自动登录
            mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());
            setAutoLogin();

        } else if (viewId == R.id.sb_setting_cache) {

            // 清除内存缓存（必须在主线程）
            GlideApp.get(getActivity()).clearMemory();
            ThreadPoolManager.getInstance().execute(() -> {
                CacheDataManager.clearAllCache(this);
                // 清除本地缓存（必须在子线程）
                GlideApp.get(getActivity()).clearDiskCache();
                post(() -> {
                    // 重新获取应用缓存大小
                    mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(getActivity()));
                });
            });

        } else if (viewId == R.id.sb_setting_exit) {

            if (true) {
                startActivity(LoginActivity.class);
                // 进行内存优化，销毁除登录页之外的所有界面
                ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                return;
            }

            // 退出登录
            EasyHttp.post(this)
                    .api(new LogoutApi())
                    .request(new HttpCallback<HttpData<Void>>(this) {

                        @Override
                        public void onSucceed(HttpData<Void> data) {
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            mMmkv.encode(MMKVUtils.TOKEN, "");
                            mMmkv.encode(MMKVUtils.USERINFO, new UserInfo());
                            mMmkv.putBoolean(MMKVUtils.AUTOLOGIN, true);
                            MMKVUtils.setArray(mMmkv, getContext(), null, MMKVUtils.DEVICEBINDLIST);
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                        }
                    });

        }
    }

    private void closeAcount(){
        // 注销
        EasyHttp.post(this)
                .api(new UnRegisterApi())
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.close_count_success);
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            mMmkv.encode(MMKVUtils.TOKEN, "");
                            mMmkv.encode(MMKVUtils.USERINFO, new UserInfo());
                            mMmkv.putBoolean(MMKVUtils.AUTOLOGIN, true);
                            MMKVUtils.setArray(mMmkv, getContext(), null, MMKVUtils.DEVICEBINDLIST);
                            startActivity(LoginActivity.class);
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity.class);
                        }
                    }
                });
    }

    private void setNickName(){

        if(newname.length()>10){
            newname = newname.substring(0,9);
        }
        // 修改呢称
        EasyHttp.post(this)
                .api(new NickNameApi()
                        .setNickname(newname))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.success);
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
                            userInfo.setNickname(newname);
                            mMmkv.encode(MMKVUtils.USERINFO, userInfo);
                            mNickNameView.setRightText(userInfo.getNickname());

                        }

                    }
                });
    }
    private void setPhone( String countrycode, String phone){
        // 设置手机号码
        EasyHttp.post(this)
                .api(new PhoneApi()
                        .setPhone(phone)
                        .setCountrycode(countrycode))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.success);
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
                            userInfo.setCountryno(countrycode);
                            userInfo.setMobile(data.getData());
                            mMmkv.encode(MMKVUtils.USERINFO, userInfo);
                            mPhoneView.setRightText(userInfo.getCountryno() + "-" + userInfo.getMobile());
                        }

                    }
                });
    }

    private void setPassword(String oldpassword, String newpassword, String surepassword){
        // 设置密码
        EasyHttp.post(this)
                .api(new PasswordApi()
                        .setNewpassword(newpassword)
                        .setOldpassword(oldpassword)
                        .setSurepassword(surepassword))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.success);
                            if(newpassword.length()<8){
                                mPasswordView.setRightText(R.string.password_complexa);
                            }else if(CommonUtil.isValidPassword(newpassword)) {
                                mPasswordView.setRightText(R.string.password_complexc);
                            } else {
                                mPasswordView.setRightText(R.string.password_complexb);
                            }
                        }

                    }
                });
    }

    private void setAutoLogin(){
        boolean isauto = mAutoSwitchView.isChecked();
        //
        EasyHttp.post(this)
                .api(new SetAutoLoginApi()
                        .setAutologin(isauto?1:0))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                        mMmkv.putBoolean(MMKVUtils.AUTOLOGIN, isauto);
                        userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
                        userInfo.setIsautologin(isauto);
                        mMmkv.encode(MMKVUtils.USERINFO, userInfo);
                        toast(R.string.success);
                    }
                });
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        //toast(checked);
        //if(checked != userInfo.isLogined()){
            setAutoLogin();
        //}

    }

    /**
     * 注册监听
     */
    public interface OnSetNickNameListener {

        /**
         * 设置昵称成功
         *
         * @param name  昵称
         */
        void onSucceed(String name);

        /**
         * 取消注册
         */
        default void onCancel() {}
    }

}