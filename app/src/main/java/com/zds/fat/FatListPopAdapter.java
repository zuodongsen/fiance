package com.zds.fat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zds.finance.FinanceList;
import com.zds.finance.R;

import java.util.Arrays;
import java.util.List;

public class FatListPopAdapter extends ArrayAdapter<String> {
    private int listViewResId;
    public static int selectCmd;
    public final static int CMD_INVALID = -1;
    public final static int CMD_DEL = 0;
    public final static int CMD_MODIFY = 1;
    final static List<String> listData = Arrays.asList("删除", "修改");
    public FatListPopAdapter(Context context, int resource) {
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
                    FatListPopAdapter.selectCmd = CMD_DEL;
                }else {
                    FatListPopAdapter.selectCmd = CMD_MODIFY;
                }
                int a = btn.getId();
                FatList.popWin.dismiss();
            }
        });
        return view;
    }
}
