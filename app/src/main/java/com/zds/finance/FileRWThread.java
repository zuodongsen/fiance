package com.zds.finance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileRWThread extends Thread{
    int typeRW;
    String fileName;
    String folder;
    List<String> data;

    public static final int FILE_RW_TYPE_READ = 0;
    public static final int FILE_RW_TYPE_WRITE = 1;

    public interface FileReadCallBack {
        void run(List<String> data_) ;
    }

    public FileReadCallBack fileReadCallBack;

    public FileRWThread(List<String> data_, String folder_, String fileName_, int typeRW_) {
        System.out.println(folder_);
        System.out.println(fileName_);
        this.typeRW = typeRW_;
        this.data = data_;
        this.folder = folder_;
        this.fileName = fileName_;
        this.fileReadCallBack = null;
    }

    public FileRWThread(String folder_, String fileName_, int typeRW_) {
        System.out.println(folder_);
        System.out.println(fileName_);
        this.typeRW = typeRW_;
        this.data = new ArrayList<>();
        this.folder = folder_;
        this.fileName = fileName_;
        this.fileReadCallBack = null;
    }

    public void setFileReadCallBack(FileReadCallBack fileReadCallBack_) {
        this.fileReadCallBack = fileReadCallBack_;
    }

    public static void deleteFile(String folder_, String fileName_) {
        File file = new File(folder_ + File.separator + fileName_);
        if (file.exists()) {
            System.out.println("delete: " + folder_ + File.separator + fileName_);
            file.delete();
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_DELETE_PROGRESS, "删除文件 " + fileName_ + " 完成.");
        }
    }

    public static void readFile(List<String> data_, String folder_, String fileName_) {
        HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_READ_PROGRESS, "开始导入...");
        File file = new File(folder_ + File.separator + fileName_);
        if (!file.exists()) {
            System.out.println("file to read not exist!");
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_READ_PROGRESS, "导入文件 " + fileName_ + " 不存在!");
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                data_.add(line);
                line = br.readLine();
            }
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_READ_PROGRESS, "导入文件 " + fileName_ + " 成功.");
        } catch (IOException e) {
            e.printStackTrace();
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_READ_PROGRESS, "导入文件失败 " + e.toString());
        }
    }

    public static void writeFile(List<String> data_, String folder_, String fileName_) {
        HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_WRITE_PROGRESS, "开始导出...");
        File file = new File(folder_ + File.separator + fileName_);
        if (!file.exists()) {
            System.out.println("file not exist");
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                System.out.println("file create fail");
                e.printStackTrace();
                HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_WRITE_PROGRESS, "导出文件失败 " + e.toString());
                return;
            }
        }
        try {
            FileOutputStream os = new FileOutputStream(file, true);
            for(String it : data_) {
                os.write(it.getBytes());
                os.write("\r\n".getBytes());
            }
            os.flush();
            os.close();
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_WRITE_PROGRESS, "导出文件 " + fileName_ + " 成功.");
        } catch (Exception e) {
            e.printStackTrace();
            HandlerMsgId.sendMsg(HandlerMsgId.RW_FILE_WRITE_PROGRESS, "导出文件失败 " + e.toString());
        }
    }

    @Override

    public void run() {
        super.run();
        if(this.typeRW == FILE_RW_TYPE_READ) {
            readFile(this.data, this.folder, this.fileName);
            if(this.fileReadCallBack != null) {
                this.fileReadCallBack.run(this.data);
            }
        }else if(this.typeRW == FILE_RW_TYPE_WRITE) {
            writeFile(this.data, this.folder, this.fileName);
        }
    }
}

