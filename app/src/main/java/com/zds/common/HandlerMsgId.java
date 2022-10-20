package com.zds.common;

import android.os.Message;

import com.zds.finance.MainActivity;

public class HandlerMsgId {
    public static final int FTP_FILE_LIST_RSP = 0x001;
    public static final int FTP_FILE_UPLOAD_PROGRESS = 0x002;
    public static final int FTP_FILE_DOWNLOAD_PROGRESS = 0x003;
    public static final int FTP_FILE_SET_HOST_IP_SUCCESS = 0x007;
    public static final int FTP_FILE_SET_HOST_IP_FAIL = 0x008;
    public static final int FTP_FILE_CONNECT_FAIL = 0x009;

    public static final int RW_FILE_READ_PROGRESS = 0x004;
    public static final int RW_FILE_WRITE_PROGRESS = 0x005;
    public static final int RW_FILE_DELETE_PROGRESS = 0x006;


    public static void sendMsg(int msgId, String msg) {
        Message message = MainActivity.handler
                .obtainMessage(msgId, msg);
        MainActivity.handler.sendMessage(message);
    }
}
