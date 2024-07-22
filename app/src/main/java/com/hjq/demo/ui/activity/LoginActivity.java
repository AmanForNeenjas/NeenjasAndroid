package com.hjq.demo.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.DeviceBindMacListApi;
import com.hjq.demo.http.api.LoginApi;
import com.hjq.demo.http.api.UserInfoApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.manager.InputManyManager;
import com.hjq.demo.other.KeyboardWatcher;
import com.hjq.demo.ui.fragment.DeviceFragment;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.language.MultiLanguages;
import com.hjq.widget.view.SubmitButton;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 登录界面
 */
public final class LoginActivity extends AppActivity
        implements KeyboardWatcher.SoftKeyboardStateListener,
        TextView.OnEditorActionListener {

    //private static final String INTENT_KEY_IN_PHONE = "13414485712@139.com";
    //private static final String INTENT_KEY_IN_PASSWORD = "123456";

    @Log
    public static void start(Context context, String phone, String password) {
        Intent intent = new Intent(context, LoginActivity.class);
        //intent.putExtra(INTENT_KEY_IN_PHONE, phone);
        //intent.putExtra(INTENT_KEY_IN_PASSWORD, password);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
    private ImageView mLogoView;


    private ViewGroup mBodyLayout;
    private EditText mPhoneView;
    private EditText mPasswordView;

    private View mForgetView;
    private SubmitButton mCommitView;
    private CheckBox mContractView;

    private TextView mUserAgreementView;

    private TextView mPrivacyAgreementView;



    /** logo 缩放比例 */
    private final float mLogoScale = 0.8f;
    /** 动画时间 */
    private final int mAnimTime = 300;

    @Override
    protected int getLayoutId() {
        return R.layout.login_activity;
    }

    @Override
    protected void initView() {
        mLogoView = findViewById(R.id.iv_login_logo);
        mBodyLayout = findViewById(R.id.ll_login_body);
        mPhoneView = findViewById(R.id.et_login_phone);
        mPasswordView = findViewById(R.id.et_login_password);
        mForgetView = findViewById(R.id.tv_login_forget);
        mCommitView = findViewById(R.id.btn_login_commit);
        mContractView = findViewById(R.id.cb_user_contract);
        mUserAgreementView = findViewById(R.id.tv_userAgreementtwo);
        mPrivacyAgreementView = findViewById(R.id.tv_privacyAgreementtwo);

        setOnClickListener(R.id.tv_login_forget, R.id.btn_login_commit, R.id.tv_userAgreementtwo, R.id.tv_privacyAgreementtwo);

        mPasswordView.setOnEditorActionListener(this);

        TitleBar titleBar = findViewById(R.id.tb_login);
        titleBar.getRightView().setOnClickListener(view -> {
            startActivity(RegisterActivity.class);
        });

        /*InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mPasswordView)
                (mContractView)
                .setMain(mCommitView)
                .build();*/

        // 登录按键是否启用
        InputManyManager.with(this)
                .addView(mPhoneView)
                .addView(mPasswordView)
                .addRadio(mContractView)
                .setMain(mCommitView)
                .build();
        initData();
    }

    @Override
    protected void initData() {
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        //mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        /*
        postDelayed(() -> {
            KeyboardWatcher.with(LoginActivity.this)
                    .setListener(LoginActivity.this);
            // 自动填充手机号和密码
            //mPhoneView.setText(INTENT_KEY_IN_PHONE);
            //.setText(INTENT_KEY_IN_PASSWORD);
        }, 6000);
         */



    }

    @Override
    public void onRightClick(View view) {
        // 跳转到注册界面
        RegisterActivity.start(this, mPhoneView.getText().toString(),
                mPasswordView.getText().toString(), (phone, password) -> {
            // 如果已经注册成功，就执行登录操作
            mPhoneView.setText(phone);
            mPasswordView.setText(password);
            mPasswordView.requestFocus();
            mPasswordView.setSelection(mPasswordView.getText().length());

            //onClick(mCommitView);
        });
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mForgetView) {
            startActivity(PasswordForgetActivity.class);
            mCommitView.showError(3000);
            return;
        }


        if (view == mCommitView) {
            /*
                // 测试先注释 邮箱验证
            if(!mContractView.isChecked()){
                toast(R.string.login_check_tip);
                return;
            }
            if (!EmailUtil.isValidEmail(mPhoneView.getText().toString())) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }
            */
            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());

            /*if (true) {
                mCommitView.showProgress();
                postDelayed(() -> {
                    mCommitView.showSucceed();
                    postDelayed(() -> {
                        HomeActivity.start(getContext(), MineFragment.class);
                        finish();
                    }, 1000);
                }, 2000);
                return;
            }*/

            EasyHttp.post(this)
                    .api(new LoginApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setPassword(mPasswordView.getText().toString())
                            .setType("android"))
                    .request(new HttpCallback<HttpData<LoginApi.Bean>>(this) {

                        @Override
                        public void onStart(Call call) {
                            mCommitView.showProgress();
                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<LoginApi.Bean> data) {
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            // 更新 Token
                            //EasyConfig.getInstance()
                            //        .addParam("token", data.getData().getToken());
                            mMmkv.encode(MMKVUtils.TOKEN, data.getData().getToken());
                            //mMmkv =null;
                            getUserInfo();

                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            postDelayed(() -> {
                                mCommitView.showError(3000);
                            }, 1000);
                        }
                    });
            return;
        }else if(view.getId() == R.id.tv_userAgreementtwo){
            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_zh.html");
            }else{
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/agree_en.html");
            }
        }
        else if(view.getId() == R.id.tv_privacyAgreementtwo){
            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_zh.html");
            }else{
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/priv_en.html");
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 友盟回调
        //UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    private void getUserInfo(){
        // 刷新用户信息
        EasyHttp.post(this)
                .api(new UserInfoApi())
                .request(new HttpCallback<HttpData<UserInfo>>(this) {

                    @Override
                    public void onSucceed(HttpData<UserInfo> data) {
                        if(data.getCode() == 200){
                            MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                            mMmkv.encode(MMKVUtils.USERINFO, data.getData());
                            //mMmkv =null;
                            // 获取绑定MAC列表
                            getBindMacList();
                        }else{
                            toast(data.getMessage());
                        }
                    }
                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        toast(R.string.user_info_error);
                    }
                });
    }
    private void getBindMacList(){
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
                        postDelayed(() -> {
                            mCommitView.showSucceed();
                            postDelayed(() -> {
                                // 跳转到首页
                                HomeActivity.start(getContext(), DeviceFragment.class);
                                finish();
                            }, 1000);
                        }, 1000);
                    }

                    @Override
                    public void onFail(Exception e) {
                        postDelayed(() -> {
                            mCommitView.showSucceed();
                            postDelayed(() -> {
                                // 跳转到首页
                                HomeActivity.start(getContext(), DeviceFragment.class);
                                finish();
                            }, 1000);
                        }, 1000);
                    }
                });
    }



    /**
     * {@link KeyboardWatcher.SoftKeyboardStateListener}
     */

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", 0, -mCommitView.getHeight());
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        // 执行缩小动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", 1f, mLogoScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", 1f, mLogoScale);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", 0f, -mCommitView.getHeight());
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    @Override
    public void onSoftKeyboardClosed() {
        // 执行位移动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBodyLayout, "translationY", mBodyLayout.getTranslationY(), 0f);
        objectAnimator.setDuration(mAnimTime);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        if (mLogoView.getTranslationY() == 0) {
            return;
        }

        // 执行放大动画
        mLogoView.setPivotX(mLogoView.getWidth() / 2f);
        mLogoView.setPivotY(mLogoView.getHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mLogoView, "scaleX", mLogoScale, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mLogoView, "scaleY", mLogoScale, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mLogoView, "translationY", mLogoView.getTranslationY(), 0f);
        animatorSet.play(translationY).with(scaleX).with(scaleY);
        animatorSet.setDuration(mAnimTime);
        animatorSet.start();
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击登录按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }
}