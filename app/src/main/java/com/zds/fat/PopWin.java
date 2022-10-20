package com.zds.fat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zds.finance.CreateActivity;
import com.zds.finance.Finance;
import com.zds.finance.MainActivity;
import com.zds.finance.PopListViewAdapter;
import com.zds.finance.R;

import java.util.Arrays;
import java.util.List;

public class PopWin {
    public static AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            PopWin.selectListViewId = Integer.parseInt(((TextView) view.findViewById(R.id.textfat_id)).getText().toString());
            PopWin.popWin.showAsDropDown(view, 300, -10);
            return false;
        }
    };


    /* pop list */
    public static void initPopListView(Context context) {
        popListView = new ListView(context);
        popListView.setDivider(null);
        popListView.setVerticalScrollBarEnabled(false);
        popListView.setAdapter(new PopWinAdapter(popListData, context, R.layout.poplistview_item));
        popListView.setBackgroundResource(R.drawable.textview_border);

        popWin = new PopupWindow(context);
        popWin.setWidth(300);//设置宽度 和编辑框的宽度相同
        popWin.setHeight(180);
        popWin.setContentView(popListView);
        popWin.setOutsideTouchable(true);

        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(selectListViewId == INVALID_LIST_VIEW_ITEM_ID){
                    return;
                }
                if(PopWinAdapter.selectCmd == PopWinAdapter.CMD_INVALID) {
                    selectListViewId = INVALID_LIST_VIEW_ITEM_ID;
                    return;
                }
                if(PopWinAdapter.selectCmd == PopWinAdapter.CMD_DEL) {
                    ((com.zds.finance.MainActivity)context).showDeleteAlertDialog("删除记账！");
                }else {
                    ((com.zds.finance.MainActivity)context).startActivity(FatCreateActivity.class);
                }
            }
        });
    }

    public static void resetPopListSelect() {
        selectListViewId = INVALID_LIST_VIEW_ITEM_ID;
        PopWinAdapter.selectCmd = PopWinAdapter.CMD_INVALID;
    }


    final static int REQUEST=10;
    static int txtIdViewId;
    public static int selectListViewId;
    static PopupWindow popWin;
    private static ListView popListView;
    final static List<String> popListData = Arrays.asList("删除", "修改");
    final static int INVALID_LIST_VIEW_ITEM_ID = -1;
}
