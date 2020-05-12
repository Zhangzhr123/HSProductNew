package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VPlan;

import java.util.ArrayList;
import java.util.List;

public class ProductItemAdapter extends BaseAdapter {
    private Context context;
    private List<VPlan> vPlanList = new ArrayList<>();

    public ProductItemAdapter(Context context, List<VPlan> vPlanList) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_product_item, null);
        final VPlan vPlan = vPlanList.get(position);
        //设置页面数据
        if (vPlan.getMchid() != null) {
            ((TextView) convertView.findViewById(R.id.mchid)).setText(vPlan.getMchid());
        }
        if (vPlan.getLr() != null) {
            ((TextView) convertView.findViewById(R.id.lr)).setText(vPlan.getLr());
        }
        if (vPlan.getDnum() != null) {
            ((TextView) convertView.findViewById(R.id.dnum)).setText(vPlan.getDnum());
        }
        return convertView;
    }

}
