package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.*;
import com.hsproduce.bean.VLoad;
import com.hsproduce.bean.VLoadHxm;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.xuexiang.xui.widget.picker.widget.WheelTime.dateFormat;

//装车出厂页面
public class LoadFactoryActivity extends BaseActivity {

    //定义控件
    private HorizontalScrollView hs_load, hs_spesc;
    private View ll_search, ll_load, llfacok, llcode, llscanok, llcodelog, lloutcode, lloutcodelog, lloutok;
    private TextView outbarcodelog, barcode, anum, search, itnbr, itndsc, outbarcode, outanum;
    private TextView barcodelog;
    private ButtonView ok, get_code, outcode, outok;
    private Button out, loadfacok, inscan, outscan;
    private ImageButton getsearch;
    private TableRow table;
    private TableLayout lltable;
    private View view6;
    //标题控件
    private TitleBar load, inload, outload;
    //显示列表
    private ListView lvload, lvloadfac, lvloadspesc;
    private List<VLoad> lists = new ArrayList<>();
    //展示信息
    private List<VLoad> list = new ArrayList<>();
    //适配器
    private LoadAdapter loadAdapter;
    private LoadFacAdapter loadFacAdapter;
    private LoadSpescAdapter loadSpescAdapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量  主表ID  条码  装车单ID 规格
    private String Id = "", code = "", loadid = "", codeitnbr = "", outCode = "";
    private List<VLoadHxm> loaditnbr = new ArrayList<>();
    private int number = 0;
    private int outnumber = 0;
    //显示条码记录
    private List<String> log = new ArrayList<>();
    private List<String> outlog = new ArrayList<>();
    //添加条码判断是否重复
    private List<String> codelist = new ArrayList<>();
    private List<String> outcodelist = new ArrayList<>();
    private Boolean isNew = true;
    private Boolean outIsNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_loadfactory);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //标题栏
        load = (TitleBar) findViewById(R.id.load);
        inload = (TitleBar) findViewById(R.id.inload);
        outload = (TitleBar) findViewById(R.id.outload);
        //HorizontalScrollView  ListView列表
        hs_load = (HorizontalScrollView) findViewById(R.id.hs_load);
        hs_spesc = (HorizontalScrollView) findViewById(R.id.hs_spesc);
        view6 = findViewById(R.id.view6);
        //layout
        ll_search = findViewById(R.id.ll_search);
        ll_load = findViewById(R.id.ll_load_fac);
        llfacok = findViewById(R.id.ll_fac_ok);
        llscanok = findViewById(R.id.ll_scan_ok);
        llcode = findViewById(R.id.ll_code);
        llcodelog = findViewById(R.id.ll_codelog);
        table = (TableRow) findViewById(R.id.table);
        lltable = (TableLayout) findViewById(R.id.ll_table);
        //取消扫描
        lloutcode = findViewById(R.id.ll_out_code);
        lloutcodelog = findViewById(R.id.ll_out_codelog);
        lloutok = findViewById(R.id.ll_scan_out_ok);
        outcode = (ButtonView) findViewById(R.id.out_code);
        outok = (ButtonView) findViewById(R.id.out_ok);
        //条码扫描
        outbarcode = (TextView) findViewById(R.id.out_barcode);
        //条码记录
        outbarcodelog = (TextView) findViewById(R.id.out_barcode_log);
        //计数
        outanum = (TextView) findViewById(R.id.out_anum);
        //搜索框
        search = (TextView) findViewById(R.id.search);
        //输入框
        barcode = (TextView) findViewById(R.id.barcode);
        //显示录入条码个数
        anum = (TextView) findViewById(R.id.anum);
        //规格
        itnbr = (TextView) findViewById(R.id.itnbr);
        itndsc = (TextView) findViewById(R.id.itndsc);
        //条码记录
        barcodelog = (TextView) findViewById(R.id.barcode_log);
        //点击按钮
        getsearch = (ImageButton) findViewById(R.id.get_search);
        inscan = (Button) findViewById(R.id.in_scan);
        outscan = (Button) findViewById(R.id.out_scan);
        out = (Button) findViewById(R.id.out);
        loadfacok = (Button) findViewById(R.id.loadfac_ok);
        ok = (ButtonView) findViewById(R.id.ok);
        get_code = (ButtonView) findViewById(R.id.get_code);
        //ListView
        lvload = (ListView) findViewById(R.id.listview);
        lvloadfac = (ListView) findViewById(R.id.lv_load_fac);
        lvloadspesc = (ListView) findViewById(R.id.lv_load_spesc);
        //展示装车单
        String parm = "SUB_CODE=" + "&CAR_CODE=";
        new SelVLoadTask().execute(parm);
    }

    public void initEvent() {
        //搜索按钮
        getsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchstr = search.getText().toString().trim();
                //展示装车单
                if (isNumeric(searchstr)) {
                    String parm = "SUB_CODE=" + searchstr + "&CAR_CODE=";
                    new SelVLoadTask().execute(parm);
                } else {
                    String parm = "SUB_CODE=" + "&CAR_CODE=" + searchstr;
                    new SelVLoadTask().execute(parm);
                }
                search.setText("");
            }
        });
        //点击列表
        lvload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //查询装车单明细
                Id = loadAdapter.getItem(position).getId();
                //查询当前单据的装车单明细
                loadVS();
            }
        });
        //出厂扫描
        inscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏
                load.setVisibility(View.GONE);//标题
                llfacok.setVisibility(View.GONE);
                hs_spesc.setVisibility(View.GONE);
                table.setVisibility(View.GONE);
                ll_load.setVisibility(View.GONE);
                lvloadfac.setVisibility(View.GONE);
                //显示
                inload.setVisibility(View.VISIBLE);//标题
                llcode.setVisibility(View.VISIBLE);
                lltable.setVisibility(View.VISIBLE);
                view6.setVisibility(View.VISIBLE);
                llcodelog.setVisibility(View.VISIBLE);
                llscanok.setVisibility(View.VISIBLE);
                //焦点扫描框
                barcode.setText("");
                barcodelog.setText("");
                barcode.requestFocus();
            }
        });
        //取消扫描
        outscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏
                load.setVisibility(View.GONE);//标题
                llfacok.setVisibility(View.GONE);
                hs_spesc.setVisibility(View.GONE);
                table.setVisibility(View.GONE);
                ll_load.setVisibility(View.GONE);
                lvloadfac.setVisibility(View.GONE);
                //显示
                outload.setVisibility(View.VISIBLE);//标题
                lloutcode.setVisibility(View.VISIBLE);
                lloutcodelog.setVisibility(View.VISIBLE);
                lloutok.setVisibility(View.VISIBLE);
                //锁定扫描框
                outbarcode.setText("");
                outbarcodelog.setText("");
                outbarcode.requestFocus();
            }
        });
        //取消扫描功能按钮
        outcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outcode();
            }
        });
        //取消扫描确定
        outok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏
                lloutok.setVisibility(View.GONE);
                lloutcodelog.setVisibility(View.GONE);
                lloutcode.setVisibility(View.GONE);
                outload.setVisibility(View.GONE);//标题
                //显示
                load.setVisibility(View.VISIBLE);//标题
                lvloadfac.setVisibility(View.VISIBLE);
                ll_load.setVisibility(View.VISIBLE);
                table.setVisibility(View.VISIBLE);
                hs_spesc.setVisibility(View.VISIBLE);
                llfacok.setVisibility(View.VISIBLE);
                //刷新规格列表
                outanum.setText("0");
                outlog.clear();
                outnumber = 0;
                new SelVLoadListMXTask().execute("ID=" + Id);
            }
        });
        //取消返回初始页面
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏
                llfacok.setVisibility(View.GONE);
                hs_spesc.setVisibility(View.GONE);
                table.setVisibility(View.GONE);
                ll_load.setVisibility(View.GONE);
                lvloadfac.setVisibility(View.GONE);
                //显示
                ll_search.setVisibility(View.VISIBLE);
                hs_load.setVisibility(View.VISIBLE);
                //刷新装车单列表
                String parm = "SUB_CODE=" + "&CAR_CODE=";
                new SelVLoadTask().execute(parm);
            }
        });
        //出厂扫描确定
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏
                inload.setVisibility(View.GONE);//标题
                llscanok.setVisibility(View.GONE);
                llcodelog.setVisibility(View.GONE);
                view6.setVisibility(View.GONE);
                lltable.setVisibility(View.GONE);
                llcode.setVisibility(View.GONE);
                //显示
                load.setVisibility(View.VISIBLE);//标题
                lvloadfac.setVisibility(View.VISIBLE);
                ll_load.setVisibility(View.VISIBLE);
                table.setVisibility(View.VISIBLE);
                hs_spesc.setVisibility(View.VISIBLE);
                llfacok.setVisibility(View.VISIBLE);
                //刷新规格列表
                anum.setText("0");
                number = 0;
                log.clear();
                new SelVLoadListMXTask().execute("ID=" + Id);
            }
        });
        //点击测试
        get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadcode();
            }
        });
        //装车完成发送wms
        loadfacok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parm = "LOADNO=" + loadid + "&USER_NAME=" + App.username;
                new ToWMSTask().execute(parm);
            }
        });
    }

    //查询当前单据的装车单明细
    public void loadVS() {
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(Id)) {
                list.add(lists.get(i));
            }
        }
        //展示当前装车单
        loadFacAdapter = new LoadFacAdapter(LoadFactoryActivity.this, list);
        lvloadfac.setAdapter(loadFacAdapter);
        loadFacAdapter.notifyDataSetChanged();
        //获取装车单明细
        new SelVLoadListMXTask().execute("ID=" + Id);
    }

    //出厂扫描
    public void loadcode() {
        //获取轮胎条码
        code = barcode.getText().toString().trim();
        codelist.add(code);
        for (int i = 0; i < codelist.size(); i++) {
            if (code.equals(codelist.get(i))) {
                isNew = false;
                return;
            }
        }
        if (isNew) {
            //查询轮胎条码规格
            String parm1 = "TYRE_CODE=" + code;
            new SelCodeTask().execute(parm1);
        } else {
            Toast.makeText(LoadFactoryActivity.this, "此条码已经扫描", Toast.LENGTH_LONG).show();
        }

        //barcode.setText("");
    }

    //取消扫描
    public void outcode() {
        outCode = outbarcode.getText().toString().trim();
        outcodelist.add(outCode);
        for (int j = 0; j < outcodelist.size(); j++) {
            if (outCode.equals(outcodelist.get(j))) {
                outIsNew = false;
                return;
            }
        }
        if (outIsNew) {
            String parm = "TYRE_CODE=" + outCode + "&VLOAD_ID=" + Id + "&USER_NAME=" + App.username;
            new DelVLoadTask().execute(parm);
        } else {
            Toast.makeText(LoadFactoryActivity.this, "此条码已经扫描", Toast.LENGTH_LONG).show();
        }
        //outbarcode.setText("");
    }

    //展示装车单信息
    class SelVLoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelVLOADList, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    lists = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VLoad>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (lists == null || lists.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到规格", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //展示装车单
                        loadAdapter = new LoadAdapter(LoadFactoryActivity.this, lists);
                        lvload.setAdapter(loadAdapter);
                        loadAdapter.notifyDataSetChanged();
//                        Toast.makeText(LoadFactoryActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "查询成功，没有匹配的信息！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //展示装车单规格信息
    class SelVLoadListMXTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelVLOADListMX, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //隐藏
            ll_search.setVisibility(View.GONE);
            hs_load.setVisibility(View.GONE);
            //显示
            lvloadfac.setVisibility(View.VISIBLE);
            ll_load.setVisibility(View.VISIBLE);
            table.setVisibility(View.VISIBLE);
            hs_spesc.setVisibility(View.VISIBLE);
            llfacok.setVisibility(View.VISIBLE);
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VLoadHxm> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VLoadHxm>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //获取装车单轮胎规格  获取装车单ID
                        for (int i = 0; i < datas.size(); i++) {
                            loaditnbr.add(datas.get(i));
                        }
                        //展示装车单明细
                        loadSpescAdapter = new LoadSpescAdapter(LoadFactoryActivity.this, datas);
                        lvloadspesc.setAdapter(loadSpescAdapter);
                        loadSpescAdapter.notifyDataSetChanged();
//                        Toast.makeText(LoadFactoryActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "查询成功，没有匹配的信息！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //根据条码查询轮胎规格
    class SelCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelTYRE_CODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //获取条码规格
                        codeitnbr = datas.get(0).getItnbr();
                        //判断条码规格是否在装车单中
                        System.out.println(loaditnbr.size());
                        for (int i = 0; i < loaditnbr.size(); i++) {
                            if (codeitnbr.equals(loaditnbr.get(i).getItnbr())) {
                                //loadid = loaditnbr.get(i).getId();
                                //展示装车单明细
                                itnbr.setText("");
                                itndsc.setText("");
                                itnbr.setText(loaditnbr.get(i).getItnbr());
                                itndsc.setText(loaditnbr.get(i).getItdsc());
                                //出厂扫描
                                String parm2 = "TYRE_CODE=" + code + "&VLOAD_ID=" + Id + "&USER_NAME=" + App.username;
                                new InsVLoadTask().execute(parm2);
                                return;
                            } else {
                                //Toast.makeText(LoadFactoryActivity.this, "装车单中无此规格，请重新扫描！", Toast.LENGTH_LONG).show();
                            }
                        }
//                        itnbr.setText("装车单没有此条码规格");
//                        itndsc.setText("装车单没有此条码规格");
                        //Toast.makeText(LoadFactoryActivity.this, "装车单中无此规格，请重新扫描！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "查询成功，没有匹配的条码！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //出厂扫描
    class InsVLoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.InsVLOAD, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //显示绑定条码数量
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        log.add("[" + date + "]" + code);
                        //log.add(code);
                        barcodelog.setText("");
                        for (int i = 0; i < log.size(); i++) {
                            if (i == 0) {
                                barcodelog.setText(log.get(i));
                            } else {
                                barcodelog.setText(getlog(log));
                            }
                        }
                        anum.setText("");
                        number++;
                        anum.setText(number + "");
                        Toast.makeText(LoadFactoryActivity.this, "操作成功，条码出厂！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(LoadFactoryActivity.this, "未找到轮胎信息，操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(LoadFactoryActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "装车单中，并无该规格，无法出库！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("600")) {
                        Toast.makeText(LoadFactoryActivity.this, "该条码已出库，无法重复出库！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //取消扫描
    class DelVLoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.DelVLOAD, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //显示绑定条码数量
                        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        outlog.add("[" + date + "]" + outCode);
                        outbarcodelog.setText("");
                        for (int i = 0; i < outlog.size(); i++) {
                            if (i == 0) {
                                outbarcodelog.setText(outlog.get(i));
                            } else {
                                outbarcodelog.setText(getoutlog(outlog));
                            }
                        }
                        outanum.setText("");
                        outnumber++;
                        outanum.setText(outnumber + "");
                        Toast.makeText(LoadFactoryActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(LoadFactoryActivity.this, "未找到轮胎信息，操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(LoadFactoryActivity.this, "操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "该轮胎并未出库，无法取消！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "错误，请重新操作", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //发送WMS
    class ToWMSTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SendWMS, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(LoadFactoryActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(LoadFactoryActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        Toast.makeText(LoadFactoryActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(LoadFactoryActivity.this, "该出库单已经发送，操作失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(LoadFactoryActivity.this, "操作失败，请重新上传！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(LoadFactoryActivity.this, "未找到该出库单，操作失败！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoadFactoryActivity.this, "错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoadFactoryActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //是否是纯数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //递归显示
    public String getlog(List<String> list) {
        String logstr = "";
        Collections.reverse(list);//倒序
        for (int i = 0; i < list.size(); i++) {
            logstr += list.get(i) + "\n";
        }
        return logstr;
    }

    public String getoutlog(List<String> list) {
        String logstr = "";
        Collections.reverse(list);//倒序
        for (int i = 0; i < list.size(); i++) {
            logstr += list.get(i) + "\n";
        }
        return logstr;
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //右方向键
        if (keyCode == 22) {
            if (barcode.getText().toString().trim() != null && !barcode.getText().toString().trim().equals("")) {
                loadcode();
            } else if (outbarcode.getText().toString().trim() != null && !outbarcode.getText().toString().trim().equals("")) {
                outcode();
            } else {
                Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
            }

        }
        if (keyCode == 0) {
            barcode.setText("");
            outbarcode.setText("");
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
        //返回键时间间隔超过两秒 返回功能页面
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
//            if (barcode.getText().toString().trim() != null && !barcode.getText().toString().trim().equals("")) {
//                loadcode();
//            } else if (outbarcode.getText().toString().trim() != null && !outbarcode.getText().toString().trim().equals("")) {
//                outcode();
//            } else {
//                Toast.makeText(this, "扫描失败", Toast.LENGTH_SHORT).show();
//            }
//
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }

}
