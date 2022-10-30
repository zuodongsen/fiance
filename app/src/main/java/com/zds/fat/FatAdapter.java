package com.zds.fat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zds.finance.Finance;
import com.zds.finance.FinanceAdapter;
import com.zds.finance.MainActivity;
import com.zds.finance.R;

import java.util.ArrayList;
import java.util.List;

public class FatAdapter extends ArrayAdapter<Fat> {

    public FatAdapter(List<Fat> listData_, @NonNull Context context_, int txtViewResId_) {
        super(context_, txtViewResId_, listData_);
        this.listViewResId = txtViewResId_;
    }

    public static void setListViewAdapter(int year, int month, Context context_, ListView listView, int txtViewResId_) {
        fatList = Fat.getOneMonthFormDb(year, month);
        fatAdapter = new FatAdapter(fatList, context_, txtViewResId_);
        listView.setAdapter(fatAdapter);
//        PopWin.initPopListView(context_);
        listView.setOnItemLongClickListener(PopWin.itemLongClickListener);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Fat fat = getItem(i);
        view = LayoutInflater.from(getContext()).inflate(this.listViewResId,null);
        fat.setHolder(
                (TextView)view.findViewById(R.id.txtfat_date),
                (TextView)view.findViewById(R.id.textfat_morning),
                (TextView)view.findViewById(R.id.textfat_noon),
                (TextView)view.findViewById(R.id.textfat_night),
                (TextView)view.findViewById(R.id.textfat_rope),
                (TextView)view.findViewById(R.id.textfat_circle),
                (TextView)view.findViewById(R.id.textfat_id));

        return view;
    }

    private int listViewResId;
    private static List<Fat> fatList;
    private static FatAdapter fatAdapter;
}
