package com.example.natalia.super_inzynierka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        String url = "http://natalia123.pythonanywhere.com/spy_app/list/";

        try {
            URLConnection connection = new URL(url).openConnection();
            System.out.println("dziala");
        } catch (IOException e) {
            System.out.println("nie dzisla");
        }
//        try {
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }


//        URL url = null;
//        try {
//            url = new URL("http://www.android.com/");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection urlConnection = null;
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }

    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }


}
