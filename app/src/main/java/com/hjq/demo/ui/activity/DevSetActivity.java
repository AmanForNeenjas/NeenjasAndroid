package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.LifecycleOwner;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.ConnectType;
import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.DelDevBindByIdApi;
import com.hjq.demo.http.api.DevAddBindApi;
import com.hjq.demo.http.api.DevGetBindByMacApi;
import com.hjq.demo.http.api.DevSetNameApi;
import com.hjq.demo.http.api.DeviceBindMacListApi;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.manager.CacheDataManager;
import com.hjq.demo.manager.ThreadPoolManager;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.ui.dialog.SafeDialog;
import com.hjq.demo.util.ByteUtil;
import com.hjq.demo.util.FormatUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.demo.util.RecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.language.MultiLanguages;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.RoundBottomSettingBar;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设备设置界面
 */
public final class DevSetActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

    private static final String INTENT_KEY_IN_DEVICESET = "deviceset";
    private static final String INTENT_KEY_NICKNAME = "nickname";
    private static final String INTENT_KEY_MAXIMPLUS = "maximplus";
    private static final String INTENT_KEY_MINDISCHARGING = "mindischarging";
    private static final String INTENT_KEY_ISSETFASTIN = "issetfastin";
    private static final String INTENT_KEY_ISSETYBOOSTIN = "issetyboostin";

    private DeviceBind model;

    private RoundTopSettingBar mDevNicknameView;
    private SettingBar mInboundView;
    private SettingBar mDevTypeView;
    private SettingBar mDevImplusView;
    // 最小放电值设置
    private SettingBar mDevDischargingView;
    private SettingBar mSoftwareView;
    private SettingBar mFastInView;
    private SwitchButton mAutoSwitchView;
    private SwitchButton mYboostSwitchView;
    private RoundBottomSettingBar mDevHelpView;
    private AppCompatButton mDevdisbandView;
    private String newname;
    private boolean isSetFastin;
    private boolean isSetYboost;

    // 最大充电值
    private String maxImplus;
    // 最小放电值
    private String minDischarging;
    public FscBleCentralApi mBleApi;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;
    private byte [] revarray;
    private int[] statusarray;
    private int[] statusarrtwo; //存放67状态变量值
    // 控制信号每次发送的字节
    private byte[] sendBytes;
    // 控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
    private int controlType;

    //UI线程中的handler

    private int readType;

    //以防退出界面后Handler还在执行
    private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    // 按地址划分
    private int step;
    // 停止充电容量值
    private int setnum;

    boolean connected;
    private int receivenum;
    private Set<String> bindMac;
    private boolean isBind;


    public static void start(BaseActivity context, DeviceBind model, String maxImplus,String minDischarging, String issetFastIn,String issetYboost,  OnSetNickNameListener listener) {
        Intent intent = new Intent(context, DevSetActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICESET, model);
        intent.putExtra(INTENT_KEY_MAXIMPLUS, maxImplus);
        intent.putExtra(INTENT_KEY_MINDISCHARGING, minDischarging);
        intent.putExtra(INTENT_KEY_ISSETFASTIN, issetFastIn);
        intent.putExtra(INTENT_KEY_ISSETYBOOSTIN, issetYboost);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivityForResult(intent, (resultCode, data) -> {

            if (listener == null || data == null) {
                return;
            }

            if (resultCode == RESULT_OK) {
                listener.onSucceed(data.getStringExtra(INTENT_KEY_NICKNAME));
            } else {
                listener.onCancel();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_set_activity;
    }

    @Override
    protected void initView() {
        mDevNicknameView = findViewById(R.id.sb_devnickname);
        mInboundView = findViewById(R.id.sb_dev_inbound);

        mDevdisbandView = findViewById(R.id.btn_dev_disband);
        mSoftwareView = findViewById(R.id.sb_software);
        mDevHelpView = findViewById(R.id.sb_dev_help);
        mDevTypeView = findViewById(R.id.sb_dev_type);
        mDevImplusView = findViewById(R.id.sb_dev_implus);
        mFastInView = findViewById(R.id.sb_dev_setfastin);
        mAutoSwitchView = findViewById(R.id.sb_setting_fastin_switch);
        mYboostSwitchView = findViewById(R.id.sb_setting_yboost_switch);
        mDevDischargingView = findViewById(R.id.sb_dev_discharging);
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        UserInfo userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);

        bindMac = mMmkv.decodeStringSet(MMKVUtils.DEVICEBINDMACLIST);
        if(bindMac == null){
            bindMac = new HashSet<>();
        }else{

        }


        if(!userInfo.isIpupdev()){
            mInboundView.setVisibility(View.GONE);
        }
        if(!userInfo.isIsupgrade()){
            mSoftwareView.setVisibility(View.GONE);
        }



        setOnClickListener(R.id.sb_devnickname, R.id.sb_dev_inbound, R.id.btn_dev_disband, R.id.sb_software,  R.id.sb_dev_help, R.id.sb_dev_type, R.id.sb_dev_implus,R.id.sb_dev_discharging, R.id.sb_dev_setfastin, R.id.btn_dev_disband);
        getTitleBar().getLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!newname.isEmpty()){
                    postDelayed(() -> {

                        setResult(RESULT_OK, new Intent()
                                .putExtra(INTENT_KEY_NICKNAME, newname)
                        );
                        finish();
                    }, 1000);
                }
                else{
                    postDelayed(() -> {
                        setResult(RESULT_CANCELED);
                        finish();
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void initData() {
        // 获取应用缓存大小
        //mCleanCacheView.setRightText(CacheDataManager.getTotalCacheSize(this));

       //mNickNameView.setRightText("简体中文");
        //mPhoneView.setRightText("181****1413");
        //mPasswordView.setRightText("密码强度较低");
        model = getParcelable(INTENT_KEY_IN_DEVICESET);
        maxImplus =  getString(INTENT_KEY_MAXIMPLUS);
        minDischarging = getString(INTENT_KEY_MINDISCHARGING);
        String isSetFastinStr =  getString(INTENT_KEY_ISSETFASTIN);
        String isSetYboostStr =  getString(INTENT_KEY_ISSETYBOOSTIN);

        if(model == null){
            return;
        }

        // 测试账号model userid 设置为0 不能解绑设备 20230825
        if(bindMac!= null&& bindMac.contains(model.getMacaddr())){
            isBind = true;
        }
        if(model.getUser_id() ==0){
            //mDevdisbandView.setVisibility(View.GONE);
            // 测试账号model userid 设置为0增加绑定设备  20231026
            if(!isBind){
                mDevdisbandView.setText(R.string.scanner_subtitle_bonded);
            }else{
                mDevdisbandView.setVisibility(View.GONE);
            }

            mDevNicknameView.setVisibility(View.GONE);
        }

        connected = false;

        readType = 0;

        statusarray = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        statusarrtwo = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        receivenum = 0;


        // EasyLog.print("======================== 3 maxImplus:" +maxImplus  + " isFastIn" + isSetFastinStr);

        if(isSetFastinStr.equals("1")){
            mAutoSwitchView.setChecked(true);
            isSetFastin = true;
        }else{
            isSetFastin = false;
            mAutoSwitchView.setChecked(false);
        }


        mAutoSwitchView.setOnCheckedChangeListener(this);
        if(isSetYboostStr.equals("1")){
            mYboostSwitchView.setChecked(true);
            isSetYboost = true;
        }else{
            isSetYboost = false;
            mYboostSwitchView.setChecked(false);
        }
        mYboostSwitchView.setOnCheckedChangeListener(this);

        // EasyLog.print("======================== 3 maxImplus maxImplus"  + maxImplus);
        if(maxImplus.equals("") || maxImplus.equals("0")){
            mDevImplusView.setRightText("100%");
        }else{
            mDevImplusView.setRightText(maxImplus+"%");
        }
        if(minDischarging.equals("") || minDischarging.equals("0")){
            mDevDischargingView.setRightText("100%");
        }else{
            mDevDischargingView.setRightText(minDischarging+"%");
        }

        mBleApi = FscBleCentralApiImp.getInstance();

        if(mBleApi.isConnected()){
            // EasyLog.print("======================== 3 mBleApi Connected" );
            connected = true;
        }else{
            connected = false;
            // EasyLog.print("======================== 3 mBleApi not Connected" );
            mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == RESULT_OK){
                                perminssionsStart();
                            }
                        }
                    }
            );
        }
        newname = "";
        if(model == null){
            return;
        }
    }
    private void perminssionsStart(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(XXPermissions.isGranted(this, Permission.BLUETOOTH_SCAN,Permission.BLUETOOTH_ADVERTISE,Permission.BLUETOOTH_CONNECT)){
                bleConnect();
            }else{
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
                        });
            }


        }else{
            if(XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION)){

                bleConnect();
            }else {
                XXPermissions.with(this)
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
                        });
            }
        }

    }
    /**
     * 检查蓝牙是否打开
     */
    private void checkBluetooth(){
        if(!(mBleApi!= null && mBleApi.isEnabled())){
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
        }

        perminssionsStart();

    }
    private void bleConnect(){


        //checkBluetooth();
        // EasyLog.print("========================start");
        if(model !=null){

            // EasyLog.print("======================== model 开始连接");

            mBleApi.connect(model.getMacaddr());

        }
    }

    private void uiDeviceConnected() {
        connected = true;
        //toast(R.string.update_ready);

        // 与设备同步显示加载框
        //showDialog();
        //loading = true;
        //closeLoadingDialogTimerTask();
        // 为了等待蓝牙与串口通信通道建立起来延时3秒执行。
        initThreadReadStaus();
        postDelayed(() -> {

            step =128;


            mThreadHandler.sendEmptyMessage(128);
        }, 300);

    }
    private void uiDeviceDisconnected() {

    }
    /**
     * 初始化读取设备信息 相关的Thread
     */
    private void initThreadReadStaus() {
        mHandlerThread = new HandlerThread("check-message-coming");
        mHandlerThread.start();
        // EasyLog.print("======================== initThread");
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
                    case 899:
                        maxImplusSend();
                        break;

                }

            }
        };
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
     * 一次取125个数据 发送
     */
    private void maxImplusSend() {
        //String sendCheckHexStr = RecodeUtil.getRegisterAddr(RecodeUtil.STANDBYADDR, RecodeUtil.FUNCTIONPARAM, "33", "0c", RecodeUtil.REGISTERCONTENTHIGH, RecodeUtil.REGISTERCONTENTLOW);
        step = 899;
        sendDevicebyAddrHexStr(RecodeUtil.getRegisterAddrSetMaxImplus(RecodeUtil.int2HexString( setnum)), "V125");
    }
    /**
     *控制信号类型 AC Charge +2   AC Discharge +4  DC Discharge +8  Max Charge Current +16 Loop Parameter Debug +32  Mood light +64  Emergency light +128  Mosquito killer light +256
     *
     */
    private void controlSend(){

        int num = 1;
        // AC Charge +2
        step = 999;
        if(controlType == 4096  || controlType == 8192) {

            if( isSetFastin == true){
                num += 4096;
            }
            if(isSetYboost == true){
                num += 8192;
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

        }
        // EasyLog.print("========================Control num -> "+num);
        String sendStr = "0141" + "3200" +  ByteUtil.toHexString(RecodeUtil.intTobyteTwo(num));
        // EasyLog.print("========================Control sendStr -> "+sendStr);
        sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
        // 发送
        mBleApi.send(sendBytes);


    }

    private void SetImplusReceiv(String address, String strValue, String hexString, byte[] data) {
        //// EasyLog.print("======================== receivData HEX-> " + FormatUtil.bytesToHexString(data));
        toast(R.string.success);
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
        // EasyLog.print("======================== receivData HEX-> " + FormatUtil.bytesToHexString(data));
        //toast(R.string.success);
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
                        int tempa =revarray[123]&0XFF;
                        //// EasyLog.print("========================revarray[123]-> " + tempa);
                        tempa =revarray[124]&0XFF;
                        //// EasyLog.print("========================revarray[124]-> " + tempa);
                        statusarray= RecodeUtil.readDecodeStatus(revarray[123], revarray[124]);
                        statusarrtwo = RecodeUtil.readDecodeStatusTwo(revarray[135], revarray[136]);

                        for(int i=0; i< statusarray.length; i++){
                            // EasyLog.print("========================statusarray-> " + i + " " + statusarray[i]);
                        }

                        //versionMap.put("dc", dcVersion);
                        //versionMap.put("llc", llcVersion);
                        //versionMap.put("pfc", pfcVersion);
                        // EasyLog.print("======================== receivDataList.get(68)-> " + receivDataList.get(68) + "   statusarrtwo[0]: " + statusarrtwo[0]+"   statusarrtwo[1]: " + statusarrtwo[1] +"   statusarrtwo[2]: " + statusarrtwo[2] +"   statusarrtwo[3]: " + statusarrtwo[3]+"   statusarrtwo[4]: " + statusarrtwo[4]);
                        maxImplus = receivDataList.get(68)+"";
                        minDischarging = receivDataList.get(69)+"";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*if(statusarray[8]==1){
                                    mAutoSwitchView.setChecked(true);
                                }else {
                                    mAutoSwitchView.setChecked(false);
                                }*/
                                if(receivDataList.get(68)== 0){
                                    mDevImplusView.setRightText("100%");
                                }else{
                                    mDevImplusView.setRightText(receivDataList.get(68)+"%");
                                }
                                if(receivDataList.get(69)== 0){
                                    mDevDischargingView.setRightText("0%");
                                }else{
                                    mDevDischargingView.setRightText(receivDataList.get(69)+"%");
                                }

                            }
                        });

                    }


                }
                // 测试100毫秒不行，200毫秒可以的。
            }, 300);
        }
        else{
            revarray = RecodeUtil.byteMergerAll(revarray, data);
        }



    }
    private void ControlReceiv(String address, String strValue, String hexString, byte[] data) {
        // EasyLog.print("========================Control  address -> " + address + "\r\n strValue ->" + strValue + "  \r\n data -> " + ByteUtil.toHexString(data));
        toast(R.string.success);
    }

        @Override
    protected void onDestroy() {
        //mBleApi.disconnect();
        // EasyLog.print("======================== 1 onDestroy 已经关闭");
        super.onDestroy();
        // EasyLog.print("======================== 1 mBleApi.isConnected()" + mBleApi.isConnected());
        /*if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        mBleApi = null;
        model = null;
        //connected = false;
        mResultLauncher = null;


    }
    @Override
    protected void onResume() {



        super.onResume();
        // EasyLog.print("======================== DevSetActive onResume 开始连接");

        if(mBleApi == null){
            mBleApi = FscBleCentralApiImp.getInstance();
        }
        //loading = false;
        if(mBleApi.isConnected()){
            // EasyLog.print("======================== DevSetActive onResume connected = true");
            connected = true;
            setBleCallBack();
            initThreadReadStaus();
            postDelayed(() -> {

                step =128;


                mThreadHandler.sendEmptyMessage(128);
            }, 2000);
        }else{
            connected = false;
            step =128;
            // EasyLog.print("======================== DevSetActive onResume connected = false");
            checkBluetooth();
        }


    }
    private void setBleCallBack(){
        mBleApi.setCallbacks(new FscBleCentralCallbacksImp(){
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


            @Override
            public void packetReceived(String address, String strValue, String hexString, byte[] data) {
                //android.util.Log.e(TAG,"packetReceived");
                //uiReceiveData(strValue, hexString, data);
                // 根据读数据类型判断哪种方式解析数据 readType ==0 读地址高位0x33;readType == 2 调试模式
                // readType ==0 读地址高位0x33;

                    if(step == 128){
                        StatusReceivAll(address, strValue, hexString, data);
                    }else if(step == 999){
                        ControlReceiv(address, strValue, hexString, data);
                    }else if(step == 899){
                        SetImplusReceiv(address, strValue, hexString, data);
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
                // EasyLog.print("======================== packetSend address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
            }

        });
    }

    /**
     * 通过 HexStr 向设备发送请求
     */
    private void sendDevicebyAddrHexStr(String sendCheckHexStr, String logid){
        byte[] sendCheckBytes = FormatUtil.BytesAddCrc(sendCheckHexStr.toUpperCase());
        // EasyLog.print("======================== 发送的十六进制代码：" + FormatUtil.bytesToHexString(sendCheckBytes));
        try{
            if(connected){
                // EasyLog.print("======================== 当前的地址ID：" +logid );
                mBleApi.send(sendCheckBytes);
            }
        }catch(Exception e){
            // EasyLog.print("======================== 测试与设备通讯出错" + e.toString());
        }
    }


    @SingleClick
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.sb_devnickname) {
            // 输入对话框
            new InputDialog.Builder(this)
                    // 标题可以不用填写
                    .setTitle(R.string.set_devnickname)
                    // 内容可以不用填写
                    .setContent(model.getDevnickname())
                    // 提示可以不用填写
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new InputDialog.OnListener() {
                        @Override
                        public void onConfirm(BaseDialog dialog, String content) {
                            if(content.length()<40){
                                EasyHttp.post((LifecycleOwner) getActivity())
                                        .api(new DevSetNameApi()
                                                .setId(model.getId())
                                                .setNickname(content)
                                        ).request(new HttpCallback<HttpData<DeviceBind>>((OnHttpListener) getActivity()) {
                                            @Override
                                            public void onStart(Call call) {

                                            }
                                            @Override
                                            public void onEnd(Call call) {}

                                            @Override
                                            public void onSucceed(HttpData<DeviceBind> data) {
                                                if(data.getCode() == 200) {
                                                    model.setDevnickname(content);
                                                    mDevNicknameView.setRightText(content);
                                                    newname = content;

                                                    toast(R.string.success);
                                                }else{
                                                    toast(R.string.common_network_error);
                                                }
                                            }
                                            @Override
                                            public void onFail(Exception e) {
                                                super.onFail(e);
                                                postDelayed(() -> {

                                                }, 1000);
                                            }
                                        });
                            }else{
                                toast(R.string.common_str_length_error);
                            }
                        }
                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.common_cancel);
                        }
                    })
                    .show();
        }  else if (viewId == R.id.sb_software) {
            // 固件升级
            DevCheckRefreshActivity.start(this, model );
        }else if (viewId == R.id.sb_setting_fastin_switch  || view == mAutoSwitchView) {
            // EasyLog.print("======================== sb_setting_fastin_switch" );
            // 自动登录
            mAutoSwitchView.setChecked(!mAutoSwitchView.isChecked());
            controlType = 4096;
            postDelayed(() -> {
                initThreadReadStaus();
                step = 999;


                mThreadHandler.sendEmptyMessage(999);
            }, 100);

        } else if (viewId == R.id.sb_dev_type) {
            // 设备规格信息
            DevTypeInfoActivity.start(this, model);

        }else if (viewId == R.id.sb_dev_implus) {
            // 最大充电值设置
            DevSetMaxImplusActivity.start(this, model, maxImplus);

        } else if (viewId == R.id.sb_dev_discharging) {
            // 最小放电值设置
            DevSetDisChargingActivity.start(this, model, minDischarging);

        } else if (viewId == R.id.sb_dev_help) {
            //帮助中心
            if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/zh_help_meta2000.html");
            }else{
                BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/en_help_meta2000.html");
            }

        } else if (viewId == R.id.sb_dev_inbound) {
            //设备入库
            DevInboundActivity.start(this, model);

        } else if (viewId == R.id.btn_dev_disband) {
            if(model.getUser_id() ==0){
                // 测试账号model userid 设置为0增加绑定设备
                if(!isBind){
                    String newName = model.getDevicename() +model.getMacaddr().substring(15);
                    // 输入对话框
                    new InputDialog.Builder(getActivity())
                            // 标题可以不用填写
                            .setTitle(R.string.setdevname)
                            // 内容可以不用填写
                            .setContent(newName)
                            // 提示可以不用填写
                            .setHint(R.string.setdevname_tip)
                            // 确定按钮文本
                            .setConfirm(getString(R.string.common_confirm))
                            // 设置 null 表示不显示取消按钮
                            .setCancel(getString(R.string.common_cancel))
                            // 设置点击按钮后不关闭对话框
                            //.setAutoDismiss(false)
                            .setListener(new InputDialog.OnListener() {

                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if(content.length()<30){
                                        // 新增设备绑定
                                        EasyHttp.post((LifecycleOwner) getActivity())
                                                .api(new DevAddBindApi()
                                                        .setNickname(content)
                                                        .setName(model.getDevicename())
                                                        .setMacaddr(model.getMacaddr())
                                                        .setDev_id(-1)
                                                        .setUid(-1)
                                                ).request(new HttpCallback<HttpData<DeviceBind>>((OnHttpListener) getActivity()) {

                                                    @Override
                                                    public void onStart(Call call) {

                                                    }

                                                    @Override
                                                    public void onEnd(Call call) {}

                                                    @Override
                                                    public void onSucceed(HttpData<DeviceBind> data) {
                                                        if(data.getCode() == 200) {
                                                            //startActivity(DevInfoActivity.class);
                                                            toast(R.string.bind_dev_success_tip);
                                                            DeviceBind itemdata = data.getData();
                                                            // 更新绑定地址列表
                                                            EasyHttp.post((LifecycleOwner) getActivity())
                                                                    .api(new DeviceBindMacListApi())
                                                                    .request(new HttpCallback<HttpListData<String>>(this) {

                                                                        @Override
                                                                        public void onSucceed(HttpListData<String> data) {
                                                                            if(data.getCode() == 200){
                                                                                bindMac = new HashSet<>(data.getData().getItems());
                                                                                MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                                                                mMmkv.encode(MMKVUtils.DEVICEBINDMACLIST, bindMac);

                                                                            }
                                                                            runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    mDevdisbandView.setVisibility(View.GONE);
                                                                                }});
                                                                            isBind = true;
                                                                            //DevInfoV1Activity.start(getActivity(), itemdata);
                                                                            //finish();

                                                                        }
                                                                    });


                                                        }else if(data.getCode() == 252) {
                                                            toast(R.string.bind_dev_error_tip);
                                                        }
                                                        else{
                                                            toast(R.string.common_network_error);
                                                        }


                                                    }

                                                    @Override
                                                    public void onFail(Exception e) {
                                                        //super.onFail(e);

                                                        //if(((HttpException) e).getStatusCode() == 252){
                                                        //    toast(R.string.bind_dev_error_tip);
                                                        //}
                                                        if( e.getMessage().toString().equals("device mac address was binded")) {
                                                            toast(R.string.bind_dev_error_tip);
                                                        }else if( e.getMessage().toString().equals("device mac address error")) {
                                                            toast(R.string.bind_dev_error_tip);
                                                        }else{
                                                            //super.onFail(e);
                                                            toast(R.string.common_network_error);
                                                        }

                                                    }
                                                });
                                    }else{
                                        toast(R.string.setting_nickname_tip);
                                    }


                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast(R.string.common_cancel);
                                }
                            })
                            .show();
                }
            }else{
                //已经绑定设备后可以解绑设备
                // 消息对话框
                new MessageDialog.Builder(getActivity())
                        // 标题可以不用填写
                        .setTitle(R.string.delete_alert)
                        // 内容必须要填写
                        .setMessage(R.string.delete_alert_info)
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                // 解除绑定
                                EasyHttp.post((LifecycleOwner) getActivity())
                                        .api(new DelDevBindByIdApi().setId(model.getId()))
                                        .request(new HttpCallback<HttpListData<String>>((OnHttpListener) getActivity()) {

                                            @Override
                                            public void onSucceed(HttpListData<String> data) {
                                                if(data.getCode() == 200){
                                                    toast(R.string.unbind_dev_success);
                                                    HashSet<String> macset = new HashSet<>(data.getData().getItems());
                                                    MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                                    mMmkv.encode(MMKVUtils.DEVICEBINDMACLIST, macset);
                                                    ActivityManager.getInstance().finishAllActivities(HomeActivity.class);
                                                }else if (data.getCode() == 250) {
                                                    toast(R.string.unbind_dev_id_error);
                                                }

                                            }

                                            @Override
                                            public void onFail(Exception e) {
                                                if(e.getMessage().toString().equals("id error")){
                                                    toast(R.string.unbind_dev_id_error);
                                                }else{
                                                    toast(R.string.unbind_dev_fail);
                                                }

                                            }
                                        });

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast(R.string.common_cancel);
                            }
                        })
                        .show();
            }




        }
    }



    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */

    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        if(button == mAutoSwitchView){
            //toast(checked);
            isSetFastin = mAutoSwitchView.isChecked();

            controlType = 4096;
            postDelayed(() -> {
                initThreadReadStaus();
                step = 999;


                mThreadHandler.sendEmptyMessage(999);
            }, 100);
        }else{
            isSetYboost = mYboostSwitchView.isChecked();
            if(isSetYboost){
                toast(R.string.open_yboost_tip);
            }
            controlType = 8192;
            postDelayed(() -> {
                initThreadReadStaus();
                step = 999;


                mThreadHandler.sendEmptyMessage(999);
            }, 100);
        }

    }

    /**
     * 注册监听
     */
    public interface OnSetNickNameListener {

        /**
         * 设置昵称成功
         *
         * @param name  昵称
         */
        void onSucceed(String name);

        /**
         * 取消注册
         */
        default void onCancel() {}
    }
}