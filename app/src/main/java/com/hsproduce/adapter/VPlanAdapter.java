package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.activity.VulcanizationActivity;
import com.hsproduce.bean.VPlan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VPlanAdapter extends BaseAdapter {
    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();

    public VPlanAdapter(Context context, List<VPlan> vPlanList){
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_vplan_item, null);
        final VPlan vPlan = vPlanList.get(position);
        //设置页面数据
        if (vPlan.getPdate() != null) {
            int index = vPlan.getPdate().indexOf(" ");
            ((TextView) convertView.findViewById(R.id.plandata)).setText(vPlan.getPdate().substring(0,index).replaceAll("/","-"));
        }
        if (vPlan.getItnbr() != null) {
            ((TextView) convertView.findViewById(R.id.spesc)).setText(vPlan.getItnbr());
        }
        if (vPlan.getItdsc() != null) {
            ((TextView) convertView.findViewById(R.id.spescname)).setText(vPlan.getItdsc());
        }
        if (vPlan.getPnum() != null) {
            TextView tv = convertView.findViewById(R.id.pnum);
            String num = vPlan.getPnum().toString();
            tv.setText(num);
        }
        return convertView;
    }
}
