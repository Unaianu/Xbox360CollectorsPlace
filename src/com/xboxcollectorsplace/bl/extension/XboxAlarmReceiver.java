package com.xboxcollectorsplace.bl.extension;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Class not used, only for Google Play. Code from Google Developers.
 * Originally used to download the expansion file containing the covers
 */
public class XboxAlarmReceiver extends BroadcastReceiver 
{
    @Override    
    public void onReceive(Context context, Intent intent) 
    {
        try 
        {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, XboxDownloaderService.class);
        } 
        catch (NameNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
}