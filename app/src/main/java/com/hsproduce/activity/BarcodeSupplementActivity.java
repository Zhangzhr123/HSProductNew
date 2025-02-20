package com.hsproduce.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.adapter.PlanDialogItemAdapter;
import com.hsproduce.bean.Team;
import com.hsproduce.bean.VPlan;
import com.hsproduce.bean.VreCord;
import com.hsproduce.broadcast.SystemBroadCast;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.*;

import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_ACTION_SCODE;
import static com.hsproduce.broadcast.SystemBroadCast.SCN_CUST_EX_SCODE;

/**
 * 条码补录页面
 * 扫描条码，填写条码明细，将条码添加进硫化生产实绩中
 * createBy zhangzr @ 2019-12-20
 * 1.规格不用填，由规格名称带出
 * 2.机台为查询下拉模式选择
 * 3.时间格式为yyyy-mm-dd，时间控件月份自动加一为正确时间
 * 4.规格名称有中文和特殊字符需要转换
 * 5.扫描回调改为广播监听方式
 * 6.封装SDK
 * 7.增加霍尼韦尔SDK
 */
public class BarcodeSupplementActivity extends BaseActivity {

    //定义控件
    private TextView tvBarCode, tvSpesc, tvDate, tvMaster, tvSpescName;
    private AutoCompleteTextView autoTvMchid;
    private Spinner spShift, spLorR, spTeam;
    private ButtonView btOk, btGetItnbr;
    //下拉列表
    private List<String> shiftList = new ArrayList<>();
    private ArrayAdapter<String> shiftAdapter;
    //存放班组数据
    private List<Team> teamList = new ArrayList<>();
    //定义变量
    private String barCode = "", spesc = "", spescName = "", lorR = "", pDate = "", shift = "", mchId = "", team = "", creatUser = "", planID = "";
    //Dialog显示列表
    private List<String> itdscList = new ArrayList<>();
    private DialogItemAdapter itnbrAdapter;
    private List<String> mchidList = new ArrayList<>();
    private DialogItemAdapter mchidAdapter;
    private List<VPlan> planList = new ArrayList<>();
    private PlanDialogItemAdapter planAdapter;
    private Map<String, VreCord> itdscMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_barcodesupplement);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onResume() {
//        //注册广播监听
//        IntentFilter intentFilter = new IntentFilter(SCN_CUST_ACTION_SCODE);
//        registerReceiver(scanDataReceiver, intentFilter);
//        super.onResume();
//    }

    public void initView() {
        //补录条码
        tvBarCode = (TextView) findViewById(R.id.barcode);
        //获得焦点
        tvBarCode.requestFocus();
        tvBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        //规格编码
        tvSpesc = (TextView) findViewById(R.id.spesc);
        //规格名称
        tvSpescName = (TextView) findViewById(R.id.spescname);
        btGetItnbr = (ButtonView) findViewById(R.id.getitnbr);
        //生产时间
        tvDate = (TextView) findViewById(R.id.date);
        tvDate.setFocusable(false);//让EditText失去焦点，然后获取点击事件
        //机台号
        autoTvMchid = (AutoCompleteTextView) findViewById(R.id.mchid);
        //左右膜
        spLorR = (Spinner) findViewById(R.id.LorR);
        //班组
        spTeam = (Spinner) findViewById(R.id.team);
        //班次
        spShift = (Spinner) findViewById(R.id.shift);
        //主手
//        master = (TextView)findViewById(R.id.master);
//        master.setText(App.username);
        //条码补录
        btOk = (ButtonView) findViewById(R.id.ok);

        //班组 下拉列表
        new ShiftTask().execute();
        shiftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shiftList);
        spTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                team = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < teamList.size(); i++) {
                    if (team.equals(teamList.get(i).getName())) {
                        team = teamList.get(i).getId();
                        break;
                    } else if (team.equals(teamList.get(i).getName())) {
                        team = teamList.get(i).getId();
                        break;
                    } else if (team.equals(teamList.get(i).getName())) {
                        team = teamList.get(i).getId();
                        break;
                    } else if (team.equals(teamList.get(i).getName())) {
                        team = teamList.get(i).getId();
                        break;
                    } else {
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getMchid();
    }

    public void initEvent() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(BarcodeSupplementActivity.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                TextView show = (TextView) findViewById(R.id.date);
                                show.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //条码补录
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supCode();
            }
        });
        //查询规格编码
        btGetItnbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItnbr();
            }
        });
    }

    //条码补录
    public void supCode() {
        //规格编码
        spesc = tvSpesc.getText().toString().trim();
        //规格名称
        spescName = tvSpescName.getText().toString().trim();
        spescName = StringUtil.toUtf8String(spescName);

        //补录条码
        barCode = tvBarCode.getText().toString().trim();
        //生产日期
        pDate = tvDate.getText().toString().trim();
        //左右膜
        lorR = spLorR.getSelectedItem().toString().trim();
        //班次
        shift = spShift.getSelectedItemPosition() + "";
        //班组
        team = spTeam.getSelectedItemPosition() + "";
        //机台
        mchId = autoTvMchid.getText().toString().trim();

        //主手
//        creatuser = master.getText().toString().trim();
        if (StringUtil.isNullOrBlank(spesc)) {
            Toast.makeText(BarcodeSupplementActivity.this, "请填写规格编码", Toast.LENGTH_LONG).show();
        } else if (StringUtil.isNullOrBlank(spescName)) {
            Toast.makeText(BarcodeSupplementActivity.this, "请填写规格名称", Toast.LENGTH_LONG).show();
        } else if (StringUtil.isNullOrBlank(barCode)) {
            Toast.makeText(BarcodeSupplementActivity.this, "请扫描补录条码", Toast.LENGTH_LONG).show();
        } else {
            String parm = "MCHID=" + mchId + "&ITNBR=" + spesc + "&ITDSC=" + spescName + "&LoR=" + lorR + "&SHIFT=" + shift//+"&TEAM="+Team
                    + "&TIME_A=" + pDate + "&USER_NAME=" + App.username + "&SwitchTYRE_CODE=" + barCode;
            System.out.println(parm);
            new SupCodeTask().execute(parm);
        }
    }

    //筛选规格
    public void getItnbr() {
        //清空数据
        itdscList.clear();
        itdscMap.clear();
        tvSpesc.setText("");
        String search = tvSpescName.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(search) && search.length() > 12) {
            search = search.substring(0, 12);
        }
        search = search.toUpperCase();//大写转换
        String parm = "ITDSC=" + search;
        new GetSpecTask().execute(parm);
    }

    //筛选机台
    public void getMchid() {
        //清空数据
        mchidList.clear();
        String parm = "TYPE_ID=10098";
        new MCHIDTask().execute(parm);
    }

//    //广播监听
//    private BroadcastReceiver scanDataReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(SCN_CUST_ACTION_SCODE)) {
//                try {
//                    String barCode = "";
//                    barCode = intent.getStringExtra(SCN_CUST_EX_SCODE);
//                    //判断条码是否为空 是否为12位 是否纯数字组成
//                    if (!StringUtil.isNullOrEmpty(barCode) && barCode.length() == 12 && isNum(barCode) == true) {
//                        tvBarCode.setText(barCode);
//                    } else {
//                        Toast.makeText(BarcodeSupplementActivity.this, "请重新扫描", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("ScannerService", e.toString());
//                }
//            }
//        }
//    };

    //班组
    class ShiftTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            String result = HttpUtil.sendGet(PathUtil.GET_SHIFT, null);
            return result;
        }

        //事后执行
        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType());
                List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                List<com.hsproduce.bean.Team> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<Team>>() {
                }.getType());
                if (res.get("code").equals("200")) {
                    //班组数据清空
                    teamList.clear();
                    teamList.addAll(datas);
                    //班组名称数据清空
                    shiftList.clear();
                    for (int i = 0; i < map.size(); i++) {
                        shiftList.add(map.get(i).get("name"));
                    }
                    spTeam.setAdapter(shiftAdapter);
                }
            }
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
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到机台", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        String search = autoTvMchid.getText().toString().trim();
                        for (int i = 0; i < map.size(); i++) {
                            if (search.contains(map.get(i).get("itemid"))) {
                            }
                            mchidList.add(map.get(i).get("itemid"));
                        }
                        //创建 AutoCompleteTextView 适配器 (输入提示)
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                BarcodeSupplementActivity.this, android.R.layout.simple_dropdown_item_1line, mchidList);
                        //初始化autoCompleteTextView
                        autoTvMchid.setAdapter(adapter);
                        //设置输入多少字符后提示，默认值为2，在此设为１
                        autoTvMchid.setThreshold(1);
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BarcodeSupplementActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarcodeSupplementActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //根据规格编码模糊查询规格
    class GetSpecTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.GetSPECIFICATION, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //填入规格信息
                        for (int i = 0; i < datas.size(); i++) {
                            itdscList.add(datas.get(i).getItdsc());
                            itdscMap.put(datas.get(i).getItdsc(), datas.get(i));
                        }
                        itnbrAdapter = new DialogItemAdapter(BarcodeSupplementActivity.this, itdscList);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(BarcodeSupplementActivity.this)
                                .setSingleChoiceItems(itnbrAdapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        spescName = itdscList.get(which);
                                        tvSpescName.setText(spescName);
                                        tvSpesc.setText(itdscMap.get(spescName).getItnbr());
                                        dialog.dismiss();
//                                        Toast.makeText(BarcodeSupplementActivity.this,"选择了"+Spesc,Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
//                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功，没有匹配的规格！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BarcodeSupplementActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarcodeSupplementActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    //条码补录
    class SupCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SupplementTYRE_CODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
//                        barcode.setText("");
                        tvSpesc.setText("");
                        tvSpescName.setText("");
                        tvDate.setText("");
                        autoTvMchid.setText("");
                        tvBarCode.setText("");
                        tvBarCode.requestFocus();
                        Toast.makeText(BarcodeSupplementActivity.this, "补录成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(BarcodeSupplementActivity.this, "新条码被使用过无法更换！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(BarcodeSupplementActivity.this, "补录失败！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BarcodeSupplementActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BarcodeSupplementActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onPause() {
//        unregisterReceiver(scanDataReceiver);
//        super.onPause();
//    }

    //是否纯数字
    public Boolean isNum(String s) {
        char[] ch = s.toCharArray();
        for (char c : ch) {
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //按键按下
        switch (keyCode) {
            case 0://扫描键按下清空
                tvBarCode.requestFocus();
                tvBarCode.setText("");
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //扫描键 弹开时获取计划
        //右方向键
        String msg = "";
        switch (keyCode) {
            case 0:
                if (App.pdaType.equals("PDA")) {
                    if (!StringUtil.isNullOrEmpty(SystemBroadCast.barCode) && (SystemBroadCast.barCode).length() == 12 && isNum(SystemBroadCast.barCode) == true) {
                        tvBarCode.setText(SystemBroadCast.barCode);
                    }
                    SystemBroadCast.barCode = "";
                    Toast toast = Toast.makeText(BarcodeSupplementActivity.this, "请重新扫描", Toast.LENGTH_LONG);
                    showMyToast(toast, 500);
                }
                break;
            case 288://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            case 289://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            case 290://扫描键
                scan(BaseActivity.tvBarCode);
                break;
            //返回键
            case 4:
                //返回上级页面
                clear();
                startActivity(new Intent(BarcodeSupplementActivity.this, FunctionActivity.class));
                this.finish();
                break;
        }
        return true;
    }

    public void scan(String barcode) {
        //霍尼韦尔
        if (App.pdaType.equals("EDA50KP-3")) {
            if (!StringUtil.isNullOrEmpty(barcode) && barcode.length() == 12) {
                tvBarCode.setText(barcode);
            }
            BaseActivity.tvBarCode = "";
        }
    }

    private void clear() {
        barCode = "";
        spesc = "";
        spescName = "";
        lorR = "";
        pDate = "";
        shift = "";
        mchId = "";
        team = "";
        creatUser = "";
        planID = "";
    }

}
