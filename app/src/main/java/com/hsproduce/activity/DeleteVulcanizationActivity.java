package com.hsproduce.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.VPlanAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.SoundPlayUtils;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_ACTION_SCODE;
import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_EX_SCODE;

/**
 * 硫化取消扫描页面
 * 扫描机台号直接进入扫描条码页面，扫描条码，删除此条码的生产实绩
 * 广播监听回调触发事件，成功后此条码写入扫描纪录框中
 * createBy zhangzhr @ 2019-12-30
 */
public class DeleteVulcanizationActivity extends BaseActivity {

    //view 输入机台  记录条码  扫描条码
    private View ll_Mchid, ll_CodeLog, ll_Code;
    //计划展示list
    private ListView listView;
    //机台号  轮胎条码 条码计数  条码记录
    private TextView tvBarCode, tvAnum, tvBarCodeLog;
    private TextView tvMchid;
    //计划按钮   扫描按钮
    private ButtonView btGetplan, btBarCode_Ok;
    //加载
    private MiniLoadingView loadingView;
    //计划展示适配器
    private VPlanAdapter adapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //保证每次操作完成后再进行下一次操作
    public boolean iscomplate = true;
    //绑定条码个数
    private int number = 0;
    //轮胎条码
    private String barCode = "", planId = "";
    private List<String> list = new ArrayList<>();
    //添加条码防止重复扫描
    private List<String> codeList = new ArrayList<>();
    private Boolean isVual = false;
    //判断弹窗是否已经存在
    private MaterialDialog materialDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_vulcanization);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onResume() {
        //注册广播监听
        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);
        registerReceiver(scanDataReceiver, intentFilter);
        super.onResume();
    }

    //初始化控件
    public void initView() {
        //layout
        ll_Code = findViewById(R.id.ll_code);
        ll_Mchid = findViewById(R.id.ll_mchid);
        ll_CodeLog = findViewById(R.id.ll_codelog);
        //list列表
        listView = (ListView) findViewById(R.id.lv_plan);
        //扫描框
        tvMchid = (TextView) findViewById(R.id.mchid);
        //条码扫描框
        tvBarCode = (TextView) findViewById(R.id.barcode);
        //条码记录
        tvBarCodeLog = (TextView) findViewById(R.id.barcode_log);
        tvBarCodeLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        //不可编辑
        tvBarCodeLog.setFocusable(false);
        tvBarCodeLog.setFocusableInTouchMode(false);
        //扫描条码计数
        tvAnum = (TextView) findViewById(R.id.anum);
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.bt_getPlan);
        //条码按钮
        btBarCode_Ok = (ButtonView) findViewById(R.id.barcode_ok);
        //加载条
        loadingView = (MiniLoadingView) findViewById(R.id.loading);
        //设置条码扫描框输入字符数
        tvBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
    }


    //初始化事件
    public void initEvent() {
        //获取机台计划
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示加载
                loadingView.setVisibility(View.VISIBLE);
                //获取计划
                getPlan();
            }
        });
        //获取扫描条码绑定计划
        btBarCode_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarCode();
            }
        });
    }

    //获取计划  按键调用
    public void getPlan() {
        //获取输入机台上barcode
        String mchid = tvMchid.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(mchid)) {
            mchid = mchid.toUpperCase();
        }
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(DeleteVulcanizationActivity.this, "请扫描机台号", Toast.LENGTH_SHORT).show();
        } else {
            String param = "MCHIDLR=" + mchid + "&SHIFT=" + App.shift;
            new MyTask().execute(param);
        }
    }

    //获取轮胎条码  按键调用
    public void getBarCode() {
        //获取轮胎上barcode
        barCode = tvBarCode.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(barCode)) {
            Toast.makeText(DeleteVulcanizationActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
        } else {
            //扫描记录中是否已经存在该条码，存在提示已扫描，不存在调用接口记录硫化记录
            if (codeList.contains(barCode)) {
                Toast.makeText(DeleteVulcanizationActivity.this, "此条码已经扫描", Toast.LENGTH_SHORT).show();
                tvBarCode.setText("");
            } else {
                //记录硫化生产记录
//                String param1 = "PLAN_ID=" + planId + "&barcode=" + barCode + "&User_Name=" + App.username + "&TEAM=" + App.shift + "&doit=0";
//                new TypeCodeTask().execute(param1);
            }
        }
    }

    //获取计划  广播监听调用
    public void getPlan(String mchid) {
        //获取输入机台上barcode
        if (!StringUtil.isNullOrEmpty(mchid)) {
            mchid = mchid.toUpperCase();
        }
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(DeleteVulcanizationActivity.this, "请扫描机台号", Toast.LENGTH_SHORT).show();
        } else {
            String param = "MCHIDLR=" + mchid + "&SHIFT=" + App.shift;
            new MyTask().execute(param);
            Toast.makeText(DeleteVulcanizationActivity.this, mchid, Toast.LENGTH_SHORT).show();
        }
    }

    //获取轮胎条码  广播监听调用
    public void getBarCode(String barCode) {
        //获取轮胎上barcode
        if (StringUtil.isNullOrEmpty(barCode)) {
            Toast.makeText(DeleteVulcanizationActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
        } else {
            //扫描记录中是否已经存在该条码，存在提示已扫描，不存在调用接口记录硫化记录
            if (codeList.contains(barCode)) {
                Toast.makeText(DeleteVulcanizationActivity.this, "此条码已经扫描", Toast.LENGTH_SHORT).show();
                tvBarCode.setText("");
            } else {
                //记录硫化生产记录
//                String param1 = "PLAN_ID=" + planId + "&barcode=" + barCode + "&User_Name=" + App.username + "&TEAM=" + App.shift + "&doit=0";
//                new TypeCodeTask().execute(param1);
                Toast.makeText(DeleteVulcanizationActivity.this, barCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //广播监听
    private BroadcastReceiver scanDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
                try {
                    String massage = "";
                    massage = intent.getStringExtra(SCN_CUST_EX_SCODE);
                    //判断条码是否为空
                    if (!StringUtil.isNullOrEmpty(massage)) {
                        massage = massage.toUpperCase();
                        if (massage.length() == 4 && (massage.endsWith("L") || massage.endsWith("R"))) {
                            getPlan(massage);
                        } else if (massage.length() == 12 && isNum(massage) == true) {
                            barCode = massage;
                            getBarCode(massage);
                        }
                    } else {
                        Toast.makeText(DeleteVulcanizationActivity.this, "请重新扫描", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ScannerService", e.toString());
                }
            }
        }
    };

    public Boolean isNum(String s) {
        char[] ch = s.toCharArray();
        for (char c : ch) {
            if (!(c > '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    //查询任务
    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.VUL_GET_PLAN, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(DeleteVulcanizationActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(DeleteVulcanizationActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                    }
                    if (res.get("code").equals("200")) {
                        //获取计划ID
                        planId = datas.get(0).getId();
                        //显示生产
                        showVual();
                        //展示列表
                        adapter = new VPlanAdapter(DeleteVulcanizationActivity.this, datas);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        tvMchid.setText("");
                    } else {
                        Toast.makeText(DeleteVulcanizationActivity.this, res.get("msg") + "", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeleteVulcanizationActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //显示硫化生产
    private void showVual() {
        //关闭加载
        loadingView.setVisibility(View.GONE);
        ll_Mchid.setVisibility(View.GONE);
        //显示硫化扫描
        listView.setVisibility(View.VISIBLE);
        ll_Code.setVisibility(View.VISIBLE);
        ll_CodeLog.setVisibility(View.VISIBLE);
        //获得焦点
        tvBarCode.requestFocus();
        isVual = true;
    }

    //显示扫描机台
    private void showMchid() {
        ll_Mchid.setVisibility(View.VISIBLE);
        //显示硫化扫描
        listView.setVisibility(View.GONE);
        ll_Code.setVisibility(View.GONE);
        ll_CodeLog.setVisibility(View.GONE);
        //获得焦点
        tvMchid.requestFocus();
        isVual = false;
    }

    //新增硫化生产记录
    class TypeCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.VUL_AddActualAchievement, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(DeleteVulcanizationActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(DeleteVulcanizationActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                    }
                    if (res.get("code").equals("200")) {
                        tvBarCodeLog.append(barCode + "\n");
                        tvBarCode.setText("");
                        codeList.add(barCode);
                        tvAnum.setText(codeList.size() + "");
                    } else {
                        Toast.makeText(DeleteVulcanizationActivity.this, res.get("msg") + "", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeleteVulcanizationActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //递归显示
    public String getlog(List<String> list) {
        String logstr = "";
        Collections.reverse(list);//倒序
        for (int i = 0; i < list.size(); i++) {
            logstr += list.get(i) + "\n";
        }
        return logstr;
    }


    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");

        switch (keyCode) {
            case 0:
                if (isVual) {
                    tvBarCode.requestFocus();
                } else {
                    tvMchid.requestFocus();
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //扫描键 弹开时获取计划
        //右方向键
        String msg = "";
        switch (keyCode) {
            //返回键
            case 4:
                //返回上级页面
                //先返回扫描机台，再返回功能页
                if (isVual) {
                    codeList.clear();
                    tvBarCodeLog.setText("");
                    tvBarCode.setText("");
                    tvMchid.setText("");
                    tvAnum.setText("0");
                    showMchid();
                } else {
                    startActivity(new Intent(DeleteVulcanizationActivity.this, FunctionActivity.class));
                    this.finish();
                }
                break;
            //右方向键
            case 22:
                operate();
                break;
//            //回车键
//            case 66:
//                msg = "扫描失败！";
//                operate(msg);
//                break;
//            //扫描键
//            case 0:
//                msg = "扫描失败！";
//                operate(msg);
//                break;
            default:

                break;

        }
        return true;
    }

    private void operate() {
        if (!StringUtil.isNullOrEmpty(tvBarCode.getText().toString().trim())) {
            getBarCode();
        } else if (!StringUtil.isNullOrEmpty(tvMchid.getText().toString().trim())) {
            getPlan();
        } else {
//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPause() {
        unregisterReceiver(scanDataReceiver);
        super.onPause();
    }


}
