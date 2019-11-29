package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VPlan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PlanDialogItemAdapter extends BaseAdapter {
    //这里可以传递个对象，用来控制不同的item的效果
    //比如每个item的背景资源，选中样式等
    public List<VPlan> list;
    LayoutInflater inflater;

    public PlanDialogItemAdapter(Context context, List<VPlan> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public VPlan getItem(int i) {
        if (i == getCount() || list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_plan_dialog_item, null);
            holder.planid = (TextView) convertView.findViewById(R.id.planid);
            holder.plandate = (TextView) convertView.findViewById(R.id.plandata);
            holder.spesc = (TextView) convertView.findViewById(R.id.spesc);
            holder.mchid = (TextView) convertView.findViewById(R.id.mchid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.planid.setText(getItem(position).getId());
        int index = getItem(position).getPdate().indexOf(" ");
        holder.plandate.setText(getItem(position).getPdate().substring(0,index).replaceAll("/","-"));
        holder.spesc.setText(getItem(position).getItnbr());
        holder.mchid.setText(getItem(position).getMchid());
        return convertView;
    }

    public static class ViewHolder {
        public TextView planid,plandate,spesc,mchid;
    }
}
