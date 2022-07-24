package com.zds.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {
    private TextView txtDate;
    private TextView txtInfo;
    private TextView txtAmount;
    private Spinner spnType;
    public static final String[] amountType = new String[] {"买菜", "话费", "保险", "医疗",
                                                            "交通", "养车", "服装", "长辈",
                                                            "居家", "养娃", "书籍", "电子",
                                                            "其他"};

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
        initSpinnerType();
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            Finance finance = Finance.getOneFormDb(MainActivity.selectListViewItemId);
            this.txtDate.setText(finance.getDate2String());
            this.txtInfo.setText(finance.info);
            this.txtAmount.setText(String.valueOf(finance.amount));
            this.spnType.setSelection(finance.type);
            this.calendar.setTime(Finance.getDate(finance.date));
        }
        this.year = calendar.get(Calendar.YEAR); // 得到当前年
        this.month = calendar.get(Calendar.MONTH) + 1; // 得到当前月
        this.day = calendar.get(Calendar.DAY_OF_MONTH); // 得到当前日
        this.txtDate.setText(String.format("%d年%02d月%02d日", year, month, day));
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

    @SuppressLint("ResourceType")
    public void btn_select_date(View view) {
        new DatePickerDialog(CreateActivity.this, 3, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_, int month_, int dayOfMonth) {
                txtDate.setText(String.format("%d年%02d月%02d日", year_, month_ + 1, dayOfMonth));
                year = year_; month = month_ + 1; day = dayOfMonth;
            }

        }, this.year, this.month - 1, this.day).show();

    }

    public void btn_save(View view) {
        float amount_ = 0;
        try {
            amount_ = Float.parseFloat(this.txtAmount.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.txtAmount.setTextColor(getResources().getColor(R.color.red));
            return;
        }
        Finance finance = new Finance(
                this.txtInfo.getText().toString(),
                this.txtDate.getText().toString(),
                amount_,
                this.spnType.getSelectedItemPosition());
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            Finance.updateToDb(MainActivity.selectListViewItemId, finance);
        }else {
            Finance.insertToDb(finance);
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
