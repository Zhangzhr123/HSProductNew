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

public class CheckActivity extends BaseActivity {

    //定义控件  条码 机台 规格编码 规格名称 日期 LR 班组 主手
    private TextView barcode,mchid,spesc,spescname,productdate,lorR,shift,creatuser;
    //质检测试按钮  不合格按钮
    private ButtonView btgetcode,not;
    //不合格原因下拉列表
    private Spinner error;
    //下拉列表
    private List<String> errorlist = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Map<String,String>> errorid = new ArrayList<>();
    private String ERROR = "";
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量  质检条码
    private String BarCode="";

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
    public void initView(){
        //检测条码
        barcode = (TextView)findViewById(R.id.barcode);
        //显示控件
        mchid = (TextView)findViewById(R.id.mchid);//机台
        spesc = (TextView)findViewById(R.id.spesc);//规格编码
        spescname = (TextView)findViewById(R.id.spescname);//规格名称
        productdate = (TextView)findViewById(R.id.product_date);//日期
        lorR = (TextView)findViewById(R.id.LorR);//LR
        shift = (TextView)findViewById(R.id.shift);//班组
        creatuser = (TextView)findViewById(R.id.creatuser);//主手 创建人
        //获得焦点
        barcode.requestFocus();
        //检测
        btgetcode = (ButtonView)findViewById(R.id.bt_getCode);
        //合格或不合格
        not = (ButtonView)findViewById(R.id.not);
        //不合格原因
        error = (Spinner)findViewById(R.id.error);
        //下拉列表
        new ERRORTask().execute();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, errorlist);
        error.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Error = parent.getItemAtPosition(position).toString();
                for(int i=0;i<errorid.size();i++){
                    if(Error.equals(errorid.get(i).get("itemname"))){
                        ERROR = errorid.get(i).get("itemid");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void initEvent(){
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
    }

    //检测操作
    public void getCodeCheck(){
        //质检条码
        BarCode = barcode.getText().toString().trim();
        //判断是否是空
        if(StringUtil.isNullOrEmpty(BarCode)){
            Toast.makeText(CheckActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
        }else{
            String parm = "SwitchTYRE_CODE="+BarCode;
            new SelDetailedTask().execute(parm);
        }

    }

    //修改轮胎合格与否
    public void check(){
        String parm = "TYRE_CODE="+BarCode+"&IS_H=1"+"&USER_NAME="+App.username+"&H_REASON="+ERROR;
        new QualityTestingTask().execute(parm);
    }

    //根据TYPEID 获取数据字典内容  不合格原因
    class ERRORTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            //TYPE_ID=10106
            String result = HttpUtil.sendGet(PathUtil.ERRORGetDictionaries, "TYPE_ID=10106");
            return result;
        }
        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(CheckActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                    List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                    if(res == null || res.isEmpty()){
                        Toast.makeText(CheckActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        errorid.clear();
                        errorlist.clear();
                        for(int i=0;i<map.size();i++){
                            errorid.add(map.get(i));
                            errorlist.add(map.get(i).get("itemname"));
                        }
                        error.setAdapter(adapter);
//                        Toast.makeText(CheckActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(CheckActivity.this, "查询成功，没有匹配的信息！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CheckActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(CheckActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(CheckActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        //展示信息
                        mchid.setText(datas.get(0).getMchid());
                        spesc.setText(datas.get(0).getItnbr());
                        spescname.setText(datas.get(0).getItdsc());
                        productdate.setText(datas.get(0).getWdate().substring(0,10));
                        lorR.setText(datas.get(0).getLr());
                        if(datas.get(0).getShift().equals("1")){
                            shift.setText("甲班");
                        }else if(datas.get(0).getShift().equals("2")){
                            shift.setText("乙班");
                        }else if(datas.get(0).getShift().equals("3")){
                            shift.setText("丙班");
                        }else{
                            shift.setText("丁班");
                        }
                        creatuser.setText(datas.get(0).getCreateuser());
//                        Toast.makeText(CheckActivity.this, "轮胎查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("400")){
                        Toast.makeText(CheckActivity.this, "查询成功，没有匹配的轮胎信息！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CheckActivity.this, "轮胎查询错误！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(CheckActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(CheckActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
//                        barcode.setText("");
                        Toast.makeText(CheckActivity.this, "标记成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(CheckActivity.this, "未找到轮胎信息，标记失败！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(CheckActivity.this, "标记失败！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(CheckActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CheckActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //右方向键
        if(keyCode == 22){
            //获取信息
            getCodeCheck();
        }
        if(keyCode == 0){
            barcode.setText("");
        }
        if(keyCode == 4){
            if(System.currentTimeMillis() - mExitTime > 2000){
                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            }else{
                System.exit(0);//注销功能
            }
        }
        //左方向键
        if(keyCode == 21){
            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时执行操作
//        if(keyCode == 0){
//            //获取信息
//            getCodeCheck();
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }
}
