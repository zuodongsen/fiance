package com.zds.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVWriteThread extends Thread{

    List<String> data;
    String fileName;
    String folder;


    public CSVWriteThread(List<String> data_, String folder_, String fileName_) {
        System.out.println(folder_);
        System.out.println(fileName_);
        this.data = data_;
        this.folder = folder_;
        this.fileName = fileName_;
    }

    private void createFolder() {
        File fileDir = new File(folder);
        boolean hasDir = fileDir.exists();
        if (!hasDir) {
            System.out.println("create dirc: " + folder);
            fileDir.mkdirs();// 这里创建的是目录
        }
    }

    public static void deleteFile(String folder_, String fileName_) {
        File file = new File(folder_ + File.separator + fileName_);
        if (file.exists()) {
            System.out.println("delete: " + folder_ + File.separator + fileName_);
            file.delete();
        }
    }

    @Override

    public void run() {
        super.run();
        createFolder();
        File file = new File(folder + File.separator + fileName);
        if (!file.exists()) {
            System.out.println("file not exist");
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                System.out.println("file create fail");
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream os = new FileOutputStream(file, true);
            for(String it : this.data) {
                System.out.println(it);
                os.write(it.getBytes(StandardCharsets.UTF_8));
            }

            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

