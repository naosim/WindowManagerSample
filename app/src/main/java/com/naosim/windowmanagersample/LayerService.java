package com.naosim.windowmanagersample;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by fujitanao on 2014/08/18.
 */
public class LayerService extends Service {
    WindowOverlay vc;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vc.show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        View view = LayoutInflater.from(this).inflate(R.layout.overlay, null);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LayerService.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vc.dismiss();
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,// 表示するレイヤー位置
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,// タッチやフォーカスの設定
                PixelFormat.TRANSLUCENT);

        vc = new WindowOverlay.Builder(this)
                .setView(view)
                .setLayoutParams(params)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vc.dismiss();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
