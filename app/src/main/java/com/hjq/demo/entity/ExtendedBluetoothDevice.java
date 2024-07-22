package com.hjq.demo.entity;

import androidx.annotation.NonNull;

import com.feasycom.common.bean.FscDevice;

public class ExtendedBluetoothDevice {
    public static final int NO_RSSI = -1000;
    public final FscDevice device;
    public String name;
    public int rssi;
    public boolean isBonded;
    public boolean isTest;

    public ExtendedBluetoothDevice(@NonNull final FscDevice device, boolean isBonded,boolean isTest) {
        this.device = device;
        this.name = device.getName();
        this.rssi = NO_RSSI;
        this.isBonded = isBonded;
        this.isTest = isTest;

    }

    /*public ExtendedBluetoothDevice(@NonNull final ScanResult scanResult) {
        this.device = scanResult.getDevice();
        this.name = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName(): null;
        this.rssi = scanResult.getRssi();
        this.isBonded = true;

    }*/

    public boolean matched(@NonNull final FscDevice scanResult){
        return device.getAddress().equals(scanResult.getDevice().getAddress());
    }
}
