package com.zds.fat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.DataBaseHelper;
import com.zds.finance.CreateActivity;
import com.zds.finance.FinanceList;
import com.zds.finance.FinanceListPopAdapter;
//import com.zds.fat.PopWinAdapter.PopListViewAdapter;
import com.zds.finance.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FatList {

    public FatList(Context context_) {
        this.context = context_;
    }



    public void setView() {
        this.viewFatList = LayoutInflater.from(this.context).inflate(R.layout.view_fatlist, null);
        LinearLayout linearLayout = ((AppCompatActivity)context).findViewById(R.id.layout_fiance);
        linearLayout.removeAllViews();
        linearLayout.addView(this.viewFatList);
        this.initFatList();
        this.initBtn();
    }

    private void initFatList() {
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
        this.textMonth = (TextView)this.viewFatList.findViewById(R.id.fattext_month);
    }

    private void initListView() {
        DataBaseHelper.initDb(this.context);
        this.resetPopListSelect();
        this.listView = (ListView)this.viewFatList.findViewById(R.id.fatlist_view);
        this.reflushListViewData();
    }

    private void initBtn() {
        Button btn_left = (Button)this.viewFatList.findViewById(R.id.fatbt_left);
        Button btn_right = (Button)this.viewFatList.findViewById(R.id.fatbt_right);
        Button btn_cur = (Button)this.viewFatList.findViewById(R.id.fatbt_showCurMonth);
        View.OnClickListener monthChangeBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FatList.this.btn_dateChange(v);
            }
        };

        View.OnClickListener createBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FatList.this.btn_create(v);
            }
        };

        btn_left.setOnClickListener(monthChangeBtnListener);
        btn_right.setOnClickListener(monthChangeBtnListener);
        btn_cur.setOnClickListener(monthChangeBtnListener);

        Button btn_create = (Button)this.viewFatList.findViewById(R.id.fatbt_create);
        btn_create.setOnClickListener(createBtnListener);
    }


    /* pop list */
    private void initPopListView() {
        this.popListView = new ListView(this.context);
        this.popListView.setDivider(null);
        this.popListView.setVerticalScrollBarEnabled(false);
        this.popListView.setAdapter(new FatListPopAdapter(this.context, R.layout.poplistview_item));
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
                if(FatList.selectListViewItemId == INVALID_LIST_VIEW_ITEM_ID){
                    return;
                }
                if(FatListPopAdapter.selectCmd == FatListPopAdapter.CMD_INVALID) {
                    FatList.selectListViewItemId = INVALID_LIST_VIEW_ITEM_ID;
                    return;
                }
                if(FatListPopAdapter.selectCmd == FatListPopAdapter.CMD_DEL) {
                    FatList.this.showDeleteAlertDialog("删除记录！");
                }else {
                    startActivity(FatCreateActivity.class);
                }
            }
        });
    }

    private void resetPopListSelect() {
        FatList.selectListViewItemId = INVALID_LIST_VIEW_ITEM_ID;
        FatListPopAdapter.selectCmd = FatListPopAdapter.CMD_INVALID;
    }

    private void reflushListViewData() {
        this.listData = Fat.getOneMonthFormDb(this.selectYear, this.selectMonth);
        FatAdapter.setListViewAdapter(this.selectYear, this.selectMonth, this.context, this.listView, R.layout.listview_fat);
        FinanceList.setListViewHeightBasedOnChildren(this.listView);

        this.textMonth.setText(String.format("%d年%02d月", this.selectYear, this.selectMonth));
    }

    public void showDeleteAlertDialog(String title){
        new AlertDialog.Builder(this.context)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_delete)
                .setMessage("确定吗")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Fat.deleteFromDb(FatList.selectListViewItemId);
                        FatList.this.reflushListViewData();
                        FatList.this.resetPopListSelect();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FatList.this.resetPopListSelect();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        FatList.this.resetPopListSelect();
                    }
                })
                .show();
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
        if(btn.getId() == R.id.fatbt_left) {
            modifyListViewShowMonth(-1);
        }else if(btn.getId() == R.id.fatbt_right) {
            modifyListViewShowMonth(1);
        }else {
            this.initSelectYearMonth();
        }
        this.reflushListViewData();
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this.context, c);
        ((AppCompatActivity)context).startActivityForResult(intent, REQUEST);
    }

    public void btn_create(View view) {
        startActivity(FatCreateActivity.class);
    }

    private int selectYear;
    private int selectMonth;
    private List<List<Fat>> fatListArray = new ArrayList<>();
    private List<Fat> listData = new ArrayList<>();
    private List<FatAdapter> fatAdapterList = new ArrayList<>();
    private FatAdapter fatAdapter;

    private Context context;
    private View viewFatList;
    private ListView listView;
    private ListView popListView;
    private TextView textMonth;
    private TextView textTotalAmount;

    static int selectListViewItemId;

    static PopupWindow popWin;
    final static int REQUEST=10;
    final static int INVALID_LIST_VIEW_ITEM_ID = -1;

}
