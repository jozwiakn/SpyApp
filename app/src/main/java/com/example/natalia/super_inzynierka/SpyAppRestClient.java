package com.example.natalia.super_inzynierka;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.sql.SQLOutput;

/**
 * Created by Natalia on 25.11.2016.
 */
public class SpyAppRestClient {
    private static final String BASE_URL = "http://natalia123.pythonanywhere.com/spy_app/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        System.out.println("POST");
        client.post(getAbsoluteUrl(url), params, responseHandler);
        System.out.println("POST 2");
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        System.out.println("GET ABSOLUTE RESULT");
        return BASE_URL + relativeUrl;
    }
}
