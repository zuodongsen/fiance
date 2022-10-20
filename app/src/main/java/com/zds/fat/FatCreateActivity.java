package com.zds.fat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.DateTimeTrans;
import com.zds.finance.CreateActivity;
import com.zds.finance.Finance;
import com.zds.finance.MainActivity;
import com.zds.finance.PopListViewAdapter;
import com.zds.finance.R;

import java.util.Calendar;

public class FatCreateActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fat_create);
        this.txtDate = (TextView)findViewById(R.id.texteditcreatefat_date);
        this.txtMorning = (TextView)findViewById(R.id.texteditcreatefat_morning);
        this.txtNoon = (TextView)findViewById(R.id.texteditcreatefat_noon);
        this.txtNight = (TextView)findViewById(R.id.texteditcreatefat_night);
        this.txtRope = (TextView)findViewById(R.id.texteditcreatefat_rope);
        this.txtCircle = (TextView)findViewById(R.id.texteditcreatefat_cricle);
        if(PopWinAdapter.selectCmd == PopWinAdapter.CMD_MODIFY) {
            Fat fat = Fat.getOneFormDb(PopWin.selectListViewId);
            this.txtDate.setText(DateTimeTrans.getDate2String(fat.date));
            this.txtMorning.setText(String.valueOf(fat.morningWeight));
            this.txtNoon.setText(String.valueOf(fat.noonWeight));
            this.txtNight.setText(String.valueOf(fat.nightWeight));
            this.txtRope.setText(String.valueOf(fat.ropeNum));
            this.txtCircle.setText(String.valueOf(fat.runningCircleNum));
        }else {
            this.calendar = Calendar.getInstance();
            this.year = calendar.get(Calendar.YEAR); // 得到当前年
            this.month = calendar.get(Calendar.MONTH) + 1; // 得到当前月
            this.day = calendar.get(Calendar.DAY_OF_MONTH); // 得到当前日
            this.txtDate.setText(String.format("%d年%02d月%02d日", year, month, day));
        }

    }

    private float string2Float(String srt) throws NumberFormatException{
        if(srt.isEmpty()) {
            return 0;
        }
        return Float.parseFloat(srt);
    }

    private int string2Int(String srt) throws NumberFormatException{
        if(srt.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(srt);
    }

    @SuppressLint("ResourceType")
    public void btnfat_select_date(View view) {
        new DatePickerDialog(FatCreateActivity.this, 3, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_, int month_, int dayOfMonth) {
                txtDate.setText(String.format("%d年%02d月%02d日", year_, month_ + 1, dayOfMonth));
                year = year_; month = month_ + 1; day = dayOfMonth;
            }

        }, this.year, this.month - 1, this.day).show();

    }


    public void btnfat_save(View view) {
        String strDate = this.txtDate.getText().toString(),
            strMorning = this.txtMorning.getText().toString(),
            strNoon = this.txtNoon.getText().toString(),
            strNight = this.txtNight.getText().toString(),
            strRope = this.txtRope.getText().toString(),
            strCircle = this.txtCircle.getText().toString();

        float morning = 0, noon = 0, night = 0;
        int rope = 0, cricle = 0;
        try {
            morning = string2Float(strMorning);
            noon = string2Float(strNoon);
            night = string2Float(strNight);
            rope = string2Int(strRope);
            cricle = string2Int(strCircle);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Fat fat = new Fat(strDate, morning, noon, night, rope, cricle);
        if(PopWinAdapter.selectCmd == PopWinAdapter.CMD_MODIFY) {
            Fat.updateToDb(PopWin.selectListViewId, fat);
        }else {
            Fat.insertToDb(fat);
        }
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void btnfat_cancel(View view) {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    private TextView txtDate;
    private TextView txtMorning;
    private TextView txtNoon;
    private TextView txtNight;
    private TextView txtRope;
    private TextView txtCircle;
    private Calendar calendar = null;
    int year = 0; // 得到当前年
    int month = 0; // 得到当前月
    int day = 0; // 得到当前日
}
