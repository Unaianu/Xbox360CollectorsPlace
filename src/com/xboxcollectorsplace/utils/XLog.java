package com.xboxcollectorsplace.utils;

import com.xboxcollectorsplace.bl.BLUtils;

import android.util.Log;

/**
 * Class used to write logs in a simpler way 
 */
public class XLog
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final String TAG_LOG =  BLUtils.getAppName();
	
	//------------------------------------------------------------------------- PUBLIC METHODS*/
	
	public static void d(String message, Throwable exception)
	{
		Log.d(TAG_LOG, message, exception);
	}
	
	public static void d(String message)
	{
		Log.d(TAG_LOG, message);
	}
	
	public static void i(String message, Throwable exception)
	{
		Log.i(TAG_LOG, message, exception);
	}
	
	public static void i(String message)
	{
		Log.i(TAG_LOG, message);
	}
	
	public static void w(String message, Throwable exception)
	{
		Log.w(TAG_LOG, message, exception);
	}
	
	public static void w(String message)
	{
		Log.w(TAG_LOG, message);
	}
	
	public static void e(String message, Throwable exception)
	{
		Log.e(TAG_LOG, message, exception);
	}
	
	public static void e(String message)
	{
		Log.e(TAG_LOG, message);
	}
}
