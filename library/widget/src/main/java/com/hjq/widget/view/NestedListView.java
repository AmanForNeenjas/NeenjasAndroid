package com.hjq.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * NestedScrollView里面的listview设置
 * 亲测有效
 *
 * NestedScrollView和listview可能会出现滑动冲突，还可能出现listview只显示一个item，无论Listview里面的数据有多少的情况。
 *
 * 解决方法：
 *
 * 创建NestedListView继承ListView,然后重新onMeasure方法
 * ————————————————
 * 版权声明：本文为CSDN博主「weixin_43677710」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/weixin_43677710/article/details/116657051
 */
public class NestedListView extends ListView {
    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //在Android上，如何删除列表底部listview中出现的行？
        //setDivider(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
