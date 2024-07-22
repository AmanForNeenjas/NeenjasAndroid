package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.LogoutApi;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.manager.CacheDataManager;
import com.hjq.demo.manager.ThreadPoolManager;
import com.hjq.demo.ui.dialog.MenuDialog;
import com.hjq.demo.util.ByteUtil;
import com.hjq.demo.util.DateUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.demo.util.RecodeUtil;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.RoundBottomSettingBar;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.hjq.widget.layout.SettingBar;
import com.hjq.widget.view.SwitchButton;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/03/01
 *    desc   : 设备信息界面
 */
public final class DevTypeInfoActivity extends AppActivity {

    private static final String INTENT_KEY_IN_DEVICETYPEINFO = "devicetypeinfo";
    private DeviceBind model;

    private RoundTopSettingBar mTypeSpecView;
    private SettingBar mMacAddressView;
    private SettingBar mIdView;
    private SettingBar mBatteryCellsView;
    private SettingBar  mVoltageLevelView;
    private SettingBar mDcVersionView;
    private SettingBar mLlcVersionView;
    private SettingBar mDccodeView;
    private SettingBar mPfccodeView;
    private SettingBar mLlccodeView;
    private RoundBottomSettingBar mPfcVersionView;
    private byte [] revarray;
    private List<String> batterycells;

    public static void start(Context context, DeviceBind model) {
        Intent intent = new Intent(context, DevTypeInfoActivity.class);
        intent.putExtra(INTENT_KEY_IN_DEVICETYPEINFO, model);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dev_typeinfo_activity;
    }

    @Override
    protected void initView() {
        mTypeSpecView = findViewById(R.id.sb_type_spec);
        mMacAddressView = findViewById(R.id.sb_macaddress);
        mIdView = findViewById(R.id.sb_id);
        mBatteryCellsView = findViewById(R.id.sb_batterycells);
        mVoltageLevelView = findViewById(R.id.sb_voltagelevel);
        mDcVersionView = findViewById(R.id.sb_dcversion);
        mLlcVersionView = findViewById(R.id.sb_llcversion);
        mPfcVersionView = findViewById(R.id.sb_pfcversion);
        mDccodeView = findViewById(R.id.sb_dccode);
        mPfccodeView = findViewById(R.id.sb_pfccode);
        mLlccodeView = findViewById(R.id.sb_llccode);
    }

    @Override
    protected void initData() {

        model = getParcelable(INTENT_KEY_IN_DEVICETYPEINFO);
        if(model == null){
            return;
        }
        mTypeSpecView.setRightText(model.getDevicename());
        mMacAddressView.setRightText(model.getMacaddr());
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        revarray = mMmkv.decodeBytes(MMKVUtils.APPDEVRAW+ model.getId() , null);

        AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);

        // 获取可以搜索的MAC地址前缀
        if(appSetInfo != null){
            batterycells = appSetInfo.batterycells;
            // EasyLog.print("========================dEVTYPEINFO batterycells.size() " + batterycells.size());
            if(batterycells.size()<3){
                // EasyLog.print("========================dEVTYPEINFO batterycells.size() 8888" );
                batterycells = new ArrayList<>();
                batterycells.add("LFP");
                batterycells.add("NCM");
                batterycells.add("SSB");
            }else{
                // EasyLog.print("========================dEVTYPEINFO batterycells.size() " + batterycells.get(0)+  batterycells.get(1)+ batterycells.get(2));
            }

        } else{
            // EasyLog.print("========================dEVTYPEINFO batterycells.size()  999999" );
            batterycells = new ArrayList<>();
            batterycells.add("LFP");
            batterycells.add("NCM");
            batterycells.add("SSB");
        }
        if(revarray != null){
            setDevTypeInfo();
        }


    }

    private void setDevTypeInfo(){

        // 解码数据值
        List<Integer> receivDataList = RecodeUtil.readDecodeAll(revarray);



        if(receivDataList.size() == 125) {
            //statusarray= RecodeUtil.readDecodeStatus(revarray[123],revarray[124]);
            int dtype_id;
            int profile_id;

            int onecorecode;
            int twocorecode;
            String dlabel;
            String dcota;
            String llcota;
            String pfcota;

            int batterycell;
            int voltagelevel;
            int dcversion;
            int dcsubversion;
            int llcversion;
            int llcsubversion;
            int pfcversion;
            int pfcsubversion;
            int frequency;
            int bmsversion;
            // 设备额度电压
            voltagelevel = receivDataList.get(97);
            String strV98 = RecodeUtil.GetRealValues("33", "64", voltagelevel);

            // 设备额度频率
            frequency = receivDataList.get(99);
            String strV100 = RecodeUtil.GetRealValues("33", "64", frequency);

            // LLC芯片编码
            //onecorecode = receivDataList.get(102);
            //String strV103 = RecodeUtil.GetRealValues("33", "67", onecorecode);

            String strV101 = ByteUtil.toHexString(new byte[] {revarray[203], revarray[204]}).toUpperCase().replace(" ","");;
            String strV102 = ByteUtil.toHexString(new byte[] {revarray[205], revarray[206]}).toUpperCase().replace(" ","");;
            String strV103 = ByteUtil.toHexString(new byte[] {revarray[207], revarray[208]}).toUpperCase().replace(" ","");;

            //twocorecode = receivDataList.get(103);
            //String strV104 = RecodeUtil.GetRealValues("33", "68", twocorecode);

            //String strV105 = RecodeUtil.GetRealValues("33", "69", batterycells);
            String strV105 = "";
            batterycell = receivDataList.get(104);
            if(batterycell == 1){
                strV105 = batterycells.get(0);
            }else if (batterycell == 2){
                strV105 = batterycells.get(1);
            }else if (batterycell == 3){
                strV105 = batterycells.get(2);
            }else{
                strV105 = batterycells.get(0);
            }


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

            // EasyLog.print("======================== model.getDevicename-> " + model.getDevicename());
            //mTypeSpecView.setRightText("");

            mIdView.setRightText(RecodeUtil.codeSN(model.getMacaddr()));
            mDccodeView.setRightText(strV101);
            mPfccodeView.setRightText(strV102);
            mLlccodeView.setRightText(strV103);
            mBatteryCellsView.setRightText(strV105);
            mVoltageLevelView.setRightText(strV98);
            mDcVersionView.setRightText("V" + strV115 + "S" + strV116);
            mLlcVersionView.setRightText("V" +strV108  + "S" + strV109);
            mPfcVersionView.setRightText("V" +strV111  + "S" + strV112);
        }
    }


}