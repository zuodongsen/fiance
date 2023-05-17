package com.zds.finance;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zds.common.DataBaseHelper;
import com.zds.common.HandlerMsgId;

import java.util.ArrayList;
import java.util.List;

public class FtpInfo extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FtpInfo.init(this);
        setContentView(R.layout.view_ftpinfo);

        this.editTextHostIp = (EditText) findViewById(R.id.etxt_ftpinfo_host);
        this.editTextPort = (EditText) findViewById(R.id.etxt_ftpinfo_port);
        this.editTextUsrName = (EditText) findViewById(R.id.etxt_ftpinfo_usr);
        this.editTextPasswd = (EditText) findViewById(R.id.etxt_ftpinfo_passwd);
        this.editTextPrefix = (EditText) findViewById(R.id.etxt_ftpinfo_prefix);

        this.editTextHostIp.setText(FtpInfo.serverIp);
        this.editTextPort.setText(String.valueOf(FtpInfo.serverPort));
        this.editTextUsrName.setText(FtpInfo.usrName);
        this.editTextPasswd.setText(FtpInfo.passwd);
        this.editTextPrefix.setText(FtpInfo.prefix);
    }

    public static void init(Context context) {
        DataBaseHelper.initDb(context);
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + insertColName + " from ftpinfo", null);
        if(cursor == null) {
            return;
        }
        while(cursor.moveToNext()) {
            FtpInfo.serverIp = cursor.getString(0);
            FtpInfo.serverPort = cursor.getInt(1);
            FtpInfo.usrName = cursor.getString(2);
            FtpInfo.passwd = cursor.getString(3);
            FtpInfo.prefix = cursor.getString(4);
            break;
        }
        cursor.close();
    }

    public static void update(String serverIp_, String serverPort_, String useName_, String passwd_, String prefix_) {
        FtpInfo.serverIp = serverIp_;
        FtpInfo.serverPort = Integer.parseInt(serverPort_);
        FtpInfo.usrName = useName_;
        FtpInfo.passwd = passwd_;
        FtpInfo.prefix = prefix_;

        DataBaseHelper.dbExecSQL("update ftpinfo set " + updateColName ,
                new Object[]{FtpInfo.serverIp, FtpInfo.serverPort, FtpInfo.usrName, FtpInfo.passwd, FtpInfo.prefix});
        String msgInfo = "设置ftp服务器：" + FtpInfo.serverIp + ":" + FtpInfo.serverPort + FtpInfo.prefix + " " + FtpInfo.usrName + "/" + FtpInfo.passwd + " ";
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_SET_HOST_IP_SUCCESS, msgInfo + "成功");
    }

    public void btn_ftpInfo_save(View view) {
        FtpInfo.update(this.editTextHostIp.getText().toString(), this.editTextPort.getText().toString(),
                this.editTextUsrName.getText().toString(), this.editTextPasswd.getText().toString(),
                this.editTextPrefix.getText().toString());
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void btn_ftpInfo_cancel(View view) {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public static String serverIp;
    public static int serverPort;
    public static String usrName;
    public static String passwd;
    public static String prefix;

    private EditText editTextHostIp;
    private EditText editTextPort;
    private EditText editTextUsrName;
    private EditText editTextPasswd;
    private EditText editTextPrefix;
    private final static String insertColName= "ip, port, usr, passwd, prefix";
    private final static String updateColName= insertColName.replace(",", " = ?,") + " = ?";

}
