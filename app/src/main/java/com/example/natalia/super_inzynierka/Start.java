package com.example.natalia.super_inzynierka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Intent intent = getIntent();

    }

    public void connect(View view) {
//        postRequest();
        Intent intent = new Intent(this, SecureMessagesActivity.class);
        startActivity(intent);
    }

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

}
