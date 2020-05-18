package com.hsproduce.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.hsproduce.activity.BarCodeDetailActivity;
import com.hsproduce.util.StringUtil;

public class SystemBroadCast {

    //扫描条码服务广播
    //Scanning barcode service broadcast.
    public static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
    //条码扫描数据广播
    //Barcode scanning data broadcast.
    public static final String SCN_CUST_EX_SCODE = "scannerdata";

    public static String barCode = "";

    public static void addBroadcastReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);
        context.registerReceiver(scanDataReceiver, intentFilter);
    }

    public static void closeBroadcastReceiver(Context context){
        context.unregisterReceiver(scanDataReceiver);
    }

    //调用广播监听方法
    public  static BroadcastReceiver scanDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
                try {
                    barCode = "";
                    barCode = intent.getStringExtra(SCN_CUST_EX_SCODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ScannerService", e.toString());
                }
            }
        }
    };
}
