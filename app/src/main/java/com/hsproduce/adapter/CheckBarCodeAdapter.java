package com.hsproduce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hsproduce.R;
import com.hsproduce.bean.VreCord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckBarCodeAdapter extends BaseAdapter {

    private Context context;
    private List<VreCord> vreCodeList = new ArrayList<>();

    public CheckBarCodeAdapter(Context context, List<VreCord> vreCodeList) {
        this.context = context;
        this.vreCodeList = vreCodeList;
    }

    @Override
    public int getCount() {
        return vreCodeList.size();
    }

    @Override
    public VreCord getItem(int position) {
        return vreCodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_check_barcode_item, null);
        final VreCord vreCord = vreCodeList.get(position);
        //设置页面数据
        if(vreCord.getMchid() != null){
            ((TextView)convertView.findViewById(R.id.mchid)).setText(vreCord.getMchid());
        }
        if(vreCord.getItnbr() != null){
            ((TextView)convertView.findViewById(R.id.spesc)).setText(vreCord.getItnbr());
        }
        if(vreCord.getItdsc() != null){
            ((TextView)convertView.findViewById(R.id.spescname)).setText(vreCord.getItdsc());
        }
        if(vreCord.getWdate() != null){
            int index = vreCord.getWdate().indexOf(" ");
            ((TextView)convertView.findViewById(R.id.product_date)).setText(vreCord.getWdate().substring(0,index).replaceAll("/","-"));
        }
        ((TextView)convertView.findViewById(R.id.LorR)).setText(vreCord.getLr());
        if(vreCord.getShift() != null){
            if(vreCord.getShift().equals("1")){
                ((TextView)convertView.findViewById(R.id.shift)).setText("甲班");
            }else if(vreCord.getShift().equals("2")){
                ((TextView)convertView.findViewById(R.id.shift)).setText("乙班");
            }else if(vreCord.getShift().equals("3")){
                ((TextView)convertView.findViewById(R.id.shift)).setText("丙班");
            }else{
                ((TextView)convertView.findViewById(R.id.shift)).setText("不在甲乙丙班");
            }
        }
        if(vreCord.getCreateuser() != null){
            ((TextView)convertView.findViewById(R.id.creatuser)).setText(vreCord.getCreateuser());
        }
        return convertView;
    }
}
