package com.hsproduce.activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.FormingItemAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生产追溯页面
 * 扫描条码显示硫化条码明细和成型条码明细
 * createBy zahngzr @ 2019-12-20
 */
public class BarCodeDetailActivity extends BaseActivity {

    //轮胎条码
    private TextView barCode;
    //获取计划按钮
    private ImageButton btGetplan;
    //成型明细
    private TextView fspesc, fspescname, fmchid, fdate, fshift, fmaster, fstate;
    //硫化明细
    private TextView vspesc, vspescname, vmchid, lorR, vdate, vshift, vmaster, vstate;
    //轮胎条码
    public String tvbarCode = "";
    private List<VreCord> data1 = new ArrayList<>();
    private List<VreCord> data2 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_barcodedetail);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //扫描框
        barCode = (TextView) findViewById(R.id.BarCode);
        //获得焦点
        barCode.requestFocus();
        //获取计划按钮
        btGetplan = (ImageButton) findViewById(R.id.getBarCode);
        //成型
        fspesc = (TextView) findViewById(R.id.fspesc);
        fspescname = (TextView) findViewById(R.id.fspescname);
        fmchid = (TextView) findViewById(R.id.fmchid);
        fdate = (TextView) findViewById(R.id.fdate);
        fshift = (TextView) findViewById(R.id.fshift);
        fmaster = (TextView) findViewById(R.id.fmaster);
        fstate = (TextView) findViewById(R.id.fstate);
        //硫化
        vspesc = (TextView) findViewById(R.id.vspesc);
        vspescname = (TextView) findViewById(R.id.vspescname);
        vmchid = (TextView) findViewById(R.id.vmchid);
        lorR = (TextView) findViewById(R.id.LorR);
        vdate = (TextView) findViewById(R.id.vdate);
        vshift = (TextView) findViewById(R.id.vshift);
        vmaster = (TextView) findViewById(R.id.vmaster);
        vstate = (TextView) findViewById(R.id.vstate);

    }

    public void initEvent() {
        //点击查询成型和硫化条码信息
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentVPlan();
            }
        });
    }

    //生产追溯
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        tvbarCode = barCode.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(tvbarCode)) {
//            Toast.makeText(BarCodeDetailActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
            return;
        } else {
            String parm = "SwitchTYRE_CODE=" + tvbarCode;
            new GetFormingDetail().execute(parm);
            new GetVulcanizaDetail().execute(parm);
        }
    }


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
            fspesc.setText("");
            fspescname.setText("");
            fmchid.setText("");
            fdate.setText("");
            fshift.setText("");
            fmaster.setText("");
            fstate.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarCodeDetailActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
                    data1.addAll(datas);

                    if (res.get("code").equals("200")) {
                        //成型明细
                        fspesc.setText(datas.get(0).getItnbr());
                        fspescname.setText(datas.get(0).getItdsc());
                        fmchid.setText(datas.get(0).getMchid());
                        fdate.setText(datas.get(0).getWdate().substring(0, 10));
                        fshift.setText(datas.get(0).getShift());
                        fmaster.setText(datas.get(0).getCreateuser());
                        fstate.setText(datas.get(0).getiS_H());
                    } else {
//                        Toast.makeText(BarCodeDetailActivity.this, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarCodeDetailActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
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
            vspesc.setText("");
            vspescname.setText("");
            vmchid.setText("");
            lorR.setText("");
            vdate.setText("");
            vshift.setText("");
            vmaster.setText("");
            vstate.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarCodeDetailActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
                    data2.addAll(datas);

                    if (res.get("code").equals("200")) {
                        //硫化明细
                        vspesc.setText(datas.get(0).getItnbr());
                        vspescname.setText(datas.get(0).getItdsc());
                        vmchid.setText(datas.get(0).getMchid());
                        lorR.setText(datas.get(0).getLr());
                        vdate.setText(datas.get(0).getWdate().substring(0, 10).replaceAll("/", "-"));
                        vshift.setText(datas.get(0).getShift());
                        vmaster.setText(datas.get(0).getCreateuser());
                        vstate.setText(datas.get(0).getiS_H());
                    } else {
//                        Toast.makeText(BarCodeDetailActivity.this, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                        if ((data1 == null && data2 == null) || (data1.isEmpty() && data2.isEmpty())) {
                            Toast.makeText(BarCodeDetailActivity.this, "没有条码明细", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        return;
                    }
                    data1.clear();
                    data2.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarCodeDetailActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //按键按下
        switch (keyCode) {
            case 0://右方向键
                barCode.setText("");
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode) {
            case 66://回车键
                getCurrentVPlan();
                break;
            case 22://右方向键
                getCurrentVPlan();
                break;
            case 4://返回键
                tofunction();//返回功能菜单页面
                break;
            default:
                break;
        }
        return true;
    }

}
