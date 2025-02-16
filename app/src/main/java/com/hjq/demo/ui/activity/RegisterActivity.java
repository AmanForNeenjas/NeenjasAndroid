package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.CheckEmailApi;
import com.hjq.demo.http.api.RegByEmailApi;
import com.hjq.demo.http.api.SendEmailApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.util.CommonUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;
import com.hjq.widget.view.SubmitButton;
import com.tencent.mmkv.MMKV;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 注册界面
 */
public final class RegisterActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private static final String INTENT_KEY_PHONE = "phone";
    private static final String INTENT_KEY_PASSWORD = "password";
    //private static final String INTENT_KEY_IN_PHONE = "szhmjc@163.com";
    //private static final String INTENT_KEY_IN_PASSWORD = "123456";


    public static void start(BaseActivity activity, String phone, String password, OnRegisterListener listener) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_PASSWORD, password);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_PHONE), data.getStringExtra(INTENT_KEY_PASSWORD));
            } else {
                listener.onCancel();
            }
        });
    }

    private EditText mPhoneView;
    private CountdownView mCountdownView;

    private EditText mCodeView;

    private EditText mFirstPassword;
    private EditText mSecondPassword;

    private SubmitButton mCommitView;

    private SubmitButton mReturnView;

    @Override
    protected int getLayoutId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_register_phone);
        mCountdownView = findViewById(R.id.cv_register_countdown);
        mCodeView = findViewById(R.id.et_register_code);
        mFirstPassword = findViewById(R.id.et_register_password1);
        mSecondPassword = findViewById(R.id.et_register_password2);
        mCommitView = findViewById(R.id.btn_register_commit);
        mReturnView = findViewById(R.id.btn_register_return);

        setOnClickListener(mCountdownView, mCommitView,mReturnView);

        mSecondPassword.setOnEditorActionListener(this);

        // 给这个 View 设置沉浸式，避免状态栏遮挡
        ImmersionBar.setTitleBar(this, findViewById(R.id.tv_register_title));

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {

        // 自动填充手机号和密码
        //mPhoneView.setText(getString(INTENT_KEY_PHONE));
        //mFirstPassword.setText(getString(INTENT_KEY_PASSWORD));
        //mSecondPassword.setText(getString(INTENT_KEY_PASSWORD));

        /*postDelayed(() -> {

            // 自动填充手机号和密码
            mPhoneView.setText(INTENT_KEY_IN_PHONE);
            mFirstPassword.setText(INTENT_KEY_IN_PASSWORD);
            mSecondPassword.setText(INTENT_KEY_IN_PASSWORD);
        }, 6000);*/
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCountdownView) {
            if (!CommonUtil.isValidEmail(mPhoneView.getText().toString())) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }
            // 校验邮箱是否存在
            EasyHttp.post(this)
                    .api(new CheckEmailApi()
                            .setMobile(mPhoneView.getText().toString()))
                    .request(new HttpCallback<HttpData<String>>(this) {

                        @Override
                        public void onStart(Call call) {
                            //mCommitView.showProgress();
                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<String> data) {
                            // 更新 Token
                            //EasyConfig.getInstance()
                            //        .addParam("token", data.getData().getToken());
                            if(data.getCode() == 200){
                                //不存在 去获取验证码
                                getCode();
                            }else{
                                toast(data.getMessage());
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            postDelayed(() -> {
                                mCommitView.showError(3000);
                            }, 1000);
                        }
                    });
            /*if (true) {
                toast(R.string.common_code_send_hint);
                mCountdownView.start();
                return;
            }*/


        } else if (view == mCommitView) {
            if (!CommonUtil.isValidEmail(mPhoneView.getText().toString())) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_code_error_hint);
                return;
            }

            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mCommitView.showError(3000);
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());



            // 提交注册
            EasyHttp.post(this)
                    .api(new RegByEmailApi()
                            .setPhone(mPhoneView.getText().toString())
                            .setCode(mCodeView.getText().toString())
                            .setPassword(mFirstPassword.getText().toString()))
                    .request(new HttpCallback<HttpData<RegByEmailApi.Bean>>(this) {

                        @Override
                        public void onStart(Call call) {
                            mCommitView.showProgress();
                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<RegByEmailApi.Bean> data) {
                            if(data.getCode() == 200){
                                MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                mMmkv.putString("token", data.getData().getToken());
                                postDelayed(() -> {
                                    mCommitView.showSucceed();
                                    postDelayed(() -> {
                                        setResult(RESULT_OK, new Intent()
                                                .putExtra(INTENT_KEY_PHONE, mPhoneView.getText().toString())
                                                .putExtra(INTENT_KEY_PASSWORD, mFirstPassword.getText().toString()));
                                        finish();
                                    }, 1000);
                                }, 1000);
                            }else{
                                toast(data.getMessage());
                                postDelayed(() -> {
                                    mCommitView.showError(3000);
                                }, 1000);
                            }

                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            postDelayed(() -> {
                                mCommitView.showError(3000);
                            }, 1000);
                        }
                    });
        }else if (view == mReturnView) {
            postDelayed(() -> {
                setResult(RESULT_CANCELED);
                finish();
            }, 1000);

        }
    }

    private void getCode(){
        // 获取验证码
        EasyHttp.post(this)
                .api(new SendEmailApi()
                        .setMobile(mPhoneView.getText().toString())
                        .setTitle(getString(R.string.common_code_send_tiptitle))
                        .setTipa(getString(R.string.common_code_send_tipa))
                        .setTipb(getString(R.string.common_code_send_tipb)))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.common_code_send_hint);
                            mCountdownView.start();
                        }else{
                            toast(data.getMessage());
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        mCountdownView.start();
                    }
                });
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white)
                // 不要把整个布局顶上去
                .keyboardEnable(true);
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击注册按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }

    /**
     * 注册监听
     */
    public interface OnRegisterListener {

        /**
         * 注册成功
         *
         * @param phone             手机号
         * @param password          密码
         */
        void onSucceed(String phone, String password);

        /**
         * 取消注册
         */
        default void onCancel() {}
    }
}