package com.hjq.demo.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.api.DeviceBindListApi;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.HttpListData;
import com.hjq.demo.ui.activity.AddDevActivity;
import com.hjq.demo.ui.activity.DevInfoV1Activity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.adapter.ConnectDevAdapter;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/07/10
 *    desc   : 加载已经连接的设备
 */
public final class DeviceFragment20230713 extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    //private TextView headerView;

    public static DeviceFragment20230713 newInstance() {
        return new DeviceFragment20230713();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private ConnectDevAdapter mAdapter;
    private int page;
    private int limit;
    private int total;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {

        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new ConnectDevAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        //headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        //headerView.setText(mAdapter.getCount() + "个设备");
        //headerView.setOnClickListener(v -> toast("点击了头部"));

        //TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        //footerView.setText(R.string.adddevice);
        //footerView.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddDevActivity.class)));
        RelativeLayout footerView = mRecyclerView.addFooterView(R.layout.dev_add_item);
        footerView.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddDevActivity.class)));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        /*page =1;
        limit = 20;
        total = 0;
        if(((HomeActivity)getActivity()).getNetworkStatus()){
            analogData(page, limit);
        } else{
            // 从本地读数据
            readLocalData();

        }
        */

    }

    /**
     * 模拟数据
     */
    private List<DeviceBind> analogData(int page, int limit) {
        /*List<ConnectDev> data = new ArrayList<>();
        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
            ConnectDev dev= new ConnectDev();
            dev.id="1";
            dev.user_id="1";
            dev.name="移动电源";
            dev.dtype="MATE2000";
            dev.dtype_id="1";
            dev.macaddr="AA:BB:CC:DD:EE:FF";
            dev.ag_id="1";
            dev.dlabel="123";
            dev.profile_id="1";
            dev.dat="12";
            dev.firmware_id="1";
            dev.software_id="1";
            dev.external_id="1";
            dev.firmware_name="V1";
            dev.software_name="V1";
            dev.external_name="V1";
            dev.status="1";
            dev.customertitle="AR";
            dev.address="深圳";
            dev.latitude="116.404";
            dev.longitude="39.928";
            dev.impluse="99";
            data.add(dev);
        }*/
        //List<ConnectDev> data = CacheUtil.getInstance().read();
        List<DeviceBind> items = new ArrayList<>();
        //EasyConfig.getInstance().setInterceptor(new SignInterceptor());
        //        .addParam("token", data.getData().getToken());
        EasyHttp.post(this)
                .api(new DeviceBindListApi()
                        .setPage(page)
                         .setLimit(limit)
                        ).request(new HttpCallback<HttpListData<DeviceBind>>(this) {

                    @Override
                    public void onStart(Call call) {

                    }

                    @Override
                    public void onEnd(Call call) {}

                    @Override
                    public void onSucceed(HttpListData<DeviceBind> data) {
                        if(data.getCode() == 200){
                            total= data.getCount();
                            items.addAll(data.getData().getItems()) ;
                            setData(items);
                            // 前20条数据保存在本地
                            if(page == 1){
                                ((HomeActivity)getActivity()).setLocalDeviceBindList(items);
                            }

                        }else{
                            if(page == 1){
                                readLocalData();
                            }
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        super.onFail(e);
                        postDelayed(() -> {
                            if(page == 1){
                                readLocalData();
                            }
                        }, 1000);
                    }
                });
        return items;
    }

    private void setData(List<DeviceBind> items){
        if(mAdapter.getCount()>0){
            mAdapter.appendData(items);
            mAdapter.setLastPage(mAdapter.getCount() >= 100);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
            //headerView.setText(mAdapter.getCount() + "个设备");
        }
        else{
            mAdapter.setData(items);
            /*if(mAdapter.getCount()>0){
                headerView.setVisibility(View.VISIBLE);
                headerView.setText(mAdapter.getCount() + "个设备");
            }else{
                headerView.setVisibility(View.GONE);
            }*/

        }
        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //mDevisconnectedView.setText(R.string.outconnected);
                //sendButton.setEnabled(false);
                //sendFileButton.setEnabled(false);

                //android.util.Log.e(TAG, "uiDeviceDisconnected: false 1"  );

            }
        });*/
    }

    private void readLocalData(){
        // page 设为0，说明从本地读取数据
        page =0;
        // 从本地读取数据
        List<DeviceBind> items = ((HomeActivity)getActivity()).getLocalDeviceBindList();
        mAdapter.setData(items);
    }

    /**
     * {@link BaseAdapter.OnItemClickListener}
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        DevInfoV1Activity.start(getActivity(), mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if(((HomeActivity)getActivity()).getNetworkStatus()){
            postDelayed(() -> {

                page =1;
                limit = 20;
                total = 0;
                mAdapter.clearData();
                analogData(page, limit);
                //mAdapter.setData(analogData(page, limit));
                mRefreshLayout.finishRefresh();
                /*if(mAdapter.getCount()>0){
                    headerView.setVisibility(View.VISIBLE);
                    headerView.setText(mAdapter.getCount() + "个设备");
                }else{
                    headerView.setVisibility(View.GONE);
                }*/

            }, 1000);

        }else{
            // 从本地读数据
            readLocalData();
        }


    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(((HomeActivity)getActivity()).getNetworkStatus()) {
            // 开始从本地读数据page 设置为0，要清空数据
            if(page == 0){
                mAdapter.clearData();
            }
            postDelayed(() -> {
                if (total > mAdapter.getCount()) {
                    page = page + 1;
                    analogData(page, limit);



                } else {
                    mRefreshLayout.finishLoadMore();
                }

            }, 1000);
        } else{
            // 从本地读数据
            //readLocalData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // EasyLog.print( "======================== DeviceFragment  onResume" );
        page =1;
        limit = 20;
        total = 0;
        mAdapter.clearData();
        if(((HomeActivity)getActivity()).getNetworkStatus()){
            // EasyLog.print( "======================== DeviceFragment analogData" );
            analogData(page, limit);
        } else{
            // EasyLog.print( "======================== DeviceFragment  readLocalData" );
            // 从本地读数据
            readLocalData();

        }
    }
}