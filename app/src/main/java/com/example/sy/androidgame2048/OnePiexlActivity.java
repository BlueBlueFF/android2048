package com.example.sy.androidgame2048;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Dem-Blue on 2017/10/25.
 */

/*public class OnePiexlActivity extends Activity {



    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run () {

            handler.postDelayed(this, 30000);
        }
    };
    //电量相关
    private int batteryLevel;
    private int batteryScale;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量，如未获取具体数值，则默认为0
            batteryLevel=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            //获取最大电量，如未获取到具体数值，则默认为100
            batteryScale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,100);

        }
    };


    private static OnePiexlActivity instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,30000);




        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //注册接收器以获取电量信息
        registerReceiver(broadcastReceiver, intentFilter);


        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP );
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        params.alpha=0.0f;
        window.setAttributes(params);





    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;

        unregisterReceiver(broadcastReceiver);
    }

    public static void killHooligan() {

        if(instance != null) {
            instance.finish();
        }
    }
}
*/
public class OnePiexlActivity extends Activity {

    private BroadcastReceiver endReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        //结束该页面的广播
        endReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(endReceiver, new IntentFilter("finish"));
        //检查屏幕状态
        checkScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreen();
    }


    /**
     * 检查屏幕状态  isScreenOn为true  屏幕“亮”结束该Activity
     */
    private void checkScreen() {
        PowerManager pm = (PowerManager) OnePiexlActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(endReceiver);
    }
}