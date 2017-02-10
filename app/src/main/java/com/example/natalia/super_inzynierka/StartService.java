package com.example.natalia.super_inzynierka;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.io.SessionOutputBuffer;

import java.io.IOException;
import java.util.*;

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
