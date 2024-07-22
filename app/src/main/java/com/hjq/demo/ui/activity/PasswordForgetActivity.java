package com.hjq.demo.ui.activity;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.SendEmailApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.util.CommonUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.view.CountdownView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 忘记密码
 */
public final class PasswordForgetActivity extends AppActivity
        implements TextView.OnEditorActionListener {

    private EditText mPhoneView;
    private EditText mCodeView;
    private CountdownView mCountdownView;
    private Button mCommitView;
    // 测试用，正式要删除
    //private static final String INTENT_KEY_IN_PHONE = "18925298240@189.cn";
    //private MMKV mMmkv;

    @Override
    protected int getLayoutId() {
        return R.layout.password_forget_activity;
    }

    @Override
    protected void initView() {
        mPhoneView = findViewById(R.id.et_password_forget_phone);
        mCodeView = findViewById(R.id.et_password_forget_code);
        mCountdownView = findViewById(R.id.cv_password_forget_countdown);
        mCommitView = findViewById(R.id.btn_password_forget_commit);

        setOnClickListener(mCountdownView, mCommitView);

        mCodeView.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mPhoneView)
                .addView(mCodeView)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {
        //mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        //postDelayed(() -> {

            // 自动填充手机号和密码
            //mPhoneView.setText(INTENT_KEY_IN_PHONE);

        //}, 6000);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCountdownView) {

            if (!CommonUtil.isValidEmail(mPhoneView.getText().toString()) ) {
                //toast(R.string.common_phone_input_error + mPhoneView.getText().toString() +(EmailUtil.isValidEmail(mPhoneView.getText().toString())?"true":"false"));
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }



            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());


            // 获取验证码
            EasyHttp.post(this)
                    .api(new SendEmailApi()
                            .setMobile(mPhoneView.getText().toString())
                            .setTitle(getString(R.string.common_code_send_tiptitle))
                            .setTipa(getString(R.string.common_code_send_tipfore))
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
        } else if (view == mCommitView) {

            if (!CommonUtil.isValidEmail(mPhoneView.getText().toString())) {
                mPhoneView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_phone_input_error);
                return;
            }

            if (mCodeView.getText().toString().length() != getResources().getInteger(R.integer.sms_code_length)) {
                mCodeView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_code_error_hint);
                return;
            }

            PasswordResetActivity.start(getActivity(), mPhoneView.getText().toString(), mCodeView.getText().toString());
            finish();


        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击下一步按钮
            onClick(mCommitView);
            return true;
        }
        return false;
    }
}