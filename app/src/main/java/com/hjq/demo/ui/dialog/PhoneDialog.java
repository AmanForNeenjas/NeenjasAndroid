package com.hjq.demo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
public final class PhoneDialog {

    public static final class Builder
            extends CommonDialog.Builder<PhoneDialog.Builder> {

        @Nullable
        private PhoneDialog.OnListener mListener;

        private final RegexEditText mCountrynoView;

        private final RegexEditText mPhoneView;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.ui_phone_dialog);
            mCountrynoView = findViewById(R.id.et_input_code);
            mPhoneView = findViewById(R.id.et_input_phone);

        }



        public PhoneDialog.Builder setListener(PhoneDialog.OnListener listener) {
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
                mListener.onConfirm(getDialog(), mCountrynoView.getText().toString(), mPhoneView.getText().toString());
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
        void onConfirm(BaseDialog dialog,String countryno, String phone);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {}


    }

}