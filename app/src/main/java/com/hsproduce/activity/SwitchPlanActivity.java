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
import com.hsproduce.adapter.VPlanAdapter;
import com.hsproduce.adapter.VPlanItnbrAdapter;
import com.hsproduce.adapter.VPlanReplAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwitchPlanActivity extends BaseActivity {

    //view  机台号
    private View llmchid,lltext1,lltext2;
    //当前计划展示list  规格交替列表
    private ListView lvplan,replplan;
    //机台号  轮胎条码  条码记录
//    private TextView tvMchid;
    private AutoCompleteTextView tvMchid;
    private List<String> data1 = new ArrayList<>();
    //获取计划按钮
    private ButtonView btGetplan;
    //计划展示适配器  规格交替适配器
    private VPlanItnbrAdapter adapter;
    private VPlanReplAdapter repladaprer;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量 当前计划ID
    private String currid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_switchplan);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }
    public void initView(){
        //显示首页
        llmchid = findViewById(R.id.ll_mchid);
        lltext1 = findViewById(R.id.ll_text1);
        lltext2 = findViewById(R.id.ll_text2);
        //list列表
        lvplan = (ListView) findViewById(R.id.lv_plan);
        replplan = (ListView) findViewById(R.id.lv_vplan);
        //扫描框
        tvMchid = (AutoCompleteTextView) findViewById(R.id.mchid);
        new MCHIDTask().execute("TYPE_ID=10098");
        eventsViews();
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.bt_getPlan);

    }

    private void eventsViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
        tvMchid.setAdapter(adapter);
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

    //根据状态查询计划
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        String mchid = tvMchid.getText().toString().trim();
        if(StringUtil.isNullOrEmpty(mchid)){
            Toast.makeText(SwitchPlanActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        }else{
//            String lr = mchid.substring(mchid.length() - 1);
//            if(!"LR".contains(lr.toUpperCase())){//判断有无大写字母LR
//                Toast.makeText(SwitchPlanActivity.this, "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
//                tvMchid.setText("");
//            }else{
                //生产中
                String param1 = "MCHIDLR="+mchid+"&SHIFT="+App.shift+"&TYPE_N=30";
                new GetPlanTask().execute(param1);
                //等待中
                String param2 = "MCHIDLR="+mchid+"&SHIFT="+App.shift+"&TYPE_N=20";
                new GetPlanTask().execute(param2);
//            }
        }
//        tvMchid.setText("");
    }

    //切换规格显示列表
    public void repItndes(String planid){
        if(StringUtil.isNullOrEmpty(planid)){
            Toast.makeText(SwitchPlanActivity.this, "请选择您要替换规格", Toast.LENGTH_LONG).show();
        }else{
            String param = "CurrentID="+currid+"&SwitchID="+planid+"&USER_NAME="+App.username;
            new SwitchVplanTask().execute(param);
        }
    }

    //根据TYPEID 获取数据字典内容  机台
    class MCHIDTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.GetDictionaries, strs[0]);
            return result;
        }
        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(SwitchPlanActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                    List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchPlanActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        for (int i = 0; i < map.size(); i++) {
                            data1.add(map.get(i).get("itemid"));
                        }
//                        Toast.makeText(BarcodeSupplementActivity.this, "机台查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(SwitchPlanActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SwitchPlanActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SwitchPlanActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //获取不同状态的生产计划
    class GetPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.GetCurrentVPlan, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //关闭
            llmchid.setVisibility(View.GONE);
            //显示
            lltext1.setVisibility(View.VISIBLE);
            lvplan.setVisibility(View.VISIBLE);
            lltext2.setVisibility(View.VISIBLE);
            replplan.setVisibility(View.VISIBLE);

            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(SwitchPlanActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(SwitchPlanActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        //tvMchid.setText("");
                        for(int i=0;i<datas.size();i++){
                            if(datas.get(i).getState().equals("30")){
                                //获取当前计划ID
                                currid = datas.get(i).getId();
                                //展示当前计划列表
                                adapter = new VPlanItnbrAdapter(SwitchPlanActivity.this, datas);
                                lvplan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else if(datas.get(i).getState().equals("20")){
                                //展示规格替换列表
                                repladaprer = new VPlanReplAdapter(SwitchPlanActivity.this, datas);
                                replplan.setAdapter(repladaprer);
                                repladaprer.notifyDataSetChanged();
                            }else{
                                //Toast.makeText(SwitchPlanActivity.this, "没有适合规格交替的计划", Toast.LENGTH_LONG).show();
                            }
                        }
//                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(SwitchPlanActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        if(datas.get(0).getState().equals("30")){
                            Toast.makeText(SwitchPlanActivity.this, "查询成功，没有正在执行的计划！", Toast.LENGTH_LONG).show();
                        }else if(datas.get(0).getState().equals("20")){
                            Toast.makeText(SwitchPlanActivity.this, "查询成功，没有可以替换的计划！", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(SwitchPlanActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SwitchPlanActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
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
                Toast.makeText(SwitchPlanActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(SwitchPlanActivity.this, "未获取到数据，数据返回为空", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        getCurrentVPlan();//展示替换后的计划
                        Toast.makeText(SwitchPlanActivity.this, "切换成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(SwitchPlanActivity.this, "新切换的计划变动，切换失败，请刷新！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(SwitchPlanActivity.this, "切换失败！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SwitchPlanActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(SwitchPlanActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        //右方向键
        if(keyCode == 22){
            getCurrentVPlan();
        }
        if(keyCode == 0){
            tvMchid.setText("");
        }
        if(keyCode == 4){
            if(System.currentTimeMillis() - mExitTime > 2000){
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
//            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时获取计划
//        if(keyCode == 66){
//            getCurrentVPlan();
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
