package com.hsproduce.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.StringSearch;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.bean.CheckReason;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_ACTION_SCODE;
import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_EX_SCODE;

/**
 * 新改质检页面
 * 扫描条码显示硫化条码明细和成型条码明细，使用广播监听扫描方式获取条码
 * 点击不合格出现弹窗输入不合格原因，如果是热补则进行热补复检
 * createBy zahngzr @ 2020-02-20
 */
public class NewCheckActivity extends BaseActivity {

    //轮胎条码
    private TextView tvBarCode;
    //获取计划按钮
    private ImageButton btGetPlan;
    //成型明细
    private TextView fSpesc, fSpescName, fMchId, fDate, fShift, fMaster, fState;
    //硫化明细
    private TextView vSpesc, vSpescName, vMchId, vLorR, vDate, vShift, vMaster, vState;
    //轮胎条码
    private List<VreCord> data1 = new ArrayList<>();
    private List<VreCord> data2 = new ArrayList<>();
    //控件
    private TitleBar tb_check1, tb_check2;
    private LinearLayout ll_checkName, ll_checkReson, ll_ok, ll_ok2;
    private Button bt_not, bt_out, bt_not2, bt_out2, bt_ok2;
    private TextView tv_lx, tv_reson, tv_time, tv_name;
    //轮胎条码
    private String sBarCode = "";
    //不合格原因
    private String reason = "";
    //备注
    private String remark = "";
    //类型
    final int[] size = {0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_new_check);
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

    public void initView() {
        //扫描框
        tvBarCode = (TextView) findViewById(R.id.BarCode);
        //限制位数
        tvBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        //获取计划按钮
        btGetPlan = (ImageButton) findViewById(R.id.getBarCode);
        //成型
        fSpesc = (TextView) findViewById(R.id.fspesc);
        fSpescName = (TextView) findViewById(R.id.fspescname);
        fMchId = (TextView) findViewById(R.id.fmchid);
        fDate = (TextView) findViewById(R.id.fdate);
        fShift = (TextView) findViewById(R.id.fshift);
        fMaster = (TextView) findViewById(R.id.fmaster);
        fState = (TextView) findViewById(R.id.fstate);
        //硫化
        vSpesc = (TextView) findViewById(R.id.vspesc);
        vSpescName = (TextView) findViewById(R.id.vspescname);
        vMchId = (TextView) findViewById(R.id.vmchid);
        vLorR = (TextView) findViewById(R.id.LorR);
        vDate = (TextView) findViewById(R.id.vdate);
        vShift = (TextView) findViewById(R.id.vshift);
        vMaster = (TextView) findViewById(R.id.vmaster);
        vState = (TextView) findViewById(R.id.vstate);

        //控件
        tb_check1 = (TitleBar) findViewById(R.id.check1);
        tb_check2 = (TitleBar) findViewById(R.id.check2);
        ll_checkName = (LinearLayout) findViewById(R.id.checkName);
        ll_checkReson = (LinearLayout) findViewById(R.id.checkReson);
        ll_ok = (LinearLayout) findViewById(R.id.ll_ok);
        ll_ok2 = (LinearLayout) findViewById(R.id.ll_ok2);
        bt_not = (Button) findViewById(R.id.not);
        bt_out = (Button) findViewById(R.id.out);
        bt_not2 = (Button) findViewById(R.id.not2);
        bt_out2 = (Button) findViewById(R.id.out2);
        bt_ok2 = (Button) findViewById(R.id.ok2);
        tv_lx = (TextView) findViewById(R.id.lx);
        tv_reson = (TextView) findViewById(R.id.reson);
        tv_time = (TextView) findViewById(R.id.time);
        tv_name = (TextView) findViewById(R.id.name);

    }

    public void initEvent() {
        //点击查询成型和硫化条码信息
        btGetPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarCode();
            }
        });
        //点击不合格按钮弹窗
        bt_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isNullOrEmpty(sBarCode)) {
                    Toast.makeText(NewCheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dialogToCheck();
                }
            }
        });
        //点击返回按钮
        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tofunction();
            }
        });
        //合格
        bt_ok2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parm = "barCode=" + sBarCode + "&isHg=0" + "&type=" + size[0] + "&reason=" + reason + "&remarks=" + remark + "&userName=" + App.username;
                new reExaminationTask().execute(parm);
                //刷新数据
                getBarCode();
            }
        });
        //不合格
        bt_not2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parm = "barCode=" + sBarCode + "&isHg=1" + "&type=" + size[0] + "&reason=" + reason + "&remarks=" + remark + "&userName=" + App.username;
                new reExaminationTask().execute(parm);
                //刷新数据
                getBarCode();
            }
        });
        //返回上一级
        bt_out2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diss();
                //刷新数据
                getBarCode();
            }
        });

    }

    //质检弹窗
    public void dialogToCheck() {
        //显示弹窗
        final MaterialDialog dialog = new MaterialDialog.Builder(NewCheckActivity.this)
                .customView(R.layout.dialog_checkbox, true)
                .show();
        //控件
        View customeView = dialog.getCustomView();
        //输入框
        final EditText edResult = dialog.findViewById(R.id.input);
        final EditText edRemark = dialog.findViewById(R.id.input2);
        //点击按钮
        Button returnDialog = customeView.findViewById(R.id.finish);
        Button okDialog = customeView.findViewById(R.id.ok);
        //复选框
        final CheckBox checkBox1 = customeView.findViewById(R.id.checkBox1);
        final CheckBox checkBox2 = customeView.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = customeView.findViewById(R.id.checkBox3);
        //点击取消
        returnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ////报废
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size[0] = 1;
            }
        });
        //打磨(B)
        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size[0] = 2;
            }
        });
        //热补(A)
        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size[0] = 3;
            }
        });

        //点击确定
        okDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不合格原因
                reason = "";
                //备注
                remark = "";
                remark = edRemark.getText().toString().trim();
                //判断不合格原因是否为空以及复选框是否选定
                if (size[0] != 0 && !StringUtil.isNullOrEmpty(edResult.getText().toString().trim())) {
                    if (size[0] == 1) {//报废
                        reason = edResult.getText().toString().trim().replace(".", "-");
                        check();
                    } else if (size[0] == 2) {//打磨(B)
                        reason = edResult.getText().toString().trim();
                        check();
                    } else if (size[0] == 3) {//热补(A)
                        reason = edResult.getText().toString().trim();
                        Check2();
                    } else {
                        reason = "";
                        Toast.makeText(NewCheckActivity.this, "报废类型和不合格原因不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    reason = "";
                    Toast.makeText(NewCheckActivity.this, "报废类型和不合格原因不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
            }
        });

    }

    //修改轮胎合格与否
    public void check() {
        //不合格原因
        String error = reason;
        if (error == null || error.equals("")) {
            Toast.makeText(NewCheckActivity.this, "不合格原因为必填项，请输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNullOrEmpty(sBarCode)) {
            Toast.makeText(NewCheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        String parm = "barCode=" + sBarCode + "&type=" + size[0] + "&reason=" + error + "&remarks=" + remark + "&userName=" + App.username;
        new QualityTestingTask().execute(parm);
        //刷新数据
        getBarCode();
    }

    //热补复检
    public void Check2() {
        //不合格原因
        String error = reason;
        if (error == null || error.equals("")) {
            Toast.makeText(NewCheckActivity.this, "不合格原因为必填项，请输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNullOrEmpty(sBarCode)) {
            Toast.makeText(NewCheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        String parm = "barCode=" + sBarCode + "&type=" + size[0] + "&reason=" + error + "&remarks=" + remark + "&userName=" + App.username;
        new QualityTestingTask().execute(parm);
        //三秒后执行
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //热补查询
        String parm1 = "barCode=" + sBarCode;
        new selQualityTestingTask().execute(parm1);

    }

    //展示
    public void show() {
        tb_check1.setVisibility(View.GONE);
        tb_check2.setVisibility(View.VISIBLE);
        ll_checkName.setVisibility(View.VISIBLE);
        ll_checkReson.setVisibility(View.VISIBLE);
        ll_ok.setVisibility(View.GONE);
        ll_ok2.setVisibility(View.VISIBLE);
    }

    //隐藏
    public void diss() {
        tb_check1.setVisibility(View.VISIBLE);
        tb_check2.setVisibility(View.GONE);
        ll_checkName.setVisibility(View.GONE);
        ll_checkReson.setVisibility(View.GONE);
        ll_ok.setVisibility(View.VISIBLE);
        ll_ok2.setVisibility(View.GONE);
    }

    //生产追溯 广播监听
    public void getBarCode(String barCode) {
        //获取输入机台上barcode
        if (StringUtil.isNullOrEmpty(barCode)) {
//            Toast.makeText(BarCodeDetailActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
            return;
        } else {
            String parm = "SwitchTYRE_CODE=" + barCode;
            new GetFormingDetail().execute(parm);
            new GetVulcanizaDetail().execute(parm);
        }
    }

    //生产追溯 按键监听
    public void getBarCode() {
        sBarCode = "";
        //获取输入机台上barcode
        sBarCode = tvBarCode.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(sBarCode) && sBarCode.length() == 12 && isNum(sBarCode) == true) {
            String parm = "SwitchTYRE_CODE=" + sBarCode;
            new GetFormingDetail().execute(parm);
            new GetVulcanizaDetail().execute(parm);
        } else {
            return;
        }
    }

    //广播监听
    private BroadcastReceiver scanDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
                try {
                    String barCode = "";
                    sBarCode = "";
                    barCode = intent.getStringExtra(SCN_CUST_EX_SCODE);
                    //判断条码是否为空 是否为12位 是否纯数字组成
                    if (!StringUtil.isNullOrEmpty(barCode) && barCode.length() == 12 && isNum(barCode) == true) {
                        sBarCode = barCode;
                        getBarCode(barCode);
                    } else {
                        Toast.makeText(NewCheckActivity.this, "请重新扫描", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ScannerService", e.toString());
                }
            }
        }
    };


    //获取成型明细
    class GetFormingDetail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGSECLECTCODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //清空
            fSpesc.setText("");
            fSpescName.setText("");
            fMchId.setText("");
            fDate.setText("");
            fShift.setText("");
            fMaster.setText("");
            fState.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(NewCheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(BarCodeDetailActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //赋值判断是否提示
                    data1.addAll(datas);
                    if (res.get("code").equals("200")) {
                        //成型明细
                        fSpesc.setText(datas.get(0).getItnbr());
                        fSpescName.setText(datas.get(0).getItdsc());
                        fMchId.setText(datas.get(0).getMchid());
                        fDate.setText(datas.get(0).getWdate().substring(0, 10));
                        fShift.setText(datas.get(0).getShift());
                        fMaster.setText(datas.get(0).getCreateuser());
                        fState.setText(datas.get(0).getiS_H());
                    } else {
//                        Toast.makeText(BarCodeDetailActivity.this, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewCheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

    //获取硫化明细
    class GetVulcanizaDetail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelDetailed, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //清空
            vSpesc.setText("");
            vSpescName.setText("");
            vMchId.setText("");
            vLorR.setText("");
            vDate.setText("");
            vShift.setText("");
            vMaster.setText("");
            vState.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(NewCheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(BarCodeDetailActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //赋值判断是否提示
                    data2.addAll(datas);

                    if (res.get("code").equals("200")) {
                        //硫化明细
                        vSpesc.setText(datas.get(0).getItnbr());
                        vSpescName.setText(datas.get(0).getItdsc());
                        vMchId.setText(datas.get(0).getMchid());
                        vLorR.setText(datas.get(0).getLr());
                        vDate.setText(datas.get(0).getWdate().substring(0, 10).replaceAll("/", "-"));
                        vShift.setText(datas.get(0).getShift());
                        vMaster.setText(datas.get(0).getCreateuser());
                        vState.setText(datas.get(0).getiS_H());
                    } else {
//                        Toast.makeText(BarCodeDetailActivity.this, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                        if ((data1 == null && data2 == null) || (data1.isEmpty() && data2.isEmpty())) {
                            Toast.makeText(NewCheckActivity.this, "没有条码明细", Toast.LENGTH_SHORT).show();
                        }
                    }
                    data1.clear();
                    data2.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewCheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

    //质检 不合格品登记
    class QualityTestingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.qualityTesting_N, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(NewCheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(NewCheckActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewCheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //查询热补
    class selQualityTestingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.selQualityTesting, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //清空数据
            tv_lx.setText("");
            tv_reson.setText("");
            tv_time.setText("");
            tv_name.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(NewCheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<CheckReason> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<CheckReason>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(NewCheckActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //填入信息
                        if (datas.get(0).getType().equals("1")) {
                            tv_lx.setText("报废");
                        } else if (datas.get(0).getType().equals("2")) {
                            tv_lx.setText("打磨");
                        } else if (datas.get(0).getType().equals("3")) {
                            tv_lx.setText("热补");
                        } else {
                            tv_lx.setText(datas.get(0).getType());
                        }
                        if (datas.get(0).getReason().equals("A-1")) {
                            tv_reson.setText(datas.get(0).getReason() + " 杂物");
                        } else if (datas.get(0).getReason().equals("A-2")) {
                            tv_reson.setText(datas.get(0).getReason() + " 缺胶");
                        } else if (datas.get(0).getReason().equals("A-3")) {
                            tv_reson.setText(datas.get(0).getReason() + " 气泡");
                        } else if (datas.get(0).getReason().equals("A-4")) {
                            tv_reson.setText(datas.get(0).getReason() + " 损伤");
                        } else if (datas.get(0).getReason().equals("A-5")) {
                            tv_reson.setText(datas.get(0).getReason() + " 杂裂口物");
                        } else if (datas.get(0).getReason().equals("A-6")) {
                            tv_reson.setText(datas.get(0).getReason() + " 周牌号偏");
                        } else {
                            tv_reson.setText(datas.get(0).getReason());
                        }
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        tv_time.setText(df.format(df.parse(datas.get(0).getCreatetime())));
                        tv_name.setText(datas.get(0).getCreateuser());
                        //展示
                        show();
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewCheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //复检
    class reExaminationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.reExamination, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(NewCheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(NewCheckActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //清空数据
                        sBarCode = "";
                        reason = "";
                        remark = "";
                        size[0] = 0;
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(NewCheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NewCheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onPause() {
        unregisterReceiver(scanDataReceiver);
        super.onPause();
    }

    //是否纯数字
    public Boolean isNum(String s) {
        char[] ch = s.toCharArray();
        for (char c : ch) {
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //按键按下
        switch (keyCode) {
            case 0://扫描键按下清空
                tvBarCode.setText("");
                //获得焦点
                tvBarCode.requestFocus();
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode) {
            case 22://右方向键
                getBarCode();
                break;
            case 4://返回键
                sBarCode = "";
                tofunction();//返回功能菜单页面
                break;
            case 131://F1键
                if (StringUtil.isNullOrEmpty(sBarCode)) {
                    Toast.makeText(NewCheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
                } else {
                    dialogToCheck();
                }
                break;
            case 132://F2键
                sBarCode = "";
                tofunction();//返回功能菜单页面
                break;
            default:
                break;
        }
        return true;
    }

}
