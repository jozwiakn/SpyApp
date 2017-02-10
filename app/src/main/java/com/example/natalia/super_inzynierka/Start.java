package com.example.natalia.super_inzynierka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

public class Start extends AppCompatActivity {

    private static String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
        System.out.println("MY NUMBER: " + serialNumber);

        Intent locationIntent = new Intent(this, StartService.class);
        startService(locationIntent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
    }

}
