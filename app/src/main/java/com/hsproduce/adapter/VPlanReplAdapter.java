package com.hsproduce.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.activity.SwitchPlanActivity;
import com.hsproduce.bean.VPlan;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VPlanReplAdapter extends BaseAdapter {
    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();
    private String preCode = "", nextCode = "";

    public VPlanReplAdapter(Context context, List<VPlan> vPlanList) {
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
        if (vPlan.getState() != null) {
            if (vPlan.getState().equals("10")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("新计划");
            } else if (vPlan.getState().equals("20")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("等待中");
            } else if (vPlan.getState().equals("30")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("生产中");
            } else if (vPlan.getState().equals("40")) {
                ((TextView) convertView.findViewById(R.id.state)).setText("已完成");
            } else {
                ((TextView) convertView.findViewById(R.id.state)).setText("未知状态");
            }
        }
        if (vPlan.getPnum() != null) {
            ((TextView) convertView.findViewById(R.id.pnum)).setText(vPlan.getPnum());
        }
        return convertView;
    }
}
