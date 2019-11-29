package com.hsproduce.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.adapter.VPlanItemAdapter;
import com.hsproduce.bean.Result2;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;

import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class VulcanizationFragment extends Fragment {


    private View root;
    private ListView listView;
    private TextView tvMchid;
    private ButtonView btGetplan;
    //计划展示适配器
    private VPlanItemAdapter adapter;


    public VulcanizationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_vulcanization, container, false);

        initView();

        initEvent();

        return root;
    }
    //初始化控件
    public void initView(){
        listView = (ListView) root.findViewById(R.id.lv_plan);
        tvMchid = (TextView) root.findViewById(R.id.mchid);
        btGetplan = (ButtonView) root.findViewById(R.id.bt_getPlan);
    }
    //初始化事件
    public void initEvent(){
        btGetplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mchid = tvMchid.getText().toString().trim();
                if(StringUtil.isNullOrEmpty(mchid)){
                    Toast.makeText(getContext(), "请扫描机台号", Toast.LENGTH_LONG).show();
                }else{
                    String lr = mchid.substring(mchid.length() - 1);
                    if(!"LR".contains(lr.toUpperCase())){
                        Toast.makeText(getContext(), "机台号格式有误，请重新扫描", Toast.LENGTH_LONG).show();
                        tvMchid.setText("");
                    }else{
                        App.lr = lr.toUpperCase();
                        mchid = mchid.substring(0, mchid.length() - 1);
                        String param = "pageIndex=1&pageSize=100&where=%7B%22mchid%22%3A%22"+mchid+"%22%2C%22lr%22%3A%22"+lr+"%22%2C%22state%22%3A%22-1%22%2C%22shift%22%3A%22"+App.shift+"%22%7D";
                        new MyTask().execute(param);
                    }
                }
            }
        });
    }

    //查询任务
    class MyTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendPost(PathUtil.VUL_GET_PLAN, strings[0], HttpUtil.formContent);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                try{
                    Result2<VPlan> res = App.gson.fromJson(s, new TypeToken<Result2<VPlan>>(){}.getType());
                    List<VPlan> datas = App.gson.fromJson(App.gson.toJson(res.getData().getData()), new TypeToken<List<VPlan>>(){}.getType());
                    adapter = new VPlanItemAdapter(getActivity(), datas);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(datas == null || datas.isEmpty()){
                        Toast.makeText(getActivity(), "未获取到计划", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "数据处理异常", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    /**
     * 隐藏输入软键盘
     * @param context
     * @param view
     */
    public static void hideInputManager(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view !=null && imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  //强制隐藏
        }
    }

}
