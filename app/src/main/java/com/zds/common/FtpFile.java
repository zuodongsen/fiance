package com.zds.common;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpFile extends Thread {
    private FTPClient ftpClient = null;
    private String hostIp;
    private int port;
    private String usrName;
    private String password;
    private String root;
    private String localFilePath;
    private String localFileName;
    private String remoteFileName;
    private int type;
    public static final int FTP_TYPE_UPLOAD = 0;
    public static final int FTP_TYPE_LIST = 1;
    public static final int FTP_TYPE_DOWNLOAD = 2;

    public FtpFile(String hostIp_, int port_, String usrName_, String password_, String root_) {
        this.hostIp = hostIp_;
        this.port = port_;
        this.usrName = usrName_;
        this.password = password_;
        this.root = root_;
    }

//    public static String getHostIp() {
//        return FtpFile.hostIp;
//    }

//    public static void setHostIp(String hostIp_) {
//        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
//        String msgInfo = "设置ftp服务器地址：" + hostIp_ + " ";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(hostIp_);
//        if(matcher.matches()) {
//            HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_SET_HOST_IP_SUCCESS, msgInfo + "成功");
//            FtpFile.hostIp = hostIp_;
//        }else {
//            HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_SET_HOST_IP_FAIL, msgInfo + "失败");
//        }
//    }

    public void doTypeList() {
        this.type = FTP_TYPE_LIST;
        this.start();
    }

    public void doTypeDownOrUpload(String localFilePath_, String fileName_, int type_) {
        this.type = type_;
        this.localFilePath = localFilePath_;
        this.localFileName = fileName_;
        this.remoteFileName = fileName_;
        this.start();
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
        ftpClient.storeFile(this.root + this.localFileName, srcFileStream);
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_UPLOAD_PROGRESS, "上传 " + this.localFileName + " 文件完成");
    }

    private String[] list() throws IOException {
        return ftpClient.listNames(this.root);
    }

    private void download() throws IOException {
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_DOWNLOAD_PROGRESS, "开始下载..." + this.localFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(this.localFilePath + this.localFileName);
        ftpClient.retrieveFile(this.root + this.localFileName, fileOutputStream);
        HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_DOWNLOAD_PROGRESS, "下载 " + this.localFileName + " 文件完成");
    }

    @Override
    public void run() {
        try {
            this.connnectFtp();
        } catch (Exception e) {
            HandlerMsgId.sendMsg(HandlerMsgId.FTP_FILE_CONNECT_FAIL, "连接 " + hostIp + " 失败");
            return;
        }

        try {
            if(this.type == FTP_TYPE_UPLOAD) {
                this.upload();
            }else if(this.type == FTP_TYPE_LIST){
                String[] fiileList = this.list();
                String msg = new String();
                for (String it : fiileList) {
                    msg += it.replace(this.root, "");;
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