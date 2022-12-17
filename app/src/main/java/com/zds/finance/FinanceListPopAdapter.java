package com.zds.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class FinanceListPopAdapter extends ArrayAdapter<String> {
    private int listViewResId;
    public static int selectCmd;
    public final static int CMD_INVALID = -1;
    public final static int CMD_DEL = 0;
    public final static int CMD_MODIFY = 1;
    public final static int CMD_TEMPLATE = 2;
    final static List<String> listData = Arrays.asList("删除", "修改", "模板");
    public FinanceListPopAdapter(Context context, int resource) {
        super(context, resource, listData);
        this.listViewResId = resource;
        selectCmd = CMD_INVALID;
    }

    public static void resetSelectCmd() {
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
                CharSequence btnTxt = btn.getText();
                if(btnTxt == "删除") {
                    FinanceListPopAdapter.selectCmd = CMD_DEL;
                }else if(btnTxt == "修改") {
                    FinanceListPopAdapter.selectCmd = CMD_MODIFY;
                }else{
                    FinanceListPopAdapter.selectCmd = CMD_TEMPLATE;
                }
                FinanceList.popWin.dismiss();
            }
        });
        return view;
    }
}
