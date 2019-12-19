package com.hsproduce.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.List;
import java.util.Map;

//条码更换页面
public class BarcodeReplaceActivity extends BaseActivity {

    //声明控件   获取条码明细按钮   btn_repl
    private ButtonView btGetcode, btreplcode;
    //旧条码  新条码 规格编码 规格名称 日期 LR 班次 班组 主手
    private TextView barcode, newbarcode, spesc, spescname, date, lorR, shift, team, creatuser;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //原条码ID
    public String currentid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_barcodereplace);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //条码扫描框
        barcode = (TextView) findViewById(R.id.barcode);
        newbarcode = (TextView) findViewById(R.id.new_barcode);
        //显示
        spesc = (TextView) findViewById(R.id.spesc);
        spescname = (TextView) findViewById(R.id.spescname);
        date = (TextView) findViewById(R.id.product_date);
        lorR = (TextView) findViewById(R.id.LorR);
        shift = (TextView) findViewById(R.id.shift);
        team = (TextView) findViewById(R.id.team);
        creatuser = (TextView) findViewById(R.id.creatuser);
        //条码按钮
        btGetcode = (ButtonView) findViewById(R.id.bt_getCode);
        btreplcode = (ButtonView) findViewById(R.id.barcode_replace);
    }

    public void initEvent() {
        //获取条码明细
        btGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selCode();
            }
        });
        //更换条码
        btreplcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewCode();
            }
        });
    }

    //获取条码明细操作
    public void selCode() {
        String lvcode = barcode.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(lvcode)) {
            Toast.makeText(BarcodeReplaceActivity.this, "请扫描轮胎条码", Toast.LENGTH_SHORT).show();
        } else {
            //?TYRE_CODE=111600000375
            String parm = "TYRE_CODE=" + lvcode;
            new SelCodeTask().execute(parm);
        }
//        barcode.setText("");
    }

    //更换条码操作
    public void getNewCode() {
        String replcode = newbarcode.getText().toString().trim();
        String s_barcode = barcode.getText().toString().trim();
        if(StringUtil.isNullOrEmpty(s_barcode)){
            Toast.makeText(BarcodeReplaceActivity.this, "请扫描原轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNullOrEmpty(replcode)) {
            Toast.makeText(BarcodeReplaceActivity.this, "请扫描新轮胎条码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(s_barcode.equals(replcode)){
            Toast.makeText(BarcodeReplaceActivity.this, "新条码与原条码一致，请重新扫描条码", Toast.LENGTH_SHORT).show();
            return;
        }
        String parm = "CurrentID=" + currentid + "&SwitchTYRE_CODE=" + replcode + "&USER_NAME=" + App.username;
        new ReplCodeTask().execute(parm);
    }

    //扫描条码查询轮胎规格
    class SelCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelTYRE_CODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //获得焦点
            newbarcode.requestFocus();

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeReplaceActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(BarcodeReplaceActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                    }
                    if (res.get("code").equals("200") && datas != null && datas.size() > 0) {
                        //获取原条码ID
                        currentid = String.valueOf(datas.get(0).getId());
                        //展示信息
                        spesc.setText(datas.get(0).getItnbr());
                        spescname.setText(datas.get(0).getItdsc());
                        date.setText(datas.get(0).getWdate().substring(0, 10));
                        lorR.setText(datas.get(0).getLr());
                        String s_shift = datas.get(0).getShift();
                        if(!StringUtil.isNullOrEmpty(s_shift)){
                            if (s_shift.equals("1")) {
                                shift.setText("早班");
                            } else if (s_shift.equals("2")) {
                                shift.setText("中班");
                            } else {
                                shift.setText("晚班");
                            }
                        }
                        String s_team = datas.get(0).getTeam();
                        if(!StringUtil.isNullOrEmpty(s_team)){
                            if (s_team.equals("1")) {
                                team.setText("甲班");
                            } else if (s_team.equals("2")) {
                                team.setText("乙班");
                            } else if (s_team.equals("3")) {
                                team.setText("丙班");
                            } else {
                                team.setText("丁班");
                            }
                        }
                        creatuser.setText(datas.get(0).getCreateuser());
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(BarcodeReplaceActivity.this, "查询成功，没有匹配的条码！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BarcodeReplaceActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarcodeReplaceActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //新条码是否更换
    class ReplCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.ChangeTYRE_CODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeReplaceActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(BarcodeReplaceActivity.this, "未获取到信息", Toast.LENGTH_SHORT).show();
                    }
                    if (res.get("code").equals("200")) {
                        barcode.setText("");
                        spesc.setText("");
                        spescname.setText("");
                        date.setText("");
                        lorR.setText("");
                        shift.setText("");
                        team.setText("");
                        creatuser.setText("");
                        btreplcode.setText("");
                        Toast.makeText(BarcodeReplaceActivity.this, "更换成功！", Toast.LENGTH_SHORT).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(BarcodeReplaceActivity.this, "新条码被使用过无法更换！", Toast.LENGTH_SHORT).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(BarcodeReplaceActivity.this, "更换失败！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BarcodeReplaceActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarcodeReplaceActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //扫描键 按下时清除
        if (keyCode == 0) {
            //barcode.setText("");
            //btreplcode.setText("");
//            if(barcode.isFocused()){
//                barcode.setText("");
//            }
//            if(newbarcode.isFocused()){
//                newbarcode.setText("");
//            }
        }

        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            //扫描键
            case 0:
                if(!StringUtil.isNullOrEmpty(barcode.getText().toString().trim())){
                    selCode();
                }
                break;
            //右方向键
            case 22:
                if(!StringUtil.isNullOrEmpty(barcode.getText().toString().trim())){
                    selCode();
                }
            //返回键
            case 4:
                startActivity(new Intent(BarcodeReplaceActivity.this, FunctionActivity.class));
                this.finish();
                break;
        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
