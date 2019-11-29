package com.hsproduce.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import com.hsproduce.dialog.SingleChoiceDialogFragment;

public class DialogUtil {

    public static void showDialog(Context context, String msg){
        final android.app.AlertDialog.Builder normalDialog =
                new android.app.AlertDialog.Builder(context);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    // 返回一个普通的Dialog
    public static AlertDialog getDialog(Context context){
        int width =(int)(ScreenUtil.getScreenWidth(context)*0.7);
        int height=(int)(width*0.7);
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(width ,height);
        return dialog;
    }

    // 返回一个单选Dialog
    public static SingleChoiceDialogFragment getSingleChoiceDialog(){
        SingleChoiceDialogFragment singleChoiceDialogFragment = new SingleChoiceDialogFragment();
        return singleChoiceDialogFragment;
    }


}
