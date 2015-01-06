package com.xboxcollectorsplace.ui;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Windowed activity, used by the collection/catalog activities to navigate fast to a certain game.
 * After selecting a title and/or a year, those parameters will return to the collection/catalog 
 * activity, and the list will scroll to the first game that mets them
 */
public class GoToActivity extends Activity implements OnClickListener
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private EditText _edtTitle;
	private EditText _edtYear;
	private Button _btnAccept;
	private LinearLayout _lytGotoMain;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
	public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_goto);
	        
	        this._edtTitle = (EditText)this.findViewById(R.id.edtGotoTitle);
	        this._edtYear = (EditText)this.findViewById(R.id.edtGotoYear);
	        this._btnAccept = (Button)this.findViewById(R.id.btnAccept);
	        this._lytGotoMain = (LinearLayout)this.findViewById(R.id.lytGotoMain);
	        		
	        this._btnAccept.setOnClickListener(this);
	        
	        // Used to prevent the editTexts from getting the focus upon creation
	        this._lytGotoMain.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
	        this._lytGotoMain.setFocusable(true);
	        this._lytGotoMain.setFocusableInTouchMode(true);
	        this._lytGotoMain.setOnTouchListener(new View.OnTouchListener() 
	        {
				public boolean onTouch(View v, MotionEvent event)
				{
	                v.requestFocusFromTouch();
	                return false;
				}
	        });
	    }
		catch (Exception ex)
		{
			XLog.e("[GoToActivity.onCreate]", ex);
	    }
    }
	
	//------------------------------------------------------------------------- EVENTS*/
    
	/**
	 * Event related to the touch of the accept button. Returns the selected parameters to the 
	 * caller activity
	 */
    public void onClick(View view)
	{
		try
		{			
			if (view.getId() == this._btnAccept.getId())
			{
				Intent output = new Intent();
					output.putExtra(BLParameters.PARAMETERS.TITLE, _edtTitle.getText().toString());
					output.putExtra(BLParameters.PARAMETERS.YEAR, _edtYear.getText().toString());
				setResult(Activity.RESULT_OK, output);
				this.finish();
			}
		}
		catch (Exception ex) 
		{
			XLog.e("[GoToActivity.onClick]", ex);
		}
	}    
}
