package com.hsproduce.activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.VPlanReplAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.SoundPlayUtils;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 硫化规格交替页面
 * 扫描机台号，查询等待中的计划，点击规格交替显示是否交替，传递计划ID切换计划
 * createBy zhangzhr @ 2019-12-21
 */
public class SwitchPlanActivity extends BaseActivity {

    private LinearLayout showlist, llmchid;
    private TableLayout showVplan;
    private LinearLayout onclick;
    private Button repl, out;
    private TextView spesc, spescname, pro, state, pnum;
    //当前计划展示list  规格交替列表
    private ListView lvplan;
    private VPlan v = new VPlan();
    private List<VPlan> planList = new ArrayList<>();
    //输入框
    private TextView tvMchid;
    //获取计划按钮
    private ButtonView btGetplan;
    //计划展示适配器  规格交替适配器
    private VPlanReplAdapter adaprer;
    //定义变量 当前计划ID
    private String currid = "";
    public String mchid = "";
    private String num = "";
    private boolean iscomplate = true;

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

    public void initView() {
        //点击之前页面
        llmchid = findViewById(R.id.ll_mchid);
        showlist = findViewById(R.id.showlist);
        //点击之后页面
        showVplan = (TableLayout) findViewById(R.id.showVplan);
        onclick = findViewById(R.id.onclick);
        //点击之后的按钮
        repl = (Button) findViewById(R.id.repl);
        out = (Button) findViewById(R.id.out);
        //点击之后显示明细
        spesc = (TextView) findViewById(R.id.spesc);
        spescname = (TextView) findViewById(R.id.spescname);
        pro = (TextView) findViewById(R.id.pro);
        state = (TextView) findViewById(R.id.state);
        pnum = (TextView) findViewById(R.id.pnum);
        //list列表
        lvplan = (ListView) findViewById(R.id.lv_plan);
        //扫描框
        tvMchid = (TextView) findViewById(R.id.mchid);
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.getSwitchPlan);

    }

    public void initEvent() {
        //点击当期计划 和 规格交替计划
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentVPlan();
            }
        });
        //点击跳转
        lvplan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currid != null || !currid.equals("")){
                    currid = "";
                }
                //初始化一下控件属性
                repl.setEnabled(true);
                out.setEnabled(true);
                //获取选中的计划
                v = adaprer.getItem(position);
                //计划id
                currid = v.getId();
                //展示数据在页面
                spesc.setText(v.getItnbr());
                spescname.setText(v.getItdsc());
                pro.setText(v.getPro());
                if (v.getState().equals("10")) {
                    state.setText("新计划");
                } else if (v.getState().equals("20")) {
                    state.setText("等待中");
                } else if (v.getState().equals("30")) {
                    state.setText("生产中");
                } else if (v.getState().equals("40")) {
                    state.setText("已完成");
                } else {
                    state.setText("未知状态");
                }
                pnum.setText(v.getPnum());
                //点击之后隐藏
                llmchid.setVisibility(View.GONE);
                showlist.setVisibility(View.GONE);
                lvplan.setVisibility(View.GONE);
                //点击之后显示
                showVplan.setVisibility(View.VISIBLE);
                onclick.setVisibility(View.VISIBLE);
            }
        });
        //返回
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnPager();
            }
        });
        //切换规格
        repl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogToStart();
            }
        });

    }

    //开始计划
    public void dialogToStart() {
        //显示弹窗
        new MaterialDialog.Builder(SwitchPlanActivity.this)
                .title("提示")
                .content("请确认是否切换规格")
                .positiveText(R.string.vul_confirm)
                .negativeText(R.string.vul_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //开始计划
                        String param = "SwitchID=" + v.getId() + "&USER_NAME=" + App.username;
                        new ChangePlanTask().execute(param);
                        //提示音
                        SoundPlayUtils.startNoti(SwitchPlanActivity.this);
                        SoundPlayUtils.stopAlarm();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        iscomplate = true;
                        //提示音
                        SoundPlayUtils.startAlarm(SwitchPlanActivity.this);
                        SoundPlayUtils.stopAlarm();
                    }
                })
                .cancelable(false)
                .show();
    }

    //跳转页面
    public void returnPager() {
//        //清空ID
//        currid = "";
        //点击之后隐藏
        showVplan.setVisibility(View.GONE);
        onclick.setVisibility(View.GONE);
        //点击之后显示
        llmchid.setVisibility(View.VISIBLE);
        showlist.setVisibility(View.VISIBLE);
        lvplan.setVisibility(View.VISIBLE);
        //刷新数据
        String param1 = "MCHIDLR=" + mchid + "&SHIFT=" + App.shift + "&&TYPE_N=20";
        new GetPlanTask().execute(param1);
        adaprer.notifyDataSetChanged();
    }

    //根据状态查询计划
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        mchid = tvMchid.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(SwitchPlanActivity.this, "请扫描机台号", Toast.LENGTH_SHORT).show();
        } else {
            mchid = mchid.toUpperCase();
            String param1 = "MCHIDLR=" + mchid + "&SHIFT=" + App.shift + "&TYPE_N=20";
            new GetPlanTask().execute(param1);
        }
    }

    //获取硫化计划
    class GetPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.GetCurrentVPlan, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            iscomplate = true;
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchPlanActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchPlanActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        if (datas != null && datas.size() > 0) {//有正在执行，并且有等待中的计划
                            //显示等待中的计划；
                            adaprer = new VPlanReplAdapter(SwitchPlanActivity.this, datas);
                            lvplan.setAdapter(adaprer);
                            adaprer.notifyDataSetChanged();
                            planList = datas;
                        } else {
                            Toast.makeText(SwitchPlanActivity.this, "无可规格交替的计划！", Toast.LENGTH_SHORT).show();
                            adaprer = new VPlanReplAdapter(SwitchPlanActivity.this, new ArrayList<VPlan>());
                            lvplan.setAdapter(adaprer);
                            adaprer.notifyDataSetChanged();
                            planList = datas;
                        }

                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchPlanActivity.this, "未到换班时间不可进行倒班！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchPlanActivity.this, "查询成功，没有匹配的计划！", Toast.LENGTH_SHORT).show();
                        adaprer = new VPlanReplAdapter(SwitchPlanActivity.this, new ArrayList<VPlan>());
                        lvplan.setAdapter(adaprer);
                        adaprer.notifyDataSetChanged();
                        planList = datas;
                        return;
                    } else {
                        Toast.makeText(SwitchPlanActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchPlanActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //切换规格
    class ChangePlanTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SwitchVplan, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            iscomplate = true;
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchPlanActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchPlanActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        Toast.makeText(SwitchPlanActivity.this, "规格交替成功！", Toast.LENGTH_SHORT).show();
                        returnPager();
                    } else {
                        Toast.makeText(SwitchPlanActivity.this, res.get("msg") + "", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchPlanActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        switch (keyCode){
            case 0:
                //按下扫描键时，先清空之前内容
                tvMchid.setText("");
                break;
        }
        return true;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时获取计划
        //右方向键
        String msg = "";
        switch (keyCode){
            //返回键
            case 4:
                //返回上级页面
                startActivity(new Intent(SwitchPlanActivity.this, FunctionActivity.class));
                this.finish();
                break;
            //右方向键
            case 22:
                msg = "扫描失败！";
                operate(msg);
                break;
            //扫描键
            case 0:
                msg = "扫描失败！";
                operate(msg);
                break;
            default:

                break;

        }
        return true;
    }
    private void operate(String msg) {
        if(!iscomplate){
            Toast.makeText(this, "请等待上一次操作完成再继续！", Toast.LENGTH_SHORT).show();
            return;
        }
        iscomplate = false;
        if (!StringUtil.isNullOrBlank(tvMchid.getText().toString().trim())) {
            getCurrentVPlan();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            iscomplate = true;
        }
    }

}
