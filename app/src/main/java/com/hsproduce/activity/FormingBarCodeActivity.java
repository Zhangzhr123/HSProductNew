package com.hsproduce.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.hsproduce.adapter.VPlanAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import java.text.SimpleDateFormat;
import java.util.*;

public class FormingBarCodeActivity extends BaseActivity {

    //轮胎条码 条码计数  条码记录
    private TextView barCode, num, barcodelog;
    private ButtonView btGetplan;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //绑定条码个数
    private int number = 0;
    //轮胎条码
    private String tvbarcode = "", planid = "";
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_formingbarcode);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    //初始化控件
    public void initView() {
        //扫描框
        barCode = (TextView) findViewById(R.id.barCode);
        //获得焦点
        barCode.requestFocus();
        //条码记录
        barcodelog = (TextView) findViewById(R.id.barcode_log);
        //不可编辑
        barcodelog.setFocusable(false);
        barcodelog.setFocusableInTouchMode(false);
        //扫描条码计数
        num = (TextView) findViewById(R.id.num);
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.getplan);
    }

    //初始化事件
    public void initEvent() {
        //报废
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取计划
                getBarCode();
            }
        });
    }


    //获取轮胎条码
    public void getBarCode() {
        //获取轮胎上barcode
        tvbarcode = barCode.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(tvbarcode)) {
//            Toast.makeText(FormingBarCodeActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
            return;
        } else {
            String param1 = "ScrapCode=" + tvbarcode + "&FormingID=" + "&User_Name=" + App.username + "&TEAM=" + App.shift;
            new TypeCodeTask().execute(param1);
            barCode.requestFocus();
        }
    }

    //成型胚胎报废
    class TypeCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGBARCODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingBarCodeActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingBarCodeActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //判断用户是否重复输入
                        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        list.add("[" + date + "]" + tvbarcode + "报废成功");
                        barcodelog.setText("");
                        System.out.println(list.size());
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 0) {
                                barcodelog.setText(list.get(i));
                            } else {
                                barcodelog.setText(getlog(list));
                            }
                        }
                        num.setText("");
                        number++;//计算成功次数
                        num.setText(number + "");
//                        Toast.makeText(FormingBarCodeActivity.this, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingBarCodeActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingBarCodeActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
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
        //按键按下
        switch (keyCode) {
            case 0:
                barCode.requestFocus();
                barCode.setText("");
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode) {
            case 0://回车键
                getBarCode();//获取明细
                break;
            case 22://右方向键
                getBarCode();//获取明细
                break;
            case 4:
                tofunction();
                num.setText("0");
                break;
        }
        return true;
    }
}
