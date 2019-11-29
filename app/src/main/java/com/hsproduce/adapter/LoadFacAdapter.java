package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VLoad;
import com.hsproduce.bean.VLoadHxm;

import java.util.ArrayList;
import java.util.List;

public class LoadFacAdapter extends BaseAdapter {

    private Context context;
    private List<VLoad> vLoadList = new ArrayList<>();

    public LoadFacAdapter(Context context, List<VLoad> vLoadList) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_load_hxm_item, null);
        final VLoad vLoad = vLoadList.get(position);
        if (vLoad.getId() != null) {
            ((TextView) convertView.findViewById(R.id.loadid)).setText(vLoad.getId());
        }
        if (vLoad.getCaR_CODE() != null) {
            ((TextView) convertView.findViewById(R.id.carnum)).setText(vLoad.getCaR_CODE());
        }
        if (vLoad.getSuB_CODE() != null) {
            ((TextView) convertView.findViewById(R.id.subcode)).setText(vLoad.getSuB_CODE());
        }
        return convertView;
    }
}
