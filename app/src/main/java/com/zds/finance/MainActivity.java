package com.zds.finance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.zds.common.DateTimeTrans;
import com.zds.common.FileRWThread;
import com.zds.common.FtpFile;
import com.zds.common.HandlerMsgId;
import com.zds.fat.Fat;
import com.zds.fat.FatList;
import com.zds.fat.FatStats;
import com.zds.finance.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

    //adb path C:\Users\dosens\AppData\Local\Android\Sdk\platform-tools
/*
git config --global --unset http.proxy
git config --global --unset https.proxy
C:\Users\dosens\AndroidStudioProjects\finance\app\release
*/


public class MainActivity extends AppCompatActivity {
    final static int REQUEST=10;


    private TextView textLogInfo;

    private final int CRU_SELECT_ACTIVITY_FRANCE = 0;
    private final int CRU_SELECT_ACTIVITY_FAT = 1;
    private final int CRU_SELECT_TAB_LIST = 0;
    private final int CRU_SELECT_TAB_STATS = 1;
    private int CRU_SELECT_ACTIVITY = CRU_SELECT_ACTIVITY_FRANCE;
    private int CRU_SELECT_TAB = CRU_SELECT_TAB_LIST;

    static int selectFileIndexForInput;
    static int selectFileIndexForUpload;
    static int selectFileIndexForRemoteInput;
    static List<Integer> selectFileIndexForDelete = new ArrayList<>();
    static List<String> backupFileNameList = new ArrayList<>();

    final static int INVALID_FILE_SELECT_ID = -1;
    private static String FILE_FOLDER;
    private static String BACKUP_FILE_FOLDER;
    private static final String BACKUP_PATH = "amount_backup";
    public static Handler handler;

    private FinanceList financeList;
    private FinanceStats financeStats;

    private FatList fatList;
    private FatStats fatStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initMenuBar();
        this.initTabLayout();
        this.initHandler();
        this.initStaticValue();
        this.initFileDirc();

        this.textLogInfo = (TextView)findViewById(R.id.text_logInfo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.setLayoutContainer();
    }

    private void exportToFile() {
        String fileName;
        List<String> dataInfos;
        if(this.CRU_SELECT_ACTIVITY == CRU_SELECT_ACTIVITY_FRANCE) {
            dataInfos = Finance.getAllToStringList();
            fileName = "finance_";
        } else {
            dataInfos = Fat.getAllToStringList();
            fileName = "fat_";
        }
        FileRWThread csvWriteThread = new FileRWThread(dataInfos, BACKUP_FILE_FOLDER,
                fileName + DateTimeTrans.getNowDateTime2String() + ".txt", FileRWThread.FILE_RW_TYPE_WRITE);
        csvWriteThread.run();
    }

    /* menu bar */

    private static final int FILE_LIST_TYPE_INPUT = 0;
    private static final int FILE_LIST_TYPE_DELETE = 1;
    private static final int FILE_LIST_TYPE_UPLOAD = 2;
    private static final int FILE_LIST_TYPE_REMOTEINPUT = 3;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_export) {
            MainActivity.this.exportToFile();
        }else if(id == R.id.menu_remoteexport) {
            FtpFile ftpList = new FtpFile();
            ftpList.doTypeList();
        }else if(id == R.id.menu_setftp) {
            final EditText inputServer = new EditText(this);
            inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            inputServer.setText(FtpFile.getHostIp());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("输入ftp服务器地址").setView(inputServer);
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String _sign = inputServer.getText().toString();
                    if(_sign!=null && !_sign.isEmpty()) {
                        FtpFile.setHostIp(_sign);
                    }
                }
            });
            builder.show();
        } else if(id == R.id.menu_exchange) {
            this.CRU_SELECT_ACTIVITY = (this.CRU_SELECT_ACTIVITY == CRU_SELECT_ACTIVITY_FRANCE ?
                                        CRU_SELECT_ACTIVITY_FAT : CRU_SELECT_ACTIVITY_FRANCE);
            this.setLayoutContainer();


        } else {
            File fileDir = new File(BACKUP_FILE_FOLDER);
            File[] files = fileDir.listFiles();
            for(File f : files) {
                if(f.isFile()) {
                    MainActivity.backupFileNameList.add(f.toString().replaceFirst(BACKUP_FILE_FOLDER + File.separator, ""));
                }
            }
            String[] backupFileNameStrArr = new String[MainActivity.backupFileNameList.size()];
            MainActivity.backupFileNameList.toArray(backupFileNameStrArr);
            if(id == R.id.menu_inport) {
                this.showFileListDialog("导入备份文件", backupFileNameStrArr, FILE_LIST_TYPE_INPUT);
            }else if(id == R.id.menu_deletebackup) {
                this.showFileListDialog("删除备份文件", backupFileNameStrArr, FILE_LIST_TYPE_DELETE);
            }else if(id == R.id.menu_uploadbackup) {
                this.showFileListDialog("上传备份文件", backupFileNameStrArr, FILE_LIST_TYPE_UPLOAD);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void setLayoutContainer() {
        if(this.CRU_SELECT_ACTIVITY == CRU_SELECT_ACTIVITY_FRANCE) {
            if(this.CRU_SELECT_TAB == this.CRU_SELECT_TAB_LIST) {
                this.financeList.setView();
            }else {
                this.financeStats.setView();
            }
        }else {
            if(this.CRU_SELECT_TAB == this.CRU_SELECT_TAB_LIST) {
                this.fatList.setView();
            }else {
                this.fatStats.setView();
            }
        }
    }

//    private float touchDownX = 0f;
//    private float touchUpX = 0f;
//    private final int TOUCH_MOVE_MIN_X = 100;
//    @Override
//    public  boolean onTouchEvent(MotionEvent motionEvent) {
//        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            touchDownX = motionEvent.getX();
//            return super.onTouchEvent(motionEvent);
//        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//            touchUpX = motionEvent.getX();
//            if (touchUpX - touchDownX > TOUCH_MOVE_MIN_X) {
//                modifyListViewShowMonth(-1);
//                reflushListViewData();
//            } else if (touchDownX - touchUpX > TOUCH_MOVE_MIN_X) {
//                modifyListViewShowMonth(1);
//                reflushListViewData();
//            }
//        }
//        return super.onTouchEvent(motionEvent);
//    }

//    @Override
//    public Resources getResources() {
//        Resources resources = super.getResources();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setToDefaults();
//        resources.updateConfiguration(configuration,resources.getDisplayMetrics() );
//        return resources;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    private void initMenuBar() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMenu);
        this.resetFileListSelect();
    }

    private void initFileDirc() {
        FILE_FOLDER = this.getFilesDir().toString() + File.separator;
        BACKUP_FILE_FOLDER = FILE_FOLDER + BACKUP_PATH;
        this.createFolder(BACKUP_FILE_FOLDER);
    }


    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tablayout);
        this.financeList = new FinanceList(this);
        this.financeStats = new FinanceStats(this);
        this.fatList = new FatList(this);
        this.fatStats = new FatStats(this);
        this.financeList.setView();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    MainActivity.this.CRU_SELECT_TAB = MainActivity.this.CRU_SELECT_TAB_LIST;
                }else {
                    MainActivity.this.CRU_SELECT_TAB = MainActivity.this.CRU_SELECT_TAB_STATS;
                }
                MainActivity.this.setLayoutContainer();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void createFolder(String folder) {
        File fileDir = new File(folder);
        boolean hasDir = fileDir.exists();
        if (!hasDir) {
            System.out.println("create dirc: " + folder);
            fileDir.mkdirs();// 这里创建的是目录
        }
    }

    private void showFileListDialog(String title, String[] fileList, int mode/* 0 select, 1 delete, 2 upload, 3 remote input*/){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        if(mode == FILE_LIST_TYPE_INPUT) {
            builder.setSingleChoiceItems(fileList, -1, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    MainActivity.selectFileIndexForInput = arg1;
                }
            });
        }else if(mode == FILE_LIST_TYPE_DELETE) {
            builder.setMultiChoiceItems(fileList, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if(b) {
                        MainActivity.selectFileIndexForDelete.add(i);
                    }else {
                        MainActivity.selectFileIndexForDelete.remove(new Integer(i));
                    }
                }
            });
        }else if(mode == FILE_LIST_TYPE_UPLOAD){
            builder.setSingleChoiceItems(fileList, -1, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    MainActivity.selectFileIndexForUpload = arg1;
                }
            });
        }else if(mode == FILE_LIST_TYPE_REMOTEINPUT){
            System.out.println(fileList.length);
            builder.setSingleChoiceItems(fileList, -1, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {
                    MainActivity.selectFileIndexForRemoteInput = arg1;
                }
            });
        }

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(MainActivity.selectFileIndexForInput != INVALID_FILE_SELECT_ID) {
                    String fileName = MainActivity.backupFileNameList.get(MainActivity.selectFileIndexForInput);
                    FileRWThread csvWriteThread = new FileRWThread(BACKUP_FILE_FOLDER,
                            fileName, FileRWThread.FILE_RW_TYPE_READ);
                    csvWriteThread.setFileReadCallBack(fileName.contains("finance") ? Finance::inportToDb : Fat::inportToDb);
                    csvWriteThread.run();
                }else if(!MainActivity.selectFileIndexForDelete.isEmpty()){
                    for(Integer it : MainActivity.selectFileIndexForDelete) {
                        FileRWThread.deleteFile(BACKUP_FILE_FOLDER, MainActivity.backupFileNameList.get(it));
                    }
                }else if(MainActivity.selectFileIndexForUpload != INVALID_FILE_SELECT_ID) {
                    FtpFile ftpUpload = new FtpFile();
                    ftpUpload.doTypeDownOrUpload(BACKUP_FILE_FOLDER + File.separator,
                            MainActivity.backupFileNameList.get(MainActivity.selectFileIndexForUpload),
                            FtpFile.FTP_TYPE_UPLOAD);
                }else if(MainActivity.selectFileIndexForRemoteInput != INVALID_FILE_SELECT_ID) {
                    FtpFile ftpFileDownload = new FtpFile();
                    ftpFileDownload.doTypeDownOrUpload(BACKUP_FILE_FOLDER + File.separator,
                            MainActivity.backupFileNameList.get(MainActivity.selectFileIndexForRemoteInput),
                            FtpFile.FTP_TYPE_DOWNLOAD);
                }
                MainActivity.this.resetFileListSelect();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.resetFileListSelect();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                MainActivity.this.resetFileListSelect();
            }
        });
        builder.show();
    }

    private void resetFileListSelect() {
        MainActivity.selectFileIndexForUpload = INVALID_FILE_SELECT_ID;
        MainActivity.selectFileIndexForInput = INVALID_FILE_SELECT_ID;
        MainActivity.selectFileIndexForRemoteInput = INVALID_FILE_SELECT_ID;
        MainActivity.selectFileIndexForDelete.clear();
        MainActivity.backupFileNameList.clear();
    }


    private void initStaticValue() {
//        resetPopListSelect();
        resetFileListSelect();
    }

    /* handler */
    private void initHandler() {
        handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerMsgId.FTP_FILE_LIST_RSP: {
                    MainActivity.this.resetFileListSelect();
                    String[] fileList = ((String) msg.obj).split(",");
                    String fileNameTag = "txt";
                    for(String it : fileList) {
                        System.out.println(it);
                        if(it.indexOf(fileNameTag) >= 0) {
                            MainActivity.backupFileNameList.add(it);
                            System.out.println("add " + it + MainActivity.backupFileNameList.size());
                        }
                    }
                    String[] backupFileNameStrArr = new String[MainActivity.backupFileNameList.size()];
                    MainActivity.backupFileNameList.toArray(backupFileNameStrArr);
                    MainActivity.this.showFileListDialog("下载备份文件", backupFileNameStrArr, FILE_LIST_TYPE_REMOTEINPUT);

                    break;
                }
                case HandlerMsgId.FTP_FILE_UPLOAD_PROGRESS:
                case HandlerMsgId.FTP_FILE_DOWNLOAD_PROGRESS:
                case HandlerMsgId.FTP_FILE_SET_HOST_IP_SUCCESS:
                case HandlerMsgId.FTP_FILE_SET_HOST_IP_FAIL:
                case HandlerMsgId.RW_FILE_READ_PROGRESS:
                case HandlerMsgId.RW_FILE_WRITE_PROGRESS:
                case HandlerMsgId.RW_FILE_DELETE_PROGRESS:
                case HandlerMsgId.FTP_FILE_CONNECT_FAIL:
                    MainActivity.this.textLogInfo.setText("*******" + (String) msg.obj);
                    break;

            }
        }
    };
    }




}