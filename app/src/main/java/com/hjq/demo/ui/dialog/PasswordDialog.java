package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.widget.view.RegexEditText;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 首次登录同意用户协议对话框
 */
public final class PasswordDialog {

    public static final class Builder
            extends CommonDialog.Builder<PasswordDialog.Builder> {

        @Nullable
        private PasswordDialog.OnListener mListener;

        private final RegexEditText mOldPwdView;

        private final RegexEditText mNewPwdView;

        private final RegexEditText mSurePwdView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.ui_password_dialog);
            mOldPwdView = findViewById(R.id.et_input_old);
            mNewPwdView = findViewById(R.id.et_input_new);
            mSurePwdView = findViewById(R.id.et_input_sure);

        }



        public PasswordDialog.Builder setListener(PasswordDialog.OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public BaseDialog create() {
            return super.create();
        }

        @SingleClick
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                if(mNewPwdView.getText().toString().length()<8|| mNewPwdView.getText().toString().length()>20){
                    mNewPwdView.setHint(R.string.newpassword_hip);
                    mNewPwdView.setFocusable(true);
                    return;
                }
                if(mOldPwdView.getText().toString().length()<1){
                    mNewPwdView.setHint(R.string.newpassword_hip);
                    mNewPwdView.setFocusable(true);
                    return;
                }
                if(!mNewPwdView.getText().toString().equals(mSurePwdView.getText().toString())){
                    mSurePwdView.setHint(R.string.surepassword_hint);
                    mSurePwdView.setFocusable(true);
                    return;
                }
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(getDialog(), mOldPwdView.getText().toString(), mNewPwdView.getText().toString(), mSurePwdView.getText().toString());
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }

        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog,String oldpwd, String newpwd, String surepwd);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}


    }

}