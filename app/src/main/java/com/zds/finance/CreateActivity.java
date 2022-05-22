package com.zds.finance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {
    private TextView txtDate;
    private TextView txtInfo;
    private TextView txtAmount;
    private TextView txtType;

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
        this.txtType = (TextView)findViewById(R.id.textedit_type);
        this.calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR); // 得到当前年
        this.month = calendar.get(Calendar.MONTH) + 1; // 得到当前月
        this.day = calendar.get(Calendar.DAY_OF_MONTH); // 得到当前日
        if(PopListViewAdapter.selectCmd == PopListViewAdapter.CMD_MODIFY) {
            Finance finance = Finance.getOneFormDb(CreateActivity.this, MainActivity.selectListViewItemId);
            this.txtDate.setText(finance.getDate2String());
            this.txtInfo.setText(finance.info);
            this.txtAmount.setText(String.valueOf(finance.amount));
            this.txtType.setText(String.valueOf(finance.type));
        }else {
            this.txtDate.setText(String.format("%d年%02d月%02d日", year, month, day));
        }

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
        Finance finance = new Finance(
                this.txtInfo.getText().toString(),
                this.txtDate.getText().toString(),
                Float.parseFloat(this.txtAmount.getText().toString()));
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
