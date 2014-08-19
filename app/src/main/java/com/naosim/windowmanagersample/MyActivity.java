package com.naosim.windowmanagersample;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class MyActivity extends Activity {

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

        // スリープから復帰
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "Your App Tag");
        wakelock.acquire();
//                wakelock.release();

        // lock pattern を設定していても、
        // このアプリ起動中は画面オン時にロックはでない
        // 別のアプリやホームに移動するときにロックがでる
        window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        // lock pattern を設定していない場合、
        // このアプリ起動中は画面オン時にロックはでない
        window.setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    public void finish() {
        super.finish();
        LayerService.Action.FINISHED_PHONE_SCREEN.start(MyActivity.this);
    }
}
