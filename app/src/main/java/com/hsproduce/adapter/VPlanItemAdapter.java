package com.hsproduce.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.App;
import com.hsproduce.R;
import com.hsproduce.activity.VulcanizationActivity;
import com.hsproduce.bean.Result;
import com.hsproduce.bean.Result2;
import com.hsproduce.bean.VPlan;
import com.hsproduce.util.DateUtil;
import com.hsproduce.util.HttpUtil;
import com.hsproduce.util.PathUtil;
import com.hsproduce.util.StringUtil;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VPlanItemAdapter extends BaseAdapter {

    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();

    public VPlanItemAdapter(Context context, List<VPlan> vPlanList){
        this.context = context;
        this.vPlanList = vPlanList;
    }

    @Override
    public int getCount() {
        return vPlanList.size();
    }

    @Override
    public VPlan getItem(int position) {
        return vPlanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_plan_item, null);
        final VPlan vPlan = vPlanList.get(position);
        //设置页面数据
        if(vPlan.getPdate() != null){
            ((TextView)convertView.findViewById(R.id.pdate)).setText(vPlan.getPdate());
        }
        if(vPlan.getItnbr() != null){
            ((TextView)convertView.findViewById(R.id.itnbr)).setText(vPlan.getItnbr());
        }
        if(vPlan.getItdsc() != null){
            ((TextView)convertView.findViewById(R.id.itdsc)).setText(vPlan.getItdsc());
        }
        if(vPlan.getPnum() != null){
            TextView tv = convertView.findViewById(R.id.pnum);
            String num = vPlan.getPnum().toString();
            tv.setText(num);
        }
        final TextView tvBarcodeStart = (TextView) convertView.findViewById(R.id.barcode_start);
        final TextView tvBarcodeEnd = (TextView) convertView.findViewById(R.id.barcode_end);

        //声明控件
        final View llscan = (View)convertView.findViewById(R.id.ll_scan);
        Spinner scan = (Spinner)convertView.findViewById(R.id.scan);
        final ButtonView scanbarcode = (ButtonView)convertView.findViewById(R.id.scan_barcode);
        final ButtonView endplan = (ButtonView)convertView.findViewById(R.id.end_plan);

        //下拉列表点击事件
        scan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View convertView, int position, long id) {
                String scanstr = parent.getItemAtPosition(position).toString();
                if(scanstr.equals("连续扫描")){
                    //隐藏下拉列表
                    llscan.setVisibility(View.GONE);
                    //显示条码扫描和结束计划
                    scanbarcode.setVisibility(View.VISIBLE);
                    endplan.setVisibility(View.VISIBLE);

                }else if(scanstr.equals("首尾扫描")){
                    //隐藏下拉列表
                    llscan.setVisibility(View.GONE);
                    //首尾扫描
                    seScan(convertView, vPlan, tvBarcodeStart, tvBarcodeEnd);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置扫描条码点击事件
        scanbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示dialog
                new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                        .title("提示")
                        .content("请扫描条码")
                        .inputType(
                                InputType.TYPE_CLASS_TEXT
                                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .input(
                                "请扫描条码",
                                "",
                                false,
//                                ((dialog, input) -> Toast("123"))
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    }
                                }
                        )
                        .inputRange(10, 12)
                        .positiveText(R.string.vul_confirm)
                        .negativeText(R.string.vul_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String barcode = dialog.getInputEditText().getText().toString();
                                if(StringUtil.isNullOrBlank(barcode)){
                                    Toast.makeText(context, "请扫描条码", Toast.LENGTH_LONG).show();
                                }else{
                                    vPlan.setState("2");
                                    //开始计划
//                                    new MyTask().execute(PathUtil.VUL_START_PLAN, App.gson.toJson(vPlan));
                                    vPlan.setBarcodestart(barcode);
                                    //绑定计划
//                                    new InRecordTask().execute(PathUtil.VUL_IN_RECORD, App.gson.toJson(vPlan));
                                }
//                                dialog.show();

                                //点击不取消对话框
//                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                                    @Override
//                                    public void onShow(DialogInterface dialog) {
//
//                                    }
//                                });
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });

        //设置结束计划点击事件
        endplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vPlan.setState("3");
                vPlan.setUpdateuser(App.username);
                endPlan(vPlan);
            }
        });

        return convertView;
    }

    //首尾扫描
    private void seScan(View convertView, final VPlan vPlan, TextView tvBarcodeStart, TextView tvBarcodeEnd) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_plan_item, null);
        //判断是否显示条码和按钮
        if(vPlan.getBarcodestart() != null){
            tvBarcodeStart.setText(vPlan.getBarcodestart());
            convertView.findViewById(R.id.tr_barcode_start).setVisibility(View.VISIBLE);
        }
        if(vPlan.getBarcodeend() != null){
            tvBarcodeEnd.setText(vPlan.getBarcodeend());
            convertView.findViewById(R.id.tr_barcode_end).setVisibility(View.VISIBLE);
        }
        //未生产的计划不显示条码
        if(vPlan.getState() != null && vPlan.getState().equals("1")){
            convertView.findViewById(R.id.tr_barcode_start).setVisibility(View.GONE);
            convertView.findViewById(R.id.tr_barcode_end).setVisibility(View.GONE);
        }
        //未生产的计划显示开始按钮
        if(vPlan.getState() != null && vPlan.getState().equals("1")){
            ((ButtonView)convertView.findViewById(R.id.vul_start)).setVisibility(View.VISIBLE);
            ((ButtonView)convertView.findViewById(R.id.vul_end)).setVisibility(View.GONE);
        }
        //生产中的计划显示结束按钮
        if(vPlan.getState() != null && vPlan.getState().equals("2")){
            ((ButtonView)convertView.findViewById(R.id.vul_start)).setVisibility(View.GONE);
            ((ButtonView)convertView.findViewById(R.id.vul_end)).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.tr_barcode_end).setVisibility(View.GONE);
        }
        //已完成的计划不显示
        if(vPlan.getState() != null && vPlan.getState().equals("3")){
            convertView.setVisibility(View.GONE);
        }

        //设置按钮点击事件
        convertView.findViewById(R.id.vul_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示dialog
                new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                        .title("提示")
                        .content("请扫描开始条码")
                        .inputType(
                                InputType.TYPE_CLASS_TEXT
                                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .input(
                                "请扫描开始条码",
                                "",
                                false,
//                                ((dialog, input) -> Toast("123"))
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    }
                                }
                        )
                        .inputRange(10, 12)
                        .positiveText(R.string.vul_confirm)
                        .negativeText(R.string.vul_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String barcode = dialog.getInputEditText().getText().toString();
                                if(StringUtil.isNullOrBlank(barcode)){
                                    Toast.makeText(context, "请扫描条码", Toast.LENGTH_LONG).show();
                                }else{
                                    vPlan.setBarcodestart(barcode);
                                    vPlan.setState("2");
//                                    new MyTask().execute(PathUtil.VUL_START_PLAN, App.gson.toJson(vPlan));
                                }
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
        //设置按钮点击事件
        convertView.findViewById(R.id.vul_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示dialog
                new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                        .title("提示")
                        .content("请扫描结束条码")
                        .inputType(
                                InputType.TYPE_CLASS_TEXT
                                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .input(
                                "请扫描结束条码",
                                "",
                                false,
//                                ((dialog, input) -> Toast("123"))
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    }
                                }
                        )
                        .inputRange(10, 12)
                        .positiveText(R.string.vul_confirm)
                        .negativeText(R.string.vul_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String barcode = dialog.getInputEditText().getText().toString();
                                if(StringUtil.isNullOrBlank(barcode)){
                                    Toast.makeText(context, "请扫描结束条码", Toast.LENGTH_LONG).show();
                                }else{
                                    vPlan.setBarcodeend(barcode);
                                    vPlan.setState("3");
                                    vPlan.setUpdateuser(App.username);
                                    submit(vPlan);
                                }
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
    }

    //结束计划
    public void endPlan(final VPlan vPlan){
        new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                .title("提示")
                .content("计划结束后不能再次执行，确定结束？")
                .positiveText(R.string.vul_confirm)
                .negativeText(R.string.vul_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        new MyTask().execute(PathUtil.VUL_END_PLAN, App.gson.toJson(vPlan));
                    }
                })
                .cancelable(false)
                .show();
    }

    //扫描结束条码执行事件
    public void submit(final VPlan vPlan){
        new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                .title("提示")
                .content("计划结束后不能再次执行，确定结束？")
                .positiveText(R.string.vul_confirm)
                .negativeText(R.string.vul_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        new MyTask().execute(PathUtil.VUL_COMPLATE_PLAN, App.gson.toJson(vPlan));
                    }
                })
                .cancelable(false)
                .show();
    }

    //绑定计划执行线程
    class InRecordTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendPut(strings[0], strings[1], HttpUtil.jsonContent);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(context, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                HashMap<String, Object> res = App.gson.fromJson(s, new TypeToken<HashMap<String, Object>>(){}.getType());
                if(res.get("code").toString().equals("200")){
                    Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                }
                ((VulcanizationActivity)context).getPlan(null);
            }
        }
    }


    class MyTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = HttpUtil.sendPut(strings[0], strings[1], HttpUtil.jsonContent);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtil.isNullOrBlank(s)){
                Toast.makeText(context, "网络连接异常", Toast.LENGTH_LONG).show();
            }else{
                HashMap<String, Object> res = App.gson.fromJson(s, new TypeToken<HashMap<String, Object>>(){}.getType());
                if(res.get("code").toString().equals("200")){
                    Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, res.get("msg").toString(), Toast.LENGTH_LONG).show();
                }
                ((VulcanizationActivity)context).getPlan(null);
            }
        }
    }
}
