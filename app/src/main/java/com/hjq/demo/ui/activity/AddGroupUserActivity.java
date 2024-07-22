package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.AddGroupUserApi;
import com.hjq.demo.http.api.AddUserGroupApi;
import com.hjq.demo.http.api.GetUserGroupApi;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.util.DateUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.RoundSettingBar;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 添加组用户
 */
public final class AddGroupUserActivity extends AppActivity {
    private RoundSettingBar mSeScanView;
    private TitleBar mTitleBarView;
    private int userid;
    private TextView mAddUserView;
    private TextView mUserTitleView;


    public static void start(BaseActivity activity, OnAddGuserListener listener) {
        Intent intent = new Intent(activity, AddGroupUserActivity.class);
        activity.startActivityForResult(intent, (resultCode, data) -> {
            if (listener == null || data == null) {
                return;
            }
            if (resultCode == RESULT_OK) {
                listener.onSucceed();
            } else {
                listener.onCancel();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.add_group_user_activity;
    }

    @Override
    protected void initView() {
        mSeScanView = findViewById(R.id.sb_select_scan);
        mAddUserView = findViewById(R.id.tv_add_user);
        mUserTitleView = findViewById(R.id.tv_user_title);
        mTitleBarView = findViewById(R.id.tb_selectuser);
        mSeScanView.setOnClickListener(this);
        mAddUserView.setVisibility(View.GONE);
        mUserTitleView.setVisibility(View.GONE);
        mTitleBarView.getRightView().setOnClickListener(this);
        mTitleBarView.getLeftView().setOnClickListener(this);
    }

    @Override
    protected void initData() {
        userid = 0;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sb_select_scan){
            //startActivity(QrScanActivity.class);
            QrScanActivity.start(this, new QrScanActivity.OnScanListener() {
                @Override
                public void onSucceed(String uid, String interval,String username) {
                    userid = Integer.parseInt(uid);
                    Long timeinter = Long.parseLong(interval);

                    if((DateUtil.nowToLong() - timeinter)>600000 ){
                        toast(R.string.scan_error_invid);
                    }
                    else{
                        mAddUserView.setVisibility(View.VISIBLE);
                        mUserTitleView.setVisibility(View.VISIBLE);
                        if(username .length()<3){
                            username = getString(R.string.appleid_user);
                        }
                        mAddUserView.setText(username);
                        mTitleBarView.setTitle(R.string.add_group_select);
                    }
                }
            });
        }else if(view ==  mTitleBarView.getRightView()){
            if(userid == 0){
                toast(R.string.add_group_unselect);
            }else{
                // 添加组用户
                EasyHttp.post(this)
                        .api(new AddGroupUserApi()
                                .setUserid(userid+""))
                        .request(new HttpCallback<HttpData<GetUserGroupApi.GroupBean>>(this) {

                            @Override
                            public void onSucceed(HttpData<GetUserGroupApi.GroupBean> data) {
                                if(data.getCode() == 200){
                                    toast(R.string.success);
                                    postDelayed(() -> {
                                        setResult(RESULT_OK, new Intent());
                                        finish();
                                    }, 1000);
                                }
                            }
                        });
            }
        }else if(view ==  mTitleBarView.getLeftView()){
            postDelayed(() -> {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }, 1000);
        }
    }

    /**
     * 注册监听
     */
    public interface OnAddGuserListener {

        /**
         * 添加成功
         *
         */
        void onSucceed();

        /**
         * 取消
         */
        default void onCancel() {}
    }
}