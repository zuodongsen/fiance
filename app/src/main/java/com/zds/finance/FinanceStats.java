package com.zds.finance;

import android.content.Context;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        if(beginYear == 0) {
            endYear = beginYear = calendar.get(Calendar.YEAR);
        }
    }

    public void setView() {
        this.initFinanceLayout();
        flushYearStats();
    }


    public void flushYearStats() {
        this.getFianceData();
        this.setFinanceYearPieView();
        this.reflushTypeListView(null);
        this.txtBeginYear.setText(String.valueOf(beginYear));
        this.txtEndYear.setText(String.valueOf(endYear));
    }

    private void initFinanceLayout() {
        this.layoutFinance = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        this.viewfFinanceStats = LayoutInflater.from(context).inflate(R.layout.view_financestats, null);
        layoutFinance.removeAllViews();
        layoutFinance.addView(this.viewfFinanceStats);
        this.txtBeginYear = this.viewfFinanceStats.findViewById(R.id.txt_fs_yb);
        this.txtEndYear = this.viewfFinanceStats.findViewById(R.id.txt_fs_ye);
        this.txtBeginYear.setOnTouchListener(this.txtOnTouchListener);
        this.txtEndYear.setOnTouchListener(this.txtOnTouchListener);
    }

    private void getFianceData() {
        this.finances = Finance.getMuiltyYearFormDb(beginYear, endYear);
        this.jsFinance = Finance.toJsonByType(this.finances);
    }

    private void genFinanceDateList() {
        this.listData = Finance.getTypeInYearFormDb(beginYear, endYear, this.selectType);
        for(String t : this.selectPieData.pieStringPoly) {
            List<Finance> f = Finance.getTypeInYearFormDb(beginYear, endYear, t);
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
                this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getDate2String(lastDate),
                        this.context, R.layout.listview_finance,false));
                this.fianceListArray.add(new ArrayList<>());
                financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
            }
            financeList.add(f);
            lastDate = f.date;
        }
        if(!financeList.isEmpty()){
            this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getDate2String(lastDate),
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
        TextView txtStatsType = this.viewfFinanceStats.findViewById(R.id.txt_fs_type);
        listView.setAdapter(this.financeDateAdapter);
        FinanceList.setListViewHeightBasedOnChildren(listView);
        txtStatsType.setText("  " + this.selectType + "消费 " + this.listData.size() + " 次，共 " + this.selectPieData.pieStringDown + " 元");

    }

    private void setFinanceYearPieView() {
        LinearLayout layoutMonth = this.viewfFinanceStats.findViewById(R.id.layoutfat_year);
        TextView txtYear = this.viewfFinanceStats.findViewById(R.id.txt_fs_ytitle);

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
        layoutMonth.removeAllViews();
        layoutMonth.addView(pieChart);
        this.selectType = pieDataList.get(0).pieString;
        this.selectPieData = pieDataList.get(0);
        txtYear.setText("年共消费RMB：" + String.format("%.1f", pieChart.pieValueSum));
    }

    private void selectYearChange(View txtView, int changeValue) {
        if(txtView.getId() == R.id.txt_fs_yb && beginYear + changeValue <= endYear) {
            beginYear += changeValue;
            flushYearStats();
        }
        if(txtView.getId() == R.id.txt_fs_ye && endYear + changeValue >= beginYear) {
            endYear += changeValue;
            flushYearStats();
        }

    }



    private Context context;
    private LinearLayout layoutFinance;
    private TextView txtBeginYear;
    private TextView txtEndYear;
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

    private static int beginYear = 0;
    private static int endYear = 0;

    private float touchDownX = 0f;
    private float touchUpX = 0f;
    private final int TOUCH_MOVE_MIN_X = 10;
    private View.OnTouchListener txtOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                touchDownX = motionEvent.getX();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                touchUpX = motionEvent.getX();
                if (touchUpX - touchDownX > TOUCH_MOVE_MIN_X) {
                    selectYearChange(v, -1);
                } else if (touchDownX - touchUpX > TOUCH_MOVE_MIN_X) {
                    selectYearChange(v, 1);
                }
            }
            return false;
        }
    };

}
