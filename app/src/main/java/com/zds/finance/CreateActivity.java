package com.zds.finance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {
    private TextView txtDate;
    private TextView txtInfo;
    private TextView txtAmount;
    private Spinner spnType;
    public static final String[] amountType = new String[] {"买菜", "话费", "保险", "医疗",
                                                            "交通", "养车", "服装", "长辈",
                                                            "居家", "养娃", "书籍", "其他"};

    Calendar calendar = null;
    int year = 0; // 得到当前年
    int month = 0; // 得到当前月
    int day = 0; // 得到当前日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        this.txtDate = (TextView)findViewById(R.id.textedit_date);
        this.txtInfo = (TextView)findViewById(R.id.textedit_info);
        this.txtAmount = (TextView)findViewById(R.id.textedit_amount);
        this.calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR); // 得到当前年
        this.month = calendar.get(Calendar.MONTH) + 1; // 得到当前月
        this.day = calendar.get(Calendar.DAY_OF_MONTH); // 得到当前日
        initSpinnerType();
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            Finance finance = Finance.getOneFormDb(CreateActivity.this, MainActivity.selectListViewItemId);
            this.txtDate.setText(finance.getDate2String());
            this.txtInfo.setText(finance.info);
            this.txtAmount.setText(String.valueOf(finance.amount));
            System.out.println("finance.type" + finance.type);
            this.spnType.setSelection(finance.type);
        }else {
            this.txtDate.setText(String.format("%d年%02d月%02d日", year, month, day));
        }
    }

    private void initSpinnerType() {
        this.spnType = (Spinner)findViewById(R.id.spn_type);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, amountType);
        this.spnType.setAdapter(typeAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void btn_select_date(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR); // 得到当前年
        int month = calendar.get(Calendar.MONTH); // 得到当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH); // 得到当前日
        new DatePickerDialog(CreateActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDate.setText(String.format("%d年%02d月%02d日", year, month + 1, dayOfMonth));
            }

        },year,month,day).show();

    }

    public void btn_save(View view) {
        System.out.println(this.spnType.getSelectedItemPosition());
        Finance finance = new Finance(
                this.txtInfo.getText().toString(),
                this.txtDate.getText().toString(),
                Float.parseFloat(this.txtAmount.getText().toString()),
                this.spnType.getSelectedItemPosition());
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            Finance.updateToDb(CreateActivity.this, MainActivity.selectListViewItemId, finance);
        }else {
            Finance.insertToDb(CreateActivity.this, finance);
        }
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void btn_cancel(View view) {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

}
