package com.hjq.demo.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
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
import com.hjq.demo.http.api.AppSetInfoApi;
import com.hjq.demo.http.api.DevGetBindByMacApi;
import com.hjq.demo.http.api.GetDeviceTypeAndProfileInfoListApi;
import com.hjq.demo.http.api.GetOtaPackageList;
import com.hjq.demo.http.api.SetDeviceApi;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.HttpListSelectTwoData;
import com.hjq.demo.http.model.OtaPackage;
import com.hjq.demo.http.model.ReadAddress;
import com.hjq.demo.http.model.SelectItem;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.util.ByteUtil;
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
import com.hjq.widget.layout.RoundBottomSettingBar;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.CircleProgressView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import timber.log.Timber;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 设备入库
 */

public final class DevInboundActivity extends AppActivity {


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
    private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    //存放请求地址信息
    private List<ReadAddress> readAddressList;
    // 第一次打开页面读一次所以信息 第一次读0，指定读一些数据按 1，2，3，4，5定义， 2代表debug，8,代表控制 99 代表停止读设备数据
    private int readType;


    // 按地址划分
    private int step;
    private byte [] revarray;

    private List reacevResult;
    private int receivenum;


    private SettingBar mTypeView;
    private SettingBar mMacAddressView;
    private SettingBar mProfileIdView;
    private SettingBar mBatteryCellsView;
    private SettingBar  mVoltageLevelView;
    private SettingBar mOnecorecodeView;
    private SettingBar mTwocorecodeView;
    private SettingBar mDcversionView;

    private SettingBar mDcsubversionView;
    private SettingBar mLlcversionView;
    private SettingBar mLlcsubversionView;
    private SettingBar mPfcversionView;
    private SettingBar  mPfcsubversionView;
    private SettingBar mFrequencyView;
    private SettingBar mBmsversionView;
    private AppCompatButton mInboundView;

    //暂存读取的值
    private  int dtype_id ;
    private  int profile_id ;

    private int onecorecode;
    private int twocorecode;
    private String dlabel;
    private String  dcota;
    private String  llcota;
    private String pfcota;

    private int batterycells;
    private int voltagelevel;
    private int dcversion;
    private int dcsubversion;
    private int llcversion;
    private int llcsubversion;
    private int pfcversion;
    private int pfcsubversion;
    private int frequency;
    private int bmsversion;
    private List<SelectItem> typeList;
    private List<SelectItem> proList;





    public static void start(Context context, DeviceBind model) {
        Intent intent = new Intent(context, DevInboundActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICEBIND, model);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_inbound_activity;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initView() {
        mMacAddressView = findViewById(R.id.sb_macaddress);
        mTypeView = findViewById(R.id.sb_dev_type);
        mProfileIdView = findViewById(R.id.sb_profile_id);
        mBatteryCellsView = findViewById(R.id.sb_batterycells);
        mVoltageLevelView = findViewById(R.id.sb_voltagelevel);
        mOnecorecodeView = findViewById(R.id.sb_onecorecode);
        mTwocorecodeView = findViewById(R.id.sb_twocorecode);
        mDcversionView = findViewById(R.id.sb_dcversion);

        mDcsubversionView = findViewById(R.id.sb_dcsubversion);
        mLlcversionView = findViewById(R.id.sb_llcversion);
        mLlcsubversionView = findViewById(R.id.sb_llcsubversion);
        mPfcversionView = findViewById(R.id.sb_pfcversion);
        mPfcsubversionView = findViewById(R.id.sb_pfcsubversion);
        mFrequencyView = findViewById(R.id.sb_frequency);
        mBmsversionView = findViewById(R.id.sb_bmsversion);

        mInboundView = findViewById(R.id.btn_dev_inbound);

        setOnClickListener(R.id.btn_dev_inbound,  R.id.sb_dev_type,
                R.id.sb_profile_id);


    }


    @Override
    protected void initData() {
        typeList = new ArrayList<>();
        proList = new ArrayList<>();
        model = getParcelable(INTENT_KEY_IN_DEVICEBIND);
        if (model == null) {
            return;
        }
        // 获取初始化数据
        getSetlectData();
        reacevResult = new ArrayList();
        receivenum = 0;
        readType = 0;

        connected = false;
        // EasyLog.print("========================11start");
        /*mBleApi = FscBleCentralApiImp.getInstance();
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            perminssionsStart();
                        }
                    }
                }
        );*/

        mMacAddressView.setRightText(model.getMacaddr());


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
        }
        perminssionsStart();
    }

    private void bleConnect() {



        if (model != null) {
            //// EasyLog.print(!mBleApi.isConnected() ? "==========true":"========false");
            //if(!mBleApi.isConnected()){
            // EasyLog.print("======================== model 开始连接");

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


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

            // 为了等待蓝牙与串口通信通道建立起来延时3秒执行。
            postDelayed(() -> {
                initThreadReadStaus();
                step =128;
                mThreadHandler.sendEmptyMessage(128);
            }, 5000);
        }
    }

    private void uiDeviceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //sendButton.setEnabled(false);
                //sendFileButton.setEnabled(false);

                //android.util.Log.e(TAG, "uiDeviceDisconnected: false 1"  );

            }
        });
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
        connected = false;
        mResultLauncher = null;
        if (mThreadHandler != null) {
            mHandlerThread.quit();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        if (mBleApi != null && mBleApi.isConnected()) {
            mBleApi.disconnect();
        }
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

    }

    @Override
    protected void onResume() {
        // EasyLog.print("======================== 3 onResume 开始连接");


        super.onResume();
        if (mBleApi == null) {
            mBleApi = FscBleCentralApiImp.getInstance();
        }
        if(mBleApi.isConnected()){
            // EasyLog.print("======================== DevSetActive onResume connected = true");
            connected = true;
            setBleCallBack();
            postDelayed(() -> {
                initThreadReadStaus();
                step =128;
                mThreadHandler.sendEmptyMessage(128);
            }, 5000);
        }else{
            connected = false;
            step =128;
            // EasyLog.print("======================== DevSetActive onResume connected = false");
            checkBluetooth();
        }
        //checkBluetooth();
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
                // EasyLog.print("======================== packetSend address -> " + address + "\r\n strValue ->" + strValue + "  \r\n data -> " + ByteUtil.toHexString(data));
            }

        });
        // EasyLog.print("========================start");

    }
    @Override
    public void onClick(View view) {
        if (view == mInboundView) {
            String sn = RecodeUtil.codeSN(model.getMacaddr());
            EasyHttp.post((LifecycleOwner) getActivity())
                    .api(new SetDeviceApi()
                            .AddDevice(dtype_id,dlabel,model.getMacaddr(), profile_id,FormatUtil.bytesToHexString(revarray), 0,0,0,"",
                                    2,batterycells+"", voltagelevel+"",onecorecode, twocorecode,dcversion+"", dcsubversion+"", llcversion+"", llcsubversion+"", pfcversion+"", pfcsubversion+"",frequency+"", bmsversion+"",sn)
                    ).request(new HttpCallback<HttpData<String>>((OnHttpListener) getActivity()) {

                        @Override
                        public void onStart(Call call) {

                        }

                        @Override
                        public void onEnd(Call call) {}

                        @Override
                        public void onSucceed(HttpData<String> data) {
                            if(data.getCode() == 200) {
                                //startActivity(DevInfoActivity.class);
                                toast(R.string.inboundsuccess);
                                finish();
                            }else{
                                toast(data.getData());
                            }

                        }

                        @Override
                        public void onFail(Exception e) {
                            super.onFail(e);
                            postDelayed(() -> {

                            }, 1000);
                        }
                    });

        }else if( view == mTypeView){
            // 选择设备型号
            // 底部选择框
            List<String> data = new ArrayList<>();
            for (SelectItem item: typeList
                 ) {
                data.add(item.name);
            }
            new MenuDialog.Builder(this)
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(data)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String string) {
                            dtype_id = typeList.get(position).getId();
                            mTypeView.setRightText(string);
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.common_cancel);
                        }
                    })
                    .show();
        }else if(view == mProfileIdView){
            // 选择设备版本
            // 选择设备型号
            // 底部选择框
            List<String> data = new ArrayList<>();
            for (SelectItem item: proList
            ) {
                data.add(item.name);
            }
            new MenuDialog.Builder(this)
                    // 设置 null 表示不显示取消按钮
                    //.setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(data)
                    .setListener(new MenuDialog.OnListener<String>() {

                        @Override
                        public void onSelected(BaseDialog dialog, int position, String string) {
                            profile_id = proList.get(position).getId();
                            mProfileIdView.setRightText(string);
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.common_cancel);
                        }
                    })
                    .show();
        }
    }

    private void getSetlectData(){
        EasyHttp.post(this)
                .api(new GetDeviceTypeAndProfileInfoListApi())
                .request(new HttpCallback<HttpListSelectTwoData<SelectItem>>(this) {



                    @Override
                    public void onSucceed(HttpListSelectTwoData<SelectItem> data) {
                        if(data.getCode() == 200){
                            typeList = data.getData().getItema();
                            proList = data.getData().getItemb();
                        }
                    }
                });
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
     * 总输出
     *
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void StatusReceivAll(String address, String strValue, String hexString, byte[] data) {
        // EasyLog.print("======================== receivData HEX-> " + FormatUtil.bytesToHexString(data));
        //分五次接收数据
        if(receivenum == 0){
            receivenum ++;
            revarray = data;
            postDelayed(() -> {
                // EasyLog.print("======================== revarray.length-> " + revarray.length);
                if(revarray.length==255){
                    receivenum=0;
                    // 解码数据值
                    List<Integer> receivDataList = RecodeUtil.readDecodeAll(revarray);

                    if(receivDataList.size() == 125){
                        //statusarray= RecodeUtil.readDecodeStatus(revarray[123],revarray[124]);

                        // 设备额度电压
                        voltagelevel = receivDataList.get(97);
                        String strV98 = RecodeUtil.GetRealValues("33", "64", voltagelevel);

                        // 设备额度频率
                        frequency = receivDataList.get(99);
                        String strV100 = RecodeUtil.GetRealValues("33", "64", frequency);

                        // LLC芯片编码
                        onecorecode = receivDataList.get(102);
                        String strV103 = RecodeUtil.GetRealValues("33", "67", onecorecode);

                        twocorecode = receivDataList.get(103);
                        String strV104 = RecodeUtil.GetRealValues("33", "68", twocorecode);

                        batterycells = receivDataList.get(104);
                        String strV105 = RecodeUtil.GetRealValues("33", "69", batterycells);

                        // LLC1系统软件版本
                        llcversion = receivDataList.get(107);
                        String strV108 = RecodeUtil.GetRealValues("33", "6C", llcversion);


                        llcsubversion = receivDataList.get(108);
                        String strV109 = RecodeUtil.GetRealValues("33", "6D", llcsubversion);

                        // PFC系统软件版本
                        pfcversion = receivDataList.get(110);
                        String strV111 = RecodeUtil.GetRealValues("33", "6F", pfcversion);
                        // PFC系统软件版本
                        pfcsubversion = receivDataList.get(111);
                        String strV112 = RecodeUtil.GetRealValues("33", "70", pfcsubversion);

                        // DC系统软件版本
                        dcversion = receivDataList.get(114);
                        String strV115 = RecodeUtil.GetRealValues("33", "73", dcversion);

                        // DC系统软件版本
                        dcsubversion = receivDataList.get(115);
                        String strV116 = RecodeUtil.GetRealValues("33", "74", dcsubversion);

                        // BMS软件版本
                        bmsversion = receivDataList.get(112);
                        String strV113 = RecodeUtil.GetRealValues("33", "71", bmsversion);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mVoltageLevelView.setRightText(strV98);
                                mFrequencyView.setRightText(strV100);
                                mOnecorecodeView.setRightText(strV103);
                                mTwocorecodeView.setRightText(strV104);
                                mBatteryCellsView.setRightText(strV105);

                                mLlcversionView.setRightText(strV108);
                                mLlcsubversionView.setRightText(strV109);

                                mPfcversionView.setRightText(strV111);
                                mPfcsubversionView.setRightText(strV112);

                                mDcversionView.setRightText(strV115);
                                mDcsubversionView.setRightText(strV116);

                                mDcversionView.setRightText(strV108);
                                mDcsubversionView.setRightText(strV109);

                                mBmsversionView.setRightText(strV113);

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

        //step = 128;
        //mThreadHandler.sendEmptyMessage(128);

    }







    /**
     * 通过 HexStr 向设备发送请求
     */
    @Log
    private void sendDevicebyAddrHexStr(String sendCheckHexStr, String logid){
        byte[] sendCheckBytes = FormatUtil.BytesAddCrc(sendCheckHexStr.toUpperCase());
        // EasyLog.print("======================== 发送的十六进制代码：" + FormatUtil.bytesToHexString(sendCheckBytes));
        try{
            if(connected){
                // EasyLog.print("======================== 当前的地址ID：" +logid );
                mBleApi.send(sendCheckBytes);
            }
        }catch(Exception e){
            Timber.d("======================== 测试与设备通讯出错" + e.toString());
        }
    }

    /**
     * 获取设备型号及设备版本表列表
     */
    private void getSelectItem(){
        // EasyLog.print( "======================== GetDeviceTypeAndProfileInfoListApi");
        // 查询更新包
        EasyHttp.post(this)
                .api(new GetDeviceTypeAndProfileInfoListApi()
                )
                .request(new HttpCallback<HttpListSelectTwoData<SelectItem>>(this) {

                    @Override
                    public void onSucceed(HttpListSelectTwoData<SelectItem> data) {
                        if(data.getCode() == 200){
                            typeList = data.getData().getItema();
                            proList = data.getData().getItemb();

                        }else {
                            dtype_id = 0;
                            profile_id = 0;
                            mTypeView.setVisibility(View.GONE);
                            mProfileIdView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }




}