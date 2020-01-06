package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VLoad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LoadAdapter extends BaseAdapter {

    private Context context;
    private List<VLoad> vLoadList = new ArrayList<>();

    public LoadAdapter(Context context, List<VLoad> vLoadList) {
        this.context = context;
        this.vLoadList = vLoadList;
    }

    @Override
    public int getCount() {
        return vLoadList.size();
    }

    @Override
    public VLoad getItem(int position) {
        return vLoadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_load_item, null);
        final VLoad vLoad = vLoadList.get(position);
        if (vLoad.getWdate() != null) {
            int index = vLoad.getWdate().indexOf(" ");
            ((TextView) convertView.findViewById(R.id.date)).setText(vLoad.getWdate().substring(0,index).replaceAll("/","-"));
        }
        if (vLoad.getCaR_CODE() != null) {
            ((TextView) convertView.findViewById(R.id.car_number)).setText(vLoad.getCaR_CODE());
        }
        if (vLoad.getState() != null) {
            ((TextView) convertView.findViewById(R.id.state)).setText(vLoad.getState());
        }
//        if (vLoad.getSuB_CODE() != null) {
//            ((TextView) convertView.findViewById(R.id.delivery)).setText(vLoad.getSuB_CODE());
//        }
        return convertView;
    }
}
