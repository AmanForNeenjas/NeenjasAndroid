package com.hjq.demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.FragmentPagerAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppActivity;
import com.hjq.demo.app.AppFragment;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.GroupID;
import com.hjq.demo.http.model.GroupUser;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.manager.ActivityManager;
import com.hjq.demo.other.DoubleClickHelper;
import com.hjq.demo.ui.adapter.NavigationAdapter;
import com.hjq.demo.ui.fragment.FindFragment;
import com.hjq.demo.ui.fragment.HomeFragment;
import com.hjq.demo.ui.fragment.MessageFragment;
import com.hjq.demo.ui.fragment.MineFragment;
import com.hjq.demo.util.MMKVUtils;
import com.tencent.mmkv.MMKV;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 首页界面
 */
public final class HomeActivity extends AppActivity
        implements NavigationAdapter.OnNavigationListener {

    private static final String INTENT_KEY_IN_FRAGMENT_INDEX = "fragmentIndex";
    private static final String INTENT_KEY_IN_FRAGMENT_CLASS = "fragmentClass";
    private final MMKV mMmkv;




    private ViewPager mViewPager;
    private RecyclerView mNavigationView;

    private NavigationAdapter mNavigationAdapter;
    private FragmentPagerAdapter<AppFragment<?>> mPagerAdapter;
    private UserInfo userInfo;

    public HomeActivity() {
        super();
        mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
    }


    public static void start(Context context) {
        start(context, HomeFragment.class);
    }

    public static void start(Context context, Class<? extends AppFragment<?>> fragmentClass) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_activity;
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.vp_home_pager);
        mNavigationView = findViewById(R.id.rv_home_navigation);



    }

    @Override
    protected void initData() {

        userInfo = mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setLogined(false);
            userInfo.setGroudid(0);
            userInfo.setUid(0);
        }else if(userInfo.getUid()<1){
            userInfo = new UserInfo();
            userInfo.setLogined(false);
            userInfo.setGroudid(0);
            userInfo.setUid(0);
        }
        mNavigationAdapter = new NavigationAdapter(this);
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_index),
                ContextCompat.getDrawable(this, R.drawable.home_home_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_found),
                ContextCompat.getDrawable(this, R.drawable.home_found_selector)));
        //mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_message),
        //        ContextCompat.getDrawable(this, R.drawable.home_message_selector)));
        mNavigationAdapter.addItem(new NavigationAdapter.MenuItem(getString(R.string.home_nav_me),
                ContextCompat.getDrawable(this, R.drawable.home_me_selector)));
        mNavigationAdapter.setOnNavigationListener(this);
        mNavigationView.setAdapter(mNavigationAdapter);
        mPagerAdapter = new FragmentPagerAdapter<>(this);
        mPagerAdapter.addFragment(HomeFragment.newInstance());
        mPagerAdapter.addFragment(FindFragment.newInstance());
        //mPagerAdapter.addFragment(MessageFragment.newInstance());
        mPagerAdapter.addFragment(MineFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);

        onNewIntent(getIntent());

    }





    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switchFragment(mPagerAdapter.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前 Fragment 索引位置
        outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX));
    }

    private void switchFragment(int fragmentIndex) {
        if (fragmentIndex == -1) {
            return;
        }

        switch (fragmentIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(fragmentIndex);
                mNavigationAdapter.setSelectedPosition(fragmentIndex);
                break;
            default:
                break;
        }
    }

    /**
     * {@link NavigationAdapter.OnNavigationListener}
     */

    @Override
    public boolean onNavigationItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                mViewPager.setCurrentItem(position);
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    protected ImmersionBar createStatusBarConfig() {
        return super.createStatusBarConfig()
                // 指定导航栏背景颜色
                .navigationBarColor(R.color.white);
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint);
            return;
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false);
        postDelayed(() -> {
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.setAdapter(null);
        mNavigationView.setAdapter(null);
        mNavigationAdapter.setOnNavigationListener(null);
    }

    public boolean getNetworkStatus(){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        return mMmkv.getBoolean(MMKVUtils.NETWORKSTATUS, false);
    }

    public List<DeviceBind> getLocalDeviceBindList(){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        return  MMKVUtils.getArray(mMmkv, this, MMKVUtils.DEVICEBINDLIST,new DeviceBind());
    }
    public void setLocalDeviceBindList(List<DeviceBind> items){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        MMKVUtils.setArray(mMmkv, this, items, MMKVUtils.DEVICEBINDLIST);
    }
    public void setUserInfo(UserInfo userInfo){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        mMmkv.encode(MMKVUtils.USERINFO, userInfo);

    }
    public UserInfo getUserInfo(){
        return userInfo;
    }
    public void setUserInfo(String nickname){
        userInfo.setNickname(nickname);
    }

    public GroupID getLocalGroup(){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        return mMmkv.decodeParcelable(MMKVUtils.GROUPID, GroupID.class);
    }

    public void setLocalGroup(GroupID groupID){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        mMmkv.encode(MMKVUtils.GROUPID, groupID);
    }

    public List<GroupUser> getLocalGroupUserList(){
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        return  MMKVUtils.getArray(mMmkv, this, MMKVUtils.GROUPUSERLIST,new GroupUser());
    }

    public void setLocalGroupUserList(List<GroupUser> list) {
        //MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        MMKVUtils.setArray(mMmkv, this, list, MMKVUtils.GROUPUSERLIST);
    }



    /**
     * 设置语言 重启Activity
     */
    public void reStart(){
        // 我们可以充分运用 Activity 跳转动画，在跳转的时候设置一个渐变的效果
        startActivity(new Intent(this, SplashActivity.class));
        overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out);
        finish();


    }



}