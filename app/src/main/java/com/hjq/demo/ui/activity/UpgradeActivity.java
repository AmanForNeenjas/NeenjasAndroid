package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
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
import androidx.lifecycle.LifecycleOwner;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.ConnectType;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;

import com.hjq.demo.http.api.SetDeviceUpgradeTwoApi;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.OtaPackage;
import com.hjq.demo.ui.dialog.MessageDialog;

import com.hjq.demo.util.DateUtil;
import com.hjq.demo.util.FileParseUtil;
import com.hjq.demo.util.FormatUtil;
import com.hjq.demo.util.RecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.view.CircleProgressView;
import com.hjq.widget.view.SubmitButton;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 *    desc   : 设备升级页
 */
public final class UpgradeActivity extends AppActivity {

    private static final String INTENT_KEY_IN_DEVICEUPGRADE = "deviceupgrade";
    private static final String INTENT_KEY_IN_DEVICEOTAPACKAGE = "otapackage";
    private static final String INTENT_KEY_IN_DEVICECURRVER = "currentversion";
    private DeviceBind model;
    private OtaPackage otaPackage;
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
    private Handler mMainHandler = new Handler();
    private int readType;

    //以防退出界面后Handler还在执行
    private boolean isUpdateInfo;
    //用以表示该handler的常熟
    private static final int MSG_UPDATE_INFO = 0x110;
    // 进入boot模式
    private static final int MSG_UPDATE_BOOT = 0x001;
    // 清除程序
    private static final int MSG_UPDATE_ERASE = 0x011;
    // 下载Bin文件
    private static final int MSG_UPDATE_DOWN = 0x111;
    // 通知
    private static final int MSG_UPDATE_FINISH = 0x101;
    // 复位
    private static final int MSG_UPDATE_RESET = 0x100;
    //初始地址
    private static final int MSG_LOAD_FIRST =0X000;

    // 升级阶段 1 下载文件  3 读bin文件 5 进入boot模式 6 下载Bin文件  8 通知  9 复位  进度按 step*10%
    private int step;


    // 设备板卡类型，根据bin文件来识别
    private int target ;
    // bin文件名
    private String filename;

    // 每次发送的字节
    private byte[] sendBytes;
    private byte[] realBuffer ;
    private int realBufferLen;
    private int sendLen;
    // bin文件数据分页数
    private int pagenum;
    private int pagenumEraseCount;
    // 发送boot指令结果
    private boolean bootResult;

    // 出错时循环发送次数
    private int timeerLoopcount ;

    // 发送Load指令时 记录的下标
    private int loadi;
    private int loadj;


    // //给PFC或LLC升级 target == "53") baseAddress = 0x3f0000;// was 0x003f0002
    //  target == "52" baseAddress = 0x84000;
    private int baseAddress;
    private StringBuffer firstData;//初始地址内容
    // 给PFC或LLC升级 下载每帧程序循环变量  8次
    private int loadinloop;

    private SubmitButton mUpgradeStartView;
    // 当前版本
    private TextView mCurrentView;
    // 新版本
    private TextView mNewView;
    // 进度条
    private CircleProgressView circleProgressView;
    // 进度条文字
    private TextView mProgressstrView;

    // 升级结果文字
    private TextView mResultTipView;

    // 多次阶段接收数据
    private byte [] revarray;
    private int receivenum;

    private int loopcount;	//从0开始计数，每运行一次timertask次数加一，运行制定次数后结束。
    // 设置发送重发定时器
    private Timer timer;
    // 定时器
    TimerTask task;
    String starttime;
    String endtime;

    private String selectCurrentVer;

    public static void start(Context context, DeviceBind model, OtaPackage otaPackage,String version) {
        Intent intent = new Intent(context, UpgradeActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICEUPGRADE, model);
        intent.putExtra(INTENT_KEY_IN_DEVICEOTAPACKAGE, otaPackage);
        intent.putExtra(INTENT_KEY_IN_DEVICECURRVER, version);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_upgrade_activity;
    }

    @Override
    protected void initView() {
        mUpgradeStartView = findViewById(R.id.btn_upgrate_start);
        // 先不显示出来，等连接建立好了再显示
        mUpgradeStartView.setVisibility(View.GONE);
        mCurrentView = findViewById(R.id.tv_currentversion);
        mNewView = findViewById(R.id.tv_newversion);
        circleProgressView = findViewById(R.id.cpv_upgrade);
        mProgressstrView = findViewById(R.id.tv_upgrade);
        mResultTipView = findViewById(R.id.tv_resulttip);
        setOnClickListener(mUpgradeStartView);

    }

    @Override
    protected void initData() {
        model = getParcelable(INTENT_KEY_IN_DEVICEUPGRADE);
        otaPackage = getParcelable(INTENT_KEY_IN_DEVICEOTAPACKAGE);
        selectCurrentVer = getString(INTENT_KEY_IN_DEVICECURRVER);
        mCurrentView.setText(selectCurrentVer);
        //// EasyLog.print("========================11selectCurrentVer " + selectCurrentVer);
        mNewView.setText(otaPackage.getVersion() + otaPackage.getSubversion());
        circleProgressView.setVisibility(View.GONE);
        mProgressstrView.setVisibility(View.GONE);
        //firstData= new StringBuffer();
        if(model == null || otaPackage == null){
            return;
        }

        connected = false;
        step = 0;
        loopcount = 0;
        timeerLoopcount = 0;
        revarray = new byte[]{};
        receivenum = 0;
        starttime = "";
        endtime ="";
        //// EasyLog.print("========================11start");
        mBleApi = FscBleCentralApiImp.getInstance();
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
    @SingleClick
    @Override
    public void onClick(View view) {
        // 点击开始升级
        if (view == mUpgradeStartView) {
            if(connected ){

                new MessageDialog.Builder(getActivity())
                        // 标题可以不用填写
                        .setTitle(R.string.upgrate_alter)
                        // 内容必须要填写
                        .setMessage(R.string.upgrate_alter_tip)
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mResultTipView.setText(R.string.upgrate_alter_tip);
                                    }});
                                // 去升级
                                starttime = DateUtil.nowDateToString();
                                DownloadDevBinFile();

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mUpgradeStartView.reset();
                                    }});

                            }
                        })
                        .show();
            }else {
                toast(R.string.outconnected);
                postDelayed(() -> {
                    mUpgradeStartView.showError(3000);
                }, 1000);
            }
        }
    }

    /**
     * 设置进度
     * @param progress
     */
    private void setUpgradeProgress(int progress){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleProgressView.setProgress(progress,3);
                mProgressstrView.setText(progress+ "%");
            }
        });
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

        setBleCallBack();

        // EasyLog.print("========================start");
        if(model !=null){
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
        connected = true;
        //toast(R.string.update_ready);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //sendButton.setEnabled(false);
                //sendFileButton.setEnabled(false);

                //android.util.Log.e(TAG, "uiDeviceDisconnected: false 1"  );
                mUpgradeStartView.setVisibility(View.VISIBLE);
            }
        });

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
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }
        if(mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        mBleApi = null;
        model = null;
        //connected = false;
        mResultLauncher = null;
        //threadSucceedQuit();
        //取消定时发送任务


    }

    @Override
    protected void onPause() {
        super.onPause();
        /*step =0;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task= null;
        }
        if(mThreadHandler != null) {
            mThreadHandler.removeMessages(MSG_UPDATE_INFO);
        }
        if(mBleApi != null && mBleApi.isConnected()){
            mBleApi.disconnect();
        }
        connected = false;*/
        // EasyLog.print("======================== 2 onPause ");
        // EasyLog.print("======================== 2 mBleApi.isConnected()" + mBleApi.isConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //step = 0;
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
        /*if(mBleApi == null){
            mBleApi = FscBleCentralApiImp.getInstance();
        }
        checkBluetooth();*/
        if(mBleApi.isConnected()){
            connected = true;
            setBleCallBack();
            mUpgradeStartView.setVisibility(View.VISIBLE);

        }else{
            connected = false;
            checkBluetooth();
        }
    }

    private void threadQuit(){
        endtime = DateUtil.nowDateToString();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            task= null;
        }

        if(mThreadHandler != null) {
            postDelayed(() -> {
                mUpgradeStartView.showError(3000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mResultTipView.setText(R.string.upgrade_error);
                    }});
            }, 1000);
            mHandlerThread.quit();
        }else{
            mUpgradeStartView.showSucceed();
        }

    }
    private void threadSucceedQuit(){
        cancelTime();
        if(mThreadHandler != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResultTipView.setText(R.string.update_finish);
                }});
            mHandlerThread.quit();

        }
    }

    /**
     * 每一步 从服务器下载用户选择的bin文件
     */
    private void DownloadDevBinFile(){
        //filename = "PFCXX_YYY_D127_V100_S00802.bin";
        //md5str = "b91dbb67ccf358824e3cc664e19daa96";
        //filename = "DCXXX_YYY_D127_V100_S00803.bin";
        //String md5str = "d49cf589baf7266bdbd7be0826171795";
        circleProgressView.setVisibility(View.VISIBLE);
        mProgressstrView.setVisibility(View.VISIBLE);
        setUpgradeProgress(0);

        filename = otaPackage.getTitle();
        String md5str = otaPackage.getChecksum();
        // 检查文件名是否满足规范要求
        target = FileParseUtil.checkBinFile(filename);
        if(!(target == 54  || target == 51 || target ==52  || target == 53)){
            // EasyLog.print("======================== target：" + target );
            toast(R.string.binfile_error);
            return ;
        }
        // 1 下载文件
        step = 1;
        setUpgradeProgress(10);
        // 如果是放到外部存储目录下则需要适配分区存储
//            String fileName = "微信 8.0.15.apk";
//
//            File file;
//            Uri outputUri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // 适配 Android 10 分区存储特性
//                ContentValues values = new ContentValues();
//                // 设置显示的文件名
//                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
//                // 生成一个新的 uri 路径
//                outputUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
//                file = new FileContentResolver(getContentResolver(), outputUri, fileName);
//            } else {
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//            }

        // 如果是放到外部存储的应用专属目录则不需要适配分区存储特性
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);

        EasyHttp.download(this)
                .method(HttpMethod.GET)
                .file(file)

                .url(otaPackage.getUrl())
                .md5(md5str)
                .listener(new OnDownloadListener() {

                    @Override
                    public void onStart(File file) {
                        //mProgressBar.setProgress(0);
                        //mProgressBar.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onProgress(File file, int progress) {
                        //mProgressBar.setProgress(progress);
                    }

                    @Override
                    public void onComplete(File file) {
                        //Toaster.show("下载完成：" + file.getPath());
                        // EasyLog.print("======================== 下载完成：" + file.getPath() );
                        //3 读bin文件
                        step = 3;
                        setUpgradeProgress((step-2)* 10);
                        ReadBinFile(file);
                    }

                    @Override
                    public void onError(File file, Exception e) {
                        toast(R.string.binfile_error_md5);
                        //if (e instanceof FileMD5Exception) {
                        //    // 如果是文件 md5 校验失败，则删除文件
                        //   file.delete();
                        //}
                    }

                    @Override
                    public void onEnd(File file) {
                        //mProgressBar.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * 每二步 读取Bin文件
     * @param file
     */
    private void ReadBinFile(final File file) {
        try {

            // 读取文件为字节流
            ByteBuffer byteBuffer = FileParseUtil.parseBinFile(file);
            // 去掉多余的零并且数据对齐
            realBuffer = FileParseUtil.clearExtraZeroAndMore(byteBuffer.array(), target);
            realBufferLen = realBuffer.length;
            // 计算发送次数
            pagenum = (realBufferLen - realBufferLen % 2048)/2048 + 1;
            realBufferLen = realBuffer.length;
            sendLen = 0;
            // 4  进入boot模式
            step =4;
            setUpgradeProgress((step-2)* 10);
            /*
            if(target == 51){
                // 执行下一步 清除程序  根据分页大小需要
                pagenumEraseCount = 0;
            }else if(target == 53 || target == 52){
                //给PFC或LLC  可以重试4次
                loopcount = 0;
            }
            step = 5;
            */


            initThreadBin();
            //运行线程
            // 为了等待蓝牙与串口通信通道建立起来延时3秒执行。
            postDelayed(() -> {
                mThreadHandler.sendEmptyMessage(MSG_UPDATE_BOOT);
            }, 100);
            //mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);

        }catch (Exception exception){
            // EasyLog.print("======================== error" + exception.toString());
        }
    }




    /**
     * 初始化读取设备信息 相关的Thread
     */
    private void initThreadBin() {
        mHandlerThread = new HandlerThread("check-message-coming");
        mHandlerThread.start();
        //// EasyLog.print("======================== initThread");
        mThreadHandler = new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {

                //// EasyLog.print("======================== handleMessage" + msg.what);
                switch (msg.what){
                    // 进入boot模式
                    case MSG_UPDATE_BOOT:
                        bootSend();
                        break;
                    // 清除程序
                    case MSG_UPDATE_ERASE:
                        eraseSend();
                        break;
                    // 下载Bin文件
                    case MSG_UPDATE_DOWN:
                        loadSend();
                        break;
                    // 下载Bin文件 初始地址内容
                    case MSG_LOAD_FIRST:
                        loadSendFirstData();
                        break;
                    // 通知
                    case MSG_UPDATE_FINISH:
                        notifySend();
                        break;
                    // 复位
                    case MSG_UPDATE_RESET:
                        resetSend();
                        break;

                }

            }
        };
    }

    /**
     * 进入boot模式
      */
    private void bootSend(){
        if(target == 51){
            String sendStr =target + "10" + RecodeUtil.byte8HexString(realBuffer.length)+ "0" + filename.substring(16, 19) + filename.substring(22,26) + filename.substring(10, 14) + "0000000000000000";
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }else if( target == 53 || target == 52){
            String sendStr =target + "10" + RecodeUtil.byte8HexString(realBuffer.length)+ "0" + filename.substring(16, 19) + filename.substring(22,26) + filename.substring(10, 14) + "0000000000000000";

            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }
        sendDataTimerTask();
    }



    /**
     * 发送进入 boot模式 接收设备反馈信息
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void bootReceiv(String address, String strValue, String hexString, byte[] data){
        //// EasyLog.print("========================bootReceiv  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
        if(receivenum == 0){
            receivenum ++;
            revarray = data;
            cancelTime();
            timeerLoopcount = 0;
            int delaytime = 200;
            if(target == 53) {
                delaytime = 1000;
            }else if (target == 52) {
                delaytime = 400;
            }
            postDelayed(() -> {
                receivenum =0;
                //取消定时发送任务


                int result = 0;
                if(target == 51){
                    result = RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Check", 16);
                }else if ( target == 53 || target == 52){
                    result = RecodeUtil.readDecodeUpgade(revarray, sendBytes, "PFCCheck", 24);
                }

                if(result == 1){

                    loopcount = 0;
                    //// EasyLog.print("========================boot  succeed");
                    bootResult = true;
                    if(target == 51){
                        // 执行下一步 清除程序  根据分页大小需要
                        pagenumEraseCount = 0;
                    }else if(target == 53 || target == 52){
                        //给PFC或LLC  可以重试4次
                        loopcount = 0;
                    }
                    // EasyLog.print("=======================boot  succeed");
                    step = 5;
                    setUpgradeProgress((step-2)* 10);
                    mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                }else{
                    // EasyLog.print("========================boot  result: " + result);
                    if(loopcount > 6){
                        loopcount = 999;
                        // EasyLog.print("========================bootReceiv  threadQuit()  loopcount -> " + loopcount);
                        threadQuit();
                    }
                    else{
                        loopcount++;
                        // EasyLog.print("========================bootReceiv  loopcount -> " + loopcount);
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_BOOT);
                    }
                }
            }, delaytime);
        }else{
                revarray = RecodeUtil.byteMergerAll(revarray, data);
        }

    }

    /**
     * 进入erase模式
     */
    private void eraseSend(){
        if(target == 51){
            String sendStr = "513002" + RecodeUtil.byte4HexString(pagenumEraseCount) + "0000";
            //pagenumEraseCount ++ ;
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());


            // 发送
            //mBleApi.send(sendBytes);
        }else if( target == 53 || target == 52){
            String sendStr = target + "300000000000";
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());

        }
        sendDataTimerTask();
    }

    private void sendDataTimerTask(){

        // 发送
        //mBleApi.send(sendBytes);

        timeerLoopcount = 0;
        if(loopcount>0){
            timeerLoopcount = loopcount;
        }
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {

                if((timeerLoopcount < 6 ) &&( connected == true)){
                    if(sendBytes!=null && sendBytes.length>0){
                        // 发送
                        mBleApi.send(sendBytes);
                    }

                }else{
                    // EasyLog.print("========================step->" + step+ "  timeerLoopcount-> " + timeerLoopcount);
                    if(timer !=null ){
                        timeerLoopcount = 0;
                        timer.cancel();
                        threadQuit();
                        return;
                    }
                }
                timeerLoopcount++;

            }
        };


        // 定义开始等待时间  --- 等待 5 秒
        // 1000ms = 1s
        long delay = 0;
        // 定义每次执行的间隔时间
        long intevalPeriod = 6000;
        // schedules the task to be run in an interval
        // 安排任务在一段时间内运行
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }
    private void cancelTime(){
        //取消定时发送任务
        if(timer != null){
            timer.cancel();
            timer = null;

        }
        if(task != null){

            task.cancel();
            task = null;
        }

    }

    /**
     * 发送进入erase模式 接收设备反馈信息
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void eraseReceiv(String address, String strValue, String hexString, byte[] data){
        //// EasyLog.print("========================eraseReceiv  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
        if(receivenum == 0){
            receivenum ++;
            revarray = data;
            cancelTime();
            timeerLoopcount = 0;
            if(revarray.length== 9){
                receivenum =0;
                //取消定时发送任务

                if( target == 51){

                    if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Erase", 9) == 1 )
                    {
                        //// EasyLog.print("========================erase  succeed " + pagenumEraseCount  + "/" + pagenum);
                        pagenumEraseCount ++ ;
                        if(pagenumEraseCount< pagenum){
                            setUpgradeProgress((step-2)* 10+ (pagenumEraseCount*10/pagenum));
                            // 执行下一步 清除程序
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                        }else {
                            // 执行下一步 下载程序
                            // EasyLog.print("=======================erase  succeed");
                            step = 6;
                            //初始化下标
                            loadi = 0;
                            loadj = 0;
                            setUpgradeProgress((step-2)* 10);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }
                    }else{
                        // EasyLog.print("========================erase  error " + pagenumEraseCount  + "/" + pagenum);
                        //threadQuit();
                        if(loopcount > 6){
                            loopcount = 999;
                            // EasyLog.print("========================erase Receiv  threadQuit() loopcount -> " + loopcount);
                            threadQuit();
                            return;
                        }
                        else{
                            loopcount++;
                            // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                        }
                    }


                }else if ( target == 53 || target == 52){

                    if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Erase", 9) == 1){
                        loopcount = 0;
                        // EasyLog.print("=======================erase  succeed");
                        // 执行下一步 下载程序
                        step = 6;
                        //初始化下标
                        loadi = 0;
                        loadj = 0;
                        loadinloop = 0;
                        if(target == 53) {
                            baseAddress = 0x3f0000;// was 0x003f0002
                        } else {
                            baseAddress = 0x84000;
                        }
                        /*
                        新增20230325
                         */
                        /////////////////////////////
                        //firstData = new StringBuffer();
                        //for(loadj =loadi; firstData.length()<128; loadj++){
                        //    firstData.append(RecodeUtil.byte2HexString(realBuffer[loadj]));
                        //}
                        //baseAddress = baseAddress + (loadj - loadi)/2;
                        baseAddress = baseAddress + 32;
                        loadi = loadj = 64;
                        /////////////////////////////////

                        setUpgradeProgress((step-2)* 10);
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                    }else{
                        if(loopcount > 6){
                            loopcount = 999;
                            // EasyLog.print("========================erase Receiv  threadQuit() loopcount -> " + loopcount);
                            threadQuit();
                            return;
                        }
                        else{
                            loopcount++;
                            // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                        }
                    }

                }
            }else{
                postDelayed(() -> {
                    receivenum =0;
                    //取消定时发送任务

                    if( target == 51){

                        if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Erase", 9) == 1 )
                        {
                            //// EasyLog.print("========================erase  succeed " + pagenumEraseCount  + "/" + pagenum);
                            pagenumEraseCount ++ ;
                            if(pagenumEraseCount< pagenum){
                                setUpgradeProgress((step-2)* 10+ (pagenumEraseCount*10/pagenum));
                                // 执行下一步 清除程序
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                            }else {
                                // 执行下一步 下载程序
                                // EasyLog.print("=======================erase  succeed");
                                step = 6;
                                //初始化下标
                                loadi = 0;
                                loadj = 0;
                                setUpgradeProgress((step-2)* 10);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                            }
                        }else{
                            // EasyLog.print("========================erase  error " + pagenumEraseCount  + "/" + pagenum);
                            //threadQuit();
                            if(loopcount > 6){
                                loopcount = 999;
                                // EasyLog.print("========================erase Receiv  threadQuit() loopcount -> " + loopcount);
                                threadQuit();
                                return;
                            }
                            else{
                                loopcount++;
                                // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                            }
                        }


                    }else if ( target == 53 || target == 52){

                        if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Erase", 9) == 1){
                            loopcount = 0;
                            // EasyLog.print("=======================erase  succeed");
                            // 执行下一步 下载程序
                            step = 6;
                            //初始化下标
                            loadi = 0;
                            loadj = 0;
                            loadinloop = 0;
                            if(target == 53) {
                                baseAddress = 0x3f0000;// was 0x003f0002
                            } else {
                                baseAddress = 0x84000;
                            }
                        /*
                        新增20230325
                         */
                            /////////////////////////////
                            //firstData = new StringBuffer();
                            //for(loadj =loadi; firstData.length()<128; loadj++){
                            //    firstData.append(RecodeUtil.byte2HexString(realBuffer[loadj]));
                            //}
                            //baseAddress = baseAddress + (loadj - loadi)/2;
                            baseAddress = baseAddress + 32;
                            loadi = loadj = 64;
                            /////////////////////////////////

                            setUpgradeProgress((step-2)* 10);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }else{
                            if(loopcount > 6){
                                loopcount = 999;
                                // EasyLog.print("========================erase Receiv  threadQuit() loopcount -> " + loopcount);
                                threadQuit();
                                return;
                            }
                            else{
                                loopcount++;
                                // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                            }
                        }

                    }
                    // 测试100毫秒不行，200毫秒可以的。
                }, 400);
            }

        }else{
            revarray = RecodeUtil.byteMergerAll(revarray, data);
        }

    }

    /**
     * 进入load模式
     */
    private void loadSend(){
        if(target == 51){
            /*StringBuffer sendStringBuff =new StringBuffer();
            for (loadj = loadi; (sendStringBuff.length() < 128) && (loadj < realBuffer.length); loadj++){
                sendStringBuff.append(RecodeUtil.byte2HexString(realBuffer[loadj]));
            }
            Integer baseLoadAddr = 134479872 + loadi;
            String sendStr = target + "32" + RecodeUtil.byte8HexString(baseLoadAddr) + sendStringBuff.toString();*/
            /*

            Arrays.copyOfRange(T[] original, int form, int to)是Arrays类提供的一个静态方法,用来复制数组的一部分。
            其中original代表要操作的数组，from代表数组下标起始位置，to代表数组下标结束位置。
            此方法是复制数组original中下标从from到to的元素（不包括下标to），返回一个新数组。
            原文链接：https://blog.csdn.net/weixin_42047723/article/details/94325173
             */
            loadj = loadi;
            if((loadj+64) < realBuffer.length){
                loadj =  loadj+ 64;
            }else{
                loadj =  realBuffer.length;
            }
            //FormatUtil.bytesToHexString

            Integer baseLoadAddr = 134479872 + loadi;
            String sendStr = target + "32" + RecodeUtil.byte8HexString(baseLoadAddr) + FormatUtil.bytesToHexStringNoSpace(Arrays.copyOfRange(realBuffer, loadi,loadj));
            //// EasyLog.print("========================loadi:" + loadi  + "     sendStr:" + sendStr);
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        } else if( target == 53 || target == 52){

            /*
            String datastr = "";
            for (loadj = loadi, datastr = ""; (datastr.length() <128) && (loadj < realBuffer.length); loadj++){
                datastr += RecodeUtil.byte2HexString(realBuffer[loadj]);
            }
            String sendStr = target + "32" + RecodeUtil.byte8HexString(baseAddress) + datastr ;

             */
            loadj = loadi;
            if((loadj+64) < realBuffer.length){
                loadj =  loadj+ 64;
            }else{
                loadj =  realBuffer.length;
            }
            String sendStr = target + "32" + RecodeUtil.byte8HexString(baseAddress) +  FormatUtil.bytesToHexStringNoSpace(Arrays.copyOfRange(realBuffer, loadi,loadj));
            //// EasyLog.print("========================loadi:" + loadi  + "     sendStr:" + sendStr);

            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }
        sendDataTimerTask();
    }



    /**
     * 发送进入load模式 接收设备反馈信息
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void loadReceiv(String address, String strValue, String hexString, byte[] data){
        if(receivenum == 0){
            receivenum ++;
            revarray = data;
            cancelTime();
            timeerLoopcount = 0;
            if(revarray.length == 9){
                receivenum =0;
                //// EasyLog.print("=========================LoadReceiv  revarray.length:"+ revarray.length);
                //// EasyLog.print("=========================LoadReceiv  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
                if(target == 51){
                    if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Load", 9) == 1){
                        loadi = loadj;
                        if(loadi < realBuffer.length){
                            //// EasyLog.print("========================Load  succeed  and next Load" + loadi  + "/" + realBuffer.length);
                            setUpgradeProgress((step-2)* 10+ (loadi*40/realBuffer.length));
                            // 执行下一步 下载程序
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }else {
                            // EasyLog.print("========================Load  succeed " + loadi  + "/" + realBuffer.length);
                            // 执行下一步 通知程序
                            step = 9;
                            //初始化下标
                            setUpgradeProgress(step* 10);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                            //初始化下标
                            step = 10;
                            setUpgradeProgress(step* 10);
                            postDelayed(() -> {
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_RESET);

                            }, 1000);
                            //step = 10;
                            //setUpgradeProgress(step* 10);
                        }
                    }else{
                        // EasyLog.print("========================load  error " );
                        //threadQuit();
                        loadj = loadi;
                        if(loopcount > 6 ){
                            loopcount = 999;
                            // EasyLog.print("========================load  error " );
                            threadQuit();
                            return;
                        }
                        else{
                            loopcount++;
                            // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }
                    }
                }else if  (  target == 53 || target == 52){
                    if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "PFCLoad", 9) == 1){
                        baseAddress = baseAddress + (loadj - loadi)/2;
                        loadi = loadj;
                        if(loadi < realBuffer.length){
                            //// EasyLog.print("========================Load  succeed  and next Load" + loadi  + "/" + realBuffer.length);

                            setUpgradeProgress((step-2)* 10+ (loadi*40/realBuffer.length));
                            // 执行下一步 下载程序
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }else {
                            // EasyLog.print("========================Load  succeed " + loadi  + "/" + realBuffer.length);
                            // 执行下一步 初始化程序
                            step = 8;
                            setUpgradeProgress(step* 10);
                            //初始化下标
                            mThreadHandler.sendEmptyMessage(MSG_LOAD_FIRST);


                        }
                    }else{
                        loadj = loadi;
                        if(loopcount > 6){
                            loopcount = 999;
                            // EasyLog.print("========================load  threadQuit()  error " );
                            threadQuit();
                            return;
                        }
                        else{
                            loopcount++;
                            // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                        }

                    }

                }
            }else{
                postDelayed(() -> {
                    receivenum =0;
                    //// EasyLog.print("=========================LoadReceiv  revarray.length:"+ revarray.length);
                    //// EasyLog.print("=========================LoadReceiv  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
                    if(target == 51){
                        if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "Load", 9) == 1){
                            loadi = loadj;
                            if(loadi < realBuffer.length){
                                //// EasyLog.print("========================Load  succeed  and next Load" + loadi  + "/" + realBuffer.length);
                                setUpgradeProgress((step-2)* 10+ (loadi*40/realBuffer.length));
                                // 执行下一步 下载程序
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                            }else {
                                // EasyLog.print("========================Load  succeed " + loadi  + "/" + realBuffer.length);
                                // 执行下一步 通知程序
                                step = 9;
                                //初始化下标
                                setUpgradeProgress(step* 10);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                                //初始化下标
                                step = 10;
                                setUpgradeProgress(step* 10);
                                postDelayed(() -> {
                                    mThreadHandler.sendEmptyMessage(MSG_UPDATE_RESET);

                                }, 1000);
                                //step = 10;
                                //setUpgradeProgress(step* 10);
                            }
                        }else{
                            // EasyLog.print("========================load  error "   + loadi );
                            //threadQuit();
                            loadj = loadi;
                            if(loopcount > 6){
                                loopcount = 999;
                                // EasyLog.print("========================load  error   threadQuit()" );
                                threadQuit();
                                return;
                            }
                            else{
                                loopcount++;
                                // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                            }
                        }
                    }else if  (  target == 53 || target == 52){
                        if(RecodeUtil.readDecodeUpgade(revarray, sendBytes, "PFCLoad", 9) == 1){
                            baseAddress = baseAddress + (loadj - loadi)/2;
                            loadi = loadj;
                            if(loadi < realBuffer.length){
                                //// EasyLog.print("========================Load  succeed  and next Load" + loadi  + "/" + realBuffer.length);

                                setUpgradeProgress((step-2)* 10+ (loadi*40/realBuffer.length));
                                // 执行下一步 下载程序
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                            }else {
                                //// EasyLog.print("========================Load  succeed " + loadi  + "/" + realBuffer.length);
                                // 执行下一步 初始化程序
                                step = 8;
                                setUpgradeProgress(step* 10);
                                //初始化下标
                                mThreadHandler.sendEmptyMessage(MSG_LOAD_FIRST);


                            }
                        }else{
                            // EasyLog.print("========================load  error threadQuit() " );
                            loadj = loadi;
                            if(loopcount > 6){
                                loopcount = 999;
                                // EasyLog.print("========================load  error threadQuit() "  + loadi);
                                threadQuit();
                                return;
                            }
                            else{
                                loopcount++;
                                // EasyLog.print("========================erase Receiv  loopcount -> " + loopcount);
                                mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                            }

                        }

                    }
                    // 测试100毫秒不行，200毫秒可以的。
                }, 400);
            }

        }else{
            revarray = RecodeUtil.byteMergerAll(revarray, data);
        }

    }

    private void loadSendFirstData(){
        if(  target == 53 || target == 52){
            String baseAddr ="";
            if(target == 53) baseAddr = "003f0000";// was 0x003f0002   这个地方要改一下
            else baseAddr = "00084000";
            String sendStr = target + "32" + baseAddr + FormatUtil.bytesToHexStringNoSpace(Arrays.copyOfRange(realBuffer, 0,64)) ;
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }
        sendDataTimerTask();
    }

    private void loadReceivFirstData(String address, String strValue, String hexString, byte[] data){
        if(receivenum == 0) {
            receivenum++;
            revarray = data;
            cancelTime();
            timeerLoopcount = 0;
            if(revarray.length == 9){
                //// EasyLog.print("=========================loadReceivFirstData  revarray.length:" + revarray.length);
                if (target == 53 || target == 52) {
                    if (RecodeUtil.readDecodeUpgade(revarray, sendBytes, "PFCLoad", 9) == 1) {
                        // EasyLog.print("=======================loadReceivFirstData  succeed");
                        step = 10;
                        setUpgradeProgress(step * 10);
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_RESET);


                    } else {
                        if (loopcount > 6) {
                            loopcount = 999;
                            // EasyLog.print("========================load  loadReceivFirstData  error ");
                            threadQuit();
                            return;
                        } else {
                            loopcount++;
                            // EasyLog.print("========================loadReceivFirstData  Receiv  loopcount -> " + loopcount);
                            mThreadHandler.sendEmptyMessage(MSG_LOAD_FIRST);
                        }

                    }

                }
            }else{
                postDelayed(() -> {
                    if (target == 53 || target == 52) {
                        if (RecodeUtil.readDecodeUpgade(revarray, sendBytes, "PFCLoad", 9) == 1) {

                            step = 10;
                            setUpgradeProgress(step * 10);
                            mThreadHandler.sendEmptyMessage(MSG_UPDATE_RESET);


                        } else {
                            if (loopcount > 6) {
                                loopcount = 999;
                                // EasyLog.print("========================loadReceivFirstData error ");
                                threadQuit();
                                return;
                            } else {
                                loopcount++;
                                // EasyLog.print("========================loadReceivFirstData  loopcount -> " + loopcount);
                                mThreadHandler.sendEmptyMessage(MSG_LOAD_FIRST);
                            }

                        }

                    }
                    // 测试100毫秒不行，200毫秒可以的。
                }, 400);
            }

        }else{
            revarray = RecodeUtil.byteMergerAll(revarray, data);
        }

    }

    /**
     * 进入下载传输完成notify模式  UPDATE_FINISH
     */
    private void notifySend(){
        if(target == 51){
            String sendStr = target + "666666666666" ;
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            mBleApi.send(sendBytes);
        }
    }

    /**
     * 发送进入下载传输完成notify模式 接收设备反馈信息 UPDATE_FINISH
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void notifyReceiv(String address, String strValue, String hexString, byte[] data){
        //// EasyLog.print("========================Reset notify  Receiv address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
        if(RecodeUtil.readDecodeUpgade(data, sendBytes, "Reset", 9) == 1){
            // EasyLog.print("========================Reset notify succeed");
            // 执行下一步 通知程序
            step = 9;
            //初始化下标
            mThreadHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
        }else{
            // EasyLog.print("========================Reset notify  error " );
            threadQuit();
        }
    }
    /**
     * 进入下载传输完成reset模式
     */
    private void resetSend(){
        if(target == 51){
            String sendStr = "51500200000000" ;
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }else if  (  target == 53 || target == 52){
            String sendStr = target+"500200000000";
            sendBytes = FormatUtil.BytesAddCrc(sendStr.toUpperCase());
            // 发送
            //mBleApi.send(sendBytes);
        }
        sendDataTimerTask();

        endtime = DateUtil.nowDateToString();
        postDelayed(() -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mUpgradeStartView.reset();
                            mUpgradeStartView.setVisibility(View.GONE);
                            setUpgradeProgress(100);
                        }
                    });
                    toast(R.string.update_success);
                    threadSucceedQuit();
                    setDevUpgradeLog(1);
                }, 500);



    }

    /**
     * 发送进入下载传输完成reset模式 接收设备反馈信息
     * @param address
     * @param strValue
     * @param hexString
     * @param data
     */
    private void resetReceiv(String address, String strValue, String hexString, byte[] data){
        // EasyLog.print("========================Reset Receiv address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
        if(RecodeUtil.readDecodeUpgade(data, sendBytes, "Reset", 9) == 1){
            // EasyLog.print("========================Reset succeed");
            // EasyLog.print("========================upgrade succeed==========================================");
            step = 10;
            mUpgradeStartView.showSucceed();
            threadSucceedQuit();
        }else{
            // EasyLog.print("========================Reset notify  error " );
            threadQuit();
        }
        //取消定时发送任务
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(task != null){
            task.cancel();
            timer = null;
        }
    }


    private void upGradeSuccess(){

        step = 0;
        mUpgradeStartView.showSucceed();

        threadSucceedQuit();

    }

    /**
     * 设置设备升级日志
     * @param
     */
    /*
    private void setDevUpgradeLog(int status){
        EasyHttp.post((LifecycleOwner) getActivity())
                .api(new SetDeviceUpgradeApi().setDevice_id(model.getDevice_id())
                        //.setOtapackage_id(otaPackage.getId())
                        .setOtapackage_id(otaPackage.getId())
                        .setStatus(status)
                        .setTag(otaPackage.getTag())
                        .setOtapackage(otaPackage.getTitle())
                        .setStarttime(starttime)
                        .setFinishtime(endtime)
                ).request(new HttpCallback<HttpData<String>>((OnHttpListener) getActivity()) {

                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpData<String> data) {

                    }

                    @Override
                    public void onFail(Exception e) {
                        //super.onFail(e);

                    }
                });
    }*/
    /**
     * 设置设备升级日志  20230708
     * @param
     */
    private void setDevUpgradeLog(int status){
        EasyHttp.post((LifecycleOwner) getActivity())
                .api(new SetDeviceUpgradeTwoApi().setDevice_id(model.getDevice_id())
                        //.setOtapackage_id(otaPackage.getId())
                        .setOtapackage_id(otaPackage.getId())
                        .setStatus(status)
                        .setTag(otaPackage.getTag())
                        .setOtapackage(otaPackage.getTitle())
                        .setMacaddr(model.getMacaddr())
                        .setBind_id(model.getId())
                        .setAg_id(model.getAg_id())
                        .setStarttime(starttime)
                        .setFinishtime(endtime)
                ).request(new HttpCallback<HttpData<String>>((OnHttpListener) getActivity()) {

                    @Override
                    public void onStart(Call call) {
                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpData<String> data) {

                    }

                    @Override
                    public void onFail(Exception e) {
                        //super.onFail(e);

                    }
                });
    }

    private void  setBleCallBack(){
        mBleApi.setCallbacks(new FscBleCentralCallbacksImp(){
            @Override
            public void blePeripheralConnected(BluetoothGatt gatt, String address, ConnectType tye) {
                //android.util.Log.e(TAG,"blePeripheralConnected");
                // EasyLog.print(address + "======================== 已经连接" + address);
                connected = true;
                if(step == 0){
                    uiDeviceConnected();
                }else if(step == 4){
                    // 蓝牙断了重连接
                    postDelayed(() -> {
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_BOOT);
                    }, 500);
                }else if(step == 5){
                    // 蓝牙断了重连接
                    postDelayed(() -> {
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_ERASE);
                    }, 500);
                }else if(step == 6){
                    // 蓝牙断了重连接
                    postDelayed(() -> {
                        loadj = loadi;
                        mThreadHandler.sendEmptyMessage(MSG_UPDATE_DOWN);
                    }, 500);
                }


            }

            @Override
            public void blePeripheralDisconnected(BluetoothGatt gatt, String address, int status) {
                //android.util.Log.e(TAG,"blePeripheralDisconnected");

                // EasyLog.print(address + "======================== 已经关闭");

                if((step == 8 || step ==9 ) && connected == true ){
                    upGradeSuccess();
                }
                //针对蓝牙突然中断
                else if(step != 0){
                    if(step == 4){
                        cancelTime();
                        // EasyLog.print(address + " step:"+ step+ "======================== 已经关闭");
                        mBleApi.connect(model.getMacaddr());
                        // EasyLog.print(address + " step:"+ step+ "======================== 重新连接");
                    }else if(step == 5){
                        cancelTime();
                        // EasyLog.print(address + " step:"+ step+ "======================== 已经关闭");
                        mBleApi.connect(model.getMacaddr());
                        // EasyLog.print(address + " step:"+ step+ "======================== 重新连接");
                    }else if(step == 6){
                        cancelTime();
                        // EasyLog.print(address + " step:"+ step+ "======================== 已经关闭");
                        mBleApi.connect(model.getMacaddr());
                        // EasyLog.print(address + " step:"+ step+ "======================== 重新连接");
                    }

                    else if(step == 10){
                        upGradeSuccess();
                    }


                    else{
                        cancelTime();
                        threadQuit();
                    }

                }else{
                    uiDeviceDisconnected();
                    connected = false;
                }


            }


            @Override
            public void packetReceived(String address, String strValue, String hexString, byte[] data) {
                //android.util.Log.e(TAG,"packetReceived");
                //uiReceiveData(strValue, hexString, data);
                //// EasyLog.print("======================== packetReceived  address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));


                // // 升级阶段 1 下载文件  3 读bin文件 4  进入boot模式  5 清除程序  6 下载Bin文件  8 通知  9 复位  进度按 step*10%
                if(step == 4){
                    bootReceiv(address, strValue,hexString,data);
                }else if (step == 5){
                    eraseReceiv(address, strValue,hexString,data);
                }else if (step == 6){
                    loadReceiv(address, strValue,hexString,data);
                }else if (step == 8){
                    loadReceivFirstData(address, strValue,hexString,data);
                }else if (step == 9){
                    notifyReceiv(address, strValue,hexString,data);
                }else if (step == 10){
                    resetReceiv(address, strValue,hexString,data);
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
                //// EasyLog.print("======================== packetSend address -> "+address +"\r\n strValue ->"+ strValue +"  \r\n data -> " + ByteUtil.toHexString(data));
            }

        });
    }
}