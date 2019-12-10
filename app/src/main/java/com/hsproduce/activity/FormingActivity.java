package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.FormingItemAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.List;
import java.util.Map;

public class FormingActivity extends BaseActivity {

    //当前计划展示list  规格交替列表
    private ListView lvplan;
    //机台号  轮胎条码  条码记录
    private TextView tvMchid;
    //获取计划按钮
    private ImageButton btGetplan;
    //计划展示适配器  规格交替适配器
    private FormingItemAdapter adapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量 当前计划ID
    private String currid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forming);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }
    public void initView(){
        //list列表
        lvplan = (ListView) findViewById(R.id.lv_plan);
        //扫描框
        tvMchid = (TextView) findViewById(R.id.mchid);
        //获取计划按钮
        btGetplan = (ImageButton) findViewById(R.id.getPlan);

    }
    public void initEvent(){
        //点击当期计划 和 规格交替计划
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentVPlan();
            }
        });
    }
    //查询计划
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        String mchid = tvMchid.getText().toString().trim();
        if(StringUtil.isNullOrEmpty(mchid)){
            Toast.makeText(FormingActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        }else{
            String lr = mchid.substring(mchid.length() - 1);
            if(!"LR".contains(lr.toUpperCase())){//判断有无大写字母LR
                Toast.makeText(FormingActivity.this, "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
                tvMchid.setText("");
            }else{
                //已下达的计划
                String param1 = "MCHIDLR="+mchid;
                new GetPlanTask().execute(param1);
            }
        }
        tvMchid.setText("");
    }

    //切换规格显示列表
    public void repItndes(String planid){
        if(StringUtil.isNullOrEmpty(planid)){
            Toast.makeText(FormingActivity.this, "请选择您要替换规格", Toast.LENGTH_LONG).show();
        }else{
            String param = "CurrentID="+currid+"&SwitchID="+planid+"&USER_NAME="+App.username;
            new SwitchVplanTask().execute(param);
        }
    }

    //获取计划
    class GetPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGPLAN, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>(){}.getType());
                    if(datas == null || datas.isEmpty()){
                        Toast.makeText(FormingActivity.this, "未获取到计划", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        //tvMchid.setText("");
                        for(int i=0;i<datas.size();i++){
                            if(datas.get(i).getState().equals("30")){
                                //获取计划ID
                                currid = datas.get(i).getId();
                                //展示计划列表
                                adapter = new FormingItemAdapter(FormingActivity.this, datas);
                                lvplan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else{
                                //Toast.makeText(SwitchPlanActivity.this, "没有适合规格交替的计划", Toast.LENGTH_LONG).show();
                            }
                        }
//                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(FormingActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        if(datas.get(0).getState().equals("30")){
                            Toast.makeText(FormingActivity.this, "查询成功，没有正在执行的计划！", Toast.LENGTH_LONG).show();
                        }else if(datas.get(0).getState().equals("20")){
                            Toast.makeText(FormingActivity.this, "查询成功，没有可以替换的计划！", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(FormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //切换规格
    class SwitchVplanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SwitchVplan, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(FormingActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        getCurrentVPlan();//展示替换后的计划
                        Toast.makeText(FormingActivity.this, "切换成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(FormingActivity.this, "新切换的计划变动，切换失败，请刷新！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(FormingActivity.this, "切换失败！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(FormingActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        //扫描键 按下时清除
        if(keyCode == 0){
            tvMchid.setText("");
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
        //返回键时间间隔超过两秒 返回功能页面
        if(keyCode == 21){
            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时获取计划
        if(keyCode == 66){
            getCurrentVPlan();
        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
