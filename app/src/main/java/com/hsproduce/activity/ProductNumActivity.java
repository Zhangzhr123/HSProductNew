package com.hsproduce.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.bean.VPlan;
import com.hsproduce.bean.VreCord;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * 当班产量页面
 * 获取当班产量
 * createBy zhangzhr @ 2019-12-30
 */
public class ProductNumActivity extends BaseActivity {

    //定义控件
    private TextView tvDnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_productnum);
        //加载控件
        initView();
    }

    public void initView() {
        tvDnum = (TextView) findViewById(R.id.dnum);
        new GetDnumSumTask().execute("UserName=" + App.username);
    }

    //
    class GetDnumSumTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendGet(PathUtil.GetDnumSum, strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (StringUtil.isNullOrBlank(s)) {
                Toast.makeText(ProductNumActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    Map<Object, Object> res = App.gson.fromJson(s, new TypeToken<Map<Object, Object>>() {
                    }.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.get("data")), new TypeToken<List<VPlan>>() {
                    }.getType());
                    if (res == null || res.isEmpty()) {
                        Toast.makeText(ProductNumActivity.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (res.get("code").equals("200")) {
                        tvDnum.setText(datas.get(0).getDnum());
                        Toast.makeText(ProductNumActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(ProductNumActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProductNumActivity.this, "数据处理异常", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    //键盘监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按键按下
        return true;
    }

    //键盘监听
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //按键弹开
        switch (keyCode) {
            case 4:
                tofunction();
                break;
        }
        return true;
    }
}
