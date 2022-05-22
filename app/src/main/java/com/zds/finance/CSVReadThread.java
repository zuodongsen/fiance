package com.zds.finance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CSVReadThread extends Thread {
    String fileName;
    String folder;

    public CSVReadThread(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        super.run();
        File inFile = new File(folder + File.separator + fileName);
        final StringBuilder cSb = new StringBuilder();
        String inString;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            while ((inString = reader.readLine()) != null) {
                cSb.append(inString).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mCSVTv.setText(cSb.toString());// 显示
//
//            }
//
//        });

    }
}
