package com.hsproduce.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.DialogItemAdapter;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//明细变更页面
public class DetailChangeActivity extends BaseActivity {

    //定义控件
    private TextView barcode,spesc,mchid,createuser;
    private ButtonView btgetcode,ok,getitnbr,getmchid;
    private Spinner LorR,shift,team;
    //下拉列表
    private List<String> teamlist = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime = 0;
    //定义变量
    private String BarCode="",Spesc="",CreateUser="",lorR="",Shift="",MchId="",spescname="",codeid="",Team="",itnbr="",itndsc="d";
    //Dialog显示列表
    private List<String> itnbrlist = new ArrayList<>();
    private DialogItemAdapter itnbradapter;
    private List<String> mchidlist = new ArrayList<>();
    private DialogItemAdapter mchidadapter;
    private List<VreCord> Itndsc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailchange);
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
        //主手
        createuser = (TextView)findViewById(R.id.master);
        //机台号
        mchid = (TextView)findViewById(R.id.mchid);
        //左右膜
        LorR = (Spinner)findViewById(R.id.lorR);
        //班组
        team = (Spinner)findViewById(R.id.team);
        new ShiftTask().execute();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, teamlist);
        team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Team = parent.getItemAtPosition(position).toString();
                if(Team.equals("甲班")){
                    Team = "1";
                }else if(Team.equals("乙班")){
                    Team = "2";
                }else if(Team.equals("丙班")) {
                    Team = "3";
                }else{
                    Team = "15";
                }
                App.shift = Team;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //班次
        shift = (Spinner)findViewById(R.id.shift);
        //查询条码明细
        btgetcode = (ButtonView)findViewById(R.id.bt_getCode);
        //条码补录
        ok = (ButtonView)findViewById(R.id.ok);
        //筛选按钮
        getitnbr = (ButtonView)findViewById(R.id.getitnbr);
        getmchid = (ButtonView)findViewById(R.id.getmchid);
    }

    public void initEvent(){
        btgetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodeDetail();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //规格编码
                Spesc = spesc.getText().toString().trim();
                //规格名称
                CreateUser = createuser.getText().toString().trim();
                //补录条码
                BarCode = barcode.getText().toString().trim();
                //班次
                Shift = shift.getSelectedItem().toString().trim();
                //左右膜
                lorR = LorR.getSelectedItem().toString().trim();
                //MCHID=07A01 &ITNBR=CBCBS64518C14DH0 &ITDSC=185R14C-8PR(TR645)S%20BLACKSTONE
                // &LoR=L &SHIFT=1 &USER_NAME=caozuo &DateTime_W=2019-10-16 &SwitchID=17
                String parm = "MCHID="+MchId+"&ITNBR="+Spesc+"&ITDSC="+spescname+"&LoR="+lorR
                        +"&SHIFT="+Team+"&USER_NAME="+CreateUser+"&DateTime_W="+"&SwitchID="+codeid;
                new ChangeDetailedTask().execute(parm);
                barcode.setText("");
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
                String parm = "ITNBR="+search;
                new GetSpecTask().execute(parm);
            }
        });
        //筛选机台
        getmchid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空数据
                mchidlist.clear();
                String parm = "TYPE_ID=10098";
                new MCHIDTask().execute(parm);
            }
        });
    }

    public void getCodeDetail(){
        //api/PDA/SelDetailed?SwitchTYRE_CODE=111600000447
        BarCode = barcode.getText().toString().trim();
        String parm = "SwitchTYRE_CODE="+BarCode;
        new SelDetailedTask().execute(parm);
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
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(DetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                for(int i=0;i<map.size();i++){
                    teamlist.add(map.get(i).get("name"));
                }
                team.setAdapter(adapter);
            }
        }
    }

    //根据规条码模糊查询明细
    class SelDetailedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.SelDetailed, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(DetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>(){}.getType());
                    if(res.get("code").equals("200")){
                        //填入规格信息
                        spesc.setText(datas.get(0).getItnbr());
                        mchid.setText(datas.get(0).getMchid());
                        //设置默认值
                        if(datas.get(0).getLr().equals("L")){
                            LorR.setSelection(0,true);
                        }else{
                            LorR.setSelection(1,true);
                        }
                        if(datas.get(0).getTeam().equals("1")){
                            team.setSelection(0,true);
                        }else if(datas.get(0).getTeam().equals("2")){
                            team.setSelection(1,true);
                        }else if(datas.get(0).getTeam().equals("3")){
                            team.setSelection(2,true);
                        }else{
                            team.setSelection(3,true);
                        }
                        if(datas.get(0).getShift().equals("1")){
                            shift.setSelection(0,true);
                        }else if(datas.get(0).getShift().equals("2")){
                            shift.setSelection(1,true);
                        }else{
                            shift.setSelection(2,true);
                        }
                        createuser.setText(datas.get(0).getCreateuser());
                        //获取信息
                        spescname = datas.get(0).getItdsc().replaceAll(" ","%20");
                        codeid = datas.get(0).getId();
                        Toast.makeText(DetailChangeActivity.this, "轮胎查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(DetailChangeActivity.this, "查询成功，没有匹配的轮胎信息！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(DetailChangeActivity.this, "轮胎查询错误", Toast.LENGTH_LONG).show();
                    }

                    if(datas == null || datas.isEmpty()){
                        Toast.makeText(DetailChangeActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
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
                Toast.makeText(DetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>(){}.getType());
                    List<Map<String,String>> map = (List<Map<String,String>>)res.get("data");
                    if(res.get("code").equals("200")){
                        String search = mchid.getText().toString().trim();
                        for(int i=0;i<map.size();i++){
                            if(search.contains(map.get(i).get("itemid"))){}
                            mchidlist.add(map.get(i).get("itemid"));
                        }
                        mchidadapter = new DialogItemAdapter(DetailChangeActivity.this,mchidlist);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(DetailChangeActivity.this)
                                .setSingleChoiceItems(mchidadapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MchId = mchidlist.get(which);
                                        mchid.setText("");
                                        mchid.setText(MchId);
                                        dialog.dismiss();
                                        Toast.makeText(DetailChangeActivity.this,"选择了"+MchId,Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
                        Toast.makeText(DetailChangeActivity.this, "机台查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(DetailChangeActivity.this, "查询成功，没有匹配的机台！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(DetailChangeActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }
                    if(res == null || res.isEmpty()){
                        Toast.makeText(DetailChangeActivity.this, "未获取到机台", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
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
                Toast.makeText(DetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    List<VreCord> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VreCord>>(){}.getType());
                    if(res.get("code").equals("200")){
                        //填入规格信息
                        for(int i=0;i<datas.size();i++){
                            itnbrlist.add(datas.get(i).getItnbr());
                            Itndsc.add(datas.get(i));
                        }
                        itnbradapter = new DialogItemAdapter(DetailChangeActivity.this,itnbrlist);
                        //弹窗显示选中消失
                        AlertDialog alertDialog = new AlertDialog
                                .Builder(DetailChangeActivity.this)
                                .setSingleChoiceItems(itnbradapter, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        itnbr = itnbrlist.get(which);
                                        spesc.setText("");
                                        spesc.setText(itnbr);
                                        //规格名称
                                        for(int j=0;j<Itndsc.size();j++){
                                            if(Itndsc.get(j).getItnbr().equals(Spesc)){
                                                spescname = Itndsc.get(j).getItdsc().replaceAll(" ","%20");
                                            }
                                        }
                                        dialog.dismiss();
                                        Toast.makeText(DetailChangeActivity.this,"选择了"+itnbr,Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        alertDialog.show();
                        //规格名称
//                        for(int j=0;j<datas.size();j++){
//                            if(datas.get(j).getItnbr().equals(itnbr)){
//                                spescname = datas.get(j).getItdsc().replaceAll(" ","%20");
//                            }
//                        }
                        Toast.makeText(DetailChangeActivity.this, "查询成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("500")){
                        Toast.makeText(DetailChangeActivity.this, "查询成功，没有匹配的规格！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(DetailChangeActivity.this, "查询错误", Toast.LENGTH_LONG).show();
                    }
                    if(datas == null || datas.isEmpty()){
                        Toast.makeText(DetailChangeActivity.this, "未获取到规格", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //明细修改
    class ChangeDetailedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.ChangeDetailed, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(DetailChangeActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>(){}.getType());
                    if(res.get("code").equals("200")){
                        Toast.makeText(DetailChangeActivity.this, "变更成功！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("100")){
                        Toast.makeText(DetailChangeActivity.this, "未找到轮胎信息，变更失败！", Toast.LENGTH_LONG).show();
                    }else if(res.get("code").equals("300")){
                        Toast.makeText(DetailChangeActivity.this, "变更失败！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(DetailChangeActivity.this, "错误："+res.get("ex"), Toast.LENGTH_LONG).show();
                    }
                    if(res == null || res.isEmpty()){
                        Toast.makeText(DetailChangeActivity.this, "未获取到信息", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(DetailChangeActivity.this, "数据处理异常", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        Log.e("key", keyCode + "  ");
        //扫描键 按下时清除
        if(keyCode == 0){
            //barcode.setText("");
        }
        if(keyCode == 4){
            if(System.currentTimeMillis() - mExitTime > 2000){
                Toast.makeText(this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            }else{
                System.exit(0);//注销功能
            }
        }
        //返回键时间间隔超过两秒 返回功能页面
        if(keyCode == 21){
            tofunction(); //BaseActivity  返回功能页面函数
            Toast.makeText(this, "返回菜单栏", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        //扫描键 弹开时获取计划
        if(keyCode == 0){
            getCodeDetail();
        }
        super.onKeyDown(keyCode, event);
        return true;
    }



}
