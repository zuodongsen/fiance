package com.zds.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class FinanceAdapter extends ArrayAdapter<Finance> {
    private String date;
    private int listViewResId;
    private boolean isDateParent;

    public FinanceAdapter(List<Finance> listData_, String date_, Context context_, int txtViewResId_) {
        super(context_, txtViewResId_, listData_);
        this.listViewResId = txtViewResId_;
        this.date = date_;
        this.isDateParent = true;
    }

    public FinanceAdapter(List<Finance> listData_, Context context_, int txtViewResId_) {
        super(context_, txtViewResId_, listData_);
        this.listViewResId = txtViewResId_;
        this.isDateParent = false;
    }

    public void setHolder(TextView txtVeiwDate_, ListView listFinance_) {
        txtVeiwDate_.setText(this.date);
        listFinance_.setAdapter(this);
        listFinance_.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.selectListViewItemId = Integer.parseInt(((TextView) view.findViewById(R.id.txtfinance_id)).getText().toString());
                MainActivity.popWin.showAsDropDown(view, 100, -10);
                return false;
            }
        });
        MainActivity.setListViewHeightBasedOnChildren(listFinance_);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Finance finance = getItem(i);
        view = LayoutInflater.from(getContext()).inflate(this.listViewResId,null);
        if(this.isDateParent) {
            finance.setHolder(
                    (TextView)view.findViewById(R.id.txtfinance_info),
                    (TextView)view.findViewById(R.id.txtfinance_id),
                    (TextView)view.findViewById(R.id.txtfinance_amount),
                    (TextView)view.findViewById(R.id.txtfinance_type));
        }else {
            finance.setHolder(
                    (TextView)view.findViewById(R.id.txtfinance_info),
                    (TextView)view.findViewById(R.id.txtfinance_id),
                    (TextView)view.findViewById(R.id.txtfinance_amount),
                    (TextView)view.findViewById(R.id.txtfinance_type),
                    (TextView)view.findViewById(R.id.txtfinance_date));
        }

        return view;
    }
}
