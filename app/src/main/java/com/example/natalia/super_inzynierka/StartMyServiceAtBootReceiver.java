package com.example.natalia.super_inzynierka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Natalia on 10.12.2016.
 */
public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LocationTrace.class);
            System.out.println("StartMyServiceAtBootReceiver");
            context.startService(serviceIntent);
        }
    }
}
