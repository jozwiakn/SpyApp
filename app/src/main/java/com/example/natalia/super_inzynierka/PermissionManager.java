package com.example.natalia.super_inzynierka;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by Natalia on 26.11.2016.
 */
public class PermissionManager extends Activity {
    public static String serialNr;
    Context context;

    public String getSerialNr() {
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        serialNr = telemamanger.getSimSerialNumber();
        return serialNr;
    }
    //A method that can be called from any Activity, to check for specific permission
//    public static void check(Activity activity, String permission, int requestCode){
    //If requested permission isn't Granted yet
//        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
    //Request permission from user
//            ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
//        }
//    }
}
