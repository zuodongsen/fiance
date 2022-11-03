package com.zds.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.BarChart;
import com.zds.common.BarData;
import com.zds.common.DateTimeTrans;
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

    public void setView() {
        this.initFinanceLayout();
        this.getFianceData();
        this.setFinanceYearPieView();
        this.reflushTypeListView(null);
    }


    private void initFinanceLayout() {
        this.layoutFinance = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        this.viewfFinanceStats = LayoutInflater.from(context).inflate(R.layout.view_financestats, null);
        layoutFinance.removeAllViews();
        layoutFinance.addView(this.viewfFinanceStats);
    }

    private void getFianceData() {
        this.finances = Finance.getOneYearFormDb(this.selectYear);
        this.jsFinance = Finance.toJsonByType(this.finances);
    }

    private void genFinanceDateList() {
        this.listData = Finance.getTypeInOneYearFormDb(this.selectYear, this.selectType);
        for(String t : this.selectPieData.pieStringPoly) {
            List<Finance> f = Finance.getTypeInOneYearFormDb(this.selectYear, t);
            this.listData.addAll(f);
        }
        this.financeAdapterList.clear();
        this.fianceListArray.clear();
        this.fianceListArray.add(new ArrayList<>());
        List<Finance> financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
        long lastDate = -1;
        for (Finance f : this.listData) {
            if(lastDate == f.date) {
                financeList.add(f);
                continue;
            }
            if(!financeList.isEmpty()){
                this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getMonthDay2String(lastDate),
                        this.context, R.layout.listview_finance,false));
                this.fianceListArray.add(new ArrayList<>());
                financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
            }
            financeList.add(f);
            lastDate = f.date;
        }
        if(!financeList.isEmpty()){
            this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getMonthDay2String(lastDate),
                    this.context, R.layout.listview_finance,false));
        }
    }

    private void reflushTypeListView(String type) {
        if(type != null) {
            if(this.selectType.equals(type)) {
                return;
            }
            this.selectType = type;
        }

        this.genFinanceDateList();
        this.financeDateAdapter = new FinanceDateAdapter(this.financeAdapterList, this.context, R.layout.listview_date);
        ListView listView = this.viewfFinanceStats.findViewById(R.id.financestats_list);
        TextView txtStatsType = this.viewfFinanceStats.findViewById(R.id.txtstats_type);
        listView.setAdapter(this.financeDateAdapter);
        FinanceList.setListViewHeightBasedOnChildren(listView);
        txtStatsType.setText("  " + this.selectType + "消费 " + this.listData.size() + " 次，共 " + this.selectPieData.pieStringDown + " 元");

    }

    private void setFinanceYearPieView() {
        LinearLayout layoutMonth = this.viewfFinanceStats.findViewById(R.id.layoutfat_year);
        TextView txtYear = this.viewfFinanceStats.findViewById(R.id.txtstats_year);

        pieDataList = new ArrayList<>();
        for(String it : CreateActivity.amountType) {
            if(!this.jsFinance.has(it)) {
                continue;
            }
            float amount = Finance.getAmountFromJson(this.jsFinance, it);
            pieDataList.add(new PieData(amount, it));
        }
        PieChart pieChart = new PieChart(context, pieDataList);
        pieChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                selectPieData = ((PieChart)v).getDataByPoint(event.getX(), event.getY());
                if(selectPieData == null) {
                    return false;
                }
                reflushTypeListView(selectPieData.pieString);
                return false;
            }
        });
        layoutMonth.addView(pieChart);
        this.selectType = pieDataList.get(0).pieString;
        this.selectPieData = pieDataList.get(0);
        txtYear.setText("  " + String.valueOf(this.selectYear) + "年 共消费RMB：" + String.valueOf(pieChart.pieValueSum));
    }



    private Context context;
    private int selectYear;
    private LinearLayout layoutFinance;
    private List<Finance> finances;
    private List<PieData> pieDataList;
    private JSONObject jsFinance;
    View viewfFinanceStats;

    private PieData selectPieData;
    private String selectType;
    private List<List<Finance>> fianceListArray = new ArrayList<>();
    private List<Finance> listData = new ArrayList<>();
    private List<FinanceAdapter> financeAdapterList = new ArrayList<>();
    private FinanceDateAdapter financeDateAdapter;

}
