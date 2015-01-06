package com.xboxcollectorsplace.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.utils.XLog;

/**
 * Windowed activity, used to edit the title of a photo. It only contains an EditText
 * and a Button
 */
public class EditPhotoTitleActivity extends Activity implements OnClickListener 
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private EditText _edtTitle;
	private Button _btnOK;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_edit_title);
	        	 
	        this._edtTitle = (EditText) this.findViewById(R.id.edtTitle);
	        this._btnOK = (Button) this.findViewById(R.id.btnOK);
	        	        
	        if (this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.TITLE))
			{
	        	this._edtTitle.setText(this.getIntent().getExtras().getString(BLParameters.PARAMETERS.TITLE));
	        	this._edtTitle.setSelection(this._edtTitle.length());
	        }
	        
	        this._btnOK.setOnClickListener(this);
	    }
    	catch (Exception ex)
		{
			XLog.e("[EditPhotoTitleActivity.onCreate]", ex);
        }
    }
    
    //------------------------------------------------------------------------- EVENTS*/
	
    /**
     * Event related to the touch of the OK button. Returns the new title to the caller activity
     */
	public void onClick(View view)
	{
		try
		{
			if (view.getId() == this._btnOK.getId())
			{
				Intent output = new Intent();
				output.putExtra(BLParameters.PARAMETERS.TITLE, this._edtTitle.getText().toString());
				this.setResult(RESULT_OK, output);
				this.finish();
			}
		}
		catch (Exception ex)
		{
			XLog.e("[EditPhotoTitleActivity.onClick]", ex);
		}
	}
}
