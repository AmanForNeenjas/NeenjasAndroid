package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.task.QrCodrAsyncTask;
import com.hjq.demo.util.DateUtil;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 显示我的二维码
 */
public final class MyQrcodeActivity extends AppActivity {

    private ImageView mQrView;
    private TextView mNameView;
    private static final String INTENT_KEY_IN_UID = "uid";
    private static final String INTENT_KEY_IN_USERNAME = "username";

    public static void start(Context context, Integer uid, String username) {
        Intent intent = new Intent(context, MyQrcodeActivity.class);
        intent.putExtra(INTENT_KEY_IN_UID, uid);
        intent.putExtra(INTENT_KEY_IN_USERNAME, username);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.myqrcode_activity;
    }

    @Override
    protected void initView() {
        mQrView = findViewById(R.id.iv_my_logo);
        mNameView = findViewById(R.id.tv_my_name);
    }

    @Override
    protected void initData() {
        int uid = getInt(INTENT_KEY_IN_UID);
        String username = getString(INTENT_KEY_IN_USERNAME);

        QrCodrAsyncTask qrCodrAsyncTask = new QrCodrAsyncTask(this, mQrView, "uid="+ uid+";inval=" + DateUtil.nowToLong()+";uname=" +username);
        qrCodrAsyncTask.execute();
        mNameView.setText(username);
    }
}