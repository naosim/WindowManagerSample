package com.naosim.windowmanagersample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MyActivity.this, LayerService.class));
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MyActivity.this, LayerService.class));
            }
        });
    }
}
