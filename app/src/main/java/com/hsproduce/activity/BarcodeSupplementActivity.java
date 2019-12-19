package com.hsproduce.activity;

import android.app.DatePickerDialog;
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
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.BarCodeAdapter;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.adapter.PlanDialogItemAdapter;
import com.hsproduce.bean.Team;
import com.hsproduce.bean.VPlan;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//条码补录页面
public class BarcodeSupplementActivity extends BaseActivity {

    //定义控件
    private TextView barcode,spesc,date,master, spescname;
    private AutoCompleteTextView mchid;
    private Spinner shift,LorR,team;
    private ButtonView ok,getitnbr;
    //下拉列表
    private List<String> shiftlist = new ArrayList<>();
    private ArrayAdapter<String> shiftadapter;
    //存放班组数据
    private List<Team> teamList = new ArrayList<>();
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量
    private String BarCode="",Spesc="",SpescName="",lorR="",Pddate="",Shift="",MchId="",Team="",creatuser="",PlanID="";
    //Dialog显示列表
    private List<String> itnbrlist = new ArrayList<>();
    private DialogItemAdapter itnbradapter;
    private List<String> mchidlist = new ArrayList<>();
    private DialogItemAdapter mchidadapter;
    private List<VPlan> planlist = new ArrayList<>();
    private PlanDialogItemAdapter planadapter;
    private List<VreCord> itndsc = new ArrayList<>();

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

    public void initView(){
        //补录条码
        barcode = (TextView)findViewById(R.id.barcode);
        //获得焦点
        barcode.requestFocus();
        //规格编码
        spesc = (TextView)findViewById(R.id.spesc);
        //规格名称
        spescname = (TextView) findViewById(R.id.spescname);
        getitnbr = (ButtonView) findViewById(R.id.getitnbr);
        //生产时间
        date = (TextView)findViewById(R.id.date);
        date.setFocusable(false);//让EditText失去焦点，然后获取点击事件
        //机台号
        mchid = (AutoCompleteTextView)findViewById(R.id.mchid);
        //左右膜
        LorR = (Spinner)findViewById(R.id.LorR);
        //班组
        team = (Spinner)findViewById(R.id.team);
        //班次
        shift = (Spinner)findViewById(R.id.shift);
        //主手
//        master = (TextView)findViewById(R.id.master);
//        master.setText(App.username);
        //条码补录
        ok = (ButtonView)findViewById(R.id.ok);

        //班组 下拉列表
        new ShiftTask().execute();
        shiftadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, shiftlist);
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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        getMchid();
    }

    public void initEvent(){
        date.setOnClickListener(new View.OnClickListener() {
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
                                show.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //条码补录
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supCode();
            }
        });
        //查询规格编码
        getitnbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItnbr();
            }
        });
    }

    //条码补录
    public void supCode(){
        //规格编码
        Spesc = spesc.getText().toString().trim();
        //规格名称
        SpescName = spescname.getText().toString().trim();
        SpescName = SpescName.replace(" ","%20");

        //补录条码
        BarCode = barcode.getText().toString().trim();
        //生产日期
        Pddate = date.getText().toString().trim();
        //左右膜
        lorR = LorR.getSelectedItem().toString().trim();
        //班次
        Shift = shift.getSelectedItem().toString().trim();
        //主手
//        creatuser = master.getText().toString().trim();
        if(StringUtil.isNullOrBlank(Spesc)){
            Toast.makeText(BarcodeSupplementActivity.this, "请填写规格编码", Toast.LENGTH_LONG).show();
        }else if(StringUtil.isNullOrBlank(SpescName)){
            Toast.makeText(BarcodeSupplementActivity.this, "请填写规格名称", Toast.LENGTH_LONG).show();
        }else if(StringUtil.isNullOrBlank(BarCode)){
            Toast.makeText(BarcodeSupplementActivity.this, "请扫描补录条码", Toast.LENGTH_LONG).show();
        }else{
            String parm = "MCHID="+MchId+"&ITNBR="+Spesc+"&ITDSC="+SpescName+"&LoR="+lorR+"&SHIFT="+Team
                    +"&TIME_A="+Pddate+"&USER_NAME="+App.username+"&SwitchTYRE_CODE="+BarCode;
            System.out.println(parm);
            new SupCodeTask().execute(parm);
        }
    }

    //筛选规格
    public void getItnbr(){
        //清空数据
        itnbrlist.clear();
        itndsc.clear();
        spesc.setText("");
        String search = spescname.getText().toString().trim();
        search = search.toUpperCase();//大写转换
        String parm = "ITDSC="+search;
        new GetSpecTask().execute(parm);
    }

    //筛选机台
    public void getMchid(){
        //清空数据
        mchidlist.clear();
        String parm = "TYPE_ID=10098";
        new MCHIDTask().execute(parm);
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                List<com.hsproduce.bean.Team> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<Team>>(){}.getType());
                if (res.get("code").equals("200")) {
                    //班组数据清空
                    teamList.clear();
                    teamList.addAll(datas);
                    //班组名称数据清空
                    shiftlist.clear();
                    for (int i = 0; i < map.size(); i++) {
                        shiftlist.add(map.get(i).get("name"));
                    }
                    team.setAdapter(shiftadapter);
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                    List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                    if(res == null || res.isEmpty()){
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到机台", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        String search = mchid.getText().toString().trim();
                        for(int i=0;i<map.size();i++){
                            if(search.contains(map.get(i).get("itemid"))){}
                            mchidlist.add(map.get(i).get("itemid"));
                        }
                        //创建 AutoCompleteTextView 适配器 (输入提示)
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                BarcodeSupplementActivity.this, android.R.layout.simple_dropdown_item_1line,mchidlist);
                        //初始化autoCompleteTextView
                        mchid.setAdapter(adapter);
                        //设置输入多少字符后提示，默认值为2，在此设为１
                        mchid.setThreshold(1);
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(BarcodeSupplementActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
                        //填入规格信息
                        for(int i=0;i<datas.size();i++){
                            itnbrlist.add(datas.get(i).getItnbr());
                            itndsc.add(datas.get(i));
                        }
                        itnbradapter = new DialogItemAdapter(BarcodeSupplementActivity.this,itnbrlist);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(BarcodeSupplementActivity.this)
                                .setSingleChoiceItems(itnbradapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Spesc = itnbrlist.get(which);
                                        spesc.setText("");
                                        spesc.setText(Spesc);
                                        //规格名称
                                        for(int j=0;j<itndsc.size();j++){
                                            if(itndsc.get(j).getItnbr().equals(Spesc)){
                                                spescname.setText(itndsc.get(j).getItdsc());
                                                SpescName = itndsc.get(j).getItdsc().replaceAll(" ","%20");
                                            }
                                        }
                                        dialog.dismiss();
                                        Toast.makeText(BarcodeSupplementActivity.this,"选择了"+Spesc,Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
//                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(BarcodeSupplementActivity.this, "查询成功，没有匹配的规格！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(BarcodeSupplementActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(BarcodeSupplementActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res == null || res.isEmpty()){
                        Toast.makeText(BarcodeSupplementActivity.this, "未获取到数据", Toast.LENGTH_LONG).show();
                    }
                    if(res.get("code").equals("200")){
//                        barcode.setText("");
                        spesc.setText("");
                        spescname.setText("");
                        date.setText("");
                        mchid.setText("");
                        barcode.setText("");
                        barcode.requestFocus();
                        Toast.makeText(BarcodeSupplementActivity.this, "补录成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(BarcodeSupplementActivity.this, "新条码被使用过无法更换！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(BarcodeSupplementActivity.this, "补录失败！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(BarcodeSupplementActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(BarcodeSupplementActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
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
                startActivity(new Intent(BarcodeSupplementActivity.this, FunctionActivity.class));
                this.finish();
                break;
        }
        return true;
    }


}
