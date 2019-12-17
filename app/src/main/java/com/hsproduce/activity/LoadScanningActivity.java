package com.hsproduce.activity;

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
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.text.SimpleDateFormat;
import java.util.*;

//退厂扫描页面
public class LoadScanningActivity extends BaseActivity {

    //声明控件  扫描条码  记录条码  条码计数
    private TextView barcode,barcodelog,anum;
    //退厂扫描按钮测试
    private ButtonView getcode;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量   退厂条码
    private String scanbarcode="";
    private List<String> list = new ArrayList<>();
    //条码计数初始值
    private int number=0;
    //添加条码防止重复扫描
    private List<String> codelist = new ArrayList<>();
    private Boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_loadscanning);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView(){
        //条码扫描框
        barcode = (TextView)findViewById(R.id.scan_barcode);
        //焦点扫描框
        barcode.requestFocus();
        //条码记录
        barcodelog = (TextView)findViewById(R.id.barcode_log);
        //不可编辑
        barcodelog.setFocusable(false);
        barcodelog.setFocusableInTouchMode(false);
        //扫描个数
        anum = (TextView)findViewById(R.id.anum);
        //按钮测试用
        getcode = (ButtonView)findViewById(R.id.bt_getCode);
    }

    public void initEvent(){
        //测试按钮
        getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outVLoad();
            }
        });
    }
    //退厂扫描
    public void outVLoad(){
        //退厂扫描条码
        scanbarcode = barcode.getText().toString().trim();
        //判断是否为空
        if(StringUtil.isNullOrEmpty(scanbarcode)){
            Toast.makeText(LoadScanningActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
        }else{
            if(codelist.size() == 0 || codelist == null){
                isNew = true;
                return;
            }else{
                for (int i = 0; i < codelist.size(); i++) {
                    if (scanbarcode.equals(codelist.get(i))) {
                        isNew = false;
                        return;
                    }
                }
            }

            if(isNew){
                String parm = "TYRE_CODE="+scanbarcode+"&USER_NAME="+App.username;
                new OutsVLoadTask().execute(parm);
            }else{
                Toast.makeText(LoadScanningActivity.this, "此条码已经扫描", Toast.LENGTH_LONG).show();
            }

        }
        //barcode.setText("");
    }

    //退厂扫描
    class OutsVLoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.OutsVLOAD, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(LoadScanningActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(LoadScanningActivity.this, "未获取到数据，数据返回异常", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        codelist.add(scanbarcode);
                        //显示绑定条码数量
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        list.add("["+date+"]"+scanbarcode);
                        //list.add(scanbarcode);
                        barcodelog.setText("");
                        for(int i=0;i<list.size();i++){
                            if(i==0){
                                barcodelog.setText(list.get(i));
                            }else{
                                barcodelog.setText(getlog(list));
                            }
                        }
                        anum.setText("");
                        number++;
                        anum.setText(number+"");
                        //成功后清空扫描框
//                        barcode.setText("");
                        Toast.makeText(LoadScanningActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(LoadScanningActivity.this, "未找到轮胎信息，操作失败！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(LoadScanningActivity.this, "操作失败，请重新扫描！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(LoadScanningActivity.this, "该轮胎并未出库，无法取消！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(LoadScanningActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(LoadScanningActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //递归显示
    public String getlog(List<String> list){
        String logstr = "";
        Collections.reverse(list);//倒序
        for(int i=0;i<list.size();i++){
            logstr += list.get(i)+"\n";
        }
        return logstr;
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        //扫描键 按下时清除
        if(keyCode == 22){
            outVLoad();
//            barcode.setText("");
        }
        if(keyCode == 0){
            barcode.setText("");
        }
        //返回键时间间隔超过两秒 返回功能页面
        if(keyCode == 4){
            if(System.currentTimeMillis() - mExitTime > 2000){
                codelist.clear();
                tofunction();
//                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            }else{
                System.exit(0);//注销功能
            }
        }
        //左方向键
        if(keyCode == 21){
//            codelist.clear();
//            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //按键弹开
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时执行操作
//        if(keyCode == 66){
//            outVLoad();
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
