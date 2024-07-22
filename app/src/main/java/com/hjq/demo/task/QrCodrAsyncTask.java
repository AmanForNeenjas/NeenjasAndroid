package com.hjq.demo.task;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.demo.R;
import com.hjq.http.EasyLog;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 生成该类的对象，并调用execute方法之后
 * 首先执行的是onProExecute方法
 * 其次执行doInBackgroup方法
 *
 */
public class QrCodrAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    // 画布
    private ImageView mView;
    // 上下文
    private Context context;
    // 二维码存储内存
    private String qrstr;


    public QrCodrAsyncTask(Context context,ImageView view, String str) {
        super();
        this.mView = view;
        this.qrstr = str;
        this.context = context;
    }


    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_round48);
        return QRCodeEncoder.syncEncodeQRCode(qrstr, BGAQRCodeUtil.dp2px(context, 150), Color.parseColor("#000000"), logoBitmap);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            mView.setImageBitmap(bitmap);
        } else {
            // EasyLog.print("error");
        }
    }
}