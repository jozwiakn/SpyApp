package com.example.natalia.super_inzynierka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Natalia on 01.12.2016.
 */
public class ServiceAutoStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "onReceive ServiceAitoStarter", Toast.LENGTH_SHORT).show();
        System.out.println("serviceAutoStarter");
        context.startService(new Intent(context, MyService.class));
    }
}
