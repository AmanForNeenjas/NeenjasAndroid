package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.aop.Permissions;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 服务 Fragment
 */
public final class MessageFragment extends TitleBarFragment<HomeActivity> {

    private RecyclerView mTabView;
    private ViewPager mViewPager;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.message_fragment;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_sev_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(BrowserFragment.newInstance("http://www.yiwashi.cn/problems/2019.html"),getString(R.string.service));
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @SingleClick
    @Override
    public void onClick(View view) {

    }


}