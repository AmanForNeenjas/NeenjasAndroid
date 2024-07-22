package com.hjq.demo.config;

public class BleConfig {
    public static boolean debug = false;
    public static String LOG_TAG = "Ble";
    public static int segmentationSleepTime = 60000; // 单位毫秒
    public static boolean segmentationAddIndex = false;
    public static int bleWriteDelayedTime = 500;   // 发布命令延迟时长（毫秒）
    public static int maxRetryNumber = 3;          // 命令发送失败重发次数（毫秒）
    public static int retrySleepTime = 1000;       // 命令重发时间间隔
    public static int reconnectTime = 20000;       // 重连时间（毫秒）
    public static int maxReconnectNumber = 3;      // 重连次数
    public static int scanBleTimeoutTime = 15000;  // 连接扫描时间，比重连时间短
    public static int bleResultWaitTime = 2000;    // 命令發送后等待结果时间，到时才能执行下一个命令任务。也能通过BleUitl.finishResult()立即执行下个命令
    public static int autConnectTime = 30000;      // 自动重连时间（毫秒）
    public static int MTU = 20;                    // MTU值，大于20才有效
}
