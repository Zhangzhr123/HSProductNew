package com.hsproduce.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.bean.Team;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//明细变更页面
public class FormingDetailChangeActivity extends BaseActivity {

    //定义控件
    private TextView barcode, spesc, mchid, createuser, date;
    private ButtonView ok, getitnbr, getmchid;
    private ImageButton btgetcode;
    private Spinner LorR, shift, team;
    //下拉列表
    private List<String> teamlist = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    //存放班组数据
    private List<Team> teamList = new ArrayList<>();
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量
    private String BarCode = "", Spesc = "", CreateUser = "", lorR = "", Shift = "", MchId = "", spescname = "", codeid = "", Team = "", itnbr = "", itndsc = "", Date = "";
    //Dialog显示列表
    private List<String> itnbrlist = new ArrayList<>();
    private DialogItemAdapter itnbradapter;
    private List<String> mchidlist = new ArrayList<>();
    private DialogItemAdapter mchidadapter;
    private List<VreCord> Itndsc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_forming_detailchange);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //补录条码
        barcode = (TextView) findViewById(R.id.barcode);
        //获得焦点
        barcode.requestFocus();
        //规格编码
        spesc = (TextView) findViewById(R.id.spesc);
        //主手
        createuser = (TextView) findViewById(R.id.master);
        //机台号
        mchid = (TextView) findViewById(R.id.mchid);
        //生产日期
        date = (TextView) findViewById(R.id.cdate);
        //左右膜
        LorR = (Spinner) findViewById(R.id.lorR);
        //班组
        team = (Spinner) findViewById(R.id.team);
        new ShiftTask().execute();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamlist);
        team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Team = parent.getItemAtPosition(position).toString();
                for(int i = 0;i<teamList.size();i++){
                    if (Team.equals(teamList.get(i).getName())) {
                        Team = teamList.get(i).getId();
                        break;
                    } else if (Team.equals(teamList.get(i).getName())) {
                        Team = teamList.get(i).getId();
                        break;
                    } else if (Team.equals(teamList.get(i).getName())) {
                        Team = teamList.get(i).getId();
                        break;
                    } else if (Team.equals(teamList.get(i).getName())) {
                        Team = teamList.get(i).getId();
                        break;
                    } else {
                        break;
                    }
                }
//                if (Team.equals("甲班")) {
//                    Team = "1";
//                } else if (Team.equals("乙班")) {
//                    Team = "2";
//                } else if (Team.equals("丙班")) {
//                    Team = "3";
//                } else {
//                    Team = "15";
//                }
//                App.shift = Team;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //班次
        shift = (Spinner) findViewById(R.id.shift);
        //查询条码明细
        btgetcode = (ImageButton) findViewById(R.id.searchdetail);
        //条码补录
        ok = (ButtonView) findViewById(R.id.ok);
        //筛选按钮
        getitnbr = (ButtonView) findViewById(R.id.getitnbr);
        getmchid = (ButtonView) findViewById(R.id.getmchid);
    }

    public void initEvent() {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(FormingDetailChangeActivity.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                TextView show = (TextView) findViewById(R.id.cdate);
                                show.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btgetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodeDetail();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //机台号
                MchId = mchid.getText().toString().trim();
                //规格编码
                Spesc = spesc.getText().toString().trim();
                //主手
                CreateUser = createuser.getText().toString().trim();
                //补录条码
                BarCode = barcode.getText().toString().trim();
                //时间
                Date = date.getText().toString().trim();
                //班组
//                Team = team.getSelectedItem().toString().trim();
                //ST205/75R14-6PR(TR643)L 胎胚
                //MCHID=01 &ITNBR=BBZ20514H02 &ITDSC=ST205%2F75R14-6PR(TR643)L%20%E8%83%8E%E8%83%9A
                // &SHIFT=2 &USER_NAME=shao &DateTime_W=2019-12-11 &SwitchID=208
                String parm = "MCHID=" + MchId + "&ITNBR=" + Spesc + "&ITDSC=" + spescname
                        + "&SHIFT=" + Team + "&USER_NAME=" + CreateUser + "&DateTime_W=" + Date + "&SwitchID=" + codeid;
                new ChangeDetailedTask().execute(parm);
//                barcode.setText("");
            }
        });
        //筛选规格
        getitnbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空数据
                itnbrlist.clear();
                Itndsc.clear();
                String search = spesc.getText().toString().trim();
                search = search.toUpperCase();//大写转换
                String parm = "ITNBR=" + search;
                new GetSpecTask().execute(parm);
            }
        });
        //筛选成型机台
        getmchid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空数据
                mchidlist.clear();
                String parm = "TYPE_ID=10107";
                new MCHIDTask().execute(parm);
            }
        });
    }

    //获取条码明细
    public void getCodeDetail() {
        //api/PDA/SelDetailed?SwitchTYRE_CODE=111600000447
        BarCode = barcode.getText().toString().trim();
        if (BarCode.length() == 12) {
            String parm = "SwitchTYRE_CODE=" + BarCode;
            new SelDetailedTask().execute(parm);
        }else{
            Toast.makeText(FormingDetailChangeActivity.this, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
            return;
        }

        //barcode.setText("");
    }

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
                Toast.makeText(FormingDetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                }.getType());
                List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                List<Team> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<Team>>(){}.getType());
                if (res.get("code").equals("200")) {
                    //班组数据清空
                    teamList.clear();
                    teamList.addAll(datas);
                    //班组名称数据清空
                    teamlist.clear();
                    for (int i = 0; i < map.size(); i++) {
                        teamlist.add(map.get(i).get("name"));
                    }
                    team.setAdapter(adapter);
                }
//                for (int i = 0; i < map.size(); i++) {
//                    teamlist.add(map.get(i).get("name"));
//                }
//                team.setAdapter(adapter);
            }
        }
    }

    //根据条码查询明细
    class SelDetailedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGSECLECTCODE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingDetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingDetailChangeActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //填入规格信息
                        spesc.setText(datas.get(0).getItnbr());
                        mchid.setText(datas.get(0).getMchid());
                        //转换时间格式
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        date.setText(formatter.format(formatter.parse(datas.get(0).getWdate())));
                        //设置默认值
                        if (datas.get(0).getLr().equals("L")) {
                            LorR.setSelection(0, true);
                        } else {
                            LorR.setSelection(1, true);
                        }
                        if (datas.get(0).getTeam().equals("1")) {
                            team.setSelection(0, true);
                        } else if (datas.get(0).getTeam().equals("2")) {
                            team.setSelection(1, true);
                        } else if (datas.get(0).getTeam().equals("3")) {
                            team.setSelection(2, true);
                        } else {
                            team.setSelection(3, true);
                        }
                        if (datas.get(0).getShift().equals("1")) {
                            shift.setSelection(0, true);
                        } else if (datas.get(0).getShift().equals("2")) {
                            shift.setSelection(1, true);
                        } else {
                            shift.setSelection(2, true);
                        }
                        createuser.setText(datas.get(0).getCreateuser());
                        //获取信息
                        MchId = datas.get(0).getMchid();
                        Spesc = datas.get(0).getItnbr();
//                        System.out.println("规格名称："+datas.get(0).getItdsc());
//                        System.out.println("规格名称中文转url格式："+toUtf8String(datas.get(0).getItdsc()));
//                        System.out.println("空格和斜杠转换："+toUtf8String(datas.get(0).getItdsc()).replace("/","%2F").replaceAll(" ","%20"));
                        spescname = toUtf8String(datas.get(0).getItdsc()).replace("/", "%2F").replaceAll(" ", "%20");
                        Team = datas.get(0).getTeam();
                        Date = formatter.format(formatter.parse(datas.get(0).getWdate()));
//                        System.out.println("转换后的时间："+Date);
                        CreateUser = datas.get(0).getCreateuser();
                        codeid = datas.get(0).getId();
                        Toast.makeText(FormingDetailChangeActivity.this, "轮胎查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingDetailChangeActivity.this, "查询成功，没有匹配的轮胎信息！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingDetailChangeActivity.this, "轮胎查询错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingDetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
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
                Toast.makeText(FormingDetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingDetailChangeActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        String search = mchid.getText().toString().trim();
                        for (int i = 0; i < map.size(); i++) {
                            if (search.contains(map.get(i).get("itemid"))) {
                            }
                            mchidlist.add(map.get(i).get("itemid"));
                        }
                        mchidadapter = new DialogItemAdapter(FormingDetailChangeActivity.this, mchidlist);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(FormingDetailChangeActivity.this)
                                .setSingleChoiceItems(mchidadapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MchId = mchidlist.get(which);
                                        mchid.setText("");
                                        mchid.setText(MchId);
                                        dialog.dismiss();
                                        Toast.makeText(FormingDetailChangeActivity.this, "选择了" + MchId, Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
//                        Toast.makeText(DetailChangeActivity.this, "机台查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingDetailChangeActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingDetailChangeActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingDetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //根据规格编码模糊查询规格
    class GetSpecTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGSECLECTITNBR, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingDetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingDetailChangeActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
                        //填入规格信息
                        for (int i = 0; i < datas.size(); i++) {
                            itnbrlist.add(datas.get(i).getItnbr());
                            Itndsc.add(datas.get(i));
                        }
                        itnbradapter = new DialogItemAdapter(FormingDetailChangeActivity.this, itnbrlist);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(FormingDetailChangeActivity.this)
                                .setSingleChoiceItems(itnbradapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        itnbr = itnbrlist.get(which);
                                        spesc.setText("");
                                        spesc.setText(itnbr);
                                        //规格名称
                                        for (int j = 0; j < Itndsc.size(); j++) {
                                            if (Itndsc.get(j).getItnbr().equals(Spesc)) {
                                                spescname = toUtf8String(Itndsc.get(j).getItdsc()).replace("/", "%2F").replaceAll(" ", "%20");
                                            }
                                        }
                                        dialog.dismiss();
                                        Toast.makeText(FormingDetailChangeActivity.this, "选择了" + itnbr, Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
//                        Toast.makeText(DetailChangeActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("500")) {
                        Toast.makeText(FormingDetailChangeActivity.this, "查询成功，没有匹配的规格！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingDetailChangeActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingDetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //明细修改
    class ChangeDetailedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.FORMINGCHANGE, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(FormingDetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(FormingDetailChangeActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if (res.get("code").equals("200")) {
//                        barcode.setText("");
                        spesc.setText("");
                        mchid.setText("");
                        date.setText("");
                        LorR.setSelection(1, true);
                        team.setSelection(1, true);
                        shift.setSelection(1, true);
                        createuser.setText("");
                        Toast.makeText(FormingDetailChangeActivity.this, "变更成功！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("100")) {
                        Toast.makeText(FormingDetailChangeActivity.this, "未找到轮胎信息，变更失败！", Toast.LENGTH_LONG).show();
                    } else if (res.get("code").equals("300")) {
                        Toast.makeText(FormingDetailChangeActivity.this, "变更失败！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FormingDetailChangeActivity.this, "错误：" + res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FormingDetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //转换为%E4%BD%A0形式  中文转url编码
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //右方向键
        if (keyCode == 22) {
            getCodeDetail();
            //barcode.setText("");
        }
        if (keyCode == 0) {
            barcode.setText("");
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
//        if(keyCode == 0){
//            getCodeDetail();
//        }
        super.onKeyDown(keyCode, event);
        return true;
    }


}
