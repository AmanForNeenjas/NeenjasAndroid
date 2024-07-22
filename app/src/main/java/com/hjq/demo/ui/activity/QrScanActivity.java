package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.hjq.base.BaseActivity;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.http.EasyLog;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.DrawableTextView;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 扫码
 */
public final class QrScanActivity extends AppActivity implements QRCodeView.Delegate{
    private ZXingView mZXingView;
    private DrawableTextView mLightView;
    private static final String INTENT_KEY_UID = "uid";
    private static final String INTENT_KEY_INVAL = "inval";
    private static final String INTENT_KEY_USERNAME = "username";
    boolean islight;

    public static void start(BaseActivity activity,  OnScanListener listener) {
        Intent intent = new Intent(activity, QrScanActivity.class);
        activity.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_UID), data.getStringExtra(INTENT_KEY_INVAL), data.getStringExtra(INTENT_KEY_USERNAME));
            } else {
                listener.onCancel();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.qrscan_activity;
    }

    @Override
    protected void initView() {
        mZXingView = findViewById(R.id.zxingview);
        mLightView = findViewById(R.id.tv_flashlight);
        mZXingView.setDelegate(this);
        mLightView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        islight = false;
        perminssionsStart();
    }

    private void perminssionsStart(){
        XXPermissions.with(this)
                .permission(Permission.CAMERA)
                .permission(Permission.READ_MEDIA_IMAGES)//读取照片
                //.permission(Permission.READ_EXTERNAL_STORAGE)
                // 如果不需要在后台使用定位功能，请不要申请此权限
                //.permission(Permission.ACCESS_BACKGROUND_LOCATION)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            return;
                        }
                        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
                        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                    }
                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            toast(R.string.ble_deinied_info);
                            XXPermissions.startPermissionActivity(getContext(), permissions);
                        } else {
                            toast(R.string.ble_deinied_error);
                        }
                    }
                });
    }
    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        // EasyLog.print("扫描结果为：" + result);
        // I/EasyHttp: 扫描结果为：uid=581;inval=1672906573055
        String[] arrs = result.split(";");
        if(arrs.length!=3){
            return;
        }else{
            if(arrs[0].contains("uid=") && arrs[1].contains("inval=") && arrs[2].contains("uname=")){
                String uid = arrs[0].replace("uid=", "");
                String inval = arrs[1].replace("inval=", "");
                String uname = arrs[2].replace("uname=", "");
                toast(R.string.scan_success);
                postDelayed(() -> {
                    setResult(RESULT_OK, new Intent()
                            .putExtra(INTENT_KEY_UID, uid)
                            .putExtra(INTENT_KEY_INVAL, inval)
                            .putExtra(INTENT_KEY_USERNAME, uname)
                    );
                    finish();
                }, 1000);
            }
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        toast(R.string.camera_launch_fail);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_flashlight){
            if(islight == true){
                mZXingView.closeFlashlight(); // 关闭闪光灯
                mLightView.setText(R.string.light_lamp);
                islight= false;
            } else{
                mZXingView.openFlashlight(); // 打开闪光灯
                mLightView.setText(R.string.light_nolamp);
                islight= true;
            }
        }
    }

    public interface OnScanListener {

        /**
         * 扫描成功
         * @param uid
         * @param interval
         */
        void onSucceed(String uid, String interval,String username);

        /**
         * 取消
         */
        default void onCancel() {}
    }
}