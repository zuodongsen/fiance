package com.zds.finance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.DataBaseHelper;
import com.zds.common.DateTimeTrans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinanceList {
    public FinanceList(Context context_) {
        this.context = context_;
    }

    public void setFianceListView() {
        this.viewFinanceList = LayoutInflater.from(this.context).inflate(R.layout.view_fiancelist, null);
        LinearLayout linearLayout = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        linearLayout.removeAllViews();
        linearLayout.addView(this.viewFinanceList);
        this.initFinanceList();
        this.initBtn();
    }

    private void initFinanceList() {
        this.initSelectYearMonth();
        this.initTextView();
        this.initListView();
        this.initPopListView();
    }

    private void initSelectYearMonth() {
        Calendar calendar = Calendar.getInstance();
        this.selectYear = calendar.get(Calendar.YEAR); // 得到当前年
        this.selectMonth = calendar.get(Calendar.MONTH) + 1; // 得到当前月
    }

    private void initTextView() {
        this.textMonth = (TextView)this.viewFinanceList.findViewById(R.id.text_month);
        this.textTotalAmount = (TextView)this.viewFinanceList.findViewById(R.id.text_total_amount);
    }

    private void initListView() {
        DataBaseHelper.initDb(this.context);
        this.resetPopListSelect();
        this.listView = (ListView)this.viewFinanceList.findViewById(R.id.list_view);
        this.reflushListViewData();
    }

    private void initBtn() {
        Button btn_left = (Button)this.viewFinanceList.findViewById(R.id.bt_left);
        Button btn_right = (Button)this.viewFinanceList.findViewById(R.id.bt_right);
        Button btn_cur = (Button)this.viewFinanceList.findViewById(R.id.bt_showCurMonth);
        View.OnClickListener monthChangeBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinanceList.this.btn_dateChange(v);
            }
        };

        View.OnClickListener createBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinanceList.this.btn_create(v);
            }
        };

        btn_left.setOnClickListener(monthChangeBtnListener);
        btn_right.setOnClickListener(monthChangeBtnListener);
        btn_cur.setOnClickListener(monthChangeBtnListener);

        Button btn_create = (Button)this.viewFinanceList.findViewById(R.id.bt_create);
        btn_create.setOnClickListener(createBtnListener);
    }


    /* pop list */
    private void initPopListView() {
        this.popListView = new ListView(this.context);
        this.popListView.setDivider(null);
        this.popListView.setVerticalScrollBarEnabled(false);
        this.popListView.setAdapter(new FinanceListPopAdapter(this.context, R.layout.poplistview_item));
        this.popListView.setBackgroundResource(R.drawable.textview_border);

        popWin = new PopupWindow(this.context);
        popWin.setWidth(300);//设置宽度 和编辑框的宽度相同
        popWin.setHeight(180);
        popWin.setContentView(popListView);
        popWin.setOutsideTouchable(true);

        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("onDismiss");
                if(FinanceList.selectListViewFinanceId == INVALID_LIST_VIEW_ITEM_ID){
                    return;
                }
                if(FinanceListPopAdapter.selectCmd == PopListViewAdapter.CMD_INVALID) {
                    FinanceList.selectListViewFinanceId = INVALID_LIST_VIEW_ITEM_ID;
                    return;
                }
                if(FinanceListPopAdapter.selectCmd == PopListViewAdapter.CMD_DEL) {
                    FinanceList.this.showDeleteAlertDialog("删除记账！");
                }else {
                    startActivity(CreateActivity.class);
                }
            }
        });
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this.context, c);
        ((AppCompatActivity)context).startActivityForResult(intent, REQUEST);
    }

    public void showDeleteAlertDialog(String title){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_delete)
                .setMessage("确定吗")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("是");
                        Finance.deleteFromDb(FinanceList.selectListViewFinanceId);

                        FinanceList.this.reflushListViewData();
                        FinanceList.this.resetPopListSelect();

                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FinanceList.this.resetPopListSelect();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        FinanceList.this.resetPopListSelect();
                    }
                })
                .show();
    }

    private void reflushListViewData() {
        this.listData = Finance.getOneMonthFormDb(this.selectYear, this.selectMonth);
        this.genFinanceDateList();
        this.financeDateAdapter = new FinanceDateAdapter(this.financeAdapterList, this.context, R.layout.listview_date);
        this.listView.setAdapter(financeDateAdapter);
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            this.resetPopListSelect();
        }
        setListViewHeightBasedOnChildren(this.listView);

        this.textMonth.setText(String.format("%d年%02d月", this.selectYear, this.selectMonth));
        this.textTotalAmount.setText(String.format("%.2f", this.tatalAmount));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void genFinanceDateList() {
        this.financeAdapterList.clear();
        this.fianceListArray.clear();
        this.fianceListArray.add(new ArrayList<>());
        List<Finance> financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
        long lastDate = -1;
        this.tatalAmount = 0;
        for (Finance f : this.listData) {
            this.tatalAmount += f.amount;
            if(lastDate == f.date) {
                financeList.add(f);
                continue;
            }
            if(!financeList.isEmpty()){
                this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getMonthDay2String(lastDate), this.context, R.layout.listview_finance));
                this.fianceListArray.add(new ArrayList<>());
                financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
            }
            financeList.add(f);
            lastDate = f.date;
        }
        if(!financeList.isEmpty()){
            this.financeAdapterList.add(new FinanceAdapter(financeList, DateTimeTrans.getMonthDay2String(lastDate), this.context, R.layout.listview_finance));
        }
    }

    private void resetPopListSelect() {
        FinanceList.selectListViewFinanceId = INVALID_LIST_VIEW_ITEM_ID;
        FinanceListPopAdapter.selectCmd = FinanceListPopAdapter.CMD_INVALID;
    }


    private void modifyListViewShowMonth(int monthAdd) {
        this.selectMonth += monthAdd;
        while(this.selectMonth <= 0) {
            this.selectMonth += 12;
            this.selectYear --;
        }
        if(this.selectMonth == 12) return;
        this.selectYear += (this.selectMonth / 12);
        this.selectMonth = (this.selectMonth % 12);
    }

    public void btn_dateChange(View view) {
        System.out.println("btn_dateChange");
        TextView btn = (TextView)view;
        int a = btn.getId();
        if(btn.getId() == R.id.bt_left) {
            modifyListViewShowMonth(-1);
        }else if(btn.getId() == R.id.bt_right) {
            modifyListViewShowMonth(1);
        }else {
            this.initSelectYearMonth();
        }
        this.reflushListViewData();
    }

    public void btn_create(View view) {
        startActivity(CreateActivity.class);
//        if(this.CRU_SELECT_ACTIVITY == CRU_SELECT_ACTIVITY_FRANCE) {
//            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
//            startActivityForResult(intent, REQUEST);
//        }else {
//            Intent intent = new Intent(MainActivity.this, FatCreateActivity.class);
//            startActivityForResult(intent, REQUEST);
//        }

    }

    private int selectYear;
    private int selectMonth;
    private float tatalAmount;
    private List<List<Finance>> fianceListArray = new ArrayList<>();
    private List<Finance> listData = new ArrayList<>();
    private List<FinanceAdapter> financeAdapterList = new ArrayList<>();
    private FinanceDateAdapter financeDateAdapter;
    private Context context;
    private View viewFinanceList;

    private ListView listView;
    private ListView popListView;
    private TextView textMonth;
    private TextView textTotalAmount;

    static PopupWindow popWin;
    static int selectListViewFinanceId;

    final static int REQUEST=10;
    final static int INVALID_LIST_VIEW_ITEM_ID = -1;
}
