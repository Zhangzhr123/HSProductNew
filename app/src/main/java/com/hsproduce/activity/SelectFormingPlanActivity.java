package com.hsproduce.activity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.FormingItemAdapter;
import com.hsproduce.bean.VPlan;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 查看成型计划
 * 输入机台号和时间查询成型计划
 * createBy zahngzr @ 2019-12-25
 */
public class SelectFormingPlanActivity extends BaseActivity {

    //机台号和日期
    private TextView tvDate;
    private AutoCompleteTextView tvMchid;
    private List<String> data1 = new ArrayList<>();
    //获取计划按钮
    private ImageButton btGetplan;
    //成型计划
    private ListView lvPlan;
    private FormingItemAdapter adapter;
    private String mchid = "", date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_select_formingplan);
        //加载控件
        initView();
        //设置控件事件
        initEvent();
    }

    public void initView() {
        //机台号
        tvMchid = (AutoCompleteTextView) findViewById(R.id.mchid);
        new MCHIDTask().execute("TYPE_ID=10107");
        eventsViews();
        //生产时间
        tvDate = (TextView) findViewById(R.id.date);
        tvDate.setFocusable(false);//让EditText失去焦点，然后获取点击事件
        //list列表
        lvPlan = (ListView) findViewById(R.id.lv_plan);
        //点击查询阿牛
        btGetplan = (ImageButton) findViewById(R.id.getPlan);
    }

    private void eventsViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1);
        tvMchid.setAdapter(adapter);
    }

    public void initEvent() {
        //点击查询成型计划
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentVPlan();
            }
        });
        //时间
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(SelectFormingPlanActivity.this,
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
    }

    //生产追溯
    public void getCurrentVPlan() {
        mchid = tvMchid.getText().toString().trim();
        date = tvDate.getText().toString().trim();

        if(StringUtil.isNullOrEmpty(mchid) || StringUtil.isNullOrEmpty(date)){
            Toast.makeText(SelectFormingPlanActivity.this, "查询条件不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdFormat.format(sdFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String parm = "MCHID=" + mchid + "&CurrDate=" + date;
        new GetCurrDateFormingVPlanTask().execute(parm);
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
                Toast.makeText(SelectFormingPlanActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<String, Object> res = App.gson.fromJson(s, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    List<Map<String, String>> map = (List<Map<String, String>>) res.get("data");
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SelectFormingPlanActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        for (int i = 0; i < map.size(); i++) {
                            if (map.get(i).get("itemid") == null) {
                                continue;
                            }
                            data1.add(map.get(i).get("itemid"));
                        }
                    } else {
                        Toast.makeText(SelectFormingPlanActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SelectFormingPlanActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //获取成型计划
    class GetCurrDateFormingVPlanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.GetCurrDateFormingVPlan, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(SelectFormingPlanActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(SelectFormingPlanActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        adapter = new FormingItemAdapter(SelectFormingPlanActivity.this, datas);
                        lvPlan.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SelectFormingPlanActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
    }


    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("key", keyCode + "  ");
        //按键按下
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode) {
            case 22://右方向键
                getCurrentVPlan();
                break;
            case 4://返回键
                tofunction();//返回功能菜单页面
                break;
            default:
                break;
        }
        return true;
    }

}
