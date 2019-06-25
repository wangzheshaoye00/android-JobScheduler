package com.example.android.baohuo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BaohuoService1 extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "服务1已经启动", Toast.LENGTH_LONG).show();
        Log.i("BaohuoService1", "服务1已经启动");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "服务1已经停止", Toast.LENGTH_LONG).show();
        Log.i("BaohuoService1", "服务1已经启动");
    }
}