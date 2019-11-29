package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VLoadHxm;
import com.hsproduce.bean.VLoadRfcord;

import java.util.ArrayList;
import java.util.List;

public class LoadRfcordAdapter extends BaseAdapter {

    private Context context;
    private List<VLoadHxm> vLoadHxmList = new ArrayList<>();

    public LoadRfcordAdapter(Context context, List<VLoadHxm> vLoadHxmList) {
        this.context = context;
        this.vLoadHxmList = vLoadHxmList;
    }

    @Override
    public int getCount() {
        return vLoadHxmList.size();
    }

    @Override
    public VLoadHxm getItem(int position) {
        return vLoadHxmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_load_detail_item, null);
        final VLoadHxm vLoadHxm = vLoadHxmList.get(position);
        if (vLoadHxm.getItnbr() != null) {
            ((TextView) convertView.findViewById(R.id.itnbr)).setText(vLoadHxm.getItnbr());
        }
        if (vLoadHxm.getItdsc() != null) {
            ((TextView) convertView.findViewById(R.id.itndsc)).setText(vLoadHxm.getItdsc());
        }
        return convertView;
    }
}
