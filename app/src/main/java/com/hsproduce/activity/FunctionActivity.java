package com.hsproduce.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.List;
import java.util.Map;

//功能显示页面
public class FunctionActivity extends BaseActivity {

    //硫化、装车、检测---控件
    private View view1,view2,view3,view4,view5,view6,view7,view8;
    private ImageButton vplan, repl, load,loadsc,barrep,barsup,detch,check;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        //获取控件
        initView();
    }

    public void initView(){
        //view功能显示
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        view7 = findViewById(R.id.view7);
        view8 = findViewById(R.id.view8);
        //按钮
        vplan = (ImageButton) findViewById(R.id.vplan);//硫化生产
        repl = (ImageButton) findViewById(R.id.repl);//规格交替
        load = (ImageButton)findViewById(R.id.load);//装车出厂
        detch = (ImageButton) findViewById(R.id.detch);//明细更改
        loadsc = (ImageButton) findViewById(R.id.loadsc);//退厂扫描
        barrep = (ImageButton) findViewById(R.id.barrep);//条码更换
        barsup = (ImageButton)findViewById(R.id.barsup);//条码补录
        check = (ImageButton)findViewById(R.id.check);//检测

        //菜单权限管理
        String parm = "UserName="+App.username;
        new TeamTask().execute(parm);

        //Button点击事件
        initEvent();
    }

    private void initEvent() {
        //硫化生产  控件监听事件
        vplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, VulcanizationActivity.class));
                finish();
            }
        });
        //装车出厂  控件监听事件
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, LoadFactoryActivity.class));
                finish();
            }
        });
        //条码更换  控件监听事件
        barrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, BarcodeReplaceActivity.class));
                finish();
            }
        });
        //条码补录  控件监听事件
        barsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, BarcodeSupplementActivity.class));
                finish();
            }
        });
        //明细变更  控件监听事件
        detch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, DetailChangeActivity.class));
                finish();
            }
        });
        //退厂扫描  控件监听事件
        loadsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, LoadScanningActivity.class));
                finish();
            }
        });
        //规格交替  控件监听事件
        repl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, SwitchPlanActivity.class));
                finish();
            }
        });
        //检测  控件监听事件
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FunctionActivity.this, CheckActivity.class));
                finish();
            }
        });
    }

    //菜单权限管理
    class TeamTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.GET_TEAM, strs[0]);
            return result;
        }
        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(FunctionActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                if(res.get("code").equals("200")){
                    if(map.size() == 1){
                        if(map.get(0).get("m_CNAME").equals("硫化生产")){
                            startActivity(new Intent(FunctionActivity.this, VulcanizationActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("规格交替")){
                            startActivity(new Intent(FunctionActivity.this, SwitchPlanActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("装车出厂")){
                            startActivity(new Intent(FunctionActivity.this, LoadFactoryActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("条码更换")){
                            startActivity(new Intent(FunctionActivity.this, BarcodeReplaceActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("条码补录")){
                            startActivity(new Intent(FunctionActivity.this, BarcodeSupplementActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("明细变更")){
                            startActivity(new Intent(FunctionActivity.this, DetailChangeActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("退厂扫描")){
                            startActivity(new Intent(FunctionActivity.this, LoadScanningActivity.class));
                            finish();
                        }else if(map.get(0).get("m_CNAME").equals("检测")){
                            startActivity(new Intent(FunctionActivity.this, CheckActivity.class));
                            finish();
                        }else{
                            Toast.makeText(FunctionActivity.this, "您没有操作PDA权限", Toast.LENGTH_LONG).show();
                        }

                    }else if(map.size() > 1){
                        for(int i=0;i<map.size();i++){
                            if(map.get(i).get("m_CNAME").equals("硫化生产")){
                                view1.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("条码补录")){
                                view2.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("条码更换")){
                                view3.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("明细变更")){
                                view4.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("规格交替")){
                                view5.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("检测")){
                                view6.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("装车出厂")){
                                view7.setVisibility(View.VISIBLE);
                            }else if(map.get(i).get("m_CNAME").equals("退厂扫描")){
                                view8.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(FunctionActivity.this, map.get(i).get("m_CNAME")
                                        +"此功能未在PDA当中", Toast.LENGTH_LONG).show();
                            }
                        }

                    }else{
                        Toast.makeText(FunctionActivity.this, "您没有操作PDA权限,请退出", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(FunctionActivity.this, "菜单查询失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        //两次返回键时间间隔超过两秒 退出登录
        if(keyCode == 4){
            if(System.currentTimeMillis() - mExitTime > 2000){
                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            }else{
                System.exit(0);//注销功能
            }
        }
        //键盘监听 按键跳转功能 1--9
        if(keyCode == 8){
            startActivity(new Intent(FunctionActivity.this, VulcanizationActivity.class));
            finish();
        }else if(keyCode == 9){
            startActivity(new Intent(FunctionActivity.this, BarcodeSupplementActivity.class));
            finish();
        }else if(keyCode == 10){
            startActivity(new Intent(FunctionActivity.this, BarcodeReplaceActivity.class));
            finish();
        }else if(keyCode == 11){
            startActivity(new Intent(FunctionActivity.this, DetailChangeActivity.class));
            finish();
        }else if(keyCode == 12){
            startActivity(new Intent(FunctionActivity.this, SwitchPlanActivity.class));
            finish();
        }else if(keyCode == 13){
            startActivity(new Intent(FunctionActivity.this, CheckActivity.class));
            finish();
        }else if(keyCode == 14){
            startActivity(new Intent(FunctionActivity.this, LoadFactoryActivity.class));
            finish();
        }else if(keyCode == 15){
            startActivity(new Intent(FunctionActivity.this, LoadScanningActivity.class));
            finish();
        }else{
            Toast.makeText(this, "没有此快捷键功能", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
