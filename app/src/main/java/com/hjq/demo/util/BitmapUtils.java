package com.hjq.demo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

import com.hjq.demo.R;


/**
 * @author xiayiye5
 * @date 2022/9/6 17:50
 * 自定义progressBar进度条
 */
public class BitmapUtils {
    public static Bitmap createProgressBitmap(Context context, long curPosition, long duration) {
        final int width = 240;
        final int height = 6;
        final int x_offset = Math.round(width * 0.02f);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
       // paint.setColor(getColor(context, com.hjq.widget.R.color.remote_view_progress_bar_bg_color));
        //draw progress bar bg.
        drawPath(x_offset, width, width - x_offset, 0, height, canvas, paint);
        if (duration == 0) {
            return bitmap;
        }
        if (curPosition + height > duration) {
            curPosition = duration;
        }
        float curWidth = (float) (width - x_offset) * curPosition / duration;
        if (curWidth > 0) {
            paint = new Paint();
            Shader mShader = new LinearGradient(0, 0, curWidth, height,
                    new int[]{getColor(context, R.color.red),
                            getColor(context, R.color.red),
                            getColor(context, R.color.red)
                    },
                    null, Shader.TileMode.CLAMP);
            paint.setShader(mShader);
            //draw current progress bar.
            drawPath(x_offset, curWidth + x_offset, curWidth, 0, height, canvas, paint);

            //draw progress bar thumb.
            final int thumbWidth = 3;
            paint = new Paint();
            paint.setColor(getColor(context, R.color.red));
            drawPath(curWidth + x_offset, curWidth + x_offset + thumbWidth,
                    curWidth + thumbWidth, curWidth, height, canvas, paint);
        }
        return bitmap;
    }

    private static void drawPath(float x0, float x1, float x2, float x3, int y, Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo(x0, 0);
        path.lineTo(x1, 0);
        path.lineTo(x2, y);
        path.lineTo(x3, y);
        path.close();
        canvas.drawPath(path, paint);
    }

    private static int getColor(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }
}
