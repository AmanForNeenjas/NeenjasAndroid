package com.hjq.demo.ui.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.load.HttpException;
import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.FscDevice;
import com.hjq.bar.TitleBar;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.config.BleConfig;
import com.hjq.demo.entity.ExtendedBluetoothDevice;
import com.hjq.demo.http.api.DevAddBindApi;
import com.hjq.demo.http.api.DevGetBindByMacApi;
import com.hjq.demo.http.api.DeviceBindMacListApi;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.ui.adapter.ScanDeviceListAdapter;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.util.CommonUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;

import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.language.MultiLanguages;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.RoundSettingBar;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.NestedListView;
import com.hjq.widget.view.SwitchButton;
import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import androidx.activity.result.ActivityResult;


public final class AddDevActivity extends AppActivity
        implements SwitchButton.OnCheckedChangeListener {

    private RoundSettingBar mSanTipView;
    private SwitchButton mSanSwitchView;
    public static String keyWord; //查询关键字
    private FscBleCentralApi mFscBleCentralApi;
    private boolean scanning  = false;
    private ScanDeviceListAdapter deviceListAdapter;
    private NestedListView listView;
    // 设置扫描超时时间
    private Timer timer;

    private TitleBar titleBar;


    private ActivityResultLauncher<Intent> mResultLauncher;
    private Set<String> bindMac;
    private boolean isTest;// 测试模式 不需要绑定设备20230825


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguages.updateAppLanguage(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.adddev_activity;
    }

    @Override
    protected void initView() {
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        bindMac = mMmkv.decodeStringSet(MMKVUtils.DEVICEBINDMACLIST);
        if(bindMac == null){
            bindMac = new HashSet<>();
        }
        mSanTipView = findViewById(R.id.sb_scan_tip);
        mSanSwitchView = findViewById(R.id.sb_scan);
        titleBar = findViewById(R.id.tb_adddev);
        titleBar.getLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EasyLog.print("======================== titleBar.getLeftView ");
                if(scanning){
                    scanning = false;
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }
                    mFscBleCentralApi.stopScan();
                    toast(R.string.disconnect_tip);
                    postDelayed(() -> {
                        deviceListAdapter.clearDevices();
                        deviceListAdapter.clearLeftDevice();

                        if ( mFscBleCentralApi != null &&  mFscBleCentralApi.isConnected()) {
                            mFscBleCentralApi.disconnect();
                        }
                        mFscBleCentralApi =null;
                        deviceListAdapter = null;
                        finish();
                    }, 800);
                }else{
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }

                    deviceListAdapter.clearDevices();
                    deviceListAdapter.clearLeftDevice();

                    if ( mFscBleCentralApi != null &&  mFscBleCentralApi.isConnected()) {
                        mFscBleCentralApi.disconnect();
                    }
                    mFscBleCentralApi =null;
                    deviceListAdapter = null;
                    finish();
                }
            }
        });


        // 设置切换按钮的监听
        mSanSwitchView.setOnCheckedChangeListener(this);

        listView = findViewById(R.id.lv_scandev);
        listView.setEmptyView(findViewById(R.id.tv_nodev));
        deviceListAdapter = new ScanDeviceListAdapter(bindMac);

        listView.setAdapter(deviceListAdapter);

        // 点击列表进入设备详情页
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                stopScan();
                ExtendedBluetoothDevice ebdev = (ExtendedBluetoothDevice) deviceListAdapter.getItem(position);
                //Log.d("device click", ebdev.device.getName() + ":" + ebdev.device.getAddress());
                if(ebdev == null){
                    return;
                }
                if(ebdev.isBonded){
                    EasyHttp.post((LifecycleOwner) getActivity())
                            .api(new DevGetBindByMacApi()
                                    .setMacaddr(ebdev.device.getAddress())
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
                                        //DevInfoV1Activity.start(getActivity(), data.getData());
                                        DevInfoV1Activity.start(getActivity(), data.getData());
                                        finish();
                                    }else{
                                        toast(R.string.common_network_error);
                                    }

                                }

                                @Override
                                public void onFail(Exception e) {
                                    super.onFail(e);
                                    /*postDelayed(() -> {

                                    }, 1000);*/
                                }
                            });

                }else{
                    String newName = ebdev.device.getName() + ebdev.device.getAddress().substring(15);
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
                                                        .setName(ebdev.device.getName())
                                                        .setMacaddr(ebdev.device.getAddress())
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
                                                                            DevInfoV1Activity.start(getActivity(), itemdata);
                                                                            finish();
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


            }
        });
        listView.setTextFilterEnabled(true);
    }

    @Override
    protected void initData() {

        if(bindMac == null){
            bindMac = new HashSet<>();
        }

        scanning  = false;
        isTest = false;
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        UserInfo userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
        AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);
        if(appSetInfo.globaltest.equals("yes")){
            isTest = true;
        }else{
            if(userInfo != null && userInfo.isIpupdev()){
                isTest = true;
            }else{
                isTest = false;
            }
        }

        // EasyLog.print("======================== 0 initData ");
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        //AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);
        // 获取可以搜索的MAC地址前缀
        //allowMac = appSetInfo.allowmac;
        // 获取禁止搜索的MAC地址前缀
        //disMac = appSetInfo.dismac;
        //blenames = appSetInfo.blenames;
        deviceListAdapter.clearDevices();
        deviceListAdapter.clearLeftDevice();
        mFscBleCentralApi= FscBleCentralApiImp.getInstance(this);
        /*mFscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void blePeripheralFound(FscDevice device, int rssi, byte[] record) {
                //// EasyLog.print("======================== FOUND " + device.getName() + "  " + device.getAddress());
                //toast(device.getName());
                //过滤掉不要显示的MAC地址
                //if(CommonUtil.inArrayIndex(allowMac,device.getAddress(),true)>-1){

                deviceListAdapter.update(device);
                        //// EasyLog.print("======================== FOUND " + device.getName() + "  " + device.getAddress());

                    //}
                //}

                //toast(device.getName());
            }

            @Override
            public void startScan() {
                super.startScan();
            }

            @Override
            public void stopScan() {
                super.stopScan();
            }
        });*/
        registerBroadcastReceivers();

        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            perminssionsStart();
                        }
                        else{
                            finish();
                        }
                    }
                }
        );
        checkBluetooth();
    }



    private BroadcastReceiver mBluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
            switch (state){
                case BluetoothAdapter.STATE_ON:
                    stopScan();
                   // Log.e("ble", "onReceive: 蓝牙已开启");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    stopScan();
                    //Log.e("ble", "onReceive: 蓝牙已关闭" );
                    break;
            }
        }
    };

    private void registerBroadcastReceivers() {
        registerReceiver(mBluetoothStateBroadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void perminssionsStart(){
        //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
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
                            startScan();
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
                            startScan();
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
    private void checkBluetooth(){
        if(!mFscBleCentralApi.isEnabled()){
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

    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == titleBar.getLeftView()) {
            //toast(R.string.disconnect_tip);
            if(scanning){
                scanning = false;
                mFscBleCentralApi.stopScan();
            }
            deviceListAdapter.clearDevices();
            deviceListAdapter.clearLeftDevice();
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if ( mFscBleCentralApi != null &&  mFscBleCentralApi.isConnected()) {
                mFscBleCentralApi.disconnect();
            }

            finish();


        }
    }

    /**
     * {@link SwitchButton.OnCheckedChangeListener}
     */
    @Override
    public void onCheckedChanged(SwitchButton button, boolean checked) {
        if(checked == true){
            perminssionsStart();
        } else {
            stopScan();
        }
    }


    private void startScan() {
        if(timer == null){
            timer = new Timer();
        }
        deviceListAdapter.clearDevices();
        scanning = true;
        mSanTipView.setLeftText(R.string.scan_tip_isscanning);
        mFscBleCentralApi.startScan();
        //Log.e("ble", "999 mFscBleCentralApi.startScan();");
        if (mSanSwitchView.isChecked() == false){
            mSanSwitchView.setChecked(true);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerStopScan();
            }
        }, BleConfig.segmentationSleepTime);

    }

    private void stopScan() {
        //Log.e("ble", "999 stopScan;");
        if(scanning){
            scanning = false;
            mSanTipView.setLeftText(R.string.scan_tip_stopscan);
            mSanSwitchView.setChecked(false);
            mFscBleCentralApi.stopScan();
            if(timer != null){
                timer.cancel();
                timer = null;
            }
        }
    }

    private void timerStopScan() {
        if(scanning){
            scanning = false;
            mSanTipView.setLeftText(R.string.scan_time_timeout);
            mSanSwitchView.setChecked(false);
            mFscBleCentralApi.stopScan();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        if(mFscBleCentralApi != null && mFscBleCentralApi.isConnected()){
            if(scanning){
                mFscBleCentralApi.stopScan();
            }
            scanning= false;
            mFscBleCentralApi.disconnect();
        }
        // EasyLog.print("======================== 2 onPause ");
        //// EasyLog.print("======================== 2 mBleApi.isConnected()" + mFscBleCentralApi.isConnected());
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // EasyLog.print("======================== 2 onResume ");
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        bindMac = mMmkv.decodeStringSet(MMKVUtils.DEVICEBINDMACLIST);
        if(bindMac == null){
            bindMac = new HashSet<>();
        }
        if(deviceListAdapter !=null){
            deviceListAdapter.setBindMac(bindMac);
            if(mFscBleCentralApi ==null){
                mFscBleCentralApi= FscBleCentralApiImp.getInstance(this);
                mFscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp(){
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void blePeripheralFound(FscDevice device, int rssi, byte[] record) {
                        boolean isBind = false;
                        if(bindMac!= null&& bindMac.contains(device.getAddress())){
                            isBind = true;
                        }
                        // EasyLog.print("======================== blePeripheralFound: "  + device.getName() + isBind);
                        deviceListAdapter.update(device, isBind,isTest);
                    }

                    @Override
                    public void startScan() {
                        super.startScan();
                    }

                    @Override
                    public void stopScan() {
                        super.stopScan();
                    }
                });
                if(scanning){
                    startScan();
                }
            }else{
                mFscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp(){
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void blePeripheralFound(FscDevice device, int rssi, byte[] record) {
                        boolean isBind = false;
                        if(bindMac!= null&& bindMac.contains(device.getAddress())){
                            isBind = true;
                        }
                        // EasyLog.print("======================== blePeripheralFound: "  + device.getName() + isBind);
                        deviceListAdapter.update(device, isBind,isTest);
                    }

                    @Override
                    public void startScan() {
                        super.startScan();
                    }

                    @Override
                    public void stopScan() {
                        super.stopScan();
                    }
                });
                if(scanning){
                    startScan();
                }
            }
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // EasyLog.print("======================== onDestroy ");
        mResultLauncher = null;
        unregisterReceiver(mBluetoothStateBroadcastReceiver);
        // 关闭定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        listView.setAdapter(null);
        deviceListAdapter = null;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(newBase));
    }
}