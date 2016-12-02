package com.example.natalia.super_inzynierka;

import android.content.Context;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Natalia on 27.11.2016.
 */
public class CallReceiver extends ServiceReceiver {

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
        postRequest(number, start_time, time);
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

        postRequest(number, start_time, time);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        System.out.println("onMissedCall");

        String start_time = displayTime(start.getTime());
        System.out.println(start_time);

        postRequest(number, start_time, "--");
        //
    }

    private void postRequest(String number, String date, String time) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("time", time);
        SpyAppRestClient.post("create_connect/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = Integer.toString(minute);
        }
        String start_time = mDay + "-" + mMonth + "-" + mYear + "  " + hour + ":" + min;
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
