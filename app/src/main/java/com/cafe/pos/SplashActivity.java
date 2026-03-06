package com.bizflow.pos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable splashRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler(Looper.getMainLooper());
        splashRunnable = () -> {
            if (!isFinishing()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };
        
        handler.postDelayed(splashRunnable, 2000);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && splashRunnable != null) {
            handler.removeCallbacks(splashRunnable);
        }
    }
}