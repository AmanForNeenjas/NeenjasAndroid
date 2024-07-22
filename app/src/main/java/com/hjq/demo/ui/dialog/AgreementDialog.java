package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/21
 *    desc   : 首次登录同意用户协议对话框
 */
public final class AgreementDialog {

    public static final class Builder
            extends CommonDialog.Builder<AgreementDialog.Builder> {

        @Nullable
        private AgreementDialog.OnListener mListener;

        private final TextView mUserAgreementView;

        private final TextView mPrivacyAgreementView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.ui_agreement_dialog);
            mUserAgreementView = findViewById(R.id.tv_userAgreement);
            mPrivacyAgreementView = findViewById(R.id.tv_privacyAgreement);
            setOnClickListener(mUserAgreementView, mPrivacyAgreementView);
        }



        public AgreementDialog.Builder setListener(AgreementDialog.OnListener listener) {
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
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(getDialog());
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
            else if(viewId == R.id.tv_userAgreement){
                mListener.onClickUserAgree(getDialog());
            }
            else if(viewId == R.id.tv_privacyAgreement){
                mListener.onClickPrivacyAgree(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}

        /**
         * 点击用户协议
         * @param dialog
         */
        void onClickUserAgree(BaseDialog dialog);

        /**
         * 点击隐私协议
         * @param dialog
         */
        void onClickPrivacyAgree(BaseDialog dialog);
    }

}