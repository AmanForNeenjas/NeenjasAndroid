package com.hjq.demo.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.ui.activity.AddDevActivity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.activity.LoginActivity;
import com.hjq.demo.ui.adapter.TabAdapter;
import com.hjq.demo.widget.XCollapsingToolbarLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 关注设备状态信息 Fragment
 */
public final class HomeFragment extends TitleBarFragment<HomeActivity>
        implements TabAdapter.OnTabListener, ViewPager.OnPageChangeListener
         {

    //private XCollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    // 添加新设备
    private AppCompatImageView mAddView;
    private AppCompatImageView mHintView;
    //private AppCompatImageView mSearchView;

    private RecyclerView mTabView;
    private ViewPager mViewPager;

    private TabAdapter mTabAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initView() {
        //mCollapsingToolbarLayout = findViewById(R.id.ctl_home_bar);
        mToolbar = findViewById(R.id.tb_home_title);

        mAddView = findViewById(R.id.iv_home_add);
        mHintView = findViewById(R.id.tv_home_hint);
        //mSearchView = findViewById(R.id.iv_home_search);

        mTabView = findViewById(R.id.rv_home_tab);
        mViewPager = findViewById(R.id.vp_home_pager);

        mPagerAdapter = new FragmentPagerAdapter<>(this);
        //mPagerAdapter.addFragment(StatusFragment.newInstance(), getString(R.string.device));
        mPagerAdapter.addFragment(DeviceFragment.newInstance(), getString(R.string.device));
        //mPagerAdapter.addFragment(DevmsgFragment.newInstance(), getString(R.string.service));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mTabAdapter = new TabAdapter(getAttachActivity());
        mTabView.setAdapter(mTabAdapter);

        // 给这个 ToolBar 设置顶部内边距，才能和 TitleBar 进行对齐
        ImmersionBar.setTitleBar(getAttachActivity(), mToolbar);

        //设置渐变监听
        //mCollapsingToolbarLayout.setOnScrimsListener(this);


        mAddView.setOnClickListener(view -> {
            startActivity(AddDevActivity.class);
        });
        //mSearchView.setOnClickListener(view -> {
        //    //startActivity(LoginActivity.class);
        //});
    }

    @Override
    protected void initData() {
        mTabAdapter.addItem(getString(R.string.device));
        //mTabAdapter.addItem(getString(R.string.service));
        mTabAdapter.setOnTabListener(this);
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    //@Override
    /*public boolean isStatusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }*/

    /**
     * {@link TabAdapter.OnTabListener}
     */

    @Override
    public boolean onTabSelected(RecyclerView recyclerView, int position) {
        mViewPager.setCurrentItem(position);
        return true;
    }

    /**
     * {@link ViewPager.OnPageChangeListener}
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /*
     * CollapsingToolbarLayout 渐变回调
     *
     * {@link XCollapsingToolbarLayout.OnScrimsListener}

    @SuppressLint("RestrictedApi")
    @Override
    public void onScrimsStateChange(XCollapsingToolbarLayout layout, boolean shown) {
        getStatusBarConfig().statusBarDarkFont(shown).init();

        mAddView.setSupportImageTintList(ColorStateList.valueOf(getColor(shown ? R.color.common_icon_color : R.color.white)));
        //mHintView.setBackgroundResource(shown ? R.drawable.home_search_bar_gray_bg : R.drawable.home_search_bar_transparent_bg);
        //mHintView.setTextColor(ContextCompat.getColor(getAttachActivity(), shown ? R.color.black60 : R.color.white60));
        mHintView.setSupportImageTintList(ColorStateList.valueOf(getColor(shown ? R.color.logo_color_raw : R.color.white)));
        mSearchView.setSupportImageTintList(ColorStateList.valueOf(getColor(shown ? R.color.common_icon_color : R.color.white)));
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mViewPager.removeOnPageChangeListener(this);
        mTabAdapter.setOnTabListener(null);
    }
}