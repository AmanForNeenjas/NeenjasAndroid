package com.hjq.demo.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.feasycom.common.bean.FscDevice;
import com.hjq.demo.R;
import com.hjq.demo.entity.ExtendedBluetoothDevice;
import com.hjq.demo.http.model.AppSetInfo;
import com.hjq.demo.http.model.DeviceBind;
import com.hjq.demo.ui.activity.AddDevActivity;
import com.hjq.demo.ui.activity.DevInfoV1Activity;
import com.hjq.demo.util.CommonUtil;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyLog;
import com.hjq.language.MultiLanguages;
import com.hjq.shape.view.ShapeTextView;
import com.tencent.mmkv.MMKV;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScanDeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    //private final ArrayList<ExtendedBluetoothDevice> listBondedValues = new ArrayList<>();
    private final ArrayList<ExtendedBluetoothDevice> listValues = new ArrayList<>();
    private final ArrayList<ExtendedBluetoothDevice> prefilterListValues = new ArrayList<>();
    
    private List<String> allowMac;
    private List<String> disMac;
    private Set<String> bindMac;
    private List<String> blenames;
    private int deviceCount = 0;
    public ScanDeviceListAdapter(Set<String> bindMacmodel) {
        MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
        AppSetInfo appSetInfo = mMmkv.decodeParcelable(MMKVUtils.APPSETINFO, AppSetInfo.class);
        bindMac = bindMacmodel;

        // 获取可以搜索的MAC地址前缀
        if(appSetInfo != null){
            allowMac = appSetInfo.allowmac;
            // 获取禁止搜索的MAC地址前缀
            disMac = appSetInfo.dismac;
            blenames = appSetInfo.blenames;
        } else{
            allowMac = new ArrayList<>();
            allowMac.add("DC:0D:30:");
            disMac = new ArrayList<>();
            blenames = new ArrayList<>();
            blenames.add("META-2000");
            blenames.add("FSC-BW236-LE");
        }


    }
    public void setBindMac(Set<String> bindMacmodel){
        bindMac = bindMacmodel;
    }
    /*
     * 设置绑定设备列表
     * @param

    public void  addBondedDevices(@NonNull final Set<FscDevice> devices){
        for(FscDevice device: devices){
            listBondedValues.add(new ExtendedBluetoothDevice(device));
        }
        // 通知更新
        notifyDataSetChanged();
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update(@NonNull final FscDevice result, boolean isbind,boolean isTest){
        boolean isBonded = false;
        //// EasyLog.print("======================== FOUND " + result.getName() + "  " + result.getAddress());
        if(CommonUtil.inArrayIndex(allowMac,result.getAddress(),true)>-1 &&  CommonUtil.inArrayIndex(blenames,result.getName(),true)>-1 && CommonUtil.inArrayIndex(disMac,result.getAddress(),true)==-1
        ){
            //查找设备是否存在
            final ExtendedBluetoothDevice device = findDevice(result);

            //查找设备是否绑定
            //if(bindMac.contains(result.getAddress().toUpperCase())){
            isBonded = isbind;
            //}
            // 不存在添加到 prefilterListValues 列表
            if(device == null){
                prefilterListValues.add(new ExtendedBluetoothDevice(result, isBonded,isTest));
            } else{
                // 更新设备
                device.name = result.getName() != null ? result.getName(): null;
                device.rssi = result.getRssi();
            }

            // 过滤
            filtList(AddDevActivity.keyWord);
            // sortList();
            // 通知更新
            //// EasyLog.print("======================== 通知更新 " + result.getName() + "  " + result.getAddress());
            notifyDataSetChanged();
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filtList(String keyWord) {
        if(keyWord == null){

            keyWord = "";
        }

        deviceCount = 0;
        // 按空格过滤关键
        String[] arr = keyWord.split(" ");
        listValues.clear();
        for(int i=0; i< prefilterListValues.size(); i++){
            ExtendedBluetoothDevice device = prefilterListValues.get(i);
            String name = (device.name == null)? "N/A" : device.name.toUpperCase();
            String mac = device.device.getAddress();

            boolean isTarget = true;
            for(String key: arr){
                if(!name.contains(key) && !mac.contains(key)){
                    isTarget = false;
                    break;
                }
            }
            if(isTarget) {
                listValues.add(device);

                deviceCount ++;
            }
        }
        sortList();

        deviceCount = listValues.size();
        notifyDataSetChanged();

    }

    /**
     * 在 listBondedValues prefilterListValues 查找设备
     * @param result
     * @return
     */
    private ExtendedBluetoothDevice findDevice(@NonNull final FscDevice result) {
       /*for(final ExtendedBluetoothDevice device: listBondedValues){
           if(device.matched(result)){
               return device;
           }
       }*/
        for(final ExtendedBluetoothDevice device: prefilterListValues){
            if(device.matched(result)){
                return device;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void sortList() {
        listValues.sort(new Comparator<ExtendedBluetoothDevice>() {
            @Override
            public int compare(ExtendedBluetoothDevice o1, ExtendedBluetoothDevice o2) {
                if(o1.rssi < o2.rssi)
                    return 1;
                else if (o1.rssi == o2.rssi) {
                    String mac1 = o1.device.getAddress();
                    String mac2 = o2.device.getAddress();
                    for(int i = 0;  i < o1.device.getAddress().length(); i++) {
                        if(mac1.charAt(i) < mac2.charAt(i))
                            return -1;
                        else if (mac1.charAt(i) == mac2.charAt(i))
                            continue;
                        else
                            return 1;
                    }
                    return 0;
                }
                else
                    return -1;
            }
        });
    }

    void sortlist_2() {
        int len = listValues.size();
        for(int i = 0; i < len-1; i++) {
            ExtendedBluetoothDevice device_i = listValues.get(i);
            for(int j = i+1; j < len; j++) {
                ExtendedBluetoothDevice device_j = listValues.get(j);
                if(device_j.rssi > device_i.rssi) {
                    listValues.set(i, device_j);
                    listValues.set(j, device_i);
                }
            }
        }
    }

    /**
     * 清除显示列表
     */
    public void clearDevices(){
        if(listValues!=null && listValues.size()>0){
            listValues.clear();
            notifyDataSetChanged();
        }

    }

    /**
     * 清除预过滤列表
     */
    public void clearLeftDevice(){
        if(prefilterListValues != null  && prefilterListValues.size()>0){
            prefilterListValues.clear();
        }

    }

    @Override
    public int getCount() {

        final int availableCount = listValues.isEmpty() ? 2 : listValues.size() + 1; // 1 for title, 1 for empty text
        return availableCount;
    }

    public int getItemCount() {
        return deviceCount;
    }

    @Override
    public Object getItem(int position) {
        /*final int bondedCount = listBondedValues.size() + 1; // 1 for the title
        if(listBondedValues.isEmpty()){
            if(position ==0){
                return R.string.scanner_subtitle_not_bonded;
            }else{
                return listValues.get(position -1);
            }
        } else {
            if(position == 0){
                return R.string.scanner_subtitle_bonded;
            }
            if(position < bondedCount){
                return listBondedValues.get(position -1);
            }
            if(position == bondedCount){
                return R.string.scanner_subtitle_not_bonded;
            }
        }*/
        if(position ==0){
            //return R.string.scanner_subtitle_not_bonded;
            return "";
        }
        return listValues.get(position - 1);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_TITLE;
        }
        /*if(!listBondedValues.isEmpty() && position == listBondedValues.size() + 1){
            return TYPE_TITLE;
        }*/
        if(position == getCount() -1 && listValues.isEmpty()){
            return TYPE_EMPTY;
        }
        return TYPE_ITEM;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        int type = getItemViewType(position);
        switch (type){
            case TYPE_EMPTY:
                if(view == null){
                    view = inflater.inflate(R.layout.device_list_empty, viewGroup, false);
                }
                break;
            case TYPE_TITLE:
                if(view == null){
                    view = inflater.inflate(R.layout.device_list_title, viewGroup, false);
                }
                //TextView title = (TextView) view;
                //title.setText((Integer) getItem(position));
                break;
            default:
                if(view == null){
                    //if(position==1 && deviceCount == 1) {
                    //    view = inflater.inflate(R.layout.scandev_lst_row, viewGroup, false);
                    //}
                    //if(position==1){
                        //view = inflater.inflate(R.layout.scandev_lst_row_top, viewGroup, false);
                    //}else if(position>1 && position== deviceCount){
                    //    view = inflater.inflate(R.layout.scandev_lst_row_bottom, viewGroup, false);
                    //view = inflater.inflate(R.layout.scandev_lst_row_center, viewGroup, false);
                    ///}else {
                    view = inflater.inflate(R.layout.scandev_lst_row, viewGroup, false);
                    //}

                    DeviceViewHolder holder = new DeviceViewHolder();
                    holder.name = view.findViewById(R.id.name);
                    holder.address = view.findViewById(R.id.address);
                    holder.add = view.findViewById(R.id.stv_adddev);
                    holder.test = view.findViewById(R.id.stv_testdev);
                    holder.img = view.findViewById(R.id.iv_dev_ava);


                    //holder.rssi = view.findViewById(R.id.rssi_value);
                    view.setTag(holder);
                }
                ExtendedBluetoothDevice deviceitem = (ExtendedBluetoothDevice) getItem(position);
                DeviceViewHolder holder = (DeviceViewHolder) view.getTag();
                String name = deviceitem.name;
                holder.name.setText(name !=null ? name : "n/a");
                holder.address.setText(deviceitem.device.getAddress());
                if(deviceitem.isBonded){
                    holder.add.setText(R.string.connect);
                }else{
                    holder.add.setText(R.string.dev_add);
                }
                // EasyLog.print("======================== holder.name.equals(\"META2000 PRO\") " + name);
                if(name.equals("META-2000")){
                    holder.img.setImageDrawable(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.device));
                }else if(name.equals("META-2000 PRO")){
                    //Bitmap bitmap = BitmapFactory.decodeResource(viewGroup.getContext().getDrawable(setImageDr),R.drawable.metapro2000);
                    //holder.img.setImageResource(R.drawable.metapro2000);
                    //holder.img.setImageBitmap(bitmap);
                    holder.img.setImageDrawable(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.metapro2000));
                    // EasyLog.print("======================== holder.name.equals(\"META2000 PRO\") 2222" + name);
                } else{
                    holder.img.setImageDrawable(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.device));
                }
                /*
                if(holder.name.equals("META2000")){
                    holder.img.setImageResource(R.drawable.device);
                }else if(holder.name.equals("META2000 PRO")){
                    Bitmap bitmap = BitmapFactory.decodeResource(viewGroup.getContext().getResources(),R.drawable.metapro2000);
                    //holder.img.setImageResource(R.drawable.metapro2000);
                    holder.img.setImageBitmap(bitmap);
                }*/
                if(deviceitem.isTest == false){
                    holder.test.setVisibility(View.GONE);
                }else{
                    holder.test.setVisibility(View.VISIBLE);
                    holder.test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DeviceBind itemdata = new DeviceBind();
                            itemdata.setDevnickname(viewGroup.getContext().getString(R.string.testdevice) + " " + deviceitem.device.getAddress());
                            itemdata.setDevice_id(0);
                            // 测试账号model userid 设置为0 不更新电量值
                            itemdata.setUser_id(0);
                            itemdata.setPuser_id(0);
                            itemdata.setImplus("0");
                            itemdata.setDevicename(deviceitem.device.getName());
                            itemdata.setMacaddr(deviceitem.device.getAddress());
                            DevInfoV1Activity.start(viewGroup.getContext(), itemdata);
                        }
                    });
                }

                /*if(!deviceitem.isBonded || deviceitem.rssi != ExtendedBluetoothDevice.NO_RSSI){
                    holder.rssi.setText(String.valueOf(deviceitem.rssi) + "dBm");
                    holder.rssi.setVisibility(View.VISIBLE);
                } else{
                    holder.rssi.setVisibility(View.GONE);
                }*/
                break;
        }
        return view;
    }



    private class DeviceViewHolder{
        private TextView name;
        private TextView address;
        private ShapeTextView add;
        private ShapeTextView test;
        private AppCompatImageView img;

        //private TextView rssi;
    }
}
