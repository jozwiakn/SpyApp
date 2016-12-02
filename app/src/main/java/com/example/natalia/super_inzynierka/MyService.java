package com.example.natalia.super_inzynierka;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by Natalia on 30.11.2016.
 */
public class MyService extends Service {

    private BroadcastReceiver screenOnReceiver;
    Context context;

    private Toast toast;
    private Timer timer;
    private TimerTask timerTask;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            showToast("Your service is still working");
        }
    }

    private void showToast(String text) {
        toast.setText(text);
        toast.show();
    }

//    private void writeToLogs(String message) {
//        Log.d("HelloServices", message);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        writeToLogs("Called onCreate() method.");
        System.out.println("onCreate service");
        Toast.makeText(context, "onCreate service MyService", Toast.LENGTH_LONG).show();
        timer = new Timer();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context, "onStartCommand service MyService", Toast.LENGTH_LONG).show();
//        writeToLogs("Called onStartCommand() methond");
                clearTimerSchedule();
                initTask();
                System.out.println("onStartCommand");
                timer.scheduleAtFixedRate(timerTask, 4 * 1000, 4 * 1000);
                showToast("Your service has been started");

//        // create the notification
//        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
//                .setContentTitle(getText(R.string.service_name))
//                .setContentText(getResources().getText(R.string.service_status_monitor))
//                .setSmallIcon(R.drawable.stat_notify_chat);
//
//        // create the pending intent and add to the notification
//
//        // send the notification
//
//        startForeground(1337, m_notificationBuilder.getNotification());
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void clearTimerSchedule() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    private void initTask() {
        timerTask = new MyTimerTask();
    }



//    @Override
//    public void onDestroy() {
////        writeToLogs("Called onDestroy() method");
//        clearTimerSchedule();
//        showToast("Your service has been stopped");
//        super.onDestroy();
//        System.out.println("onDestroy MyService");
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy Service");
        unregisterReceiver(screenOnReceiver);
    }

    @Override
    public void onLowMemory() {
        System.out.println("Low memory");
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("onUnbing");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.d(TAG, "TASK REMOVED");

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), MyService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }


    //    @Override
//    public boolean stopService(Intent name) {
//        System.out.println("stopService");
//        return super.stopService(name);
//    }

//    @Override
//    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
//        return super.bindService(service, conn, flags);
//    }

//
//    public class MyBinder extends Binder {
//        public MyService getService() {
//            return MyService.this;
//        }
//    }

}
