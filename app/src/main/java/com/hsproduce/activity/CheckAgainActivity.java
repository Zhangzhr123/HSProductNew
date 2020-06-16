package com.hsproduce.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hsproduce.broadcast.SystemBroadCast;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_ACTION_SCODE;
import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_EX_SCODE;

/**
 * 热补复检检页面
 * 扫描条码显示质检信息、硫化条码明细和成型条码明细，使用广播监听扫描方式获取条码
 * 点击不合格出现弹窗输入不合格原因，点击合格则直接设为合格
 * createBy zahngzr @ 2020-03-09
 * 1.封装SDK
 */
public class CheckAgainActivity extends BaseActivity {

    //轮胎条码
    private TextView tvBarCode;
    //获取计划按钮
    private ImageButton btGetPlan;
    //质检信息
    private TextView tv_Lx, tv_Reson, tv_Time, tv_Name;
    //成型明细
    private TextView fSpesc, fSpescName, fMchId, fDate, fShift, fMaster, fState;
    //硫化明细
    private TextView vSpesc, vSpescName, vMchId, vLorR, vDate, vShift, vMaster, vState;
    //轮胎条码
    private List<VreCord> data1 = new ArrayList<>();
    private List<VreCord> data2 = new ArrayList<>();
    //控件
    private Button bt_Not, bt_Out, bt_Ok;
    //轮胎条码
    private String sBarCode = "";
    //不合格原因
    private String reason = "", error;
    //备注
    private String remark = "";
    //类型
    final int[] size = {0};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_check_again);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onResume() {
//        //注册广播监听
//        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);
//        registerReceiver(scanDataReceiver, intentFilter);
//        super.onResume();
//    }

    public void initView() {
        //扫描框
        tvBarCode = (TextView) findViewById(R.id.BarCode);
        //限制位数
        tvBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        //获取计划按钮
        btGetPlan = (ImageButton) findViewById(R.id.getBarCode);
        //质检
        tv_Lx = (TextView) findViewById(R.id.lx);
        tv_Reson = (TextView) findViewById(R.id.reson);
        tv_Time = (TextView) findViewById(R.id.time);
        tv_Name = (TextView) findViewById(R.id.name);
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
        bt_Not = (Button) findViewById(R.id.not);
        bt_Out = (Button) findViewById(R.id.out);
        bt_Ok = (Button) findViewById(R.id.ok);


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
        bt_Not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isNullOrEmpty(sBarCode)) {
                    Toast.makeText(CheckAgainActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dialogToCheck();
                }
            }
        });

        //点击返回按钮
        bt_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBarCode = "";
                reason = "";
                remark = "";
                size[0] = 0;
                tvBarCode.setText("");
                tofunction();
            }
        });

        //点击合格按钮
        bt_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck();
            }
        });
    }

    //合格
    public void isCheck() {
        if (StringUtil.isNullOrEmpty(sBarCode)) {
            Toast.makeText(CheckAgainActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNullOrEmpty(error)) {
            Toast.makeText(CheckAgainActivity.this, "不合格原因为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String parm = "barCode=" + sBarCode + "&isHg=0" + "&type=3" + "&reason=" + error + "&remarks=" + "&userName=" + App.username;
        new reExaminationTask().execute(parm);
    }

    //质检弹窗
    public void dialogToCheck() {
        //显示弹窗
        final MaterialDialog dialog = new MaterialDialog.Builder(CheckAgainActivity.this)
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
        //单选列表
        final RadioGroup group_temo = (RadioGroup) customeView.findViewById(R.id.rg_1);
        //获取默认被被选中值
        final RadioButton[] checkRadioButton = {(RadioButton) group_temo.findViewById(group_temo.getCheckedRadioButtonId())};
        //注册事件
        group_temo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //点击事件获取的选择对象
                checkRadioButton[0] = (RadioButton) group_temo.findViewById(checkedId);
                if(checkRadioButton[0].getText().equals("报废")){
                    size[0] = 1;
                }else if(checkRadioButton[0].getText().equals("打磨(B")){
                    size[0] = 2;
                }else if(checkRadioButton[0].getText().equals("热补(A")){
                    size[0] = 3;
                }else{
                    Toast.makeText(CheckAgainActivity.this, "请选择报废类型", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击取消
        returnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                        check();
                    } else {
                        reason = "";
                        Toast.makeText(CheckAgainActivity.this, "报废类型和不合格原因不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    reason = "";
                    Toast.makeText(CheckAgainActivity.this, "报废类型和不合格原因不能为空", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(CheckAgainActivity.this, "不合格原因为必填项，请输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNullOrEmpty(sBarCode)) {
            Toast.makeText(CheckAgainActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        String parm = "barCode=" + sBarCode + "&isHg=1" + "&type=" + size[0] + "&reason=" + error + "&remarks=" + remark + "&userName=" + App.username;
        new reExaminationTask().execute(parm);
    }


    //生产追溯 广播监听
    public void getBarCode(String barCode) {
        //获取输入机台上barcode
        if (StringUtil.isNullOrEmpty(barCode)) {
//            Toast.makeText(BarCodeDetailActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
            return;
        } else {
            String parm = "SwitchTYRE_CODE=" + barCode;
            String parm1 = "barCode=" + barCode;
            new selQualityTestingTask().execute(parm1);
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
            String parm1 = "barCode=" + sBarCode;
            new selQualityTestingTask().execute(parm1);
            new GetFormingDetail().execute(parm);
            new GetVulcanizaDetail().execute(parm);
        } else {
            return;
        }
    }

    //广播监听
//    private BroadcastReceiver scanDataReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
//                try {
//                    String barCode = "";
//                    sBarCode = "";
//                    barCode = intent.getStringExtra(SCN_CUST_EX_SCODE);
//                    //判断条码是否为空 是否为12位 是否纯数字组成
//                    if (!StringUtil.isNullOrEmpty(barCode) && barCode.length() == 12 && isNum(barCode) == true) {
//                        sBarCode = barCode;
//                        getBarCode(barCode);
//                    } else {
//                        Toast.makeText(CheckAgainActivity.this, "请重新扫描", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("ScannerService", e.toString());
//                }
//            }
//        }
//    };


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
                Toast.makeText(CheckAgainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CheckAgainActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CheckAgainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CheckAgainActivity.this, "没有条码明细", Toast.LENGTH_SHORT).show();
                        }
                    }
                    data1.clear();
                    data2.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CheckAgainActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

    //查询热补质检信息
    class selQualityTestingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.selQualityTesting, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //清空数据
            tv_Lx.setText("");
            tv_Reson.setText("");
            tv_Time.setText("");
            tv_Name.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(CheckAgainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<CheckReason> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<CheckReason>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(CheckAgainActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //填入信息
                        if (datas.get(0).getType().equals("1")) {
                            tv_Lx.setText("报废");
                        } else if (datas.get(0).getType().equals("2")) {
                            tv_Lx.setText("打磨");
                        } else if (datas.get(0).getType().equals("3")) {
                            tv_Lx.setText("热补");
                        } else {
                            tv_Lx.setText(datas.get(0).getType());
                        }
                        //获取不合格原因
                        error = datas.get(0).getReason();
                        tv_Reson.setText(datas.get(0).getReason());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        tv_Time.setText(df.format(df.parse(datas.get(0).getCreatetime())));
                        tv_Name.setText(datas.get(0).getCreateuser());
                        Toast.makeText(CheckAgainActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(CheckAgainActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CheckAgainActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CheckAgainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(CheckAgainActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //清空数据
                        sBarCode = "";
                        reason = "";
                        remark = "";
                        size[0] = 0;
                        tvBarCode.setText("");
                        Toast.makeText(CheckAgainActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(CheckAgainActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CheckAgainActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onPause() {
//        unregisterReceiver(scanDataReceiver);
//        super.onPause();
//    }

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
            case 0://扫描键
                if(App.pdaType.equals("PDA")){
                    sBarCode = "";
                    if (!StringUtil.isNullOrEmpty(SystemBroadCast.barCode) && (SystemBroadCast.barCode).length() == 12 && isNum(SystemBroadCast.barCode) == true) {
                        sBarCode = SystemBroadCast.barCode;
                        getBarCode(SystemBroadCast.barCode);
                    } else {
                        SystemBroadCast.barCode = "";
                        Toast toast = Toast.makeText(CheckAgainActivity.this, "请重新扫描", Toast.LENGTH_LONG);
                        showMyToast(toast, 500);
                        break;
                    }
                    SystemBroadCast.barCode = "";
                }
                break;
            case 288://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            case 289://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            case 290://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            case 22://右方向键
                getBarCode();
                break;
            case 4://返回键
                sBarCode = "";
                reason = "";
                remark = "";
                size[0] = 0;
                tvBarCode.setText("");
                tofunction();//返回功能菜单页面
                break;
            case 131://F1键
                isCheck();
                break;
            case 132://F2键
                if (StringUtil.isNullOrEmpty(sBarCode)) {
                    Toast.makeText(CheckAgainActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
                } else {
                    dialogToCheck();
                }
                break;
            case 133://F3键
                sBarCode = "";
                reason = "";
                remark = "";
                size[0] = 0;
                tvBarCode.setText("");
                tofunction();//返回功能菜单页面
                break;
            default:
                break;
        }
        return true;
    }

    public void scan(String barcode) {
        //霍尼韦尔
        if (App.pdaType.equals("EDA50KP-3")) {
            if (!StringUtil.isNullOrEmpty(barcode) && barcode.length() == 12) {
                sBarCode = barcode;
                tvBarCode.setText(barcode);
                getBarCode(barcode);
            }
            BaseActivity.tvBarCode = "";
        }
    }

}
