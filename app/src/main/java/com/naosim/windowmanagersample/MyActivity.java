package com.naosim.windowmanagersample;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Date;


public class MyActivity extends Activity {

    private PowerManager.WakeLock wakelock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayerService.Action.WILL_BE_CALLED.start(MyActivity.this);
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayerService.Action.CALL_END.start(MyActivity.this);
            }
        });

        Window window = getWindow();

        // lock pattern を設定していても、
        // このアプリ起動中は画面オン時にロックはでない
        // 別のアプリやホームに移動するときにロックがでる
        window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // lock pattern を設定していない場合、
        // このアプリ起動中は画面オン時にロックはでない
        window.setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "Your App Tag");
        wakelock.acquire();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MyActivity", "onStart");
        startDate = new Date().getTime();

    }


    long startDate;
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MyActivity", "onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MyActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MyActivity", "onPause");
    }

    @Override
    protected void onStop() {
        Log.e("MyActivity", "onStop");

        // 高速終了じゃない場合
        if(new Date().getTime() - startDate > 300) {
            LayerService.Action.FINISHED_PHONE_SCREEN.start(MyActivity.this);
            finish();//HOMEキーの場合でも死んでもらうため
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakelock.release();
    }
}
