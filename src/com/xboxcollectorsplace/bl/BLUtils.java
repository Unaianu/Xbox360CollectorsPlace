package com.xboxcollectorsplace.bl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.content.res.Configuration;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Options.Language;
import com.xboxcollectorsplace.utils.XLog;

/**
 * This class is used to provide useful and reusable methods to use anywhere in the code
 */
public class BLUtils 
{	
	//------------------------------------------------------------------------- PUBLIC METHODS*/
	
	/**
     * Returns the name of the application
     *
     * @return Name of the App
     */
	public static String getAppName()
    {    	
    	String appName;    
    	String appPackage;
    	
    	appPackage = App.getContext().getApplicationInfo().packageName;	
	    appName = appPackage.substring(appPackage.lastIndexOf('.') + 1, appPackage.length());    	

    	return appName;
    }
	
	/**
     * Converts an inputStream to a String. Used to read an asset and convert it to an appropriate
     * json
     *
     * @param inputStream Stream of the file to read (usually an asset)
     * @param codification Codification to use during the reading (Usually UTF-8)
     * @return System readable json with the info of the stream
     */
	public static String inputStreamToString(InputStream inputStream, String codification)
	{
		String result = null;
		StringBuilder stringBuilder = new StringBuilder("");
		BufferedReader bufferedReader = null;
		String line;

		try
		{
			if (inputStream != null)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, codification));  
				
                 while ((line = bufferedReader.readLine()) != null)
                 {                
                	 stringBuilder.append(line);    
                	 stringBuilder.append("\n");
                	 line = null;
                 }
              
                 result = stringBuilder.toString();
			}
		}
		catch (Exception ex)
		{			
			result = null;
			XLog.e("[BLUtils.InputStreamToString]" , ex);
		}
		finally
		{
			line = null;
			stringBuilder = null;
			
			if (bufferedReader != null)				
			{
				try 
				{
					bufferedReader.close();
					if (inputStream != null)
					{
						inputStream.close();
					}
				} 
				catch (Exception ex)
				{
					XLog.e("[BLUtils.InputStreamToString (finally)]" , ex);				
				}
			}
		}
		
		return result;
	}
	
	/**
     * Translates the date to the correct format used in each language
     *
     * @param originalDate Original date in the format yyyy/MM/dd
     * @return Date translated to the correct format of the language used
     */
	public static String translateDate(String originalDate)
	{
		String translatedDate = "";
		
		try
		{
			if (originalDate.length() > 4)
	    	{
	    		switch (StorageController.loadOptions().getLanguage())
	    		{
	    			case JAPANESE:
	    			case KOREAN:
	    				translatedDate = originalDate;
		    			break;
	    			case SWEDISH:
	    				translatedDate = originalDate.replace("/", "-");
	    				break;
		    		case BASQUE:
	    				translatedDate = originalDate.replace("/", ".");	    		
		    			break;
		    		case POLISH:
		    		case NORWEGIAN:
		    		case FINNISH:
		    		case RUSSIAN:
		    		case CZECH:
					case GERMAN:
		    			translatedDate = originalDate.subSequence(8, 10) + "." + originalDate.subSequence(5, 7) + "." + originalDate.subSequence(0, 4);
	    				break;
		    		case DUTCH:
		    			translatedDate = originalDate.subSequence(8, 10) + "-" + originalDate.subSequence(5, 7) + "-" + originalDate.subSequence(0, 4);
	    				break;
			    	case ENGLISH:
		    		case FRENCH:
		    		case SPANISH:
		    		case ITALIAN:
		    		case PORTUGUESE:
			    	default:
		    			translatedDate = originalDate.subSequence(8, 10) + "/" + originalDate.subSequence(5, 7) + "/" + originalDate.subSequence(0, 4);
	    				break;
	    		}
	    	}
	    	else
	    	{
	    		translatedDate = originalDate;
	    	}
		}
		catch (Exception ex)
		{			
			XLog.e("[BLUtils.translateDate]" , ex);
		}
		
		return translatedDate;
	}
	
	/**
     * Writes a XML/Json String in the selected file
     *
     * @param content XML/Json to write
     * @param fileName Complete name (with path) of the file to write
     * @return boolean indicating the success of the operation
     */
	public static boolean writeXML(String content, String fileName)
	{
		boolean result = true;
		
		try
		{
			File file = new File(fileName);
			file.createNewFile();
			
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			   
			outputStreamWriter.write(content);
			outputStreamWriter.flush();
			outputStreamWriter.close();
		}
		catch (Exception ex)
		{			
			XLog.e("[BLUtils.writeXML]" , ex);
			result = false;
		}
		
		return result;
	}
	
	/**
     * Reads a XML/Json from the selected file
     *
     * @param fileName Complete name (with path) of the file to read
     * @return String with the XML/Json of the file
     */
	public static String readXML(String fileName)
	{
		String result = "";
		
		try
		{
			StringBuilder text = new StringBuilder();
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    String line;

		    while ((line = br.readLine()) != null) 
		    {
		    	text.append(line);
		    	text.append('\n');
		    }
		    
		    br.close();
		    result = text.toString();
		}
		catch (Exception ex)
		{			
			XLog.e("[BLUtils.readXML]" , ex);
		}
		
		return result;
	}
	
	/**
     * Changes the language of the application setting the locale with the code associated to
     * the selected language
     *
     * @param language Language to set
     */
	public static void changeLanguage(Language language)
	{
		Locale locale = null;
		Configuration config = new Configuration();
		
		try
    	{
	    	switch (language)
	    	{
		    	case ENGLISH:
		    		locale = new Locale(BLParameters.LANGUAGES.ENGLISH_CODE);
		    		break;
		    	case GERMAN:
		    		locale = new Locale(BLParameters.LANGUAGES.GERMAN_CODE);
		    		break;
		    	case FRENCH:
		    		locale = new Locale(BLParameters.LANGUAGES.FRENCH_CODE);
		    		break;
		    	case ITALIAN:
		    		locale = new Locale(BLParameters.LANGUAGES.ITALIAN_CODE);
		    		break;
		    	case POLISH:
		    		locale = new Locale(BLParameters.LANGUAGES.POLISH_CODE);
		    		break;
		    	case BASQUE:
		    		locale = new Locale(BLParameters.LANGUAGES.BASQUE_CODE);
		    		break;
		    	case SPANISH:
		    		locale = new Locale(BLParameters.LANGUAGES.SPANISH_CODE);
		    		break;
		    	case JAPANESE:
		    		locale = new Locale(BLParameters.LANGUAGES.JAPANESE_CODE);
		    		break;
		    	case PORTUGUESE:
		    		locale = new Locale(BLParameters.LANGUAGES.PORTUGUESE_CODE);
		    		break;
		    	case FINNISH:
		    		locale = new Locale(BLParameters.LANGUAGES.FINNISH_CODE);
		    		break;
		    	case NORWEGIAN:
		    		locale = new Locale(BLParameters.LANGUAGES.NORWEGIAN_CODE);
		    		break;
		    	case SWEDISH:
		    		locale = new Locale(BLParameters.LANGUAGES.SWEDISH_CODE);
		    		break;
		    	case RUSSIAN:
		    		locale = new Locale(BLParameters.LANGUAGES.RUSSIAN_CODE);
		    		break;
		    	case CZECH:
		    		locale = new Locale(BLParameters.LANGUAGES.CZECH_CODE);
		    		break;
		    	case KOREAN:
		    		locale = new Locale(BLParameters.LANGUAGES.KOREAN_CODE);
		    		break;
		    	case DUTCH:
		    		locale = new Locale(BLParameters.LANGUAGES.DUTCH_CODE);
		    		break;
	    	}
	    	
	    	Locale.setDefault(locale);
			config.locale = locale;
			App.getContext().getResources().updateConfiguration(config, App.getContext().getResources().getDisplayMetrics());
    	}
    	catch (Exception ex)
    	{
    		XLog.e("[BLUtils.changeLanguage]", ex);
    	}
    }
	
	/**
     * Displays on screen a toast with the message indicated by parameter
     *
     * @param message Text message to display
     * @param durationShort Boolean indicating the duration of the Toast. True if short, false 
     * if long
     */
	public static void showToast(String message, boolean durationShort)
    {
    	try
    	{
    		Toast toast = Toast.makeText(App.getContext(), message, durationShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
    		
    		TextView txtMessage = (TextView) toast.getView().findViewById(android.R.id.message);
			if (txtMessage != null) 
			{
				txtMessage.setGravity(Gravity.CENTER);
			}
			
    		toast.show();
    	}
		catch (Exception ex)
		{
			XLog.e("[BLUtils.showToast]", ex);
		}
    }
}
