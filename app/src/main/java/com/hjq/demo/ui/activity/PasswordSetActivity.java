package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.FindpwdByEmailApi;
import com.hjq.demo.http.api.PasswordApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.InputTextManager;
import com.hjq.demo.ui.dialog.TipsDialog;
import com.hjq.demo.util.CommonUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/02/27
 *    desc   : 修改密码
 */
public final class PasswordSetActivity extends AppActivity
        implements TextView.OnEditorActionListener {



    @Log
    public static void start(Context context) {
        Intent intent = new Intent(context, PasswordSetActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private EditText mOldPassword;
    private EditText mFirstPassword;
    private EditText mSecondPassword;
    private Button mCommitView;

    /** 手机号 */
    private String mPhoneNumber;
    /** 验证码 */
    private String mVerifyCode;

    @Override
    protected int getLayoutId() {
        return R.layout.password_set_activity;
    }

    @Override
    protected void initView() {
        mOldPassword = findViewById(R.id.et_password_old_password);
        mFirstPassword = findViewById(R.id.et_password_reset_password1);
        mSecondPassword = findViewById(R.id.et_password_reset_password2);
        mCommitView = findViewById(R.id.btn_password_reset_commit);

        setOnClickListener(mCommitView);

        mSecondPassword.setOnEditorActionListener(this);

        InputTextManager.with(this)
                .addView(mOldPassword)
                .addView(mFirstPassword)
                .addView(mSecondPassword)
                .setMain(mCommitView)
                .build();
    }

    @Override
    protected void initData() {

    }

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mCommitView) {
            if (mOldPassword.getText().toString().equals(mFirstPassword.getText().toString())) {
                mOldPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.password_reset_inputnewpwd_error);
                return;
            }
            if (mFirstPassword.getText().toString().length()<6 || mFirstPassword.getText().toString().length()>18) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                return;
            }
            if (!mFirstPassword.getText().toString().equals(mSecondPassword.getText().toString())) {
                mFirstPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                mSecondPassword.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim));
                toast(R.string.common_password_input_unlike);
                return;
            }

            // 隐藏软键盘
            hideKeyboard(getCurrentFocus());



            // 修改密码提交
            setPassword(mOldPassword.getText().toString(), mFirstPassword.getText().toString(), mSecondPassword.getText().toString());
            /*EasyHttp.post(this)
                    .api(new FindpwdByEmailApi()
                            .setPhone(mPhoneNumber)
                            .setCode(mVerifyCode)
                            .setNewpassword(mFirstPassword.getText().toString())
                            .setSurepassword(mSecondPassword.getText().toString()))
                    .request(new HttpCallback<HttpData<String>>(this) {



                        @Override
                        public void onSucceed(HttpData<String> data) {
                            if(data.getCode() == 200){
                                new TipsDialog.Builder(getActivity())
                                        .setIcon(TipsDialog.ICON_FINISH)
                                        .setMessage(R.string.password_reset_success)
                                        .setDuration(2000)
                                        .addOnDismissListener(dialog -> finish())
                                        .show();
                                finish();

                            }else{
                                toast(data.getMessage());

                            }

                        }
                    });
            */
        }
    }

    /**
     * {@link TextView.OnEditorActionListener}
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mCommitView.isEnabled()) {
            // 模拟点击提交按钮
            onClick(mCommitView);
            return true;
        }
        return false;
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

                            new TipsDialog.Builder(getActivity())
                                    .setIcon(TipsDialog.ICON_FINISH)
                                    .setMessage(R.string.password_reset_success)
                                    .setDuration(4000)
                                    .addOnDismissListener(dialog -> finish())
                                    .show();
                            finish();
                        }else{
                            toast(R.string.set_error_tip);
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        //super.onFail(e);
                        toast(R.string.set_error_tip);
                    }
                });
    }
}