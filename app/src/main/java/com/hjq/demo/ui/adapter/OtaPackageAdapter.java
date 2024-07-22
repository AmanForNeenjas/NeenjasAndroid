package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.DeviceLog;
import com.hjq.demo.http.model.OtaPackage;
import com.hjq.demo.util.DateUtil;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 设备升级包列表
 */
public final class OtaPackageAdapter extends AppAdapter<OtaPackage> {
    public int total;

    public OtaPackageAdapter(Context context) {
        super(context);
    }

    public OtaPackageAdapter(Context context,int total) {
        super(context);
        this.total = total;
    }

    @Override
    public void setData(@Nullable List<OtaPackage> data) {
        super.setData(data);
        total = data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }



    private final class ViewHolder extends AppAdapter<?>.ViewHolder {


        private final TextView tv_packagename;
        private final TextView tv_addtime;
        private final View view_line;

        private ViewHolder() {
            super(R.layout.otapackage_item);
            tv_packagename = findViewById(R.id.tv_packagename);
            tv_addtime = findViewById(R.id.tv_addtime);
            view_line = findViewById(R.id.view_line);
        }



        @Override
        public void onBindView(int position) {
            //OtaPackage otaPackage = getItem(position);
            String name = getItem(position).getVersion() + getItem(position).getSubversion();
            if(getItem(position).getTag().equals("PFC")){
                name = name + " " + getItem(position).getTitle().substring(3,5) + "0V";
            }
            tv_packagename.setText(name);
            String addtime = getItem(position).getAddtime();
            tv_addtime.setText(DateUtil.format(addtime));
            if(total == position+ 1){
                view_line.setVisibility(View.GONE);
            }


        }
    }
}