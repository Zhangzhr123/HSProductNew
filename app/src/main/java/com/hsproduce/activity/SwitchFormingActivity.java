package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
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
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwitchFormingActivity extends BaseActivity {

    private LinearLayout showlist, llmchid;
    private TableLayout showVplan;
    private LinearLayout onclick;
    private Button repl, out;
    private TextView spesc, spescname, pro, state, pnum;
    //当前计划展示list  规格交替列表
    private ListView lvplan;
    private VPlan v = new VPlan();
    //输入框
    private AutoCompleteTextView tvMchid;
    private List<String> data1 = new ArrayList<>();
    //获取计划按钮
    private ButtonView btGetplan;
    //计划展示适配器  规格交替适配器
    private FormingReplAdapter adaprer;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量 当前计划ID
    private String currid = "";
    public String mchid = "";
    public Integer isNull = 0;//判断开始按钮弹窗显示
    private VPlan vplan = new VPlan();
    private String num = "";

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
        tvMchid = (AutoCompleteTextView) findViewById(R.id.mchid);
        new MCHIDTask().execute("TYPE_ID=10107");
        eventsViews();
        //获取计划按钮
        btGetplan = (ButtonView) findViewById(R.id.getSwitchPlan);

    }

    private void eventsViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
        tvMchid.setAdapter(adapter);
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
                //获取点击的数据
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
                //查询生产中计划
                new STARTTask().execute("MCHID=" + mchid);
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
                if (isNull == 1) {
                    //开始计划
                    dialogToStart();
                    //返回上一页面，并且上一页面重新查询。

                } else if (isNull == 2) {
                    //完成计划
                    dialogToFinish();
                    //返回上一页面，并且上一页面重新查询。

                } else if (isNull == 3) {
                    Toast.makeText(SwitchFormingActivity.this, "网络连接异常，请重新登录。", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(SwitchFormingActivity.this, "数据获取异常，请联系管理员。", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }

    //开始计划
    public void dialogToStart() {
        //显示弹窗
        final MaterialDialog dialog = new MaterialDialog.Builder(SwitchFormingActivity.this)
                .customView(R.layout.dialog_product, true)
                .show();
        //控件
        View customeView = dialog.getCustomView();
        final EditText next = dialog.findViewById(R.id.input);
        final EditText number = dialog.findViewById(R.id.input2);
        Button finish = customeView.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button ok = customeView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num != null || !num.equals("")){
                    num = "";
                }
                //条码位数为12为   05（工厂代码）19（年份）25（机台编码最后两位）123456（流水码）
                String jt = mchid;
                jt = jt.substring(jt.length() - 2, jt.length());
                String nextCode = next.getText().toString();
                num = number.getText().toString();
                //如果为空则进行操作
                if (num.equals("") || Integer.valueOf(num) <= 0 || nextCode.equals("")) {
                    Toast.makeText(SwitchFormingActivity.this, "数量或开始条码为空，请输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (nextCode.length() != 12) {
                    Toast.makeText(SwitchFormingActivity.this, "开始条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String nextjt = nextCode.substring(4, 6);
                if (!jt.equals(nextjt)) {
                    Toast.makeText(SwitchFormingActivity.this, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }

                //执行操作接口
                String param = "VPLANID=" + currid + "&StartBarcode=" + nextCode + "&Num=" + num + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                new GETSTARTTask().execute(param);
                dialog.dismiss();
            }
        });
    }

    //开始按钮中的完成上一计划
    public void dialogToFinish() {
        final MaterialDialog dialog = new MaterialDialog.Builder(SwitchFormingActivity.this)
                .customView(R.layout.dialog_prefinish, true)
                .show();
        //控件
        View customeView = dialog.getCustomView();
        final TextView itnbr = customeView.findViewById(R.id.input);
        final TextView itdec = customeView.findViewById(R.id.input4);
        final TextView number = customeView.findViewById(R.id.input2);
        final EditText precode = dialog.findViewById(R.id.input3);

        if (vplan != null) {
            itnbr.setText(vplan.getItnbr());
            itdec.setText(vplan.getItdsc());
            number.setText(vplan.getPnum());
            precode.setText(vplan.getBarcodeend());
        }
        Button finish = customeView.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button ok = customeView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jt = mchid;
                jt = jt.substring(jt.length() - 2, jt.length());
                String code = precode.getText().toString();

                //如果为空则进行操作
                if (code.equals("")) {
                    Toast.makeText(SwitchFormingActivity.this, "条码为空，请输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (code.length() != 12) {
                    Toast.makeText(SwitchFormingActivity.this, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String nextjt = code.substring(4, 6);
                if (!jt.equals(nextjt)) {
                    Toast.makeText(SwitchFormingActivity.this, "条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }

                //执行操作接口
                String param = "VPLANID=" + vplan.getId() + "&EndBarcode=" + code + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                new FINISHTask().execute(param);
                dialog.dismiss();
            }
        });
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
        String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift;
        new GetFormingPlanTask().execute(param1);
        adaprer.notifyDataSetChanged();
    }

    //根据状态查询计划
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        mchid = tvMchid.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(SwitchFormingActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        } else {
            String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift;
            new GetFormingPlanTask().execute(param1);
        }
    }

//    //切换规格显示列表
//    public void repItndes(String planid, String preCode, String nextCode) {
//        if (StringUtil.isNullOrEmpty(planid)) {
//            Toast.makeText(SwitchFormingActivity.this, "请选择您要替换规格", Toast.LENGTH_LONG).show();
//        } else {
//            //?CurrentID=34&SwitchID=33&CurrentEndCode=51901100035&SwitchStartCode=51901100036&USER_NAME=shao&TEAM=1
//            String param = "CurrentID=" + currid + "&SwitchID=" + planid + "&CurrentEndCode=" + preCode + "&SwitchStartCode=" + nextCode + "&USER_NAME=" + App.username + "&TEAM=" + App.shift;
//            new SwitchVplanTask().execute(param);
//        }
//    }

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

    //获取成型计划
    class GetFormingPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGPLAN, strings[0]);
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
                        List<VPlan> zxz = new ArrayList<>();//执行中
                        List<VPlan> ddz = new ArrayList<>();//等待中
                        for (int j = 0; j < datas.size(); j++) {
                            if (datas.get(j).getState().equals("30")) {
                                zxz.add(datas.get(j));
                                continue;
                            }
                            if (datas.get(j).getState().equals("20")) {
                                ddz.add(datas.get(j));;
                                continue;
                            }
                        }
                        if (zxz.size()>0 && ddz.size()>0) {//有正在执行，并且有等待中的计划
                            //显示等待中的计划；
                            adaprer = new FormingReplAdapter(SwitchFormingActivity.this, ddz);
                            lvplan.setAdapter(adaprer);
                            adaprer.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SwitchFormingActivity.this, "无可规格交替的计划！", Toast.LENGTH_LONG).show();
                        }

                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchFormingActivity.this, "未到换班时间不可进行倒班！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有匹配的计划！", Toast.LENGTH_LONG).show();
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

    //查询有没有已完成的计划
    class STARTTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.START, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                isNull = 3;
                return;
            }
            try {
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType());
                List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                }.getType());
                if (res != null) {
                    if (res.get("code").equals("500")) {//返回空
                        isNull = 1;
                        return;
                    } else if (res.get("code").equals("400")) {//报错异常
                        isNull = 0;
                        return;
                    } else {
                        vplan = datas.get(0);
                        isNull = 2;
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //执行开始按钮
    class GETSTARTTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.GETSTART, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
//                        returnPager();
                        state.setText("");
                        state.setText("生产中");
                        pnum.setText("");
                        pnum.setText(num);
                        //开始按钮不可用
                        repl.setEnabled(false);
                        out.setEnabled(true);
                        Toast.makeText(SwitchFormingActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(SwitchFormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(SwitchFormingActivity.this, "该计划不是等待中计划，无法执行！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("600")) {
                        Toast.makeText(SwitchFormingActivity.this, "条码并非12位，请重新输入！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("700")) {
                        Toast.makeText(SwitchFormingActivity.this, "该条码已经被使用，无法开始！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //执行完成按钮
    class FINISHTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.FINISH, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SwitchFormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //弹窗
                        dialogToStart();
//                        Toast.makeText(FormingActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SwitchFormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SwitchFormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //获取生产中的生产计划
//    class GetAPlanTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMINGPLAN, strings[0]);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (StringUtil.isNullOrBlank(s)) {
//                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
//                    }.getType());
//                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
//                    }.getType());
//                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(SwitchFormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
//                    }
//                    if (res.get("code").equals("200")) {
//                        //显示切换按钮
////                        show = true;
//                        //获取当前计划ID
//                        currid = datas.get(0).getId();
//                        //展示当前计划
//                        spesc.setText(datas.get(0).getItnbr());
//                        spescname.setText(datas.get(0).getItdsc());
////                        anum.setText(datas.get(0).getAnum());
//                        pnum.setText(datas.get(0).getPnum());
////                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("300")) {
//                        Toast.makeText(SwitchFormingActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("500")) {
//                        //隐藏切换按钮
////                        show = false;
//                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有生产中的计划！", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(SwitchFormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    }

    //获取等待中的生产计划
//    class GetPPlanTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMINGPLAN, strings[0]);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (StringUtil.isNullOrBlank(s)) {
//                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
//                    }.getType());
//                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
//                    }.getType());
//                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(SwitchFormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
//                    }
//                    if (res.get("code").equals("200")) {
//                        //展示规格替换列表
//                        adaprer = new FormingReplAdapter(SwitchFormingActivity.this, datas);
//                        lvplan.setAdapter(adaprer);
//                        adaprer.notifyDataSetChanged();
////                        Toast.makeText(SwitchPlanActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("300")) {
//                        Toast.makeText(SwitchFormingActivity.this, "机台号不正确！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("500")) {
//                        Toast.makeText(SwitchFormingActivity.this, "查询成功，没有等待的计划！", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(SwitchFormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    }

//    //切换规格
//    class SwitchVplanTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = HttpUtil.sendGet(PathUtil.SWITCHFORMING, strings[0]);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (StringUtil.isNullOrBlank(s)) {
//                Toast.makeText(SwitchFormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
//                    }.getType());
//                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(SwitchFormingActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
//                    }
//                    if (res.get("code").equals("200")) {
//                        getCurrentVPlan();//展示替换后的计划
////                        tvMchid.setText("");
//                        Toast.makeText(SwitchFormingActivity.this, "切换成功！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("100")) {
//                        Toast.makeText(SwitchFormingActivity.this, "新切换的计划变动，切换失败，请刷新！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("300")) {
//                        Toast.makeText(SwitchFormingActivity.this, "切换失败！", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(SwitchFormingActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(SwitchFormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    }

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
                tofunction();
//                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);//注销功能
            }
        }
        //左方向键
        if (keyCode == 21) {
//            tofunction(); //BaseActivity  返回功能页面函数
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
