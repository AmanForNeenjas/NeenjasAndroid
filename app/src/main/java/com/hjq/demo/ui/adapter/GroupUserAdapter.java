package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.entity.ExtendedBluetoothDevice;
import com.hjq.demo.http.model.GroupUser;
import com.hjq.http.EasyLog;
import com.hjq.widget.layout.SettingBar;

import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/09/22
 *    desc   : 状态数据列表
 */
public final class GroupUserAdapter extends BaseAdapter {

    private List<GroupUser> gUserList;

    public GroupUserAdapter() {
        this.gUserList = new ArrayList<>();
    }

    public void setData(List<GroupUser> list){
        if(gUserList!=null & gUserList.size()>0){
            gUserList.clear();
        }

        for(GroupUser item: list)
        {
            gUserList.add(item);
        }
        // gUserList = list;
        // EasyLog.print("======================== GroupUserAdapter  gUserList size " +  gUserList.size());
        // 通知更新
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return gUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return gUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if(view == null){
            view = inflater.inflate(R.layout.groupuser_lst_row, viewGroup, false);
            GroupUserAdapter.ViewHolder holder = new GroupUserAdapter.ViewHolder();
            holder.mSettinBarView = view.findViewById(R.id.sb_groupuser);;

            view.setTag(holder);
        }
        GroupUserAdapter.ViewHolder holder = (GroupUserAdapter.ViewHolder) view.getTag();
        holder.mSettinBarView.setLeftText(gUserList.get(position).getNickname());
        //mSettinBarView.setLeftDrawable()
        holder.mSettinBarView.setRightText(gUserList.get(position).getMobile());
        holder.mSettinBarView.setFocusable(false);
        holder.mSettinBarView.setClickable(false);
        holder.mSettinBarView.setFocusableInTouchMode(false);
        return view;
    }

    private final class ViewHolder  {

        private SettingBar mSettinBarView;

        /*private ViewHolder() {
            super(R.layout.groupuser_lst_row);
            mSettinBarView = findViewById(R.id.sb_groupuser);
        }

        @Override
        public void onBindView(int position) {
            GroupUser groupUser = getItem(position);
            mSettinBarView.setLeftText(groupUser.getNickname());
            //mSettinBarView.setLeftDrawable()
            mSettinBarView.setRightText(groupUser.getMobile());
        }*/
    }
}