package com.hsproduce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.activity.FormingActivity;
import com.hsproduce.bean.VPlan;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class FormingItemAdapter extends BaseAdapter {
    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();

    public FormingItemAdapter(Context context, List<VPlan> vPlanList) {
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
            @Override
            public void onClick(View v) {
                //显示dialog
                new MaterialDialog.Builder(context)
//                        .iconRes(R.drawable.icon_warning)
                        .title("提示")
                        .customView(R.layout.dialog_input,true)
                        .input("上一班结束条码", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String barcode = dialog.getInputEditText().getText().toString();
                            }
                        })
                        .input("当前班开始条码", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String barcode = dialog.getInputEditText().getText().toString();
                            }
                        })
                        .cancelable(false)
                        .positiveText(R.string.vul_confirm)
                        .negativeText(R.string.vul_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.getInputEditText().setText("");
                                dialog.findViewById(R.id.input);
                                dialog.findViewById(R.id.input2);
                                ((FormingActivity) context).repItndes(vPlan.getId());
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });
        return convertView;
    }
}
