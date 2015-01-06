package com.xboxcollectorsplace.ui;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Windowed activity, used to edit the number of unlocked achievements of a game. It only contains 
 * a Button and an EditText, which contains a listener that prevents from entering a number bigger
 * than the total number of achievements of the game
 */
public class EditAchievementsActivity extends Activity implements OnClickListener 
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private EditText _edtAchievements;
	private Button _btnOK;
	
	private int _totalAchievements;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_edit_achievements);
	        	 
	        this._edtAchievements = (EditText) this.findViewById(R.id.edtAchievements);
	        this._btnOK = (Button) this.findViewById(R.id.btnOK);
	        	        
	        if (this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.TOTAL_ACHIEVEMENTS))
			{
	        	this._totalAchievements = this.getIntent().getExtras().getInt(BLParameters.PARAMETERS.TOTAL_ACHIEVEMENTS, 0);
	        }
	        
	        if (this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.CURRENT_ACHIEVEMENTS))
			{
	        	this._edtAchievements.setText(String.valueOf(this.getIntent().getExtras().getInt(BLParameters.PARAMETERS.CURRENT_ACHIEVEMENTS, 0)));
	        	
	        	if ("0".equals(this._edtAchievements.getText().toString()))
    			{
	        		this._edtAchievements.setText("");
    			}
	        	
	        	this._edtAchievements.setSelection(this._edtAchievements.length());
	        }
	        
	        this._btnOK.setOnClickListener(this);
	        this._edtAchievements.addTextChangedListener(new TextWatcher() 
	        {
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
				}
				
				public void beforeTextChanged(CharSequence s, int start, int count, int after) 
				{
				}
				
				public void afterTextChanged(Editable s) 
				{
					if (!TextUtils.isEmpty(_edtAchievements.getText().toString()))
					{
						int selectedAchievements = Integer.valueOf(_edtAchievements.getText().toString());
						
						if (selectedAchievements < 0)
						{
							_edtAchievements.setText("0");
							_edtAchievements.setSelection(_edtAchievements.length());
						}
						else if (selectedAchievements > _totalAchievements)
						{
							_edtAchievements.setText(String.valueOf(_totalAchievements));
							_edtAchievements.setSelection(_edtAchievements.length());
						}
					}
				}
			});
	    }
    	catch (Exception ex)
		{
			XLog.e("[EditAchievementsActivity.onCreate]", ex);
        }
    }
    
    //------------------------------------------------------------------------- EVENTS*/
	
    /**
     * Event related to the touch of the OK button. Returns the new value to the caller activity
     */
	public void onClick(View view)
	{
		try
		{
			if (view.getId() == this._btnOK.getId())
			{
				Intent output = new Intent();
				output.putExtra(BLParameters.PARAMETERS.CURRENT_ACHIEVEMENTS, !TextUtils.isEmpty(this._edtAchievements.getText().toString()) ? Integer.valueOf(this._edtAchievements.getText().toString()) : 0);
				this.setResult(RESULT_OK, output);
				this.finish();
			}
		}
		catch (Exception ex)
		{
			XLog.e("[EditAchievementsActivity.onClick]", ex);
		}
	}
}
