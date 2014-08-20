package com.naosim.windowmanagersample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;

/**
 * Created by fujitanao on 2014/08/18.
 */
public class LayerService extends Service {
    WindowOverlay vc;
    boolean calling = false;
    private Ringtone ringtone;

    public enum Action {
        FINISHED_PHONE_SCREEN,
        CALL_START,
        CALL_END,
        WILL_BE_CALLED;
        public Intent createIntent(Context c) {
            Intent i = new Intent(c, LayerService.class);
            i.setAction(name());
            return i;
        }
        public void start(Context c) {
            c.startService(createIntent(c));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if("FINISHED_PHONE_SCREEN".equals(intent.getAction())) {
            if (calling) vc.show();
        } else if("CALL_START".equals(intent.getAction())) {
            if(!calling) {
                // スリープから復帰
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "Your App Tag");
                wakelock.acquire();

                calling = true;
                onReceivedCall();
                playRingtone();
            }
        } else if("CALL_END".equals(intent.getAction())) {
            calling = false;
            stopRingtone();
            vc.dismiss();
        } else if("WILL_BE_CALLED".equals(intent.getAction())) {
            setupDelayCall();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void setupDelayCall() {

        Intent i = Action.CALL_START.createIntent(this);
        PendingIntent sender = PendingIntent.getService(this, 0, i, 0);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, i, 0); // ブロードキャストを投げるPendingIntentの作成

        Calendar calendar = Calendar.getInstance(); // Calendar取得
        calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        calendar.add(Calendar.SECOND, 10); // 現時刻より15秒後を設定

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE); // AlramManager取得
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // AlramManagerにPendingIntentを登録

    }

    public void playRingtone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        ringtone.play();
    }

    public void stopRingtone() {
        ringtone.stop();
    }

    public void onReceivedCall() {
        Intent i = new Intent(LayerService.this, MyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LayerService.this.startActivity(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        View view = LayoutInflater.from(this).inflate(R.layout.overlay, null);
        view.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Action.CALL_END.start(LayerService.this);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,// 表示するレイヤー位置
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,// タッチやフォーカスの設定
                PixelFormat.TRANSLUCENT);

        vc = new WindowOverlay.Builder(this)
                .setView(view)
                .setLayoutParams(params)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        calling = false;
        vc.dismiss();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
