package com.hsproduce.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.honeywell.aidc.*;
import com.hsproduce.App;
import com.hsproduce.broadcast.SystemBroadCast;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.hsproduce.broadcast.SystemBroadCast.addBroadcastReceiver;
import static com.hsproduce.broadcast.SystemBroadCast.closeBroadcastReceiver;

/**
 * 基础页面
 * createBy zhangzr @ 2019-12-21
 */
public abstract class BaseActivity extends Activity implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, AidcManager.BarcodeDeviceListener {

    public static final String TAG = "example_demo";

    private AidcManager mAidcManager;
    private BarcodeReader mBarcodeReader;
    private BarcodeReader mInternalScannerReader;
    private boolean mKeyPressed = false;

    public static String tvBarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //弹出软键盘
        //判断PDA类型
        if (App.pdaType.equals("PDA")) {
            //PDA调用注册广播监听
            addBroadcastReceiver(BaseActivity.this);
        }else if (App.pdaType.equals("EDA50KP-3")){
            //广播监听
            Log.d(TAG, "MainActivity onCreate !!!");
            AidcManager.create(this, new MyCreatedCallback());
        }
    }

    //调度触摸事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        super.dispatchTouchEvent(ev);
        return false;
    }

    //根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //返回
    public void back(View v) {
        finish();
    }

    //登出
    public void logout(View v) {
        final android.app.AlertDialog.Builder normalDialog = new android.app.AlertDialog.Builder(this);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage("确定退出当前用户？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new LogoutTask().execute();
                        App.ip = "";
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();

    }

    //用户提示信息
    public void prompt(String v) {
        final android.app.AlertDialog.Builder normalDialog = new android.app.AlertDialog.Builder(this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(v);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    //返回功能页面
    public void tofunction() {
        startActivity(new Intent(this, FunctionActivity.class));
        finish();
    }

    //返回主页面
    public void tomain(View v) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //初始权限
    public void initpermission() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
    }

    //logout 线程
    class LogoutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpUtil.sendGet(PathUtil.LOGOUT, "");
            return null;
        }
    }

    //设置弹出信息时长
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 1000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPause() {
        //判断PDA类型
        if (App.pdaType.equals("PDA")) {
            //PDA关闭广播监听
            closeBroadcastReceiver(BaseActivity.this);
        }else if (App.pdaType.equals("EDA50KP-3")){
            if (this.mInternalScannerReader != null) {
                this.mInternalScannerReader.release();
                Log.d(TAG, "Release internal scanner");
            }
        }
        super.onPause();
    }

    class MyCreatedCallback implements AidcManager.CreatedCallback {
        MyCreatedCallback() {
        }

        @Override
        public void onCreated(AidcManager aidcManager) {
            Log.d(TAG, "MyCreatedCallback onCreate !!!");
            mAidcManager = aidcManager;
            mAidcManager.addBarcodeDeviceListener(BaseActivity.this);
            initAllBarcodeReaderAndSetDefault();
        }
    }

    void initAllBarcodeReaderAndSetDefault() {
        List<BarcodeReaderInfo> readerList = mAidcManager.listBarcodeDevices();
        Log.d(TAG, "initAllBarcodeReaderAndSetDefault readerList = "+readerList);
        mInternalScannerReader = null;

        for (BarcodeReaderInfo reader : readerList) {
            if ("dcs.scanner.imager".equals(reader.getName())) {
                mInternalScannerReader = initBarcodeReader(mInternalScannerReader, reader.getName());
            }
        }

        Log.d(TAG, "initAllBarcodeReaderAndSetDefault mInternalScannerReader = "+mInternalScannerReader);

        if (mInternalScannerReader != null) {
            mBarcodeReader = mInternalScannerReader;
        }
        else {
            Log.d(TAG, "No reader find");
        }
        if (mBarcodeReader != null) {
            try {
                mBarcodeReader.addBarcodeListener(this);
                mBarcodeReader.addTriggerListener(this);
            }
            catch (Throwable e2) {
                e2.printStackTrace();
            }
            try {
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);

                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_EAN_8_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_39_FULL_ASCII_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_93_ENABLED, true);
            } catch (UnsupportedPropertyException e) {
                e.printStackTrace();
            }

        }
    }

    BarcodeReader initBarcodeReader(BarcodeReader mReader, String mReaderName) {
        if (mReader == null) {
            if (mReaderName == null) {
                mReader = mAidcManager.createBarcodeReader();
            } else {
                mReader = mAidcManager.createBarcodeReader(mReaderName);
            }
            try {
                mReader.claim();
                Log.d(TAG, "Call DCS interface claim() " + mReaderName);
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
            try {
                mReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);

            } catch (UnsupportedPropertyException e2) {
                e2.printStackTrace();
            }
        }
        return mReader;
    }

    public void onBarcodeDeviceConnectionEvent(BarcodeDeviceConnectionEvent event) {
        Log.d(TAG, event.getBarcodeReaderInfo() + " Connection status: " + event.getConnectionStatus());
    }

    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {

                Log.d(TAG,"Enter onBarcodeEvent ==> "+ event.getBarcodeData());
                String barcodeDate = new String(event.getBarcodeData().getBytes(event.getCharset()));
                Log.d(TAG, "Enter onBarcodeEvent ==> " + barcodeDate);

                tvBarCode = barcodeDate;
            }
        });
    }

    public void onFailureEvent(final BarcodeFailureEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Enter onFailureEvent ===> " + event.getTimestamp());
                tvBarCode = "failed!";
            }
        });
    }

    public void onTriggerEvent(TriggerStateChangeEvent event) {
        if (event.getState()) {
            if (!mKeyPressed) {
                mKeyPressed = true;
                doScan(true);
            }
        } else {
            mKeyPressed = false;
            doScan(false);
        }
        Log.d(TAG, "OnTriggerEvent status: " + event.getState());
    }

    protected void onResume() {
        super.onResume();
        if (this.mInternalScannerReader != null) {
            try {
                this.mInternalScannerReader.claim();
                Log.d(TAG, "Claim internal scanner");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (this.mInternalScannerReader != null) {
            this.mInternalScannerReader.removeBarcodeListener(this);
            this.mInternalScannerReader.removeTriggerListener(this);
            this.mInternalScannerReader.close();
            this.mInternalScannerReader = null;
            Log.d(TAG, "Close internal scanner");
        }
        if (this.mAidcManager != null) {
            this.mAidcManager.removeBarcodeDeviceListener(this);
            this.mAidcManager.close();
        }
    }

    void doScan(boolean do_scan) {
        try {
            if (do_scan) {
                Log.d(TAG, "Start a new Scan!");
            } else {
                Log.d(TAG, "Cancel last Scan!");
            }
            mBarcodeReader.decode(do_scan);
        } catch (ScannerNotClaimedException e) {
            Log.e(TAG, "catch ScannerNotClaimedException",e);
            e.printStackTrace();
        } catch (ScannerUnavailableException e2) {
            Log.e(TAG, "catch ScannerUnavailableException",e2);
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

}
