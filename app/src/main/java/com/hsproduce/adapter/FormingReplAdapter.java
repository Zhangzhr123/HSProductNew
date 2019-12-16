package com.hsproduce.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.hsproduce.R;
import com.hsproduce.activity.SwitchFormingActivity;
import com.hsproduce.bean.VPlan;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class FormingReplAdapter extends BaseAdapter {
    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();
    private String preCode = "",nextCode = "";

    public FormingReplAdapter(Context context, List<VPlan> vPlanList) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_forming_repl_item, null);
        final VPlan vPlan = vPlanList.get(position);
        //设置页面数据
        if (vPlan.getItnbr() != null) {
            ((TextView) convertView.findViewById(R.id.spesc)).setText(vPlan.getItnbr());
        }
        if (vPlan.getItdsc() != null) {
            ((TextView) convertView.findViewById(R.id.spescname)).setText(vPlan.getItdsc());
        }
        if (vPlan.getPro() != null) {
            ((TextView) convertView.findViewById(R.id.pro)).setText(vPlan.getPro());
        }
        if (vPlan.getState() != null) {
            if (vPlan.getState().equals("10")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("新计划");
            } else if (vPlan.getState().equals("20")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("等待中");
            } else if (vPlan.getState().equals("30")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("进行中");
            } else if (vPlan.getState().equals("40")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("已完成");
            } else {
                ((TextView) convertView.findViewById(R.id.state)).setText("未知状态");
            }
        }
        if (vPlan.getAnum() != null) {
            ((TextView) convertView.findViewById(R.id.anum)).setText(vPlan.getAnum());
        }
        if (vPlan.getPnum() != null) {
            ((TextView) convertView.findViewById(R.id.pnum)).setText(vPlan.getPnum());
        }
        convertView.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                //显示dialog
                final MaterialDialog dialog = new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
//                        .title("生产条码录入").titleColorRes(R.color.colorPrimaryDark)
                        .customView(R.layout.dialog_input,true)
//                        .positiveText(R.string.vul_confirm).positiveColorRes(R.color.colorPrimary)
//                        .negativeText(R.string.vul_cancel).negativeColorRes(R.color.colorPrimary)
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                //获取控件
//                                EditText pre = dialog.findViewById(R.id.input);
//                                EditText next = dialog.findViewById(R.id.input2);
//                                preCode = pre.getText().toString();
//                                nextCode = next.getText().toString();
//                                //如果为空则进行操作
//                                if(nextCode == null || nextCode.equals("")){
//                                    Toast.makeText(context, "当前班开始条码不能为空", Toast.LENGTH_LONG).show();
//                                }else if(preCode == null || preCode.equals("")){
//                                    preCode = String.valueOf(Long.valueOf(nextCode)-1);
//                                }else{
//                                    if(preCode.equals(nextCode)){
//                                        Toast.makeText(context, "上一班结束条码不能与当前班开始条码一致", Toast.LENGTH_LONG).show();
//                                    }else{
//                                        Toast.makeText(context, "上一班结束条码:"+preCode+"当前班开始条码:"+nextCode, Toast.LENGTH_LONG).show();
//                                        ((SwitchFormingActivity) context).repItndes(vPlan.getId(),preCode,nextCode);
//                                    }
//                                }
//
//                            }
//                        })
//                        .cancelable(false)
                        .show();
                //控件
                View customeView = dialog.getCustomView();
                final EditText pre = dialog.findViewById(R.id.input);
                final EditText next = dialog.findViewById(R.id.input2);
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
                        preCode = pre.getText().toString();
                        nextCode = next.getText().toString();
                        //如果为空则进行操作
                        if(nextCode == null || nextCode.equals("")){
                            Toast.makeText(context, "当前班开始条码不能为空", Toast.LENGTH_LONG).show();
                        }else if(preCode == null || preCode.equals("")){
                            preCode = String.valueOf(Long.valueOf(nextCode)-1);
                        }else{
                            if(preCode.equals(nextCode)){
                                Toast.makeText(context, "上一班结束条码不能与当前班开始条码一致", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "上一班结束条码:"+preCode+"当前班开始条码:"+nextCode, Toast.LENGTH_LONG).show();
                                ((SwitchFormingActivity) context).repItndes(vPlan.getId(),preCode,nextCode);
                                dialog.dismiss();
                            }
                        }
                    }
                });


            }
        });
        return convertView;
    }
}
