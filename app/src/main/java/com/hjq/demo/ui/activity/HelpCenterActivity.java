package com.hjq.demo.ui.activity;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.http.api.DeviceBindListApi;
import com.hjq.demo.http.api.DeviceTypeListApi;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.DeviceType;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.ui.adapter.DeviceTypeAdapter;
import com.hjq.demo.ui.adapter.TabAdapter;
import com.hjq.demo.ui.fragment.DeviceFragment;
import com.hjq.demo.ui.fragment.DevmsgFragment;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.language.MultiLanguages;

import java.util.List;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 帮助中心
 */
public final class HelpCenterActivity extends AppActivity implements DeviceTypeAdapter.OnDevTypeListener{

    private List<DeviceType> deviceTypeList;
    private RecyclerView mDeviceTypeView;
    private DeviceTypeAdapter mDeviceTypeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.helpcenter_activity;
    }

    @Override
    protected void initView() {
        mDeviceTypeView = findViewById(R.id.rv_devicetype);

        mDeviceTypeAdapter = new DeviceTypeAdapter(this);
        mDeviceTypeView.setAdapter(mDeviceTypeAdapter);
        mDeviceTypeView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener(){
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                super.onTouchEvent(rv, e);
                if(MultiLanguages.getAppLanguage().getLanguage().equals("zh")){
                    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/zh_help_meta2000.html");
                }else{
                    BrowserActivity.start(getContext(), "http://m.nikotapower.net/comm/en_help_meta2000.html");
                }
            }
        });

    }

    @Override
    protected void initData() {
        getList();
    }

    private void getList(){
        EasyHttp.post(this)
                .api(new DeviceTypeListApi()
                ).request(new HttpCallback<HttpListData<DeviceType>>(this) {

                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpListData<DeviceType> data) {
                        if(data.getCode() == 200){
                            deviceTypeList = data.getData().getItems();
                            setData();

                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);

                    }
                });
    }
    private void setData(){
        for(DeviceType deviceType : deviceTypeList){
            mDeviceTypeAdapter.addItem(deviceType);
        }
        mDeviceTypeAdapter.setOnDevTypeListener(this);
    }

    @Override
    public boolean onDevTypeSelected(RecyclerView recyclerView, int position) {
        //mViewPager.setCurrentItem(position);
        toast(R.string.norecord);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceTypeList.clear();
    }
}