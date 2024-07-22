package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.http.model.DeviceLog;
import com.hjq.demo.util.DateUtil;
import com.hjq.widget.view.BatteryDrawView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 设备日志列表
 */
public final class LogDevAdapter extends AppAdapter<DeviceLog> {

    public LogDevAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final ImageView iv_dev_ava;
        private final TextView tv_dev_nickname;
        private final TextView tv_addtime;
        private final TextView tv_msg;
        private final ImageView iv_unread;

        private ViewHolder() {
            super(R.layout.devmsg_item);
            iv_dev_ava = findViewById(R.id.iv_dev_ava);
            tv_dev_nickname = findViewById(R.id.tv_dev_nickname);
            tv_addtime = findViewById(R.id.tv_addtime);
            tv_msg = findViewById(R.id.tv_msg);
            iv_unread = findViewById(R.id.iv_unread);
        }

        @Override
        public void onBindView(int position) {
            // 显示圆形的 ImageView
            GlideApp.with(getContext())
                    .load(R.drawable.device)
                    .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                    .into(iv_dev_ava);


            String name = getItem(position).getDevnickname();
            if(name.isEmpty()){
                name = getItem(position).getDevicename();
            }
            tv_dev_nickname.setText(name);
            String addtime = getItem(position).getAddtime();
            tv_addtime.setText(DateUtil.format(addtime));
            tv_msg.setText(getItem(position).getMsg());

            // 显示圆角的 ImageView
            GlideApp.with(getContext())
                    .load(R.drawable.filebox_unread)
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_3))))
                    .into(iv_unread);


        }
    }
}