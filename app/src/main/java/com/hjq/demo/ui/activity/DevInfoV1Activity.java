package com.hjq.demo.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.ConnectType;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.Log;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.DevGetIDByMacApi;
import com.hjq.demo.http.api.DevSetImplusApi;
import com.hjq.demo.http.api.SetDeviceLogApi;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.ReadAddress;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.util.ByteUtil;
import com.hjq.demo.util.DateUtil;
import com.hjq.demo.util.FormatUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.demo.util.RecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.CircleProgressView;
import com.hjq.widget.view.CornerLabelView;
import com.tencent.mmkv.MMKV;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 设备详情表
 */

public final class DevInfoV1Activity extends AppActivity {


    private static final String INTENT_KEY_IN_DEVICEBIND = "devicebind";

    private DeviceBind model;


    public FscBleCentralApi mBleApi;
    boolean connected;

    private ActivityResultLauncher<Intent> mResultLauncher;

    /**
     * https://blog.csdn.net/xiao_FireFly/article/details/62892592
     */
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;

    //UI线程中的handler
    private Handler mMainHandler = new Handler();

    //以防退出界面后Handler还在执行
    //private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    //存放请求地址信息
    private List<ReadAddress> readAddressList;
    // 第一次打开页面读一次所以信息 第一次读0，指定读一些数据按 1，2，3，4，5定义， 2代表debug，8,代表控制 99 代表停止读设备数据
    private int readType;
    //private ByteBuffer binByteBuffer;
    // 控制信号每次发送的字节
    private byte[] sendBytes;
    // 控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
    private int controlType;
    //是否点击了控制按键
    private boolean isOpenControl;


    // Debug地址列表
    //private List<ReadAddress> debugAddressList;

    // 显示冲电 或放电
    private TextView mShowTimeTypeView;
    //小时
    private TextView mTimeHoursTypeView;
    //分钟
    private TextView mTimeMinsTypeView;
    //implus
    private CircleProgressView mCirclePlusView;
    // implus text
    private TextView mPlustxtView;

    private TextView tv_right;
    // 设备连接状态
    private CornerLabelView mDevisconnectedView;
    private TitleBar titleBar;

    // 设备状态
    //private TextView mDevStatusView;
    // 输入功率
    private TextView mInputView;
    // 输出功率
    private TextView mOutputView;
    // usb
    private TextView mUsbNumView;
    // usbc
    private TextView mUsbCNumView;
    //12V W
    private TextView mDcVtwelveView;
    // 输入功率
    //private TextView mAcInputView;
    // 输出功率
    private TextView mAcOutputView;
    private TextView mYboostView;
    int[] statusarray;  //存放61
    int[] statusarrtwo; //存放67状态变量值
    // 直流开关
    private AppCompatImageView mDCControlView;
    //boolean isDcControl;
    // 交流开关
    private AppCompatImageView mACControlView;
    // 快慢速冲电设置
    private AppCompatImageView mFastInControlView;
    // 快慢速冲电设置
    private TextView mFastInSettView;
    //boolean isACControl;
    // 打开弱灯
    private AppCompatImageView mLightLessControlView;
    //boolean isLightLessControl;
    // 打开强灯
    private AppCompatImageView mLightMoreControlView;
    //boolean isLightMoreControl;
    // 打开SOS灯
    private AppCompatImageView mLightSosControlView;
    //boolean isLightSosControl;
    // 关灯
    //private AppCompatImageView mLightCloseControlView;
    //boolean isLightCloseControl;
    // 亮屏
    private AppCompatImageView mSreenControlView;
    // 亮屏
    private AppCompatImageView mproSreenControlView;
    //boolean isSreenControl;
    // 灭虫灯
    private AppCompatImageView mBlugKillControlView;
    //boolean isBlugKillControl;
    // 氛围灯
    private AppCompatImageView mMoodControlView;

    // yboost working
    private TextView mYboostWorkView;

    // yboost working
    private TextView mSlowchargingView;

    // ac标志
    private AppCompatImageView mAcLableImgView;

    // 灯光控制区
    private LinearLayout moreControlView;

    // meta-2000 pro 灯光控制区
    private LinearLayout moreproControlView;
    // 更新系统数据日志次数
    private int setCount;
    // 停止发送
    private boolean stopsend;

    private boolean setnotify100;


    // 按地址划分
    private int step;
    private byte [] revarray;

    //private List reacevResult;
    private int receivenum;
    // 是否是第一次进入
    private boolean isFirst ;
    // 控制信号
    private int num;
    // 是否显示加载框
    private boolean loading;

    // 定时器第一次关闭loading;
    private int timerfirst;

    // 预充电量值
    private String maxImplus;
    // 预充电量值
    private String minDischarging;
    // 是否设置快充
    private String issetFastIn;
    // Yboost
    private String issetYboost;
    private String lastImplus;



    Timer timer ;
    TimerTask task;

    // 状态栏通知

    private NotificationManager manager;
    private static final int NOTIFYID_ONE = 1;


    public static void start(Context context, DeviceBind model) {
        Intent intent = new Intent(context, DevInfoV1Activity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICEBIND, model);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_info_activity20230329;
    }


    @Override
    protected void initView() {

        titleBar = findViewById(R.id.tb_devinfo_title);
        mShowTimeTypeView = findViewById(R.id.dev_showtime_type);
        mTimeHoursTypeView = findViewById(R.id.dev_showtime_hours);
        mTimeMinsTypeView = findViewById(R.id.dev_showtime_mins);
        mCirclePlusView = findViewById(R.id.cpv_implus);
        mPlustxtView = findViewById(R.id.tv_implus);

        //mDevStatusView = findViewById(R.id.dev_status);
        mInputView = findViewById(R.id.dev_input_num);
        mOutputView = findViewById(R.id.dev_output_num);
        mUsbNumView = findViewById(R.id.dev_usb_num);
        mUsbCNumView = findViewById(R.id.dev_usbc_num);
        mDcVtwelveView = findViewById(R.id.dev_dcv_num);
        mDcVtwelveView = findViewById(R.id.dev_dcv_num);
        //mAcInputView = findViewById(R.id.dev_acinput_num);
        mAcOutputView = findViewById(R.id.dev_acoutput_num);

        mDCControlView = findViewById(R.id.iv_dc_control);
        mACControlView = findViewById(R.id.iv_ac_control);
        mLightLessControlView = findViewById(R.id.iv_light_less_control);
        mLightMoreControlView = findViewById(R.id.iv_lightmore_control);
        mLightSosControlView = findViewById(R.id.iv_light_sos_control);
        //mLightCloseControlView = findViewById(R.id.iv_light_close_control);
        mSreenControlView = findViewById(R.id.iv_screen_control);
        mproSreenControlView = findViewById(R.id.iv_proscreen_control);
        mBlugKillControlView = findViewById(R.id.iv_buzkill_control);
        mMoodControlView = findViewById(R.id.iv_mood_control);

        mFastInControlView = findViewById(R.id.iv_input_control);
        mFastInSettView = findViewById(R.id.tv_input_control);
        // yboost控制
        mYboostView = findViewById(R.id.tv_yboost);
        mAcLableImgView = findViewById(R.id.iv_ac_lb);
        mYboostWorkView = findViewById(R.id.tv_yboost_work);

        moreControlView = findViewById(R.id.more_control);
        moreproControlView = findViewById(R.id.promore_control);
        //慢充文本
        mSlowchargingView = findViewById(R.id.tv_slowcharging);

        tv_right = titleBar.getRightView();
        tv_right.setOnClickListener(view -> {
            //startActivity(DevSetActivity.class);
            //DevSetActivity.start(this, model);
            DevSetActivity.start(this, model,maxImplus, minDischarging, issetFastIn,issetYboost,
                    (newname) -> {

                        titleBar.setTitle(newname);
                        //// EasyLog.print("========================2 newname" + newname);
                        //onClick(mCommitView);
                    });
        });

        mDevisconnectedView = findViewById(R.id.dev_isconnected);
        titleBar.getLeftView().setOnClickListener(this);
        //setAcControlStatus(false);
        setOnClickListener(R.id.iv_dc_control, R.id.iv_ac_control, R.id.iv_light_less_control, R.id.iv_lightmore_control, R.id.iv_light_sos_control,
                R.id.iv_screen_control, R.id.iv_buzkill_control, R.id.iv_mood_control, R.id.ll_dc_control, R.id.ll_ac_control, R.id.ll_light_less_control, R.id.ll_lightmore_control, R.id.ll_light_sos_control,
                R.id.rl_screen_control, R.id.rl_mood_control, R.id.rl_buzkill_control ,R.id.dev_isconnected,R.id.iv_input_control,R.id.tv_input_control,R.id.tv_yboost,R.id.shll_yboost,R.id.iv_proscreen_control, R.id.ll_proscreen_control,R.id.shll_slowcharging);

        //初始化按键状态


    }

    // 设置Yboost状态
    @SuppressLint("RestrictedApi")
    private void setAcLableImgStatus(boolean isopen){

        if(isopen){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAcLableImgView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.logo_color_raw)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAcLableImgView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }


    }

    // 设置Yboost状态
    private void setYboostControlStatus(boolean isopen){

        if(isopen){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mYboostView.setTextColor(getColor(R.color.logo_color_raw));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mYboostView.setTextColor(getColor(R.color.font_color_disabled));
            }
        }


    }
    // 设置SlowChargin状态
    private void setSlowchargingControlStatus(boolean isopen){

        if(isopen){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSlowchargingView.setTextColor(getColor(R.color.logo_color_raw));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSlowchargingView.setTextColor(getColor(R.color.font_color_disabled));
            }
        }


    }

    @SuppressLint("RestrictedApi")
    private void setAcControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mACControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.logo_color_raw)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mACControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.common_icon_color)));
            }
        }




    }

    @SuppressLint("RestrictedApi")
    private void setDcControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDCControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.logo_color_raw)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mDCControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.common_icon_color)));
            }
        }


    }
    @SuppressLint("RestrictedApi")
    private void setFastInControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFastInControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_green)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFastInControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.common_icon_color)));
            }
        }


    }

    @SuppressLint("RestrictedApi")
    private void setLightLessControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightLessControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightLessControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }

    }

    @SuppressLint("RestrictedApi")
    private void setLightMoreControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightMoreControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightMoreControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }


    }

    @SuppressLint("RestrictedApi")
    private void setLightSosControlStatus(boolean isopen){

        if(isopen){
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightSosControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLightSosControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }


    }

    /*
        @SuppressLint("RestrictedApi")
        private void setLightCloseControlStatus(boolean isopen){
    
    
                    if (isopen) {
                        //初始化按键状态
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mLightCloseControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.logo_color_raw)));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mLightCloseControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.black10)));
                        }
                    }
    
    
    
    
    
        }
    */
    @SuppressLint("RestrictedApi")
    private void setSreenControlStatus(boolean isopen){

        if (isopen) {
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSreenControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
                mproSreenControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSreenControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
                mproSreenControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }

    }

    @SuppressLint("RestrictedApi")
    private void setBlugKillControlStatus(boolean isopen){

        if (isopen) {
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBlugKillControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBlugKillControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }



    }
    @SuppressLint("RestrictedApi")
    private void setMoodControlStatus(boolean isopen){

        if (isopen) {
            //初始化按键状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMoodControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.light_control_open_color)));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMoodControlView.setSupportImageTintList(ColorStateList.valueOf(getColor(R.color.main_color_gray_ic)));
            }
        }



    }



    @Override
    protected void initData() {
        model = getParcelable(INTENT_KEY_IN_DEVICEBIND);
        if (model == null) {
            return;
        }
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        lastImplus = model.getImplus();

        titleBar.setTitle(model.getDevnickname());


        // 测试模式 先查找设备的id
        if(model.getUser_id() == 0){
            setModelDevId();
        }

        //reacevResult = new ArrayList();
        statusarray = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        statusarrtwo = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        receivenum = 0;
        readType = 0;
        isOpenControl = false;
        controlType = 0;
        setCount = 0;
        connected = false;
        loading = false;

        //timerfirst = 0;
        timerfirst = -1;
        maxImplus = "100";
        minDischarging ="0";
        issetFastIn = "0";
        issetYboost = "0";
        setnotify100 = false;
        mYboostWorkView.setVisibility(View.GONE);
        if(model.getDevicename().equals("META-2000")){
            moreControlView.setVisibility(View.VISIBLE);
            moreproControlView.setVisibility(View.GONE);
        }else if(model.getDevicename().equals("META-2000 PRO")){
            moreControlView.setVisibility(View.GONE);
            moreproControlView.setVisibility(View.VISIBLE);
        }
        //// EasyLog.print("========================11start");

        mBleApi = FscBleCentralApiImp.getInstance();
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            perminssionsStart();
                        }else{
                            finish();
                        }
                    }
                }
        );
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        String devrawwritedate = mMmkv.getString(MMKVUtils.DEVRAWWRITEDATE + model.getId(), "");
        String dateYMDS = DateUtil.getTodayYMDString();
        // 判断今天是否写入
        if(!devrawwritedate.equals(dateYMDS)){
            isFirst = true;
        }else{
            isFirst = false;;
        }


    }



    private void perminssionsStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            XXPermissions.with(this)
                    .permission(Permission.BLUETOOTH_SCAN)
                    .permission(Permission.BLUETOOTH_ADVERTISE)
                    .permission(Permission.BLUETOOTH_CONNECT)
                    // 如果不需要在后台使用定位功能，请不要申请此权限
                    //.permission(Permission.ACCESS_BACKGROUND_LOCATION)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (!all) {
                                return;
                            }
                            bleConnect();
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

        }else{
            XXPermissions.with(this)
                    .permission(Permission.Group.BLUETOOTH)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    // 如果不需要在后台使用定位功能，请不要申请此权限
                    //.permission(Permission.ACCESS_BACKGROUND_LOCATION)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (!all) {
                                return;
                            }
                            bleConnect();
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
    }

    /**
     * 检查蓝牙是否打开
     */
    private void checkBluetooth() {
        if (!mBleApi.isEnabled()) {
            /*if (isAndroid12()) {
                //检查是否有BLUETOOTH_CONNECT权限

                if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    //打开蓝牙
                    enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    //请求权限
                    requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                return@setOnClickListener
            }*/
            //不是Android12 直接打开蓝牙
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mResultLauncher.launch(enableIntent);
            //startActivityForResult(enableIntent, 12);
        }else{
            perminssionsStart();
        }

    }

    private void bleConnect() {
        //设置回调
        setBleCallBack();
        // EasyLog.print("========================start");
        if (model != null) {
            //// EasyLog.print(!mBleApi.isConnected() ? "==========true":"========false");
            //if(!mBleApi.isConnected()){
            //// EasyLog.print("======================== model 开始连接");
            mDevisconnectedView.setText(R.string.isconnecting);
            mBleApi.connect(model.getMacaddr());

            //}
            /*else{

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBleApi.disconnect();
                        //sendButton.setEnabled(true);
                        //sendFileButton.setEnabled(true);
                    }
                });
            }*/
        }
    }

    private void uiDeviceConnected() {
        stopsend = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevisconnectedView.setText(R.string.isconnected);

                //sendButton.setEnabled(true);
                //sendFileButton.setEnabled(true);
                //synchronized(DevInfoV1Activity.this) {
                //    sendNotification(2);
                //}
            }
        });

        /*
        postDelayed(() -> {

            checkDevice();
        }, 3000);*/

        //第一次读数据
        if (readType == 0) {
            // EasyLog.print("======================== 1 开始读设备");
            //initThread();
            //mThreadHandler.sendEmptyMessage(MSG_UPDATE_INFO);

            //DownloadDevBinFile();



            if(timerfirst == -1){
                // 与设备同步显示加载框
                showDialog();
                loading = true;
                closeLoadingDialogTimerTask();
            }

            // 为了等待蓝牙与串口通信通道建立起来延时3秒执行。
            postDelayed(() -> {
                initThreadReadStaus();
                step =128;


                mThreadHandler.sendEmptyMessage(128);
            }, 3000);
        }
    }
    private void closeLoadingDialogTimerTask(){


        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                if(timerfirst == -1){
                    hideDialog();
                    timerfirst = 0;
                    loading = false;
                }else{
                    // 如果接收的次数大于暂存的次数说明 数据有接收
                    // EasyLog.print("======================== timerfirst: " + timerfirst + " setCount:" + setCount);
                    if(timerfirst <= (setCount-1)){
                        timerfirst = setCount;
                        // 如果接收的次数大于暂存的次数说明 数据有接收
                    }else{
                        if(connected){
                            if(mThreadHandler != null){
                                initThreadReadStaus();
                                step =128;
                                mThreadHandler.sendEmptyMessage(128);
                            }else{
                                initThreadReadStaus();
                                step =128;
                                mThreadHandler.sendEmptyMessage(128);
                            }

                        }

                    }
                }


                    /*if(timer !=null ){

                        timer.cancel();
                        return;
                    }*/

            }
        };


        // 定义开始等待时间  --- 等待 5 秒
        // 1000ms = 1s
        long delay = 1000;
        // 定义每次执行的间隔时间
        long intevalPeriod = 3000;
        // schedules the task to be run in an interval
        // 安排任务在一段时间内运行
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

    private void uiDeviceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevisconnectedView.setText(R.string.outconnected);
                //sendButton.setEnabled(false);
                //sendFileButton.setEnabled(false);

                //android.util.Log.e(TAG, "uiDeviceDisconnected: false 1"  );

            }
        });
        stopsend = true;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }
        if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
    }

    @Override
    protected void onDestroy() {
        if(loading == true){
            hideDialog();
            loading = false;
        }

        //mBleApi.disconnect();
        // EasyLog.print("======================== 1 onDestroy 已经关闭");
        super.onDestroy();
        // EasyLog.print("======================== 1 mBleApi.isConnected()" + mBleApi.isConnected());
        /*if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }
        if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        if (mBleApi != null && mBleApi.isConnected()) {
            mBleApi.disconnect();
        }

        mBleApi = null;
        model = null;
        connected = false;
        mResultLauncher = null;
        if (mThreadHandler != null) {
            mHandlerThread.quit();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        if (mBleApi != null && mBleApi.isConnected()) {
            mBleApi.disconnect();
        }*/
        if(loading){
            hideDialog();
            loading = false;
        }
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }
        /*if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }*/
        /*if(timer !=null ) {
            timer.cancel();
        }
        if(task  != null) {
            task.cancel();
        }*/
        //stopsend = true;
        // EasyLog.print("======================== 2 onPause ");
        //// EasyLog.print("======================== 2 mBleApi.isConnected()" + mBleApi.isConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(loading){
            hideDialog();
            loading = false;
        }
        stopsend = true;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }
        /*if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }*/
        /*if(timer !=null ) {
            timer.cancel();
        }
        if(task  != null) {
            task.cancel();
        }*/
        //stopsend = true;
        // EasyLog.print("======================== 2 onStop ");
        //// EasyLog.print("======================== 2 mBleApi.isConnected()" + mBleApi.isConnected());
        /*if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        //mBleApi.disconnect();

    }

    @Override
    protected void onResume() {
        // EasyLog.print("======================== 3 DevInfoV1 onResume 开始连接");


        super.onResume();
        if (mBleApi == null) {
            // EasyLog.print("======================== 3 DevInfoV1  mBleApi == null" );
            mBleApi = FscBleCentralApiImp.getInstance();
        }
        if((mBleApi !=null && mBleApi.isConnected())){
            // EasyLog.print("======================== 3 DevInfoV1 mBleApi Connected" );
            connected = true;
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDevisconnectedView.setText(R.string.isconnected);

                    //sendButton.setEnabled(true);
                    //sendFileButton.setEnabled(true);

                }
            });*/
            stopsend = false;
            //设置回调
            setBleCallBack();
            postDelayed(() -> {
                initThreadReadStaus();
                step =128;

                if(mThreadHandler != null){
                    mThreadHandler.sendEmptyMessage(128);
                }

            }, 300);
        }else{
            EasyLog.print("======================== 3 DevInfoV1  mBleApi not Connected" );
            connected = false;
            checkBluetooth();
        }

        receivenum = 0;
        readType = 0;
        isOpenControl = false;
        controlType = 0;
        setCount = 0;

        loading = false;

        //timerfirst = 0;

    }

    @Override
    public void onClick(View view) {
        if (view == titleBar.getLeftView()) {
            //toast(R.string.disconnect_tip);
            stopsend = true;
            // EasyLog.print("======================== 开始关闭");
            postDelayed(() -> {
                // EasyLog.print("======================== mBleApi.isConnected()" + mBleApi.isConnected());
                if (mBleApi != null && mBleApi.isConnected()) {
                    mBleApi.disconnect();
                }
                //mBleApi.disconnect();
                finish();
            }, 200);


            //finish();
           /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBleApi.disconnect();
                    //sendButton.setEnabled(true);
                    //sendFileButton.setEnabled(true);
                }
            });
            postDelayed(() -> {

                finish();
            }, 3000);
*/
            /*if(connected){
                toast(R.string.disconnect_tip);
                // EasyLog.print("======================== 开始关闭");
                mBleApi.disconnect();
            }else{
                finish();
            }*/
            //控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
        } else if (view.getId() == R.id.iv_dc_control || view.getId() == R.id.ll_dc_control) {
            //// EasyLog.print("======================== iv_dc_control  click  8");
            //checkDevice();
            isOpenControl = true;
            controlType =  8;
        }else if (view.getId() == R.id.iv_ac_control || view.getId() == R.id.ll_ac_control) {
            //checkDevice();
            //// EasyLog.print("========================iv_ac_control click 4");
            isOpenControl = true;
            controlType =  4;
        }else if (view.getId() == R.id.iv_light_less_control || view.getId() == R.id.ll_light_less_control) {
            //checkDevice();
            //// EasyLog.print("========================iv_light_less_control click 512");
            isOpenControl = true;
            controlType =  512;
        }else if (view.getId() == R.id.iv_lightmore_control || view.getId() == R.id.ll_lightmore_control) {
            //checkDevice();
            //// EasyLog.print("======================== iv_lightmore_control click 1024");
            isOpenControl = true;
            controlType =  1024;
        }else if (view.getId() == R.id.iv_light_sos_control || view.getId() == R.id.ll_light_sos_control) {
            //checkDevice();
            //// EasyLog.print("======================== iv_light_sos_contro  click 128");
            isOpenControl = true;
            controlType =  128;
        }
        /*else if (view.getId() == R.id.iv_light_close_control || view.getId() == R.id.ll_light_close_control) {
            //checkDevice();
            if(statusarray[10]==0){
                return;
            }
            // EasyLog.print("======================== iv_light_close_control  click 0");
            isOpenControl = true;
            controlType =  99;
        }*/
        else if (view.getId() == R.id.iv_screen_control || view.getId() == R.id.rl_screen_control || view.getId() == R.id.iv_proscreen_control || view.getId() == R.id.ll_proscreen_control) {

            //EasyLog.print("======================== iv_screen_control  click 64 , statusarray[13]" + statusarray[13]);
            // checkDevice();
            // if(statusarray[13]== 1){
            //    return;
            //}

            isOpenControl = true;
            controlType =  2048;
        }else if (view.getId() == R.id.iv_mood_control || view.getId() == R.id.rl_mood_control) {
            //// EasyLog.print("======================== iv_screen_control  click 64");
            //checkDevice();
            isOpenControl = true;
            controlType =  64;
        }else if (view.getId() == R.id.iv_buzkill_control || view.getId() == R.id.rl_buzkill_control) {
            //// EasyLog.print("======================== v_buzkill_control  click 256");
            //checkDevice();
            isOpenControl = true;
            controlType =  256;
        } else if(view.getId() == R.id.dev_isconnected){
            if(connected){
                return;
            }else{
                perminssionsStart();
            }
        }else if (view.getId() == R.id.iv_input_control || view.getId() == R.id.tv_input_control || view.getId() == R.id.shll_slowcharging) {
            //快速充电
            //// EasyLog.print("======================== v_buzkill_control  click 256");
            //checkDevice();
            isOpenControl = true;
            controlType =  4096;
        }else if (view.getId() == R.id.tv_yboost || view.getId() == R.id.shll_yboost) {
            //Yboost
            //// EasyLog.print("======================== v_buzkill_control  click 256");
            //checkDevice();
            // 关闭账号
            if(statusarrtwo[0]==0 && connected == true){
                new MessageDialog.Builder(getActivity())
                        .setWidth(1000)
                        // 标题可以不用填写
                        .setTitle(R.string.open_yboost_title)
                        // 内容必须要填写
                        .setMessage(R.string.open_yboost_tip)
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                // 去注销
                                //closeAcount();

                                isOpenControl = true;
                                controlType =  8192;

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {


                            }
                        })
                        .show();
            }else if(connected == true){
                isOpenControl = true;
                controlType =  8192;
            }


        }

        //mThreadHandler.sendEmptyMessage(999);
    }

    /**
     * 一次取125个数据 发送
     */
    private void sendStatusAll() {
        //String sendCheckHexStr = RecodeUtil.getRegisterAddr(RecodeUtil.STANDBYADDR, RecodeUtil.FUNCTIONPARAM, "33", "0c", RecodeUtil.REGISTERCONTENTHIGH, RecodeUtil.REGISTERCONTENTLOW);
        step = 128;
        sendDevicebyAddrHexStr(RecodeUtil.getRegisterAddrAll(), "V125");
    }



    /**
     * 初始化读取设备信息 相关的Thread
     */
    private void initThreadReadStaus() {

            mHandlerThread = new HandlerThread("check-message-coming");
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {

                    // EasyLog.print("======================== handleMessage" + msg.what);
                    switch (msg.what) {
                        case 128:
                            sendStatusAll();
                            break;

                        case 999:
                            controlSend();
                            break;


                    }

                }
            };




        // EasyLog.print("======================== initThread");

    }


    /**
     * 总输出
     *
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void StatusReceivAll(String address, String strValue, String hexString, byte[] data) {
        //// EasyLog.print("======================== receivData HEX-> " + FormatUtil.bytesToHexString(data));
        //分五次接收数据
        if(receivenum == 0){
            receivenum ++;
            revarray = data;
            postDelayed(() -> {
                //// EasyLog.print("======================== revarray.length-> " + revarray.length);
                if(revarray.length==255){
                    receivenum=0;
                    // 解码数据值
                    List<Integer> receivDataList = RecodeUtil.readDecodeAll(revarray);

                    if(receivDataList.size() == 125){
                        //int tempa =revarray[123]&0XFF;
                        //// EasyLog.print("========================revarray[123]-> " + tempa);
                        //tempa =revarray[124]&0XFF;
                        //// EasyLog.print("========================revarray[124]-> " + tempa);
                        statusarray= RecodeUtil.readDecodeStatus(revarray[123], revarray[124]);

                        statusarrtwo = RecodeUtil.readDecodeStatusTwo(revarray[135], revarray[136]);

                        //for(int i=0; i< statusarray.length; i++){
                           // EasyLog.print("========================statusarray-> " + i + " " + statusarray[i]);
                        //}
                        // EasyLog.print("========================statusarray-> " + 13 + " " + statusarray[13]);
                        // EasyLog.print("======================== receivDataList.get(68)-> " + receivDataList.get(68) +"   receivDataList.get(69):-> " + receivDataList.get(66) +"   statusarrtwo[0]: " + statusarrtwo[0]+"   statusarrtwo[1]: " + statusarrtwo[1] +"   statusarrtwo[2]: " + statusarrtwo[2] +"   statusarrtwo[3]: " + statusarrtwo[3]+"   statusarrtwo[4]: " + statusarrtwo[4]);
                        maxImplus = receivDataList.get(68)+"";
                        minDischarging = receivDataList.get(69) +"";
                        issetFastIn = statusarrtwo[3]+"";
                        issetYboost = statusarrtwo[0]+"";
                        String strV12 = RecodeUtil.GetRealValues("33", "0c", receivDataList.get(11));
                        String strV58 = RecodeUtil.GetRealValues("33", "3a", receivDataList.get(57));
                        String strV59 = RecodeUtil.GetRealValues("33", "3B", receivDataList.get(58));
                        String strV60 = RecodeUtil.GetRealValues("33", "3C", receivDataList.get(59));
                        String strV62 = RecodeUtil.GetRealValues("33", "3E", receivDataList.get(61));
                        //output
                        String strV63 = RecodeUtil.GetRealValues("33", "3F", receivDataList.get(62));
                        String strV64 = RecodeUtil.GetRealValues("33", "40", receivDataList.get(63));
                        String strV65 = RecodeUtil.GetRealValues("33", "41", receivDataList.get(64));
                        //String strV66 = RecodeUtil.GetRealValues("33", "42", receivDataList.get(65));
                        // 查故障
                        //String strV81 = RecodeUtil.GetRealValues("33", "51", receivDataList.get(65));
                        //String strV82 = RecodeUtil.GetRealValues("33", "52", receivDataList.get(65));
                        //String strV83 = RecodeUtil.GetRealValues("33", "53", receivDataList.get(65));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDcVtwelveView.setText(strV12);
                                // // EasyLog.print("======================== receivDataList.get(57)-> " + receivDataList.get(57));
                                mCirclePlusView.setProgress(receivDataList.get(57));
                                mPlustxtView.setText(strV58);
                                mUsbCNumView.setText(strV60);
                                mUsbNumView.setText(strV59);
                                mInputView.setText(strV62);
                                mOutputView.setText(strV63);
                                mTimeMinsTypeView.setText(strV64);
                                mTimeHoursTypeView.setText(strV65);
                                mAcOutputView.setText(receivDataList.get(65)+"");

                                //    // bit0: 1: 太阳能充电（MPPT工作中）
                                //    // bit1: 1:  AC输出中
                                //    // bit2: 1:  充电中
                                //    // bit3: 1:  直流输出中，
                                //    // bit4: 1:  风扇故障
                                //    // bit5: 1:  低温保护中
                                //    // bit6: 1:  过温保护中
                                //    // bit7: 1:  APP、上位机链接上
                                //    // bit8: 1:  快充显示标志
                                //    // bit9: 1:  过载显示标志
                                //    // bit11-10: 1:  照明灯亮，2， 照明灯强，3：SOS，0：关闭
                                //    // bit12: 1:  氛围灯Open
                                //    // bit13: 1:  驱蚊灯Open
                                // 输入

                                // 输出
                                if(statusarray[1] == 1 || statusarray[3] == 1){
                                    //mDevStatusView.setText(R.string.dev_status_plus);
                                    mShowTimeTypeView.setText(R.string.chargingtime);
                                }else{

                                    if( statusarray[2] == 1){
                                        //mDevStatusView.setText(R.string.dev_status_implus);
                                        mShowTimeTypeView.setText(R.string.rechargingtime);
                                    } else if(statusarray[0] == 1 ){
                                        //mDevStatusView.setText(R.string.dev_status_sum_implus);
                                        mShowTimeTypeView.setText(R.string.rechargingtime);
                                    }else{
                                        //mDevStatusView.setText(R.string.dev_standby);
                                        mShowTimeTypeView.setText(R.string.chargingtime);
                                    }
                                }
                                //mDevStatusView.setText(R.string.dev_status_plus);
                                // AC输出中
                                if(statusarray[1] == 1 ){
                                    setAcControlStatus(true);
                                }else{
                                    setAcControlStatus(false);
                                }
                                // 直流输出中
                                if(statusarray[3] == 1 ){
                                    setDcControlStatus(true);

                                }else{
                                    setDcControlStatus(false);
                                }
                                if(statusarray[10] == 0){
                                    setLightLessControlStatus(false);
                                    setLightMoreControlStatus(false);
                                    setLightSosControlStatus(false);
                                    //setLightCloseControlStatus(true);
                                }else if(statusarray[10] == 2){
                                    setLightLessControlStatus(false);
                                    setLightMoreControlStatus(true);
                                    setLightSosControlStatus(false);
                                    //setLightCloseControlStatus(false);
                                }else if(statusarray[10] == 3){
                                    setLightLessControlStatus(false);
                                    setLightMoreControlStatus(false);
                                    setLightSosControlStatus(true);
                                    //setLightCloseControlStatus(false);
                                }else if(statusarray[10] == 1){
                                    setLightLessControlStatus(true);
                                    setLightMoreControlStatus(false);
                                    setLightSosControlStatus(false);
                                    //setLightCloseControlStatus(false);
                                }

                                if(statusarray[11] == 1 ){
                                    setMoodControlStatus(true);
                                }else{
                                    setMoodControlStatus(false);
                                }
                                if(statusarray[12] == 1 ){
                                    setBlugKillControlStatus(true);
                                }else{
                                    setBlugKillControlStatus(false);
                                }
                                if(statusarray[13] == 1 ){
                                    setSreenControlStatus(true);
                                }else{
                                    setSreenControlStatus(false);
                                }
                                if(statusarrtwo[0] == 1){
                                    setYboostControlStatus(true);
                                }else{
                                    setYboostControlStatus(false);
                                }
                                //Yboost功能开启
                                if(statusarrtwo[0] == 1 ){
                                    setYboostControlStatus(true);
                                }else{
                                    setYboostControlStatus(false);
                                }
                                //慢充开启
                                if(statusarrtwo[3] == 1 ){

                                    setFastInControlStatus(true);
                                    setSlowchargingControlStatus(true);
                                    //mFastInSettView.setText(R.string.input_fast);
                                }else{
                                    //mFastInSettView.setText(R.string.input);
                                    setFastInControlStatus(false);
                                    setSlowchargingControlStatus(false);
                                }
                                // 进入boost
                                if(statusarrtwo[4] == 1 ){

                                    mYboostWorkView.setVisibility(View.VISIBLE);
                                    //mFastInSettView.setText(R.string.input_fast);
                                }else{
                                    //mFastInSettView.setText(R.string.input);
                                    mYboostWorkView.setVisibility(View.GONE);
                                }

                            }
                        });
                        if(stopsend == false){
                            //控制信号打开时，执行控制信号
                            if(isOpenControl){

                                mThreadHandler.sendEmptyMessage(999);

                            }else{
                                mThreadHandler.sendEmptyMessage(128);
                                /*if(isFirst  || (setCount % 10000 == 0)){
                                    postDelayed(() -> {

                                        mThreadHandler.sendEmptyMessage(128);
                                        finish();
                                    }, 1000);
                                }else{
                                    mThreadHandler.sendEmptyMessage(128);
                                }*/

                            }
                        }
                        if(!isOpenControl){

                            //用于测试
                            //
                            //采样时间
                            //if(setCount % 10000 == 0) {
                            //    setInplus(receivDataList.get(57) + "");
                            //}
                            if(!lastImplus.equals(receivDataList.get(57)+"")){
                                setInplus(receivDataList.get(57)+"");
                                lastImplus = receivDataList.get(57)+"";
                            }
                            // 设备处于调试模式
                            if(model.getStatus() == 999 ){

                                if(setCount % 6 == 0) {
                                    //setInplus(receivDataList.get(57) + "");
                                    //  9: '错误',
                                    //        5: '告警',
                                    //        1: '信息'
                                    int infotype =1;
                                    if(receivDataList.get(81) > 0)
                                    {
                                        infotype = 9;
                                    }else if(receivDataList.get(82) > 0){
                                        infotype = 5;
                                    }
                                    byte[] temp = new byte[250];

                                    String rawdataHex = RecodeUtil.readDecodAllToHexStr(revarray);
                                    int statusval = RecodeUtil.readStatusarray(statusarray);

                                    setDevLog(receivDataList.get(57) + "", receivDataList.get(81)+"", receivDataList.get(82)+"", infotype ,rawdataHex,statusval);
                                }
                                //setCount++;
                            }
                            // 每天进入系统第一次更新电池容量
                            else if(isFirst){
                                isFirst = false;

                                //  9: '错误',
                                //        5: '告警',
                                //        1: '信息'
                                int infotype =1;
                                if(receivDataList.get(81) > 0)
                                {
                                    infotype = 9;
                                }else if(receivDataList.get(82) > 0){
                                    infotype = 5;
                                }
                                //byte[] temp = new byte[250];

                                String rawdataHex = RecodeUtil.readDecodAllToHexStr(revarray);
                                int statusval = RecodeUtil.readStatusarray(statusarray);
                                setDevLog(receivDataList.get(57) + "", receivDataList.get(81)+"", receivDataList.get(82)+"", infotype ,rawdataHex, statusval);
                                MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                mMmkv.encode(MMKVUtils.APPDEVRAW+ model.getId() , revarray);
                                mMmkv.putString(MMKVUtils.DEVRAWWRITEDATE + model.getId(), DateUtil.getTodayYMDString());


                            }else{
                                if(setCount == 0){
                                    MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                    mMmkv.encode(MMKVUtils.APPDEVRAW+ model.getId() , revarray);
                                }
                                // 出现故障上报3次
                                if((receivDataList.get(81) > 0 || receivDataList.get(82) >0) && setCount<2 ){
                                    setInplus(receivDataList.get(57)+"");
                                    //  9: '错误',
                                    //        5: '告警',
                                    //        1: '信息'
                                    int infotype =1;
                                    if(receivDataList.get(81) > 0)
                                    {
                                        infotype = 9;
                                    }else if(receivDataList.get(82) > 0){
                                        infotype = 5;
                                    }
                                    //byte[] temp = new byte[250];
                                    //System.arraycopy(receivDataList, 3,temp, 0,250);
                                    String rawdataHex = RecodeUtil.readDecodAllToHexStr(revarray);
                                    EasyLog.print("======================== 出现故障上报次setCount -> "+setCount);
                                    setDevLog(receivDataList.get(57) + "", receivDataList.get(81)+"", receivDataList.get(82)+"", infotype ,rawdataHex,receivDataList.get(61));

                                }
                            }
                            setCount  += 1;
                            // EasyLog.print("======================== setCount -> "+setCount);

                        }
                        if(loading){

                            hideDialog();
                            loading = false;
                            timerfirst = 1;
                        }
                    }


                }
                // 测试100毫秒不行，200毫秒可以的。
            }, 300);
        }
        else{
            revarray = RecodeUtil.byteMergerAll(revarray, data);
        }

        //step = 128;
        //mThreadHandler.sendEmptyMessage(128);

    }





    /**
     * 通过 HexStr 向设备发送请求
     */
    @Log
    private void sendDevicebyAddrHexStr(String sendCheckHexStr, String logid){
        byte[] sendCheckBytes = FormatUtil.BytesAddCrc(sendCheckHexStr.toUpperCase());
        //// EasyLog.print("======================== 发送的十六进制代码：" + FormatUtil.bytesToHexString(sendCheckBytes));
        try{
            if(connected){
                //// EasyLog.print("======================== 当前的地址ID：" +logid );
                mBleApi.send(sendCheckBytes);
            }
        }catch(Exception e){
            //Timber.d("======================== 测试与设备通讯出错" + e.toString());
            // EasyLog.print("======================== 测试与设备通讯出错" + e.toString() );
        }
    }








    /**
     *控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
     *
     */
    private void controlSend(){
        // 控制信号
        readType = 2;
        num = 1;
        // AC Charge +2

        if(controlType == 4){
            //首先查找当前状态
            // 表示当前状态为 要启动AC充电
            if(statusarray[1]==0){
                num += 4;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAcControlStatus(true);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAcControlStatus(false);
                    }});
            }
            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0
            if(statusarray[3]==1){
                num += 8;
            }
            // Mood light
            if( statusarray[11]== 1){
                num += 64;
            }
            // Emergency light  statusarray[11]== 3
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            //DC Control
        }else if(controlType == 8) {
            //首先查找当前状态
            // DC Discharge statusarray[3]==0 // 表示当前状态为 要
            if(statusarray[3]==0){
                num += 8;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDcControlStatus(true);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDcControlStatus(false);
                    }});
            }

            // 查询其他状态 他们状态值也要附上
            if(statusarray[1]==1){
                num += 4;
            }


            // Mood light
            if( statusarray[11]== 1){
                num += 64;
            }
            // Emergency light  statusarray[11]== 3
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            //氛围灯控制
        }else if(controlType == 64) {
            //首先查找当前状态
            if( statusarray[11]== 0){
                num += 64;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMoodControlStatus(true);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMoodControlStatus(false);
                    }});
            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }
            // Emergency light  statusarray[11]== 3
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            //应急灯
        }else if(controlType == 128) {
            //首先查找当前状态
                    /*num += 128;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLightSosControlStatus(true);
                            setLightMoreControlStatus(false);
                            setLightLessControlStatus(false);
                            //setLightCloseControlStatus(false);
                        }});*/
            if( statusarray[10] != 3){
                num += 128;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightSosControlStatus(true);
                        setLightMoreControlStatus(false);
                        setLightLessControlStatus(false);
                        //setLightCloseControlStatus(false);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightSosControlStatus(false);
                        //setLightCloseControlStatus(true);
                    }});
            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }


            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            // 驱蚊灯Open
        }else if(controlType == 256) {
            //首先查找当前状态
            // Mood light
            if( statusarray[12] != 1){
                num += 256;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setBlugKillControlStatus(true);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBlugKillControlStatus(false);
                    }});

            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }
            // Emergency light  statusarray[11]== 3
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            // Mosquito killer light

            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            // light less
        }else if(controlType == 512) {
            //首先查找当前状态
            // Mood light
                    /*num += 512;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLightLessControlStatus(true);
                            setLightMoreControlStatus(false);
                            setLightSosControlStatus(false);
                            setLightCloseControlStatus(false);
                        }});*/
            if( statusarray[10] != 1){
                num += 512;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightLessControlStatus(true);
                        setLightMoreControlStatus(false);
                        setLightSosControlStatus(false);
                        //setLightCloseControlStatus(false);
                    }});

            } else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightLessControlStatus(false);
                        //setLightCloseControlStatus(true);
                    }});

            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }

            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
            // light more
        }else if(controlType == 1024) {
            //首先查找当前状态
            // Mood light
                    /*num += 1024;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLightMoreControlStatus(true);
                            setLightSosControlStatus(false);
                            setLightLessControlStatus(false);
                            setLightCloseControlStatus(false);
                        }});*/
            if( statusarray[10] != 2){
                num += 1024;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightMoreControlStatus(true);
                        setLightSosControlStatus(false);
                        setLightLessControlStatus(false);
                        //setLightCloseControlStatus(false);
                    }});

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLightMoreControlStatus(false);
                        //setLightCloseControlStatus(true);
                    }});

            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }

            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }
        }else if(controlType == 2048) {
            //首先查找当前状态
            // Creen light
            if( statusarray[13] != 1){
                num += 2048;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSreenControlStatus(true);

                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSreenControlStatus(false);

                    }});
            }




            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }

            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            if(statusarrtwo[0] == 1){
                num += 8192;
            }

        }else if(controlType == 4096) {

            if( statusarrtwo[3]!= 1){
                num += 4096;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFastInControlStatus(false);
                        //mFastInSettView.setText(R.string.input_fast);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mFastInSettView.setText(R.string.input);
                        setFastInControlStatus(true);

                    }});
            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }

            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }

        }else if(controlType == 8192) {

            if( statusarrtwo[0]!= 1){
                num +=  8192;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setYboostControlStatus(true);
                        //mFastInSettView.setText(R.string.input_fast);
                    }});
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mFastInSettView.setText(R.string.input);
                        setYboostControlStatus(false);

                    }});
            }

            // 查询其他状态 他们状态值也要附上
            // DC Discharge statusarray[3]==0 //
            if(statusarray[3]==1){
                num += 8;
            }
            if(statusarray[1]==1){
                num += 4;
            }

            // Mosquito killer light
            if( statusarray[12]== 1){
                num += 256;
            }
            if( statusarray[11]== 1){
                num += 64;
            }
            if( statusarray[13]== 1){
                num += 2048;
            }
            if( statusarray[10]== 3){
                num += 128;
            } else if ( statusarray[10]== 1){
                num += 512;
            }else if ( statusarray[10]== 2){
                num += 1024;
            }
            if( statusarrtwo[3]== 1){
                num += 4096;
            }

        }
        // EasyLog.print("========================Control num -> "+num);
        String sendStr = "0141" + "3200" +  ByteUtil.toHexString(RecodeUtil.intTobyteTwo(num));
        // EasyLog.print("========================Control sendStr -> "+sendStr);
        sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
        // 发送
        mBleApi.send(sendBytes);


    }

    /**
     * 发送进入 Control模式 接收设备反馈信息
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void ControlReceiv(String address, String strValue, String hexString, byte[] data){
        //EasyLog.print("========================bootReceiv  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
        isOpenControl = false;


        //int result = RecodeUtil.readDecodeUpgade(data, sendBytes, "Control", 8);
        /*if(result ==1){
            // AC Charge +2
            if(controlType == 2){
                runOnUiThread(new Runnable() {

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void run() {


                    }
                });
            }
            // AC Discharge +4
            else if(controlType == 4){
                runOnUiThread(new Runnable() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void run() {


                    }
                });
            }
            // DC Discharge +8
            else if(controlType == 8){
                runOnUiThread(new Runnable() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void run() {


                    }
                });
            }
            // Max Charge Current +16
            else if(controlType == 16){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }
            // Loop Parameter Debug +32
            else if(controlType == 32){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }


        }*/

        postDelayed(() -> {
            initThreadReadStaus();
            readType = 0;
            step =128;
            mThreadHandler.sendEmptyMessage(128);
        }, 200);

    }

    /**
     * 更新电池容量显示值信息
     * @param implus
     */
    private void setInplus(String implus){


        // 测试账号model userid 设置为0 不更新电量值
        if(model.getUser_id()>0){
            // EasyLog.print("======================== implus num -> "+implus);
            EasyHttp.post((LifecycleOwner) getActivity())
                    .api(new DevSetImplusApi()
                            .setId(model.getId())
                            .setImplus(implus)
                    ).request(new HttpCallback<HttpData<String>>((OnHttpListener) getActivity()) {

                        @Override
                        public void onStart(Call call) {

                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<String> data) {
                            if(data.getCode() == 200) {

                            }else{

                            }


                        }

                        @Override
                        public void onFail(Exception e) {
                            //super.onFail(e);

                        }
                    });
        }

    }

    /**
     * 设置设备状况信息信息
     * @param
     */
    private void setDevLog(String implus,String errors,String alerts, int infotype,String rawdata,int status){
        EasyHttp.post((LifecycleOwner) getActivity())
                .api(new SetDeviceLogApi().setDevice_id(model.getDevice_id())
                        .setAlerts(alerts)
                        .setCharging(implus)
                        .setCustomertitle(model.getMacaddr())
                        .setInfotype(infotype)
                        .setRawdata(rawdata)
                        .setErrors(errors)
                        .setStatus(status)
                        .setAg_id(model.getAg_id())
                ).request(new HttpCallback<HttpData<String>>((OnHttpListener) getActivity()) {

                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200) {

                        }else{

                        }


                    }

                    @Override
                    public void onFail(Exception e) {
                        //super.onFail(e);

                    }
                });
    }


    /**
     * 测试模式查询设备ID信息
     */
    private void setModelDevId(){
        // 测试账号model userid 设置为0 不更新电量值
        EasyHttp.post((LifecycleOwner) getActivity())
                .api(new DevGetIDByMacApi()
                        .setMacaddr(model.getMacaddr())
                ).request(new HttpCallback<HttpData<Integer>>((OnHttpListener) getActivity()) {

                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpData<Integer> data) {
                        if(data.getCode() == 200) {
                            model.setDevice_id(data.getData());
                        }else{

                        }


                    }

                    @Override
                    public void onFail(Exception e) {
                        //super.onFail(e);

                    }
         });


    }



    private void setBleCallBack(){
        mBleApi.setCallbacks(new FscBleCentralCallbacksImp() {
            @Override
            public void blePeripheralConnected(BluetoothGatt gatt, String address, ConnectType tye) {
                //android.util.Log.e(TAG,"blePeripheralConnected");
                // EasyLog.print(address + "======================== 已经连接" + address);
                connected = true;
                uiDeviceConnected();

            }

            @Override
            public void blePeripheralDisconnected(BluetoothGatt gatt, String address, int status) {
                //android.util.Log.e(TAG,"blePeripheralDisconnected");

                // EasyLog.print(address + "======================== 已经关闭");
                uiDeviceDisconnected();
                connected = false;
            }

            @Log
            @Override
            public void packetReceived(String address, String strValue, String hexString, byte[] data) {
                //android.util.Log.e(TAG,"packetReceived");
                //uiReceiveData(strValue, hexString, data);
                //// EasyLog.print("======================== packetReceived  address -> " + address + "\r\n strValue ->" + strValue + "  \r\n data -> " + ByteUtil.toHexString(data));
                //toast("strValue ->"+ strValue);
                //if (hexString.equals("02 03 02 00 00 FC 44")) {
                //    // EasyLog.print("======================== 与设备通讯正常");
                //}
                // 根据读数据类型判断哪种方式解析数据 readType ==0 读地址高位0x33;readType == 2 调试模式
                // readType ==0 读地址高位0x33;
                if (readType == 0) {
                    if(step == 128){
                        StatusReceivAll(address, strValue, hexString, data);
                    }

                }
                // readType == 2 控制模式
                if (readType == 2) {
                    //控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
                    ControlReceiv(address, strValue, hexString, data);
                }
            }

            @Override
            public void sendPacketProgress(String address, int percentage, byte[] sendByte) {
                //android.util.Log.e(TAG,"sendPacketProgress");
                //uiSendDataProgress(percentage, sendByte.length);
            }

            @Override
            public void packetSend(String address, String strValue, byte[] data) {
                //android.util.Log.e(TAG,"packetSend address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtils.toHexString(data));
                //uiSendData(data);
                //数据发送完成的回调
                //// EasyLog.print("======================== packetSend address -> " + address + "\r\n strValue ->" + strValue + "  \r\n data -> " + ByteUtil.toHexString(data));
            }

        });
    }

    // 状态栏通知
    private void sendNotification(int key){
        if(key ==1){
            //EasyLog.print("======================== sendNotification -> " + 888);
            //定义一个PendingIntent点击Notification后启动一个Activity
            //Intent it = new Intent(this, AboutActivity.class);
            //PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);
            Intent intent = new Intent(this, DevInfoV1Activity.class);
            intent.putExtra(INTENT_KEY_IN_DEVICEBIND, model);
            if (!(this instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pit = PendingIntent.getActivity(this, 0, intent, 0);
            /*Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setContentTitle(getString(R.string.chargingfinish_ctitle))   //标题 充电完成
                    .setContentText(getString(R.string.chargingfinish_ctext))
                    .setSubText(getString(R.string.chargingfinish_subt))
                    .setTicker(getString(R.string.chargingfinish_tck))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_round48)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(true)
                    .setContentIntent(pit);
            notification = mBuilder.build();*/

            // Android 8.0 及更高版本上提供通知，必须创建NotificationChannel 的实例，以便在系统中注册应用的通知渠道
            // 原文链接：https://blog.csdn.net/huang525437/article/details/132492365
            String CHANNEL_ID="implus";
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID
                        , "name", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(getString(R.string.chargingfinish_ctitle))   //标题 充电完成
                    .setContentText(getString(R.string.chargingfinish_ctext))
                    .setSubText(getString(R.string.chargingfinish_subt))
                    .setTicker(getString(R.string.chargingfinish_ctext))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_round48)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pit)
                    .build();
            // notification = mBuilder.build();
            manager.notify(NOTIFYID_ONE, notification);

        }else if(key == 2){

            Intent intent = new Intent(this, DevInfoV1Activity.class);
            intent.putExtra(INTENT_KEY_IN_DEVICEBIND, model);
            if (!(this instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            PendingIntent pit = PendingIntent.getActivity(this, 0, intent, 0);
            String CHANNEL_ID="connect";
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID
                        , "name", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle(model.getDevicename() + getString(R.string.connect))   //标题 充电完成
                    .setContentText(model.getDevnickname())
                    .setSubText(getString(R.string.connect))
                    .setTicker(getString(R.string.connect) + model.getDevnickname())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_round48)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pit)
                    .build();
            // notification = mBuilder.build();
            manager.notify(NOTIFYID_ONE, notification);
        }
    }



}