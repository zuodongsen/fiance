package com.zds.finance;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.SocketException;

class FtpFileUpload extends Thread {
    private FTPClient ftpClient = null;
    private String hostIp;
    private int port;
    private String usrName;
    private String password;
    private String localFilePath;

    public FtpFileUpload(String hostIp_, int port_, String usrName_, String password_, String localFilePath_) {
        this.hostIp = hostIp_;
        this.port = port_;
        this.usrName = usrName_;
        this.password = password_;
        this.localFilePath = localFilePath_;
    }

    private boolean isFtpClientReplyOk() throws IOException {
        int reply = ftpClient.getReply();
        if(!FTPReply.isNegativePermanent(reply)) {
            return false;
        }
        return true;
    }

    private void connnectFtp() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(hostIp, port);
        if(!isFtpClientReplyOk()) {
            ftpClient.disconnect();
            throw new IOException("connect fail.");
        }
        ftpClient.login(usrName, password);
        if(!isFtpClientReplyOk()) {
            ftpClient.disconnect();
            throw new IOException("login fail.");
        }
        FTPClientConfig config = new FTPClientConfig(this.ftpClient.getSystemType().split(" ")[0]);
        config.setServerLanguageCode("zh");
        ftpClient.configure(config);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    private void closeFtp() throws IOException {
        if(ftpClient == null) {
            return;
        }
        ftpClient.logout();
        ftpClient.disconnect();
    }

    public void upload() throws Exception {
//        remoteFolderPath = PathToolkit.formatPath5FTP(remoteFolderPath);
//        try {
//            ftpClient.changeWorkingDirectory(remoteFolderPath);
//            if (!localfilePath.exists())
//                throw new Exception("the upload FTP file"
//                        + localfile.getPath() + "not exist!");
//            if (!localfile.isFile())
//                throw new Exception("the upload FTP file"
//                        + localfile.getPath() + "is a folder!");
//            if (listener != null)
//                client.upload(localfile, listener);
//            else
//                client.upload(localfile);
//            client.changeDirectory("/");
//        } catch (Exception e) {
//            throw new Exception(e);
//        }
    }

    @Override
    public void run() {

    }

}