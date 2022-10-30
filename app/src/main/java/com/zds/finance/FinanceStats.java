package com.zds.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.BarChart;
import com.zds.common.BarData;
import com.zds.common.PieChart;
import com.zds.common.PieData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinanceStats {

    public FinanceStats(Context context_) {
        this.context = context_;
        Calendar calendar = Calendar.getInstance();
        this.selectYear = calendar.get(Calendar.YEAR); // 得到当前年
    }

    public void setFianceStatsView() {
        this.initFinanceLayout();
        this.getFianceData();
        this.setFinanceYearPieView();
        this.setFinanceYearBarView();
    }


    private void initFinanceLayout() {
        this.layoutFinance = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        this.viewfFinanceStats = LayoutInflater.from(context).inflate(R.layout.view_fiancestats, null);
        layoutFinance.removeAllViews();
        layoutFinance.addView(this.viewfFinanceStats);
    }

    private void getFianceData() {
        this.finances = Finance.getOneYearFormDb(this.selectYear);
        this.jsFinance = Finance.toJsonByType(this.finances);
    }

    private void setFinanceYearPieView() {
        LinearLayout layoutMonth = this.viewfFinanceStats.findViewById(R.id.layout_year);
        TextView txtYear = this.viewfFinanceStats.findViewById(R.id.txtstats_year);

        List<PieData> pieDataList = new ArrayList<>();
        for(String it : CreateActivity.amountType) {
            if(!this.jsFinance.has(it)) {
                continue;
            }
            float amount = Finance.getAmountFromJson(this.jsFinance, it);
            pieDataList.add(new PieData(amount, it));
        }
        PieChart pieChart = new PieChart(context, pieDataList);
        layoutMonth.addView(pieChart);
        txtYear.setText(String.valueOf(this.selectYear) + "年 共消费RMB：" + String.valueOf(pieChart.pieValueSum));
    }

    private void setFinanceYearBarView() {
        LinearLayout layoutMonth = this.viewfFinanceStats.findViewById(R.id.layout_type);
//        TextView txtYear = this.viewfFinanceStats.findViewById(R.id.txtstats_year);

        List<BarData> barDataList = new ArrayList<>();
        for(String it : CreateActivity.amountType) {
            if(!this.jsFinance.has(it)) {
                continue;
            }
            int num = Finance.getNumFromJson(this.jsFinance, it);
            barDataList.add(new BarData(num, it));
        }
        BarChart barChart = new BarChart(context, barDataList);
        layoutMonth.addView(barChart);
//        txtYear.setText(String.valueOf(this.selectYear) + "年 共消费RMB：" + String.valueOf(scanRadar.pieValueSum));
    }

    private Context context;
    private int selectYear;
    private LinearLayout layoutFinance;
    private List<Finance> finances;
    private JSONObject jsFinance;
    View viewfFinanceStats;

}
