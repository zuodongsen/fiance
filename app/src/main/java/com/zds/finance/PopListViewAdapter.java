package com.zds.finance;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class PopListViewAdapter extends ArrayAdapter<String>{
    private int listViewResId;
    public static int selectCmd;
    public final static int CMD_INVALID = -1;
    public final static int CMD_DEL = 0;
    public final static int CMD_MODIFY = 1;
    final static List<String> listData = Arrays.asList("删除", "修改");
    public PopListViewAdapter(List<String> listData_, Context context, int resource) {
        super(context, resource, listData);
        this.listViewResId = resource;
        selectCmd = CMD_INVALID;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String popInfo = getItem(i);
        view = LayoutInflater.from(getContext()) .inflate(this.listViewResId, null);
        TextView textView = (TextView)view.findViewById(R.id.textView_pop);
        textView.setText(popInfo);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView btn = (TextView)view;
                if(btn.getText() == "删除") {
                    PopListViewAdapter.selectCmd = CMD_DEL;
                }else {
                    PopListViewAdapter.selectCmd = CMD_MODIFY;
                }
                int a = btn.getId();
                MainActivity.popWin.dismiss();
            }
        });
        return view;
    }



}
