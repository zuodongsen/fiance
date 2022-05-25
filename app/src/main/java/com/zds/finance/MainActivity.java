package com.zds.finance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zds.finance.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST=10;

    private List<List<Finance>> fianceListArray = new ArrayList<>();
    private List<Finance> listData = new ArrayList<>();
    private List<FinanceAdapter> financeAdapterList = new ArrayList<>();
    private FinanceDateAdapter financeDateAdapter;

    private ListView listView;
    private ListView popListView;
    private TextView textMonth;
    private TextView textTotalAmount;
    private int selectYear;
    private int selectMonth;
    private float tatalAmount;

    final static List<String> popListData = Arrays.asList("删除", "修改");
    static PopupWindow popWin;
    static int selectListViewItemId;
    static int selectFileIndexForInput;
    static List<Integer> selectFileIndexForDelete = new ArrayList<>();
    static List<String> backupFileNameList = new ArrayList<>();

    final static int INVALID_FILE_SELECT_ID = -1;
    final static int INVALID_LIST_VIEW_ITEM_ID = -1;
    private static String FILE_FOLDER;
    private static String BACKUP_FILE_FOLDER;
    private static final String BACKUP_PATH = "amount_backup";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initMenuBar();
        this.initSelectYearMonth();
        this.initListView();
        this.initPopListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_export) {
            List<Finance> allFormDb = Finance.getAllFormDb(MainActivity.this);
            List<String> dataInfos = new ArrayList<>();
            for(Finance it : allFormDb) {
                dataInfos.add(it.toString());
            }
            CSVWriteThread csvWriteThread = new CSVWriteThread(dataInfos, BACKUP_FILE_FOLDER, "amount_" + Finance.getNowDateTime2String() + ".csv");
            csvWriteThread.run();
            return true;
        }else if(id == R.id.menu_inport || id == R.id.menu_deletebackup) {
            File fileDir = new File(BACKUP_FILE_FOLDER);
            File[] files = fileDir.listFiles();
            for(File f : files) {
                if(f.isFile()) {
                    MainActivity.backupFileNameList.add(f.toString().replaceFirst(BACKUP_FILE_FOLDER + File.separator, ""));
                }
            }
            String[] backupFileNameStrArr = new String[MainActivity.backupFileNameList.size()];
            MainActivity.backupFileNameList.toArray(backupFileNameStrArr);
            if(id == R.id.menu_inport) {
                this.showFileListDialog("导入备份文件", backupFileNameStrArr, 0);
            }else {
                this.showFileListDialog("删除备份文件", backupFileNameStrArr, 1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.reflushListViewData();
    }

    private void initMenuBar() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMenu);
        FILE_FOLDER = this.getFilesDir().toString() + File.separator;
        BACKUP_FILE_FOLDER = FILE_FOLDER + BACKUP_PATH;
        this.resetFileListSelect();
    }

    private void initSelectYearMonth() {
        Calendar calendar = Calendar.getInstance();
        this.selectYear = calendar.get(Calendar.YEAR); // 得到当前年
        this.selectMonth = calendar.get(Calendar.MONTH) + 1; // 得到当前月
        this.textMonth = (TextView) findViewById(R.id.text_month);
        this.textTotalAmount = (TextView) findViewById(R.id.text_total_amount);
    }

    private void initListView() {
        this.resetPopListSelect();
        this.listView = (ListView)findViewById(R.id.list_view);
        this.reflushListViewData();
    }

    private void showDeleteAlertDialog(String title){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_delete)
                .setMessage("确定吗")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Finance.deleteFromDb(MainActivity.this, MainActivity.selectListViewItemId);
                        MainActivity.this.reflushListViewData();
                        MainActivity.this.resetPopListSelect();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.resetPopListSelect();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        MainActivity.this.resetPopListSelect();
                    }
                })
                .show();
    }

    private void showFileListDialog(String title, String[] fileList, int mode/* 0 select, 1 delete */){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        if(mode == 0) {
            builder.setSingleChoiceItems(fileList, -1, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    MainActivity.selectFileIndexForInput = arg1;
                }
            });
        }else {
            builder.setMultiChoiceItems(fileList, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if(b) {
                        MainActivity.selectFileIndexForDelete.add(i);
                    }else {
                        MainActivity.selectFileIndexForDelete.remove(new Integer(i));
                    }
                }
            });
        }


        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(MainActivity.selectFileIndexForInput != INVALID_FILE_SELECT_ID) {
                            System.out.println("now to input backup file: " + MainActivity.backupFileNameList.get(MainActivity.selectFileIndexForInput));
                        }else if(!MainActivity.selectFileIndexForDelete.isEmpty()){
                            for(Integer it : MainActivity.selectFileIndexForDelete) {
                                CSVWriteThread.deleteFile(BACKUP_FILE_FOLDER, MainActivity.backupFileNameList.get(it));
                            }
                        }
                        MainActivity.this.resetFileListSelect();
                    }
                });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.resetFileListSelect();
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        MainActivity.this.resetFileListSelect();
                    }
                });
        builder.show();
    }

    private void initPopListView() {
        this.popListView = new ListView(MainActivity.this);
        this.popListView.setDivider(null);
        this.popListView.setVerticalScrollBarEnabled(false);
        this.popListView.setAdapter(new PopListViewAdapter(popListData, MainActivity.this, R.layout.poplistview_item));
        this.popListView.setBackgroundResource(R.drawable.textview_border);

        popWin = new PopupWindow(MainActivity.this);
        popWin.setWidth(200);//设置宽度 和编辑框的宽度相同
        popWin.setHeight(200);
        popWin.setContentView(popListView);
        popWin.setOutsideTouchable(true);

        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(MainActivity.selectListViewItemId == INVALID_LIST_VIEW_ITEM_ID){
                    return;
                }
                if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_INVALID) {
                    MainActivity.selectListViewItemId = INVALID_LIST_VIEW_ITEM_ID;
                    return;
                }
                if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_DEL) {
                    MainActivity.this.showDeleteAlertDialog("删除记账！");
                }else {
                    Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                    startActivityForResult(intent, REQUEST);
                }
            }
        });
    }

    private void resetPopListSelect() {
        MainActivity.selectListViewItemId = INVALID_LIST_VIEW_ITEM_ID;
        PopListViewAdapter.selectCmd = PopListViewAdapter.CMD_INVALID;
    }

    private void resetFileListSelect() {
        MainActivity.selectFileIndexForInput = INVALID_FILE_SELECT_ID;
        MainActivity.selectFileIndexForDelete.clear();
        MainActivity.backupFileNameList.clear();
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
                this.financeAdapterList.add(new FinanceAdapter(financeList, Finance.getMonthDay2String(lastDate), MainActivity.this, R.layout.listview_finance));
                this.fianceListArray.add(new ArrayList<>());
                financeList = this.fianceListArray.get(this.fianceListArray.size() - 1);
            }
            financeList.add(f);
            lastDate = f.date;
        }
        if(!financeList.isEmpty()){
            this.financeAdapterList.add(new FinanceAdapter(financeList, Finance.getMonthDay2String(lastDate), MainActivity.this, R.layout.listview_finance));
        }
    }

    public void reflushListViewData() {
        this.listData = Finance.getOneMonthFormDb(MainActivity.this, this.selectYear, this.selectMonth);
        this.genFinanceDateList();
        this.financeDateAdapter = new FinanceDateAdapter(this.financeAdapterList, MainActivity.this, R.layout.listview_date);
        this.listView.setAdapter(financeDateAdapter);
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            this.resetPopListSelect();
        }
        setListViewHeightBasedOnChildren(this.listView);

        this.textMonth.setText(String.format("%d年%02d月", this.selectYear, this.selectMonth));
        this.textTotalAmount.setText(String.format("%.2f", this.tatalAmount));
    }

    public void btn_create(View view) {
        Button btn = (Button)view;
        int a = btn.getId();
        Intent intent = new Intent(MainActivity.this, CreateActivity.class);
        startActivityForResult(intent, REQUEST);

    }

    public void btn_dateChange(View view) {
        TextView btn = (TextView)view;
        int a = btn.getId();
        if(btn.getId() == R.id.bt_left) {
            if(this.selectMonth == 1) {
                this.selectMonth = 12;
                this.selectYear --;
            }else {
                this.selectMonth --;
            }
        }else {
            if(this.selectMonth == 12) {
                this.selectMonth = 1;
                this.selectYear ++;
            }else {
                this.selectMonth ++;
            }
        }
        this.reflushListViewData();

    }

    // 解决listview 只能显示一条记录的问题
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

}