package com.hjq.demo.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.model.DeviceLog;
import com.hjq.demo.ui.adapter.LogDevAdapter;
import com.hjq.demo.ui.adapter.StatusAdapter;
import com.hjq.demo.util.DateUtil;
import com.hjq.widget.layout.WrapRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/07/10
 *    desc   : 关注 Fragment
 */
public final class DevmsgFragment extends TitleBarFragment<AppActivity>
        implements OnRefreshLoadMoreListener,
        BaseAdapter.OnItemClickListener {

    public static DevmsgFragment newInstance() {
        return new DevmsgFragment();
    }

    private SmartRefreshLayout mRefreshLayout;
    private WrapRecyclerView mRecyclerView;

    private LogDevAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.status_fragment;
    }

    @Override
    protected void initView() {
        mRefreshLayout = findViewById(R.id.rl_status_refresh);
        mRecyclerView = findViewById(R.id.rv_status_list);

        mAdapter = new LogDevAdapter(getAttachActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        //TextView headerView = mRecyclerView.addHeaderView(R.layout.picker_item);
        //headerView.setText("我是头部");
        //headerView.setOnClickListener(v -> toast("点击了头部"));

        TextView footerView = mRecyclerView.addFooterView(R.layout.picker_item);
        //footerView.setText("我是尾部");
        //footerView.setOnClickListener(v -> toast("点击了尾部"));

        mRefreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mAdapter.setData(analogData());
    }

    /**
     * 模拟数据
     */
    @SuppressLint("StringFormatInvalid")
    private List<DeviceLog> analogData() {
        List<DeviceLog> data = new ArrayList<>();
        for (int i = mAdapter.getCount(); i < mAdapter.getCount() + 20; i++) {
            DeviceLog item = new DeviceLog();
            item.setDevnickname("MATE2000-1");
            item.setAddtime(DateUtil.dateToString(DateUtil.randomDate("2021-10-23 16:35:25", "2022-10-23 16:35:25"), DateUtil.YYYY_MM_DD_HH_MM_SS));
            // Android string 格式化 https://blog.csdn.net/cquwentao/article/details/25881059
            String msgFormat = getResources().getString(R.string.log_implusen);
            item.setMsg(String.format(msgFormat,50));
            data.add(item);
        }
        return data;
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
        toast(mAdapter.getItem(position));
    }

    /**
     * {@link OnRefreshLoadMoreListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.clearData();
            mAdapter.setData(analogData());
            mRefreshLayout.finishRefresh();
        }, 1000);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        postDelayed(() -> {
            mAdapter.addData(analogData());
            mRefreshLayout.finishLoadMore();

            mAdapter.setLastPage(mAdapter.getCount() >= 100);
            mRefreshLayout.setNoMoreData(mAdapter.isLastPage());
        }, 1000);
    }
}