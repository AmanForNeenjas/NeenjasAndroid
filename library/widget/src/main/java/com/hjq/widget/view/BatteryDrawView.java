package com.hjq.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.hjq.widget.R;

/**
 * android 自定义View绘制电池电量（电池内带数字显示）
 * https://xiaomo.blog.csdn.net/article/details/107419332?spm=1001.2101.3001.6650.11&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-11-107419332-blog-73171319.pc_relevant_multi_platform_whitelistv4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-11-107419332-blog-73171319.pc_relevant_multi_platform_whitelistv4&utm_relevant_index=16
 */
public class BatteryDrawView extends View {
    private float percent = 0f;
    // 电池电量外面的大白框
    Paint paint = new Paint();
    // 电池电量里面的绿色
    Paint paint1 = new Paint();
    // 电池头部
    Paint paint2 = new Paint();

    public BatteryDrawView(Context context, AttributeSet set) {
        super(context, set);
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(dip2px(1.5f));
        paint1.setColor(Color.BLACK);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.WHITE);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        //以分辨率为720*1080准，计算宽高比值
        float ratioWidth = (float) mScreenWidth / 720;
        float ratioHeight = (float) mScreenHeight / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        int textSize = Math.round(20 * ratioMetrics);
        paint2.setTextSize(textSize);
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @SuppressLint("DrawAllocation")
    @Override
    // 重写该方法,进行绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startColor;
        // 大于百分之30时绿色，否则为红色
        if (percent > 0.3f) {
            //paint.setColor(Color.GREEN);
            startColor = Color.parseColor("#02D67F");
        } else {
            startColor = Color.RED;

        }
        int[] mColorArray;  // 圆环渐变色
        // 初始化进度圆环渐变色
       ;
        int firstColor = Color.parseColor("#E6FF5B");
        if (startColor != -1 && firstColor != -1) mColorArray = new int[]{startColor, firstColor};
        else mColorArray = null;

        int a = getWidth() - dip2px(2f);
        int b = getHeight() - dip2px(1.5f);
        // 根据电量百分比画图
        float d = a * percent;
        float left = dip2px(0.5f);
        float top = dip2px(0.5f);
        float right = dip2px(2.5f);
        float bottom = dip2px(1.5f);

        RectF re1 = new RectF(left, top, d - right, b + bottom); //电量填充
        RectF re2 = new RectF(0, 0, a - right, b + bottom); //电池边框
        //RectF re3 = new RectF(a - right, b / 5, a, b + bottom - b / 5);  //电池正极
        paint.setShader(new LinearGradient(left, top, re1.right, 0, mColorArray, new float[]{ 1.0f, 0.2f}, Shader.TileMode.CLAMP));
        // 绘制圆角矩形
        canvas.drawRect(re1, paint);
        canvas.drawRect(re2, paint1);
        //canvas.drawRect(re3, paint2);
        //文字的起点为(getWidth()/2,getHeight()/2)
        canvas.drawText(String.valueOf((int) (percent * 100)), getWidth() / 3 - dip2px(3), getHeight() - getHeight() / 4, paint2);

    }

    // 每次检测电量都重绘，在检测电量的地方调用
    public synchronized void setProgress(int percent) {
        this.percent = (float) (percent / 100.0);
        postInvalidate();
    }
}
