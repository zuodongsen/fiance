package com.zds.finance;
import android.os.Message;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

class FtpFile extends Thread {
    private FTPClient ftpClient = null;
    private String hostIp;
    private int port;
    private String usrName;
    private String password;
    private String localFilePath;
    private String localFileName;
    private String remoteFileName;
    private int type;
    public static final int FTP_TYPE_UPLOAD = 0;
    public static final int FTP_TYPE_LIST = 1;
    public static final int FTP_TYPE_DOWNLOAD = 2;

    public FtpFile(String hostIp_, int port_, String usrName_, String password_, String localFilePath_, String fileName_, int type_) {
        this.hostIp = hostIp_;
        this.port = port_;
        this.usrName = usrName_;
        this.password = password_;
        this.localFilePath = localFilePath_;
        this.localFileName = fileName_;
        this.remoteFileName = fileName_;
        this.type = type_;
    }

    public FtpFile(String hostIp_, int port_, String usrName_, String password_) {
        this.hostIp = hostIp_;
        this.port = port_;
        this.usrName = usrName_;
        this.password = password_;
        this.type = FTP_TYPE_LIST;
    }

    private boolean isFtpClientReplyOk() throws IOException {
        int reply = ftpClient.getReply();
        if(!FTPReply.isNegativePermanent(reply)) {
            return false;
        }
        return true;
    }

    private void connnectFtp() throws IOException, SocketException {
        ftpClient = new FTPClient();
        ftpClient.connect(hostIp, port);
        ftpClient.login(usrName, password);
        FTPClientConfig config = new FTPClientConfig(this.ftpClient.getSystemType().split(" ")[0]);
        config.setServerLanguageCode("zh");
        ftpClient.configure(config);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    private void closeFtp() {
        if(ftpClient == null) {
            return;
        }
        try {
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void upload() throws IOException {
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_UPLOAD_PROGRESS, "开始上传...");
        FileInputStream srcFileStream = new FileInputStream(this.localFilePath + this.localFileName);
        ftpClient.storeFile("/" + this.localFileName, srcFileStream);
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_UPLOAD_PROGRESS, "上传 " + this.localFileName + " 文件完成");
    }

    private String[] list() throws IOException {
        return ftpClient.listNames();
    }

    private void download() throws IOException {
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_DOWNLOAD_PROGRESS, "开始下载...");
        FileOutputStream fileOutputStream = new FileOutputStream(this.localFilePath + this.localFileName);
        ftpClient.retrieveFile("/" + this.localFileName, fileOutputStream);
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_DOWNLOAD_PROGRESS, "下载 " + this.localFileName + " 文件完成");
    }

    @Override
    public void run() {
        try {
            this.connnectFtp();
            if(this.type == FTP_TYPE_UPLOAD) {
                this.upload();
            }else if(this.type == FTP_TYPE_LIST){
                String[] fiileList = this.list();
                String msg = new String();
                for (String it : fiileList) {
                    msg += it;
                    msg += ",";
                }
                HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_LIST_RSP, msg);
            }else if(this.type == FTP_TYPE_DOWNLOAD) {
                this.download();
            }
            closeFtp();
        } catch (SocketException e) {
            e.printStackTrace();
            closeFtp();
        } catch (IOException e) {
            e.printStackTrace();
            closeFtp();
        }
    }

}