package com.hsproduce.activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.CheckBarCodeAdapter;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检测页面
 * 扫描条码输入不合格原因，标记此条码为不合格品，可以重复标记
 * createBy zhangzhr @ 2019-12-21
 */
public class CheckActivity extends BaseActivity {

    //定义控件  条码 机台 规格编码 规格名称 日期 LR 班组 主手
    private TextView barcode, mchid, spesc, spescname, productdate, lorR, shift, creatuser;
    //质检测试按钮  不合格按钮
    private ImageButton btgetcode;
    private Button not, out;
    //不合格原因下拉列表
    private TextView error;
    private String ERROR = "";
    //定义变量  质检条码
    private String BarCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_check);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //检测条码
        barcode = (TextView) findViewById(R.id.barcode);
        //显示控件
        mchid = (TextView) findViewById(R.id.mchid);//机台
        spesc = (TextView) findViewById(R.id.spesc);//规格编码
        spescname = (TextView) findViewById(R.id.spescname);//规格名称
        productdate = (TextView) findViewById(R.id.product_date);//日期
        lorR = (TextView) findViewById(R.id.LorR);//LR
        shift = (TextView) findViewById(R.id.shift);//班组
        creatuser = (TextView) findViewById(R.id.creatuser);//主手 创建人
        //获得焦点
        barcode.requestFocus();
        //检测
        btgetcode = (ImageButton) findViewById(R.id.bt_getCode);
        //合格或不合格
        not = (Button) findViewById(R.id.not);
        //不合格原因
        error = (TextView) findViewById(R.id.error);
        //返回
        out = (Button) findViewById(R.id.out);

    }

    public void initEvent() {
        //测试按钮
        btgetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodeCheck();
            }
        });
        //不合格按钮
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outfinish();
            }
        });
    }

    public void outfinish() {
        tofunction();
    }

    //检测操作
    public void getCodeCheck() {
        //质检条码
        BarCode = barcode.getText().toString().trim();
        //判断是否是空
        if (StringUtil.isNullOrEmpty(BarCode)) {
//            Toast.makeText(CheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
            return;
        } else {
            String parm = "SwitchTYRE_CODE=" + BarCode;
            new SelDetailedTask().execute(parm);
        }

    }

    //修改轮胎合格与否
    public void check() {
        ERROR = error.getText().toString().trim().replace(".","-");
        if(ERROR == null || ERROR.equals("")){
            Toast.makeText(CheckActivity.this, "不合格原因为必填项，请输入", Toast.LENGTH_SHORT).show();
            return;
        }

        String parm = "TYRE_CODE=" + BarCode + "&IS_H=1" + "&USER_NAME=" + App.username + "&H_REASON=" + ERROR;
        new QualityTestingTask().execute(parm);
    }

    //根据规条码模糊查询明细
    class SelDetailedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelDetailed, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //清空
            mchid.setText("");
            spesc.setText("");
            spescname.setText("");
            productdate.setText("");
            lorR.setText("");
            shift.setText("");
            creatuser.setText("");

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(CheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(CheckActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //展示信息
                        mchid.setText(datas.get(0).getMchid());
                        spesc.setText(datas.get(0).getItnbr());
                        spescname.setText(datas.get(0).getItdsc());
                        productdate.setText(datas.get(0).getWdate().substring(0, 10));
                        lorR.setText(datas.get(0).getLr());
                        shift.setText(datas.get(0).getShift());
                        creatuser.setText(datas.get(0).getCreateuser());

                    } else {
                        Toast.makeText(CheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //质检 不合格品登记
    class QualityTestingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.QualityTesting, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(CheckActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(CheckActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        Toast.makeText(CheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(CheckActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按键按下
        switch (keyCode){
            case 0://扫描键
                barcode.requestFocus();
                barcode.setText("");//成功后清空输入框
                break;
        }

        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode){
            case 0://扫描键
                getCodeCheck();//查询明细
                break;
            case 22://右方向键
                getCodeCheck();//查询明细
                break;
            case 4://返回键
                tofunction();//返回功能菜单页面
                break;
            case 131://F1键
                check();
                break;
            case 132://F2键
                outfinish();//返回菜单页面
                break;
            default:
                break;
        }
        return true;
    }
}
