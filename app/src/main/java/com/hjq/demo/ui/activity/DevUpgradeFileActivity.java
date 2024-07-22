package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.feasycom.ble.controler.FscBleCentralApi;
import com.feasycom.ble.controler.FscBleCentralApiImp;
import com.feasycom.ble.controler.FscBleCentralCallbacksImp;
import com.feasycom.common.bean.ConnectType;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.GetOtaPackageList;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.http.model.OtaPackage;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.ui.adapter.OtaPackageAdapter;
import com.hjq.demo.ui.adapter.TabAdapter;
import com.hjq.demo.ui.dialog.AddressDialog;
import com.hjq.demo.util.ByteUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.widget.layout.RoundSettingBar;
import com.hjq.widget.view.SubmitButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 查询可用的更新包文件
 */
public final class DevUpgradeFileActivity extends AppActivity  {

    private static final String INTENT_KEY_IN_DEVICEUPGRADE = "deviceupgrade";
    private static final String INTENT_KEY_IN_DEVICEVERSIONDC = "dcversion";
    private static final String INTENT_KEY_IN_DEVICEVERSIONLLC = "llcversion";
    private static final String INTENT_KEY_IN_DEVICEVERSIONPFC = "pfcversion";
    private static final String INTENT_KEY_IN_DEVICECODEDC = "dccode";
    private static final String INTENT_KEY_IN_DEVICECODELLC = "llccode";
    private static final String INTENT_KEY_IN_DEVICECODEPFC = "pfccode";
    private static final String INTENT_KEY_IN_DEVICEBATTERY = "battery";
    private static final String INTENT_KEY_IN_DEVICEVOLTAGELEVEL = "voltagelevel";
    private DeviceBind model;
    private ActivityResultLauncher<Intent> mResultLauncher;

    private AppCompatRadioButton mDcView;
    private AppCompatRadioButton mLlcView;
    private AppCompatRadioButton mPfcView;
    private SubmitButton mCheckUpgradeView;
    private RecyclerView mOtaPagView;
    private OtaPackageAdapter mOtaPackageAdapter;
    private List<OtaPackage> otaPackageList;
    // 用记录选择控件板的值dc,llc, pfc
    private String selectTag;
    // 用于记录检查更新按键，点击后为True,其他按钮复位状态
    private boolean isfinish ;
    private boolean iserror;

    private String dcVersion;
    private String llcVersion;
    private String pfcVersion;
    private String selectCurrentVer;
    private String dcchipcode;
    private String pfcchipcode;
    private String llcchipcode;
    private String batterycell;
    private String chipcode;
    private String voltagelevel;



    public static void start(Context context, DeviceBind model, String dcVer, String  llcVer, String pfcVer,String dcchipcode,String pfcchipcode,String llcchipcode,String batterycell, String voltagelevel) {
        Intent intent = new Intent(context, DevUpgradeFileActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICEUPGRADE, model);
        intent.putExtra(INTENT_KEY_IN_DEVICEVERSIONDC , dcVer);
        intent.putExtra(INTENT_KEY_IN_DEVICEVERSIONLLC , llcVer);
        intent.putExtra(INTENT_KEY_IN_DEVICEVERSIONPFC , pfcVer);
        intent.putExtra(INTENT_KEY_IN_DEVICECODEDC , dcchipcode);
        intent.putExtra(INTENT_KEY_IN_DEVICECODEPFC , pfcchipcode);
        intent.putExtra(INTENT_KEY_IN_DEVICECODELLC , llcchipcode);
        intent.putExtra(INTENT_KEY_IN_DEVICEBATTERY , batterycell);
        intent.putExtra(INTENT_KEY_IN_DEVICEVOLTAGELEVEL , voltagelevel);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_upgrade_file_activity;
    }

    @Override
    protected void initView() {
        mCheckUpgradeView = findViewById(R.id.btn_checkrefresh);
        mDcView = findViewById(R.id.rb_dc);
        mLlcView = findViewById(R.id.rb_llc);
        mPfcView = findViewById(R.id.rb_pfc);
        mDcView.setChecked(true);
        mLlcView.setChecked(false);
        mPfcView.setChecked((false));
        isfinish = false;
        iserror = false;
        mOtaPagView = findViewById(R.id.rv_otapackage);
        mOtaPackageAdapter = new OtaPackageAdapter(this);
        mOtaPackageAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                // 进入升级页面
                UpgradeActivity.start(getContext(), model, otaPackageList.get(position), selectCurrentVer);
            }
        });

        mOtaPagView.setAdapter(mOtaPackageAdapter);
        setOnClickListener(mCheckUpgradeView, mDcView, mLlcView, mPfcView);

    }

    @Override
    protected void initData() {
        selectTag = "DC";
        model = getParcelable(INTENT_KEY_IN_DEVICEUPGRADE);
        dcVersion = getString(INTENT_KEY_IN_DEVICEVERSIONDC);
        llcVersion = getString(INTENT_KEY_IN_DEVICEVERSIONLLC);
        pfcVersion = getString(INTENT_KEY_IN_DEVICEVERSIONPFC);
        dcchipcode = getString(INTENT_KEY_IN_DEVICECODEDC);
        pfcchipcode = getString(INTENT_KEY_IN_DEVICECODEPFC);
        llcchipcode = getString(INTENT_KEY_IN_DEVICECODELLC);
        batterycell = getString(INTENT_KEY_IN_DEVICEBATTERY);
        voltagelevel = getString(INTENT_KEY_IN_DEVICEVOLTAGELEVEL);
        otaPackageList = new ArrayList<>();

        selectCurrentVer = dcVersion;
        // EasyLog.print("========================11sdcVersion " + dcVersion);
        // EasyLog.print("========================11selectCurrentVer " + selectCurrentVer);
        if(model == null){
            return;
        }



    }
    @SingleClick
    @Override
    public void onClick(View view) {
        // 点击开始检查更新包
        if (view == mCheckUpgradeView) {
            String chipcodetemp = "";
            if(selectTag.equals("LLC") && chipcode.equals("A155")){
                chipcodetemp= "D149";
            }else if(selectTag.equals("PFC") && chipcode.equals("A155")){
                chipcodetemp= "D127";
            }else{
                chipcodetemp = chipcode;
            }
            //UpgradeActivity.start(this, model);
            // EasyLog.print( "======================== selectTag" + selectTag);
            // EasyLog.print( "======================== voltagelevel" + voltagelevel);
            // 查询更新包
            EasyHttp.post(this)
                    .api(new GetOtaPackageList()
                            .setTag(selectTag)
                            .setChipcode(chipcodetemp)
                            .setBatterycell(batterycell)
                            .setVoltagelevel(voltagelevel)
                            .setDevname(model.getDevicename())

                    )
                    .request(new HttpCallback<HttpListData<OtaPackage>>(this) {

                        @Override
                        public void onSucceed(HttpListData<OtaPackage> data) {
                            if(data.getCode() == 200){
                                if(data.getData().getTotal()>0){
                                    otaPackageList = data.getData().getItems();
                                    mCheckUpgradeView.showSucceed();
                                    isfinish = true;
                                    //更新列表
                                    updateList(otaPackageList);
                                }else{
                                    mCheckUpgradeView.reset();
                                    otaPackageList = data.getData().getItems();
                                    updateList(otaPackageList);
                                    mCheckUpgradeView.setText(R.string.update_no_update);
                                    iserror = true;
                                }


                            }else {
                                mCheckUpgradeView.reset();
                                otaPackageList = new ArrayList<>();
                                updateList(otaPackageList);
                                mCheckUpgradeView.setText(R.string.update_no_update);
                                iserror = true;
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            //super.onFail(e);
                            mCheckUpgradeView.clearAnimation();
                            mCheckUpgradeView.setText(R.string.update_no_update);

                            iserror = true;
                        }
                    });
        }
        // 控制单选按钮选择
        else if(view == mDcView){
            selectTag = "DC";
            chipcode = dcchipcode;
            mLlcView.setChecked(false);
            mPfcView.setChecked((false));
            selectCurrentVer = dcVersion;
            if(otaPackageList.size() >0){
                otaPackageList.clear();
                updateList(otaPackageList);
            }

            // 复位mCheckUpgradeView 状态
            if(isfinish){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                isfinish = false;
            }
            if(iserror){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                iserror = false;
            }

        }else if(view == mLlcView){
            selectTag = "LLC";
            chipcode = llcchipcode;
            mDcView.setChecked(false);
            mPfcView.setChecked((false));
            selectCurrentVer = llcVersion;
            if(otaPackageList.size() >0){
                otaPackageList.clear();
                updateList(otaPackageList);
            }
            // 复位mCheckUpgradeView 状态
            if(isfinish){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                isfinish = false;
            }
            if(iserror){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                iserror = false;
            }
        }else if(view == mPfcView){
            selectTag = "PFC";
            chipcode = pfcchipcode;
            mLlcView.setChecked(false);
            mDcView.setChecked(false);
            selectCurrentVer = pfcVersion;
            if(otaPackageList.size() >0){
                otaPackageList.clear();
                updateList(otaPackageList);
            }
            // 复位mCheckUpgradeView 状态
            if(isfinish){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                isfinish = false;
            }
            if(iserror){
                mCheckUpgradeView.reset();
                mCheckUpgradeView.setText(R.string.checkrefresh);
                iserror = false;
            }

        }

    }

    /**
     * 更新列表
     * @param otaPackageList
     */
    public void updateList(List<OtaPackage> otaPackageList){
        mOtaPackageAdapter.clearData();
        mOtaPackageAdapter.addData(otaPackageList);
        mOtaPackageAdapter.notifyDataSetChanged();
    }








}