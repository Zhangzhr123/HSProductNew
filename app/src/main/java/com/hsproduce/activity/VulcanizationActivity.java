package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.VPlanAdapter;
import com.hsproduce.adapter.VPlanItemAdapter;
import com.hsproduce.adapter.VPlanItnbrAdapter;
import com.hsproduce.bean.Result2;
import com.hsproduce.bean.VPlan;
import com.hsproduce.fragment.VulcanizationFragment;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VulcanizationActivity extends BaseActivity {

    //view 输入机台  记录条码  扫描条码
    private View llmchid, llcodelog, llcode;
    //计划展示list
    private ListView listView;
    //机台号  轮胎条码 条码计数  条码记录
    private TextView tvMchid, barcode, anum, barcodelog;
    //计划按钮   扫描按钮
    private ButtonView btGetplan, barcode_ok;
    //加载
    private MiniLoadingView loadingView;
    //计划展示适配器
    private VPlanAdapter adapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //绑定条码个数
    private int number = 0;
    //轮胎条码
    private String tvbarcode = "", planid = "";
    private List<String> list = new ArrayList<>();
    //添加条码防止重复扫描
    private List<String> codelist = new ArrayList<>();
    private Boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vulcanization);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    //初始化控件
    public void initView() {
        //layout
        llcode = findViewById(R.id.ll_code);
        llmchid = findViewById(R.id.ll_mchid);
        llcodelog = findViewById(R.id.ll_codelog);
        //list列表
        listView = (ListView) findViewById(R.id.lv_plan);
        //扫描框
        tvMchid = (TextView) findViewById(R.id.mchid);
        //条码扫描框
        barcode = (TextView) findViewById(R.id.barcode);
        //条码记录
        barcodelog = (TextView) findViewById(R.id.barcode_log);
        //不可编辑
        barcodelog.setFocusable(false);
        barcodelog.setFocusableInTouchMode(false);
        //扫描条码计数
        anum = (TextView) findViewById(R.id.anum);
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.bt_getPlan);
        //条码按钮
        barcode_ok = (ButtonView) findViewById(R.id.barcode_ok);
        //加载条
        loadingView = (MiniLoadingView) findViewById(R.id.loading);
        //设置条码扫描框输入字符数
        barcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
    }

    //初始化事件
    public void initEvent() {
        //获取机台计划
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示加载
                loadingView.setVisibility(View.VISIBLE);
                //获取计划
                getPlan();
            }
        });
        //获取扫描条码绑定计划
        barcode_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarCode();
            }
        });
    }

    //获取计划
    public void getPlan() {
        //获取输入机台上barcode
        String mchid = tvMchid.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(VulcanizationActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        } else {
            String lr = mchid.substring(mchid.length() - 1);
            if (!"LR".contains(lr.toUpperCase())) {//判断有无大写字母LR
                Toast.makeText(VulcanizationActivity.this, "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
                tvMchid.setText("");
            } else {
                App.lr = lr.toUpperCase();
                String param = "MCHIDLR=" + mchid + "&SHIFT=" + App.shift;
                new MyTask().execute(param);
            }
        }
        tvMchid.setText("");
    }

    //获取轮胎条码
    public void getBarCode() {
        //获取轮胎上barcode
        tvbarcode = barcode.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(tvbarcode)) {
            Toast.makeText(VulcanizationActivity.this, "请扫描轮胎条码", Toast.LENGTH_LONG).show();
        } else {
            codelist.add(tvbarcode);
            for (int i = 0; i < codelist.size(); i++) {
                if (tvbarcode.equals(codelist.get(i))) {
                    isNew = false;
                    return;
                }
            }
            if (isNew) {
                //判断轮胎条码是否重复
                String param1 = "TYRE_CODE=" + tvbarcode;
                new TypeCodeTask().execute(param1);
            } else {
                Toast.makeText(VulcanizationActivity.this, "此条码已经扫描", Toast.LENGTH_LONG).show();
            }

        }
        //barcode.setText("");
    }

    //查询任务
    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.VUL_GET_PLAN, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(VulcanizationActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (datas == null || datas.isEmpty()) {
                        Toast.makeText(VulcanizationActivity.this, "未获取到计划", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //获取计划ID
                        planid = datas.get(0).getId();
                        //关闭加载
                        loadingView.setVisibility(View.GONE);
                        llmchid.setVisibility(View.GONE);
                        //显示硫化扫描
                        listView.setVisibility(View.VISIBLE);
                        llcode.setVisibility(View.VISIBLE);
                        llcodelog.setVisibility(View.VISIBLE);
                        //获得焦点
                        barcode.requestFocus();
                        //展示列表
                        adapter = new VPlanAdapter(VulcanizationActivity.this, datas);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        //tvMchid.setText("");
//                        Toast.makeText(VulcanizationActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(VulcanizationActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(VulcanizationActivity.this, "查询成功，没有匹配的计划！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VulcanizationActivity.this, "计划查询错误，请重新操作！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VulcanizationActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    //判断轮胎条码是否重复
    class TypeCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.VUL_SelActual_TYRE_CODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(VulcanizationActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(VulcanizationActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        App.Iu = "I";
                        //扫描条码绑定计划
                        String param2 = "PLAN_ID=" + planid + "&TYRE_CODE=" + tvbarcode + "&IorU=I" + "&User_Name=" + App.username + "&TEAM=" + App.shift;
                        new CodeInPlanTask().execute(param2);
                        //Toast.makeText(VulcanizationActivity.this, "全新轮胎条码", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(VulcanizationActivity.this, "扫描条码位数不正确！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        App.Iu = "U";
                        //扫描条码绑定计划
                        String param2 = "PLAN_ID=" + planid + "&TYRE_CODE=" + tvbarcode + "&IorU=U" + "&User_Name=" + App.username + "&TEAM=" + App.shift;
                        new CodeInPlanTask().execute(param2);
                        //Toast.makeText(VulcanizationActivity.this, "已扫描过该轮胎条码！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VulcanizationActivity.this, "错误，条码未识别！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VulcanizationActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //扫描条码绑定计划
    class CodeInPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.VUL_AddActualAchievement, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(VulcanizationActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(VulcanizationActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //显示绑定条码数量
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        list.add("[" + date + "]" + tvbarcode);
                        //list.add(tvbarcode);
                        barcodelog.setText("");
                        System.out.println(list.size());
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 0) {
                                barcodelog.setText(list.get(i));
                            } else {
                                barcodelog.setText(getlog(list));
                            }
                        }
                        anum.setText("");
                        number++;//计算成功次数
                        anum.setText(number + "");
                        //barcode.setText("");
//                        Toast.makeText(VulcanizationActivity.this, "扫描成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(VulcanizationActivity.this, "扫描条码位数不正确！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(VulcanizationActivity.this, "扫描插入数据库失败！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VulcanizationActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(VulcanizationActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //递归显示
    public String getlog(List<String> list) {
        String logstr = "";
        for (int i = 0; i < list.size(); i++) {
            logstr += list.get(i) + "\n";
        }
        return logstr;
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //扫描键 按下时清除
        if (keyCode == 0) {
            //获取计划
            //getPlan();
            tvMchid.setText("");
            barcode.setText("");
        } else if (keyCode == 4) {
            //绑定条码
        }
        //返回键时间间隔超过两秒 返回功能页面
        if (keyCode == 4) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);//注销功能
            }
        }
        //右方向键 按下时获取计划
        if (keyCode == 22) {
            getPlan();
        }
        if (keyCode == 21) {
            list.clear();
            codelist.clear();
            anum.setText("0");
            tofunction(); //BaseActivity  返回功能页面函数
//            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //扫描键 弹开时获取计划
        if (keyCode == 0) {
            if (barcode.getText().toString().trim() != null && !barcode.getText().toString().trim().equals("")) {
                getBarCode();
            } else if (tvMchid.getText().toString().trim() != null && !tvMchid.getText().toString().trim().equals("")) {
                getPlan();
            } else {
                Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onKeyDown(keyCode, event);
        return true;
    }
}
