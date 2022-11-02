package com.zds.fat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.finance.R;

import java.util.Calendar;

public class FatStats {

    public FatStats(Context context_) {
        this.context = context_;
        Calendar calendar = Calendar.getInstance();
//        this.selectYear = calendar.get(Calendar.YEAR); // 得到当前年
    }

    public void setView() {
        this.initFinanceLayout();
//        this.getFianceData();
//        this.setFinanceYearPieView();
//        this.setFinanceYearBarView();
    }


    private void initFinanceLayout() {
        this.layoutFat = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        this.viewfFatStats = LayoutInflater.from(context).inflate(R.layout.view_fatstats, null);
        layoutFat.removeAllViews();
        layoutFat.addView(this.viewfFatStats);
    }

    private Context context;
    private LinearLayout layoutFat;
    private View viewfFatStats;


}
