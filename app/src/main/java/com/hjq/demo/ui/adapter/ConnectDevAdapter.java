package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.bean.ConnectDev;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.util.CommonUtil;
import com.hjq.widget.view.BatteryDrawView;
import com.hjq.widget.view.GoodProgressView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 连接设备数据列表
 */
public final class ConnectDevAdapter extends AppAdapter<DeviceBind> {

    public ConnectDevAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView tv_name;
        private final TextView tv_dtype;
        //private final TextView tv_impluse;
        private final GoodProgressView gpv_impluse;
        private final AppCompatImageView img;
        //private final BatteryDrawView pb_impluse;

        private ViewHolder() {
            super(R.layout.device_picker_item);
            tv_name= findViewById(R.id.tv_connectdevname);
            tv_dtype= findViewById(R.id.tv_dtype);
            //tv_impluse= findViewById(R.id.tv_impluse);
            gpv_impluse = findViewById(R.id.gpv_impluse);
            img = findViewById(R.id.iv_find_corner);
            //pb_impluse = findViewById(R.id.pb_impluse);
        }

        @Override
        public void onBindView(int position) {

            tv_name.setText(getItem(position).getDevnickname());
            tv_dtype.setText(getItem(position).getDevicename());
            Integer inplus = CommonUtil.StringToInt( getItem(position).getImplus());
            //tv_impluse.setText(inplus + "%");
            gpv_impluse.setProgressValue(inplus);
            if(getItem(position).getDevicename().equals("META-2000") ){
                img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device));
            }else if(getItem(position).getDevicename().equals("META-2000 PRO") ){
                img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.metapro2000));
            }else {
                img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device));
            }
            /*pb_impluse.post(new Runnable() {
                @Override
                public void run() {
                    pb_impluse.setProgress(CommonUtil.StringToInt( getItem(position).getImplus()));
                }
            });
*/

            //tv_impluse.setText(getItem(position).impluse + "%");
            //Log.i("onBindView", getItem(position).getImplus());
        }
    }
}