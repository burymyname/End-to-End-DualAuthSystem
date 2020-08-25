package org.nuaa.speakerverification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Random;

public class CommonUtils {

    public static final String DEBUG = "DEBUG_TAG";

    public static String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }



    public static boolean checkPermissions(Activity activity) {
        if (!hasPermission(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String makeRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        for (int i=0; i<length; i++) {
            sb.append(base.charAt(random.nextInt(36)));
        }

        return sb.toString();
    }

}
