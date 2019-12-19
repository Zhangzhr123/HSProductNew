package com.hsproduce.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.adapter.FormingItemAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormingActivity extends BaseActivity {

    private LinearLayout showlist, llmchid;
    private TableLayout showVplan;
    private LinearLayout onclick;
    private Button start, update, finish, out;
    private TextView spesc, spescname, pro, state, pnum;
    //当前计划展示list  规格交替列表
    private ListView lvplan;
    private VPlan v = new VPlan();
    //机台号
//    private TextView tvMchid;
    private AutoCompleteTextView tvMchid;
    private List<String> data1 = new ArrayList<>();
    //获取计划按钮
    private ImageButton btGetplan;
    //计划展示适配器  规格交替适配器
    private FormingItemAdapter adapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量 当前计划ID
    private String currid = "";
    public String mchid = "";
    public String number = "";
    public Integer isNull = 0;//判断开始按钮弹窗显示
    private VPlan vplan = new VPlan();
    private String num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_forming);
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
        start = (Button) findViewById(R.id.start);
        update = (Button) findViewById(R.id.update);
        finish = (Button) findViewById(R.id.finish);
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
        btGetplan = (ImageButton) findViewById(R.id.getPlan);

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
                start.setEnabled(true);
                update.setEnabled(true);
                finish.setEnabled(true);
                out.setEnabled(true);
                //获取点击的数据
                v = adapter.getItem(position);
                //计划id
                currid = v.getId();
//                System.out.println(currid+"   "+v.getId());
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
                //设置按钮是否可用
                if (v.getBtnflag().equals("1")) {//修改和完成不可用
                    //按钮不可用
                    update.setEnabled(false);
                    finish.setEnabled(false);
                } else if (v.getBtnflag().equals("2")) {//开始不可用
                    start.setEnabled(false);
                } else {//只有返回
                    start.setEnabled(false);
                    update.setEnabled(false);
                    finish.setEnabled(false);
                }
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
        //开始
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlan();
                //查询生产中计划
//                String parm = "?MCHID=" + mchid;
//                String str = PathUtil.START+parm;
//                String result = HttpUtil.sendGet(str, null);
//                if (StringUtil.isNullOrBlank(result)) {
//                    Toast.makeText(FormingActivity.this, "网络连接异常，请重新登录。", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                try {
//                    Map<String, Object> res = App.gson.fromJson(result, new TypeToken<Map<String, Object>>() {
//                    }.getType());
//                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
//                    }.getType());
//                    if (res != null) {
//                        if (res.get("code").equals("500")) {//返回空
//                            dialogToStart();
//                            return;
//                        } else if (res.get("code").equals("400")) {//报错异常
//                            Toast.makeText(FormingActivity.this, "获取数据错误，请联系管理员。", Toast.LENGTH_LONG).show();
//                            return;
//                        } else {
//                            dialogToFinish(datas.get(0));
//                            return;
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                new STARTTask().execute("MCHID=" + mchid);

//                if (isNull == 1) {
//                    //开始计划
//                    dialogToStart();
//                    //返回上一页面，并且上一页面重新查询。
//
//
//                } else if (isNull == 2) {
//                    //完成计划
//                    dialogToFinish();
//                    //返回上一页面，并且上一页面重新查询。
//
////                    String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift;
////                    new GetFormingPlanTask().execute(param1);
////                    adapter.notifyDataSetChanged();
//                } else if (isNull == 3) {
//                    Toast.makeText(FormingActivity.this, "网络连接异常，请重新登录。", Toast.LENGTH_LONG).show();
//                    return;
//                } else {
//                    Toast.makeText(FormingActivity.this, "数据获取异常，请联系管理员。", Toast.LENGTH_LONG).show();
//                    return;
//                }


            }
        });
        //修改
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                updateNumber();
//                final EditText et = new EditText(FormingActivity.this);
//                et.setText("");
//                et.setHint("请修改数量");
//                new AlertDialog.Builder(FormingActivity.this).setTitle("修改")
//                        .setView(et)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                if(number != null || !number.equals("")){
//                                    number = "";
//                                }
//                                number = et.getText().toString();
//                                if (number != null && !number.equals("") && Integer.valueOf(number) != 0) {
//
//                                    Integer sum = Integer.valueOf(v.getBarcodestart().substring(6,12))+Integer.valueOf(number);
//                                    if(Integer.valueOf(number)>500 || sum>999999){
//                                        Toast.makeText(FormingActivity.this, "数量不能大于500或者条码流水号不能大于999999", Toast.LENGTH_LONG).show();
//                                        return;
//                                    }
//                                    //修改操作接口
//                                    String param = "VPLANID=" + currid + "&Num=" + number + "&TEAM=" + App.shift + "&User_Name=" + App.username;
//                                    new UPDATETask().execute(param);
//
//                                } else {
//                                    Toast.makeText(FormingActivity.this, "请输入数量", Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//                        }).setNegativeButton("取消", null).show();
            }
        });
        //完成
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                finishPlan();
            }
        });

    }

    public void startPlan(){
        if (isNull == 1) {
            //开始计划
            dialogToStart();
            //返回上一页面，并且上一页面重新查询。

        } else if (isNull == 2) {
            //完成计划
            dialogToFinish();
            //返回上一页面，并且上一页面重新查询。
        } else if (isNull == 3) {
            Toast.makeText(FormingActivity.this, "网络连接异常，请重新登录。", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(FormingActivity.this, "数据获取异常，请联系管理员。", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void updateNumber(){
        final EditText et = new EditText(FormingActivity.this);
        et.setText("");
        et.setHint("请修改数量");
        new AlertDialog.Builder(FormingActivity.this).setTitle("修改")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(number != null || !number.equals("")){
                            number = "";
                        }
                        number = et.getText().toString();
                        if (number != null && !number.equals("") && Integer.valueOf(number) != 0) {

                            Integer sum = Integer.valueOf(v.getBarcodestart().substring(6,12))+Integer.valueOf(number);
                            if(Integer.valueOf(number)>500 || sum>999999){
                                Toast.makeText(FormingActivity.this, "数量不能大于500或者条码流水号不能大于999999", Toast.LENGTH_LONG).show();
                                return;
                            }
                            //修改操作接口
                            String param = "VPLANID=" + currid + "&Num=" + number + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                            new UPDATETask().execute(param);

                        } else {
                            Toast.makeText(FormingActivity.this, "请输入数量", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton("取消", null).show();
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
        //刷新页面数据
        String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift;
        new GetFormingPlanTask().execute(param1);
        adapter.notifyDataSetChanged();
    }

    private void eventsViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
        tvMchid.setAdapter(adapter);
    }

    //查询计划
    public void getCurrentVPlan() {
        //获取输入机台上barcode
        mchid = tvMchid.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(mchid)) {
            Toast.makeText(FormingActivity.this, "请扫描机台号", Toast.LENGTH_LONG).show();
        } else {
//            String lr = mchid.substring(mchid.length() - 1);
//            if (!"LR".contains(lr.toUpperCase())) {//判断有无大写字母LR
//                Toast.makeText(FormingActivity.this, "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
//                tvMchid.setText("");
//            } else {
            //已下达的计划
            String param1 = "MCHID=" + mchid + "&SHIFT=" + App.shift;
            new GetFormingPlanTask().execute(param1);
//            }
        }
//        tvMchid.setText("");
    }

    //开始计划
    public void dialogToStart() {
        //显示弹窗
        final MaterialDialog dialog = new MaterialDialog.Builder(FormingActivity.this)
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
                    Toast.makeText(FormingActivity.this, "数量或开始条码为空，请输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (nextCode.length() != 12) {
                    Toast.makeText(FormingActivity.this, "开始条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String nextjt = nextCode.substring(4, 6);
                if (!jt.equals(nextjt)) {
                    Toast.makeText(FormingActivity.this, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                Integer sum = Integer.valueOf(nextCode.substring(6,12))+Integer.valueOf(num);
                if(Integer.valueOf(num)>500 || sum>999999){
                    Toast.makeText(FormingActivity.this, "数量不能大于500或者条码流水号不能大于999999", Toast.LENGTH_LONG).show();
                    return;
                }
                //执行开始计划接口
                String param = "VPLANID=" + currid + "&StartBarcode=" + nextCode + "&Num=" + num + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                new GETSTARTTask().execute(param);
                dialog.dismiss();
            }
        });
    }

    //开始按钮中的完成上一计划
    public void dialogToFinish() {
        final MaterialDialog dialog = new MaterialDialog.Builder(FormingActivity.this)
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
                    Toast.makeText(FormingActivity.this, "条码为空，请输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (code.length() != 12) {
                    Toast.makeText(FormingActivity.this, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String nextjt = code.substring(4, 6);
                if (!jt.equals(nextjt)) {
                    Toast.makeText(FormingActivity.this, "条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String pre = vplan.getBarcodeend().substring(0,6);
                String now = code.substring(0,6);
                if(!pre.equals(now)){
                    Toast.makeText(FormingActivity.this, "条码不能跨年，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }

                //执行操作接口  ?VPLANID=53&EndBarcode=051901000018&TEAM=1&User_Name=shao
                String param = "VPLANID=" + vplan.getId() + "&EndBarcode=" + code + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                new DIALOGFINISHTask().execute(param);
                dialog.dismiss();
            }
        });
    }

    //完成操作
    public void finishPlan() {
        final MaterialDialog dialog = new MaterialDialog.Builder(FormingActivity.this)
                .customView(R.layout.dialog_finish, true)
                .show();
        //控件
        View customeView = dialog.getCustomView();
        final TextView itnbr = customeView.findViewById(R.id.input);
        final TextView itdec = customeView.findViewById(R.id.input4);
        final TextView number = customeView.findViewById(R.id.input2);
        final EditText precode = dialog.findViewById(R.id.input3);

        if (vplan != null) {
            itnbr.setText(v.getItnbr());
            itdec.setText(v.getItdsc());
            number.setText(pnum.getText().toString());
            precode.setText(v.getBarcodeend());
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
            public void onClick(View v1) {
                String jt = mchid;
                jt = jt.substring(jt.length() - 2, jt.length());
                String code = precode.getText().toString();

                //如果为空则进行操作
                if (code.equals("")) {
                    Toast.makeText(FormingActivity.this, "条码为空，请输入！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (code.length() != 12) {
                    Toast.makeText(FormingActivity.this, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String nextjt = code.substring(4, 6);
                if (!jt.equals(nextjt)) {
                    Toast.makeText(FormingActivity.this, "条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                String pre = v.getBarcodestart().substring(0,6);
                String now = code.substring(0,6);
                if(!pre.equals(now)){
                    Toast.makeText(FormingActivity.this, "条码不能跨年，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }
                //执行操作接口
                String param = "VPLANID=" + currid + "&EndBarcode=" + code + "&TEAM=" + App.shift + "&User_Name=" + App.username;
                new FINISHTask().execute(param);
                dialog.dismiss();
            }
        });

    }

    //切换计划
//    public void repItndes(String planid, String preCode, String nextCode) {
//        if (StringUtil.isNullOrEmpty(planid)) {
//            Toast.makeText(FormingActivity.this, "请选择生产计划", Toast.LENGTH_LONG).show();
//        } else {
//            String param = "VID=" + planid + "&PreviousNum=" + preCode + "&CurrentNum=" + nextCode + "&User_Name=shao" + "&TEAM=" + App.shift;
//            new StartProductionTask().execute(param); //App.username   051901100000  051901100001
//        }
//    }

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
//                Toast.makeText(FormingActivity.this, "网络连接异常，请重新登录。", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType());
                List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                }.getType());
//                System.out.println(datas.get(0).getBarcodeend());
                if (res != null) {
                    if (res.get("code").equals("500")) {//返回空
//                        dialogToStart();
                        isNull = 1;
                        return;
                    } else if (res.get("code").equals("400")) {//报错异常
                        isNull = 0;
                        return;
                    } else {
                        vplan = datas.get(0);
//                        dialogToFinish(datas.get(0));
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
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
//                        returnPager();
                        state.setText("");
                        state.setText("生产中");
                        pnum.setText("");
                        pnum.setText(num);
                        //开始按钮不可用
                        start.setEnabled(false);
                        update.setEnabled(true);
                        finish.setEnabled(true);
                        out.setEnabled(true);
                        Toast.makeText(FormingActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingActivity.this, "该计划不是等待中计划，无法执行！", Toast.LENGTH_LONG).show();
                        return;
                    } else if (res.get("code").equals("600")) {
                        Toast.makeText(FormingActivity.this, "条码并非12位，请重新输入！", Toast.LENGTH_LONG).show();
                        return;
                    } else if (res.get("code").equals("700")) {
                        Toast.makeText(FormingActivity.this, "该条码已经被使用，无法开始！", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Toast.makeText(FormingActivity.this, "错误:"+res.get("ex"), Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    //执行修改按钮
    class UPDATETask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.UPDATE, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "修改失败！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
//                        returnPager();
                        pnum.setText("");
                        pnum.setText(number);
                        //开始按钮不可用
                        start.setEnabled(false);
                        update.setEnabled(true);
                        finish.setEnabled(true);
                        out.setEnabled(true);
                        Toast.makeText(FormingActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingActivity.this, "修改失败！", Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "修改失败！", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    //结束上一个计划
    class DIALOGFINISHTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.FINISH, strs[0]);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        //弹窗执行开始计划
                        dialogToStart();
//                        Toast.makeText(FormingActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    return;
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
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
//                        returnPager();
                        state.setText("");
                        state.setText("已完成");
                        //返回按钮可用
                        start.setEnabled(false);
                        update.setEnabled(false);
                        finish.setEnabled(false);
                        out.setEnabled(true);
                        Toast.makeText(FormingActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    return;
                }
            }
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
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        for (int i = 0; i < map.size(); i++) {
                            data1.add(map.get(i).get("itemid"));
                        }
//                        Toast.makeText(DetailChangeActivity.this, "机台查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Toast.makeText(FormingActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                    return;
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
                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        boolean zxz = false;//执行中
                        boolean ywc = false;//已完成
                        for (int j = 0; j < datas.size(); j++) {
                            if (datas.get(j).getState().equals("30")) {
                                zxz = true;
                                continue;
                            }
                            if (datas.get(j).getState().equals("40")) {
                                ywc = true;
                                continue;
                            }
                        }
                        if (!zxz && !ywc) {//没有正在执行和已完成的
                            for (int i = 0; i < datas.size(); i++) {
                                datas.get(i).setBtnflag("1");//显示开始和返回
                            }
                        } else if (zxz && !ywc) {//有正在执行
                            for (int i = 0; i < datas.size(); i++) {
                                if (datas.get(i).getState().equals("30")) {
                                    datas.get(i).setBtnflag("2");//只显示修改和完成
                                } else {
                                    datas.get(i).setBtnflag("3");//只有返回
                                }
                            }
                        } else if (ywc && !zxz) {//有已完成
                            for (int i = 0; i < datas.size(); i++) {
                                datas.get(i).setBtnflag("3");//只有返回
                            }
                        } else {//有执行中和已完成的
                            for (int i = 0; i < datas.size(); i++) {
                                if (datas.get(i).getState().equals("30")) {
                                    datas.get(i).setBtnflag("2");//只显示修改和完成
                                } else {
                                    datas.get(i).setBtnflag("3");//只有返回
                                }
                            }
                        }

//                        for (int i = 0; i < datas.size(); i++) {
//                            if(datas.get(i).getState().equals("30")){
//                                show = false;
//                                break;
//                            }else{
//                                show = true;
//                                break;
//                            }
//                        }
                        //展示计划列表
                        List<VPlan> newDatas = new ArrayList<>();
                        for(int n1=0;n1<datas.size();n1++){
                            if(datas.get(n1).getState().equals("30")){
                                newDatas.add(datas.get(n1));
                            }
                        }
                        for(int n2=0;n2<datas.size();n2++){
                            if(datas.get(n2).getState().equals("20")){
                                newDatas.add(datas.get(n2));
                            }
                        }
                        for(int n3=0;n3<datas.size();n3++){
                            if(datas.get(n3).getState().equals("40")){
                                newDatas.add(datas.get(n3));
                            }
                        }
                        adapter = new FormingItemAdapter(FormingActivity.this, newDatas);
                        lvplan.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(FormingActivity.this, "计划查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(FormingActivity.this, "未到换班时间不可进行倒班！", Toast.LENGTH_LONG).show();
                        return;
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingActivity.this, "查询成功，没有匹配的计划！", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Toast.makeText(FormingActivity.this, "计划查询错误,请重新操作！", Toast.LENGTH_LONG).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        }
    }

    //切换计划
//    class StartProductionTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = HttpUtil.sendGet(PathUtil.StartProduction, strings[0]);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (StringUtil.isNullOrBlank(s)) {
//                Toast.makeText(FormingActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
//                    }.getType());
//                    if (res == null || res.isEmpty()) {
//                        Toast.makeText(FormingActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
//                    }
//                    if (res.get("code").equals("200")) {
////                        getCurrentVPlan();//展示替换后的计划
////                        tvMchid.setText("");
//                        Toast.makeText(FormingActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("300")) {
//                        Toast.makeText(FormingActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
//                    } else if (res.get("code").equals("500")) {
//                        Toast.makeText(FormingActivity.this, "操作失败，请确认上一计划的结束条码！", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(FormingActivity.this, "错误，请重新操作！", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(FormingActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//
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
        //F1
        if(keyCode == 131){
            startPlan();
        }
        //F1
        if(keyCode == 132){
            updateNumber();
        }
        //F1
        if(keyCode == 133){
            finishPlan();
        }
        //F1
        if(keyCode == 134){
            returnPager();
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
