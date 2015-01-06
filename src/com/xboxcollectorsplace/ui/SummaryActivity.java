package com.xboxcollectorsplace.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.utils.XLog;

/**
 * Windowed activity, statistics of the users collection are shown using ProgressBars. Statistics
 * can be changed for Arcade or Retail games using the available button
 */
public class SummaryActivity extends Activity implements OnClickListener
{		
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private TextView _txtOwnGames;
	private TextView _txtFinishedGames;
	private TextView _txtCompletedGames;
	private TextView _txtUnlockedAchievements;
	private RelativeLayout _lytAchievements;
	private ProgressBar _progressOwnGames;
	private ProgressBar _progressFinishedGames;
	private ProgressBar _progressCompletedGames;
	private ProgressBar _progressUnlockedAchievements;
	private Button _btnChange;
	
	private int _totalGames;
	private int _ownGames;
	private int _finishedGames;
	private int _completedGames;
	private int _totalAchievements;
	private int _unlockedAchievements;
	
	private boolean _isArcade;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_summary);
	        
	        this._txtOwnGames = (TextView)this.findViewById(R.id.txtOwnGames);
	        this._txtFinishedGames = (TextView)this.findViewById(R.id.txtFinishedGames);
	        this._txtCompletedGames = (TextView)this.findViewById(R.id.txtCompletedGames);
			this._txtUnlockedAchievements = (TextView)this.findViewById(R.id.txtUnlockedAchievements);
			this._lytAchievements = (RelativeLayout)this.findViewById(R.id.lytAchievements);
			this._progressOwnGames = (ProgressBar)this.findViewById(R.id.progressOwnGames);
			this._progressFinishedGames = (ProgressBar)this.findViewById(R.id.progressFinishedGames);
			this._progressCompletedGames = (ProgressBar)this.findViewById(R.id.progressCompletedGames);
			this._progressUnlockedAchievements = (ProgressBar)this.findViewById(R.id.progressUnlockedAchievements);
			this._btnChange = (Button)this.findViewById(R.id.btnChange);
			
			this._btnChange.setOnClickListener(this);
			
			this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
			
			this._btnChange.setText(this._isArcade ? getString(R.string.general_retail) : getString(R.string.general_arcade));
			((TextView)this.findViewById(R.id.txtTitle)).setText((this._isArcade ? getString(R.string.general_arcade) : getString(R.string.general_retail)) + " " + getString(R.string.summary_title));

			this.loadValues();
			this.setValues();
	    }
    	catch (Exception ex)
		{
			XLog.e("[SummaryActivity.onCreate]", ex);
        }
    }

    //------------------------------------------------------------------------- EVENTS*/
    
    /**
     * Event related to the touch of the arcade/retail button. It changes the buttons text and
     * loads the statistics of the selected collection (arcade or retail)
     */
	public void onClick(View view)
	{
		try
		{
			this._isArcade = !this._isArcade;
			
			this._btnChange.setText(this._isArcade ? getString(R.string.general_retail) : getString(R.string.general_arcade));
			((TextView)this.findViewById(R.id.txtTitle)).setText((this._isArcade ? getString(R.string.general_arcade) : getString(R.string.general_retail)) + " " + getString(R.string.summary_title));

			this.loadValues();
			this.setValues();
		}
    	catch (Exception ex)
		{
			XLog.e("[SummaryActivity.onClick]", ex);
        }
	}

    //------------------------------------------------------------------------- PRIVATE METHODS*/
    
	/**
	 * Values for the statistics are calculated from the stored user collection
	 */
    private void loadValues()
    {
    	try
		{
    		ArrayList<Game> gamesList = StorageController.loadCollection(this._isArcade).getCatalog();
    		
    		this._totalGames = 0;
	    	this._ownGames = 0;
	    	this._finishedGames = 0;
	    	this._completedGames = 0;
	    	this._totalAchievements = 0;
	    	this._unlockedAchievements = 0;
	    	    	
	    	for (Game game : gamesList)
	    	{
	    		this._totalGames++;
	    		
	    		switch (game.getStatus())
	    		{
		    		case COMPLETED:
		    			this._completedGames++;
		    			this._finishedGames++;
	    				this._ownGames++;
	    				this._totalAchievements += game.getTotalAchievements();
		        		this._unlockedAchievements += game.getCurrentAchievements();
		        		break;
		    		case FINISHED:
		    			this._finishedGames++;
		    			this._ownGames++;
	    				this._totalAchievements += game.getTotalAchievements();
		        		this._unlockedAchievements += game.getCurrentAchievements();
		    			break;
	    			case OWN:
	    				this._ownGames++;
	    				this._totalAchievements += game.getTotalAchievements();
	    	    		this._unlockedAchievements += game.getCurrentAchievements();
	    				break;
	    			default:
	    				this._totalAchievements += game.getTotalAchievements();
	    				this._unlockedAchievements += game.getCurrentAchievements();
	    				break;
	    		}
	    	}
		}
		catch (Exception ex) 
		{
			XLog.e("[SummaryActivity.loadValues]", ex);
		}
    }
    
    /**
     * Previously calculated values are shown on the corresponding controls
     */
    private void setValues()
    {
    	try
		{
    		this._progressOwnGames.setMax(this._totalGames);
			this._progressOwnGames.setProgress(this._ownGames);
		    this._txtOwnGames.setText(this._ownGames + "/" + this._totalGames);
		    this._txtOwnGames.bringToFront();
		    
		    this._progressFinishedGames.setMax(this._totalGames);
			this._progressFinishedGames.setProgress(this._finishedGames);
		    this._txtFinishedGames.setText(this._finishedGames + "/" + this._totalGames);
		    this._txtFinishedGames.bringToFront();
		    
		    this._progressCompletedGames.setMax(this._totalGames);
			this._progressCompletedGames.setProgress(this._completedGames);
		    this._txtCompletedGames.setText(this._completedGames + "/" + this._totalGames);
		    this._txtCompletedGames.bringToFront();
		    
		    this._progressUnlockedAchievements.setMax(this._totalAchievements);
			this._progressUnlockedAchievements.setProgress(this._unlockedAchievements);
		    this._txtUnlockedAchievements.setText(this._unlockedAchievements + "/" + this._totalAchievements);
		    this._lytAchievements.bringToFront();
		}
		catch (Exception ex) 
		{
			XLog.e("[SummaryActivity.setValues]", ex);
		}
    }
}