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
    private String preCode = "", nextCode = "";

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
//        if (vPlan.getPro() != null) {
//            ((TextView) convertView.findViewById(R.id.pro)).setText(vPlan.getPro());
//        }
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
//        if (vPlan.getAnum() != null) {
//            ((TextView) convertView.findViewById(R.id.anum)).setText(vPlan.getAnum());
//        }
        if (vPlan.getPnum() != null) {
            ((TextView) convertView.findViewById(R.id.pnum)).setText(vPlan.getPnum());
        }
        //是否隐藏按钮
//        if(!((SwitchFormingActivity) context).show){
////            convertView.findViewById(R.id.start).setVisibility(View.GONE);
////        }
////        convertView.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
////            @SuppressLint("ResourceAsColor")
////            @Override
////            public void onClick(View v) {
////                //显示dialog
////                final MaterialDialog dialog = new MaterialDialog.Builder(context)
//////                        .iconRes(R.drawable.icon_warning)
//////                        .title("生产条码录入").titleColorRes(R.color.colorPrimaryDark)
////                        .customView(R.layout.dialog_input, true)
//////                        .positiveText(R.string.vul_confirm).positiveColorRes(R.color.colorPrimary)
//////                        .negativeText(R.string.vul_cancel).negativeColorRes(R.color.colorPrimary)
//////                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//////                            @Override
//////                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//////                                //获取控件
//////                                EditText pre = dialog.findViewById(R.id.input);
//////                                EditText next = dialog.findViewById(R.id.input2);
//////                                preCode = pre.getText().toString();
//////                                nextCode = next.getText().toString();
//////                                //如果为空则进行操作
//////                                if(nextCode == null || nextCode.equals("")){
//////                                    Toast.makeText(context, "当前班开始条码不能为空", Toast.LENGTH_LONG).show();
//////                                }else if(preCode == null || preCode.equals("")){
//////                                    preCode = String.valueOf(Long.valueOf(nextCode)-1);
//////                                }else{
//////                                    if(preCode.equals(nextCode)){
//////                                        Toast.makeText(context, "上一班结束条码不能与当前班开始条码一致", Toast.LENGTH_LONG).show();
//////                                    }else{
//////                                        Toast.makeText(context, "上一班结束条码:"+preCode+"当前班开始条码:"+nextCode, Toast.LENGTH_LONG).show();
//////                                        ((SwitchFormingActivity) context).repItndes(vPlan.getId(),preCode,nextCode);
//////                                    }
//////                                }
//////
//////                            }
//////                        })
//////                        .cancelable(false)
////                        .show();
////                //控件
////                View customeView = dialog.getCustomView();
////                final EditText pre = dialog.findViewById(R.id.input);
////                final EditText next = dialog.findViewById(R.id.input2);
////                Button finish = customeView.findViewById(R.id.finish);
////                finish.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////                    }
////                });
////                Button ok = customeView.findViewById(R.id.ok);
////                ok.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        //条码位数为12为   05（工厂代码）19（年份）25（机台编码最后两位）123456（流水码）
////                        System.out.println(((SwitchFormingActivity) context).mchid);
////                        String jt = ((SwitchFormingActivity) context).mchid;
////                        jt = jt.substring(jt.length() - 2, jt.length());
////                        System.out.println("机台号编码最后两位:" + jt);
////                        preCode = pre.getText().toString();
////                        nextCode = next.getText().toString();
////
////                        //如果为空则进行操作
////                        if (nextCode.equals("") && preCode.equals("")) {
////
////                            Toast.makeText(context, "请输入条码", Toast.LENGTH_LONG).show();
////
////                        } else if (preCode.equals("") && !nextCode.equals("")) {
////
////                            if (nextCode.length() != 12) {
////                                Toast.makeText(context, "开始条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
////                            } else {
////                                //String str = String.format("%04d", youNumber);
////                                int nextNum = Integer.valueOf(nextCode.substring(6, 12));
////                                if (nextNum == 1) {
////                                    preCode = "";
////                                    Toast.makeText(context, "开始条码为起始码，结束条码为空", Toast.LENGTH_LONG).show();
////                                } else {
////                                    preCode = String.format("%06d", nextNum - 1);
////                                    preCode = nextCode.substring(0, 6) + "" + preCode;
////                                    //弹窗提示
////                                    new MaterialDialog.Builder(context)
////                                            .title("根据当前计划开始条码:" + nextCode + "，生成上一计划结束条码:" + preCode + "，请确认上计划的结束条码？").titleColorRes(R.color.black)
////                                            .positiveText(R.string.vul_confirm).positiveColorRes(R.color.colorPrimary)
////                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
////                                                @Override
////                                                public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
//////                                            ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
////                                                    pre.setText(preCode);
////                                                    dialog1.dismiss();
//////                                            dialog.dismiss();
////                                                }
////                                            })
////                                            .cancelable(false)
////                                            .show();
////                                }
////                                System.out.println("上一计划结束条码" + preCode);
////
////                            }
////
////
////                        } else if (!preCode.equals("") && nextCode.equals("")) {
////
////                            if (preCode.length() != 12) {
////                                Toast.makeText(context, "结束条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
////                            } else {
////                                int preNum = Integer.valueOf(preCode.substring(6, 12));
////                                nextCode = String.format("%06d", preNum + 1);
////                                nextCode = preCode.substring(0, 6) + "" + nextCode;
////                                System.out.println("当前计划开始条码" + nextCode);
////                                //弹窗提示
////                                new MaterialDialog.Builder(context)
////                                        .title("根据上一计划结束条码:" + preCode + "，生成当前计划开始条码:" + nextCode + "，请确认当前计划的开始条码？").titleColorRes(R.color.black)
////                                        .positiveText(R.string.vul_confirm).positiveColorRes(R.color.colorPrimary)
////                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
////                                            @Override
////                                            public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
//////                                            ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
////                                                next.setText(nextCode);
////                                                dialog1.dismiss();
//////                                            dialog.dismiss();
////                                            }
////                                        })
////                                        .cancelable(false)
////                                        .show();
////                            }
////
////
////                        } else {
////
////                        }
////
////                        if(preCode.length()!=12 || nextCode.length()!=12){
////                            Toast.makeText(context, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
////                        }else if(preCode.equals(nextCode)){
////                            Toast.makeText(context, "上一计划结束条码不能与当前计划开始条码一致", Toast.LENGTH_LONG).show();
////                        }else{
////                            if(pre.getText().toString().equals("") && Integer.valueOf(nextCode.substring(6, 12)) == 1){
////                                String nextjt = nextCode.substring(4, 6);
////                                if (!jt.equals(nextjt)) {
////                                    Toast.makeText(context, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
////                                }else{
////                                    ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
////                                    dialog.dismiss();
////                                }
////                            }else if(!pre.getText().toString().equals("") && Integer.valueOf(nextCode.substring(6, 12))>1){
////                                String prejt = preCode.substring(4, 6);
////                                String nextjt = nextCode.substring(4, 6);
////                                System.out.println(prejt + "----" + nextjt);
////                                if (!jt.equals(prejt)) {
////                                    Toast.makeText(context, "结束条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
////                                } else if (!jt.equals(nextjt)) {
////                                    Toast.makeText(context, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
////                                } else {
////                                    ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
////                                    dialog.dismiss();
////                                }
////                            }else{
////
////                            }
////                        }
////
////
////
//////                        if (preCode.length() != 12 || nextCode.length() != 12) {
//////                            Toast.makeText(context, "条码规格不正确，请重新输入", Toast.LENGTH_LONG).show();
//////                        }else if (preCode.equals(nextCode)) {
//////                            Toast.makeText(context, "上一计划结束条码不能与当前计划开始条码一致", Toast.LENGTH_LONG).show();
//////                        }else{
//////                            if (preCode.equals("") && !nextCode.equals("")) {
//////                                String nextjt = nextCode.substring(4, 6);
//////                                if (!jt.equals(nextjt)) {
//////                                    Toast.makeText(context, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
//////                                }else{
//////                                    ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
//////                                    dialog.dismiss();
//////                                }
//////                            }else{
//////                                String prejt = preCode.substring(4, 6);
//////                                String nextjt = nextCode.substring(4, 6);
//////                                System.out.println(prejt + "----" + nextjt);
//////                                if (!jt.equals(prejt)) {
//////                                    Toast.makeText(context, "结束条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
//////                                } else if (!jt.equals(nextjt)) {
//////                                    Toast.makeText(context, "开始条码不属于此机台，请重新输入", Toast.LENGTH_LONG).show();
//////                                } else {
//////                                    ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
//////                                    dialog.dismiss();
//////                                }
//////                            }
//////                        }
////
////
////
////
//////                        new MaterialDialog.Builder(context)
//////                                .title("上一计划结束条码:" + preCode + "，当前计划开始条码:" + nextCode + "，是否确定？").titleColorRes(R.color.black)
//////                                .positiveText(R.string.vul_confirm).positiveColorRes(R.color.colorPrimary)
//////                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//////                                    @Override
//////                                    public void onClick(@NonNull MaterialDialog dialog1, @NonNull DialogAction which) {
//////                                        ((SwitchFormingActivity) context).repItndes(vPlan.getId(), preCode, nextCode);
//////                                        dialog1.dismiss();
//////                                        dialog.dismiss();
//////                                    }
//////                                })
//////                                .cancelable(false)
//////                                .show();
//////                            Toast.makeText(context, "上一班结束条码:"+preCode+"当前班开始条码:"+nextCode, Toast.LENGTH_LONG).show();
//////                            ((SwitchFormingActivity) context).repItndes(vPlan.getId(),preCode,nextCode);
//////                            dialog.dismiss();
////
////                    }
////                });
////
////
////            }
////        });
        return convertView;
    }
}
