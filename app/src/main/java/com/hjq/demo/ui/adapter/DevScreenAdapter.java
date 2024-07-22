package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.bean.DevMonitor;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/08/28
 *    desc   : 设备显示屏页适配器
 */
public final class DevScreenAdapter extends AppAdapter<DevMonitor> {

    public DevScreenAdapter(Context context) {
        super(context);
       addItem(new DevMonitor(1,"99","00","99","0"));
        addItem(new DevMonitor(1,"89","00","99","10"));
        addItem(new DevMonitor(1,"79","00","69","10"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mChargingHoursView;
        private final TextView mChargingMinsView;
        private final TextView mInplusValView;
        private final TextView mOutputValView;

        private ViewHolder() {
            super(R.layout.devscreen_item);
            mChargingHoursView = findViewById(R.id.dev_charging_hours);
            mChargingMinsView = findViewById(R.id.dev_charging_mins);
            mInplusValView = findViewById(R.id.dev_inplus_val);
            mOutputValView = findViewById(R.id.dev_output_val);

        }

        @Override
        public void onBindView(int position) {
            mChargingHoursView.setText(getItem(position).charginghours);
            mChargingMinsView.setText(getItem(position).chargingmins);
            mInplusValView.setText(getItem(position).implusval);
            mOutputValView .setText(getItem(position).outputval);
        }
    }
}