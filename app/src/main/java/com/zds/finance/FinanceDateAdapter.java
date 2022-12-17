package com.zds.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class FinanceDateAdapter extends ArrayAdapter<FinanceAdapter> {
    private int listViewResId;

    public FinanceDateAdapter(List<FinanceAdapter> listData_, @NonNull Context context_, int txtViewResId_) {
        super(context_, txtViewResId_, listData_);
        this.listViewResId = txtViewResId_;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FinanceAdapter financeList = getItem(i);
        view = LayoutInflater.from(getContext()).inflate(this.listViewResId,null);
        financeList.setHolder(
                (TextView)view.findViewById(R.id.txtdate_date),
                (TextView)view.findViewById(R.id.txtdate_total),
                (ListView)view.findViewById(R.id.listdate_finance));
        return view;
    }
}
