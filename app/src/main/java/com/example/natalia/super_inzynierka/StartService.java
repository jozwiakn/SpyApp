package com.example.natalia.super_inzynierka;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Natalia on 01.12.2016.
 */
public class StartService extends Service{

    public static ContentResolver contentResolver;

    private Context context;

    public static String serialNr;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();

        MyObserver myObserver = new MyObserver(new Handler());
        contentResolver = this.getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, myObserver);

        this.context = this;

        Toast.makeText(context, "onStartCommand", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);

    }
}
