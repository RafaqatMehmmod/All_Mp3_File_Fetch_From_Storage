package com.example.allfilefetch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

/**
 * Created by Rafaqat Mehmood
 * Whatsapp No:+923101025532
 * 16/09/2022
 */
public class PermissionUtil {


    public static boolean isPermissionGranted(Context context)
    {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            return Environment.isExternalStorageManager();
        }
        else
        {
            int readExternalStorage= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStorage== PackageManager.PERMISSION_GRANTED;

        }
    }
}
