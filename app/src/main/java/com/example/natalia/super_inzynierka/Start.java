package com.example.natalia.super_inzynierka;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

    }

    private String url = "http://natalia123.pythonanywhere.com/spy_app/create/";
    private AsyncHttpClient client;

    public void connect(View view) {
//        TextView displayTextView = (TextView) findViewById(R.id.text_super);
//        displayTextView.setText("Connect click");
        postRequest();

    }

    public void getRequest() {
        client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                //success response, do something with it!
                String response = new String(responseBody);
                TextView displayTextView = (TextView) findViewById(R.id.text_super);
                displayTextView.setText(response);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
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
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("number", "555");
        params.put("start_time", "23:00");
        params.put("time", "2:20");
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (responseBody == null) { /* empty response, alert something*/
                    return;
                }
                //success response, do something with it!
                String response = new String(responseBody);
                TextView displayTextView = (TextView) findViewById(R.id.text_super);
                displayTextView.setText(response);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
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

}
