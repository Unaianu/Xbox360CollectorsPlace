package com.xboxcollectorsplace.bl.extension;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/**
 * Class not used, only for Google Play. Code from Google Developers.
 * Originally used to download the expansion file containing the covers
 */
public class XboxDownloaderService extends DownloaderService 
{
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmND+7ZjdHent8Yio3hyhCrCIFUblSx1fIaM/thVqlmx2aCewrcX6EHDpSbfP06WZSsW7aaThPQ17hGMpAFKE8Y6DXhBCIs55+yfZjY+p+EvJEjRUj1g2GszqnFIA8NRpzxwMVuuj1nX3sZ1Qh+NOZZRZ9Uaium3s3paJunuItv1GTGSK9hXhA2PHg5G4sG4/1emR3fEoc4uhsCkWPANSqNJkdhxP+wTdq4WJcgf3bFfCVtswCL+Boy2bze2QcmSaDhx5gUxnGSoxUshB9YYOE4biTVySz9A9XsPX2nD39nsutdvV9K7/r3sVUk1mhzDHNDJEH/C0edETnM2+R8rLXQIDAQAB";
    
    public static final byte[] SALT = new byte[] 
    { 
    	1, 42, -12, -1, 54, 98, -100, -12, 43, 2, -8, -4, 9, 5, -106, -107, -33, 45, -1, 84
    };

    @Override
    public String getPublicKey() 
    {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() 
    {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() 
    {
        return XboxDownloaderService.class.getName();
    }
}