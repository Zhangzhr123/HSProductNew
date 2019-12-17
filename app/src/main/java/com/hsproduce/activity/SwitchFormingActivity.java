package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.FormingAdapter;
import com.hsproduce.adapter.FormingReplAdapter;
import com.hsproduce.adapter.VPlanItnbrAdapter;
import com.hsproduce.adapter.VPlanReplAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwitchFormingActivity extends BaseActivity {

    //当前计划展示list  规格交替列表
    private ListView replplan;
    //机台号  轮胎条码  条码记录
    private TextView spesc, spescname, pro, state, anum, pnum;
    private AutoCompleteTextView tvMchid;
    private List<String> data1 = new ArrayList<>();
    //获取计划按钮
    private ButtonView btGetplan;
    //计划展示适配器  规格交替适配器
    private FormingReplAdapter repladaprer;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量 当前计划ID
    private String currid = "";
    public String mchid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_switchforming);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //list列表
        replplan = (ListView) findViewById(R.id.lv_replplan);
        //扫描框
        tvMchid = (AutoCompleteTextView) findViewById(R.id.mchid);
        new MCHIDTask().execute("TYPE_ID=10107");
        eventsViews();
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.getSwitchPlan);
        //当前声称计划控件
        spesc = (TextView) findViewById(R.id.spesc);
        spescname = (TextView) findViewById(R.id.spescname);
        pro = (TextView) findViewById(R.id.pro);
        state = (TextView) findViewById(R.id.state);
        anum = (TextView) findViewById(R.id.anum);
        pnum = (TextView) findViewById(R.id.pnum);

//        List<VPlan> a = new ArrayList<>();
//        for (int i = 1; i < 5; i++) {
//            VPlan v = new VPlan();
//            v.setItnbr("111111111");
//            v.setItdsc("222222222");
//            v.setState("20");
//            v.setPro(i + "");
//            v.setAnum("0");
//            v.setPnum("60");
//            a.add(v);
//        }
//        repladaprer = new FormingReplAdapter(SwitchFormingActivity.this, a);
//        replplan.setAdapter(repladaprer);

        replplan.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                replplan.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }

    private void eventsViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
        tvMchid.setAdapter(adapter);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        int action = e.getAction();
//        if(action==MotionEvent.ACTION_DOWN) {
//            return false;
//        }
//        return super.onInterceptTouchEvent(e);
//    }

    public void initEvent() {
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
        mchid = tvMchid.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(SwitchFormingActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        } else {
//            String lr = mchid.substring(mchid.length() - 1);
//            if(!"LR".contains(lr.toUpperCase())){//判断有无大写字母LR
//                Toast.makeText(SwitchFormingActivity.this, "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
//                tvMchid.setText("");
//            }else{
            //生产中
            String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift + "&TYPE_N=30";
            new GetAPlanTask().execute(param1);
            //等待中
            String param2 = "MCHID=" + mchid + "&SHIFT=" + App.shift + "&TYPE_N=20";
            new GetPPlanTask().execute(param2);
//            }
        }
//        tvMchid.setText("");
    }

    //切换规格显示列表
    public void repItndes(String planid, String preCode, String nextCode) {
        if (StringUtil.isNullOrEmpty(planid)) {
            Toast.makeText(SwitchFormingActivity.this, "请选择您要替换规格", Toast.LENGTH_LONG).show();
        } else {
            //?CurrentID=34&SwitchID=33&CurrentEndCode=51901100035&SwitchStartCode=51901100036&USER_NAME=shao&TEAM=1
            String param = "CurrentID=" + currid + "&SwitchID=" + planid + "&CurrentEndCode=" + preCode + "&SwitchStartCode=" + nextCode + "&USER_NAME=" + App.username + "&TEAM=" + App.shift;
            new SwitchVplanTask().execute(param);
        }
    }

    //根据TYPEID 获取数据字典内容  成型机台
    class MCHIDTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGSELECTMCHID, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        for (int i = 0; i < map.size(); i++) {
                            data1.add(map.get(i).get("itemid"));
                        }
//                        Toast.makeText(DetailChangeActivity.this, "机台查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //获取生产中的生产计划
    class GetAPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMINGPLAN, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res.get("code").equals("200")) {
                        //获取当前计划ID
                        currid = datas.get(0).getId();
                        //展示当前计划
                        spesc.setText(datas.get(0).getItnbr());
                        spescname.setText(datas.get(0).getItdsc());
                        pro.setText(datas.get(0).getPro());
                        if (datas.get(0).getState() != null) {
                            if (datas.get(0).getState().equals("10")) {
                                state.setText("新计划");
                            } else if (datas.get(0).getState().equals("20")) {
                                state.setText("等待中");
                            } else if (datas.get(0).getState().equals("30")) {
                                state.setText("进行中");
                            } else if (datas.get(0).getState().equals("40")) {
                                state.setText("已完成");
                            } else {
                                state.setText("未知状态");
                            }
                        }
                        anum.setText(datas.get(0).getAnum());
                        pnum.setText(datas.get(0).getPnum());
//                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchFormingActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有生产中的计划！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
                    }
                    if (datas == null || datas.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "未获取到计划", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //获取等待中的生产计划
    class GetPPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMINGPLAN, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //展示规格替换列表
                        repladaprer = new FormingReplAdapter(SwitchFormingActivity.this, datas);
                        replplan.setAdapter(repladaprer);
                        repladaprer.notifyDataSetChanged();
//                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchFormingActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有等待的计划！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //切换规格
    class SwitchVplanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMING, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        getCurrentVPlan();//展示替换后的计划
//                        tvMchid.setText("");
                        Toast.makeText(SwitchFormingActivity.this, "切换成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(SwitchFormingActivity.this, "新切换的计划变动，切换失败，请刷新！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchFormingActivity.this, "切换失败！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //右方向键
        if (keyCode == 22) {
            getCurrentVPlan();
        }
        if (keyCode == 0) {
            tvMchid.setText("");
        }
        if (keyCode == 4) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);//注销功能
            }
        }
        //左方向键
        if (keyCode == 21) {
            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //扫描键 弹开时获取计划
//        if (keyCode == 66) {
//            getCurrentVPlan();
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
