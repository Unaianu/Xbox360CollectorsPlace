package com.xboxcollectorsplace;

import android.app.Application;
import android.content.Context;

/**
 * Class used to recover the Application context anywhere in the code
 */
public class App extends Application
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
    private static Context _context = null;  

    //------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate()
    {
        super.onCreate();
        
        _context = this.getApplicationContext();
    }  
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/

    public static Context getContext()
    { 
    	return _context;
    }
}
