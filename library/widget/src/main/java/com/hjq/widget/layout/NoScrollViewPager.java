package com.hjq.widget.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 禁用水平滑动的ViewPager（一般用于 APP 首页的 ViewPager + Fragment）
 */
public final class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 用于拦截触摸事件
     * // return true;按下时拦截事件，不传递给子View
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 不拦截这个事件
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 不处理这个事件
        return false;
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        // 不响应按键事件
        return false;
    }

    /**
     * 关于ViewPager的setCurrentItem跳转到指定item，如果两个item相隔个数大于缓存个数，界面数据没有刷新（空白）的问题。
     * 分析，如果一页一页的滑动，不存在加载不出数据信息的，直接调用setCurrentItem滑动到相邻页也不会有问题。setCurrentItem(item, true)方法后面还有一个参数，表示是否平滑的划过去，如果是true，会一页一页的滑到指定页，不是我们想要的效果（直接跳到某页），通过Scroller设置滑动时间为0，再通过 setCurrentItem(item, true)中间滑动的动画就没有了。
     * 原文链接：https://blog.csdn.net/qq_38996911/article/details/91380416
     * @param item
     */
    @Override
    public void setCurrentItem(int item) {
        // 只有相邻页才会有动画
        super.setCurrentItem(item, Math.abs(getCurrentItem() - item) == 1);
    }
}