package com.hsproduce.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

public class SingleChoiceDialogFragment extends DialogFragment {

    private String title;

    private String[] items;

    private int checkedItem;

    private DialogInterface.OnClickListener onClickListener;

    private DialogInterface.OnClickListener positiveCallback;

    public void show(String title, String[] items, int checkedItem, DialogInterface.OnClickListener onClickListener,
                     DialogInterface.OnClickListener positiveCallback, FragmentManager fragmentManager) {
        this.title = title;
        this.items = items;
        this.checkedItem = checkedItem;
        this.onClickListener = onClickListener;
        this.positiveCallback = positiveCallback;
        show(fragmentManager, "SingleChoiceDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setSingleChoiceItems(items, checkedItem, onClickListener)
                .setPositiveButton("确定", positiveCallback);
        return builder.create();
    }

}

