package com.xboxcollectorsplace.ui;

import android.app.Activity;
import android.os.Bundle;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.utils.JustifiedTextView;
import com.xboxcollectorsplace.utils.XLog;

/**
 * Windowed activity, used to show the entire text when touching a justified text that
 * has been truncated for being too long. 
 */
public class SynopsisDetailActivity extends Activity
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private JustifiedTextView _txtSynopsis;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_synopsis_detail);
	        	 
	        this._txtSynopsis = (JustifiedTextView) this.findViewById(R.id.txtSynopsis);
	        
	        String synopsis = "";
	        
	        if (this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.SYNOPSIS))
			{
	        	synopsis = this.getIntent().getExtras().getString(BLParameters.PARAMETERS.SYNOPSIS);
	        }
	        
	        // truncatePosition is set to 0 to indicate that it doesnt have to be truncated
	        this._txtSynopsis.setText(synopsis, 0);
	    }
    	catch (Exception ex)
		{
			XLog.e("[SynopsisDetailActivity.onCreate]", ex);
        }
    }
}
