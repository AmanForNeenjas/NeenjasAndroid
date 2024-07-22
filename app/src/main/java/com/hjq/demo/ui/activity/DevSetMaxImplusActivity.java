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
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.ConnectType;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.entity.UpgradeDevLog;
import com.hjq.demo.http.api.GetDeviceUpgradeByDeviceIDApi;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.util.ByteUtil;
import com.hjq.demo.util.CommonUtil;
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
import com.hjq.widget.layout.RoundSettingBar;
import com.hjq.widget.view.SubmitButton;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 预充电量值设置为xy
 */
public final class DevSetMaxImplusActivity extends AppActivity {

    private static final String INTENT_KEY_IN_DEVICEUPGRADE = "deviceupgrade";
    private static final String INTENT_KEY_MAXIMPLUS = "maximplus";
    private DeviceBind model;
    public FscBleCentralApi mBleApi;
    boolean connected ;
    private ActivityResultLauncher<Intent> mResultLauncher;
    /**
     * https://blog.csdn.net/xiao_FireFly/article/details/62892592
     */
    private HandlerThread mHandlerThread;
    //子线程中的handler
    private Handler mThreadHandler;

    //UI线程中的handler

    private int readType;

    //以防退出界面后Handler还在执行
    private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    private SubmitButton mConfimView;
    private SeekBar mimplusView;
    private TextView msetValView;
    private String maxImplus;
    
    

    // 按地址划分
    private int step;



    private int setnum;

    // 是否显示加载框
    private boolean loading;

    public static void start(Context context, DeviceBind model, String maxImplus) {
        Intent intent = new Intent(context, DevSetMaxImplusActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICEUPGRADE, model);
        intent.putExtra(INTENT_KEY_MAXIMPLUS, maxImplus);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_setmaximplus_activity;
    }

    @Override
    protected void initView() {
        mConfimView = findViewById(R.id.btn_confim);
        mimplusView = findViewById(R.id.sb_implus);
        msetValView = findViewById(R.id.tv_setimplus);


        setOnClickListener(mConfimView);
        mimplusView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setnum = i;
                msetValView.setText(setnum + "/100");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void initData() {
        model = getParcelable(INTENT_KEY_IN_DEVICEUPGRADE);
        maxImplus =  getString(INTENT_KEY_MAXIMPLUS);
        if(model == null){
            return;
        }
        connected = false;

        readType = 0;
        
        loading = false;
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);

        AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);

        try{
            setnum = Integer.parseInt(maxImplus);
        }catch (Exception e){
            setnum = 100;
        }
        if(setnum<=0){
            mimplusView.setProgress(100);
            msetValView.setText( "100/100");
        }else{
            mimplusView.setProgress(setnum);
            msetValView.setText(setnum + "/100");
        }


        // EasyLog.print("========================11start");
        mBleApi = FscBleCentralApiImp.getInstance();
        if(mBleApi.isConnected()){
            connected = true;
        }else{
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



    }
    @SingleClick
    @Override
    public void onClick(View view) {
        // 点击开始升级
        if (view == mConfimView) {

            //finish();
            postDelayed(() -> {
                initThreadReadStaus();
                step =128;


                mThreadHandler.sendEmptyMessage(128);
            }, 300);
            mConfimView.reset();
        }
    }

    private void perminssionsStart(){
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
                    });

        }else{
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
    /**
     * 检查蓝牙是否打开
     */
    private void checkBluetooth(){
        if(!mBleApi.isEnabled()){
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
        showDialog();
        loading = true;
        //closeLoadingDialogTimerTask();
        // 为了等待蓝牙与串口通信通道建立起来延时3秒执行。


    }
    private void closeLoadingDialogTimerTask(){


        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                hideDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mConfimView.setVisibility(View.VISIBLE);
                    }
                });
                loading = false;
                if(timer !=null ){

                    timer.cancel();
                    return;
                }

            }
        };


        // 定义开始等待时间  --- 等待 5 秒
        // 1000ms = 1s
        long delay = 6000;
        // 定义每次执行的间隔时间
        long intevalPeriod = 1000;
        // schedules the task to be run in an interval
        // 安排任务在一段时间内运行
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
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
        sendDevicebyAddrHexStr(RecodeUtil.getRegisterAddrSetMaxImplus(RecodeUtil.int2HexString( setnum)), "V125");
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
        toast(R.string.success);
        //分五次接收数据
        /*if(receivenum == 0){
            receivenum ++;
            revarray = data;
            postDelayed(() -> {
                //// EasyLog.print("======================== revarray.length-> " + revarray.length);
                if(revarray.length==255){
                    receivenum=0;
                    // 解码数据值
                    List<Integer> receivDataList = RecodeUtil.readDecodeAll(revarray);

                    if(receivDataList.size() == 125){



                        //for(int i=0; i< statusarray.length; i++){
                        //    // EasyLog.print("========================statusarray-> " + i + " " + statusarray[i]);
                        //}

                        //versionMap.put("dc", dcVersion);
                        //versionMap.put("llc", llcVersion);
                        //versionMap.put("pfc", pfcVersion);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCurversionView.setRightText(dcVersion);
                                mDCversionView.setText(dcVersion);
                                mLLCversionView.setText(llcVersion);
                                mPFCversionView.setText(pfcVersion);
                                mConfimView.setVisibility(View.VISIBLE);
                            }
                        });
                        // 隐藏加载框
                        if(loading){
                            hideDialog();
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
        //mThreadHandler.sendEmptyMessage(128);*/

    }

    @Override
    protected void onDestroy() {
        //mBleApi.disconnect();
        // EasyLog.print("======================== DevSetMaxImplus 1 onDestroy 已经关闭");
        super.onDestroy();
        // EasyLog.print("======================== DevSetMaxImplus 1 mBleApi.isConnected()" + mBleApi.isConnected());
        /*if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        if(mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        mBleApi = null;
        model = null;
        //connected = false;
        mResultLauncher = null;


    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        // EasyLog.print("======================== 2 onPause ");
        // EasyLog.print("======================== 2 mBleApi.isConnected()" + mBleApi.isConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // EasyLog.print("======================== 2 onStop ");
        // EasyLog.print("======================== 2 mBleApi.isConnected()" + mBleApi.isConnected());
        /*if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }*/
        //mBleApi.disconnect();
        //mConfimView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        // EasyLog.print("======================== 3 onResume 开始连接");


        super.onResume();

        if(mBleApi == null){
            mBleApi = FscBleCentralApiImp.getInstance();
        }
        loading = false;
        if(mBleApi.isConnected()){
            connected = true;
            setBleCallBack();

        }else{
            connected = false;
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
                if (readType == 0) {
                    if(step == 128){
                        StatusReceivAll(address, strValue, hexString, data);
                    }

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


}