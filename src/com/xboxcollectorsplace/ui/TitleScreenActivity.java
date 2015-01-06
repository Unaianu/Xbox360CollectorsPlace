package com.xboxcollectorsplace.ui;

import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Options;
import com.xboxcollectorsplace.utils.XLog;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.VideoView;

/**
 * Launcher activity, it contains the title screen with the boot video and options to access 
 * both the arcade and the retail collections/catalogs
 */
public class TitleScreenActivity extends ActionBarActivity implements OnClickListener, OnTouchListener
{	
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private VideoView _videoBoot;
	private ImageView _imgCollection;
	private ImageView _imgArcadeCollection;
	
	private boolean _soundEnabled;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_title_screen);
	        
	        // The language is changed depending on what is stored in Shared Preferences (selected
	        // in the config menu by the user)
	        BLUtils.changeLanguage(StorageController.loadOptions().getLanguage());
	        
	        ActionBar actionBar = this.getSupportActionBar();
	        	actionBar.setDisplayShowHomeEnabled(false);
	        	actionBar.setDisplayShowTitleEnabled(false);

	        this._videoBoot = (VideoView)this.findViewById(R.id.videoBoot);
	        this._imgCollection = (ImageView)this.findViewById(R.id.imgCatalog);
	        this._imgArcadeCollection = (ImageView)this.findViewById(R.id.imgArcadeCatalog);
	        	        
	        this._videoBoot.setOnTouchListener(this);
	        this._imgCollection.setOnClickListener(this);
	        this._imgArcadeCollection.setOnClickListener(this);
	        
	        // The sound is enabled/disabled, the video view is linked to the media listener, and
	        // the video is played
	        this._soundEnabled = StorageController.loadSoundActive();
	        
	        this._videoBoot.setOnPreparedListener(PreparedListener);
	        this.playVideo();
	        
	        //TODO: Only for Google Play. This call was for downloading the covers expansion file
			//this.startActivity(new Intent(this, XboxInitialDownload.class));
	    }
    	catch (Exception ex)
		{
			XLog.e("[TitleScreenActivity.onCreate]", ex);
        }
    }
    
    //------------------------------------------------------------------------- ON RESUME*/
    
    /**
     * This onResume will redraw the arcade/retail icons to prevent a bug where they will be 
     * resized after changing the language in other activity
     */
    public void onResume()
    {
        super.onResume();
        
        try
        {
	        this._imgCollection.setImageDrawable(getResources().getDrawable(R.drawable.menu_retail));
	        this._imgArcadeCollection.setImageDrawable(getResources().getDrawable(R.drawable.menu_arcade));
        }
        catch (Exception ex)
        {
        	XLog.e("[TitleScreenActivity.onResume]", ex);
        }
    }
        
    //------------------------------------------------------------------------- LISTENER*/
    
    /**
     * Listener used by the video view
     */
    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener()
    {
		public void onPrepared(MediaPlayer m) 
        {
            try 
            {
            	if (m.isPlaying()) 
            	{
            		m.stop();
            		m.release();
            		m = new MediaPlayer();
            	}
            	
            	if (!_soundEnabled)
    	        {
            		m.setVolume(0f, 0f);
    	        }
            	
            	m.setLooping(false);
            	m.start();
            } 
            catch (Exception ex)
            {
            	XLog.e("[TitleScreenActivity.PreparedListener]", ex);
            }    
        }
    };
        
    //------------------------------------------------------------------------- EVENTS*/

    /**
     * Event related to the return from other activity. In most cases, the action bar will be
     * refreshed in case the language has been changed
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == BLParameters.ACTIVITY_CODES.CONFIG && resultCode == Activity.RESULT_OK)
			{
				this._soundEnabled = StorageController.loadSoundActive();
				this.supportInvalidateOptionsMenu();
		    }
			else if (requestCode == BLParameters.ACTIVITY_CODES.PHOTO_GALLERY)
			{
				this.supportInvalidateOptionsMenu();
		    }
			else if (requestCode == BLParameters.ACTIVITY_CODES.COLLECTION)
			{
				this.supportInvalidateOptionsMenu();
			}
		}
		catch (Exception ex)
		{
			XLog.e("[TitleScreenActivity.onActivityResult]", ex);
		}
	}
    
    /**
     * Event related to the touch of the arcade or retail icons. The collection activity will
     * be called, showing the arcade or retail collection, depending on the selection
     */
	public void onClick(View view) 
	{
		try
		{
			if (view.getId() == this._imgCollection.getId())
			{
				this._imgCollection.setImageDrawable(getResources().getDrawable(R.drawable.menu_retail_click));
				Intent intent = new Intent(this, CollectionActivity.class);
				intent.putExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
				startActivityForResult(intent, BLParameters.ACTIVITY_CODES.COLLECTION);
			}
			else if (view.getId() == this._imgArcadeCollection.getId())
			{
				this._imgArcadeCollection.setImageDrawable(getResources().getDrawable(R.drawable.menu_arcade_click));
				Intent intent = new Intent(this, CollectionActivity.class);
				intent.putExtra(BLParameters.PARAMETERS.IS_ARCADE, true);
				startActivityForResult(intent, BLParameters.ACTIVITY_CODES.COLLECTION);
			}
		}
        catch (Exception ex)
        {
        	XLog.e("[TitleScreenActivity.onClick]", ex);
        }
	}

	/**
	 * Event related to the touch of the video view. It will restart the selected video
	 */
	public boolean onTouch(View v, MotionEvent event) 
	{
		try
		{
			if (v.getId() == this._videoBoot.getId())
			{
				playVideo();
			}
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[TitleScreenActivity.onTouch]", ex);
	    }
		
		return false;
	}
	
	/**
	 * Event related to the creation of the action menu
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		try
		{
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.title_menu, menu);
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[TitleScreenActivity.onCreateOptionsMenu]", ex);
	    }
		
	    return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Event related to the display of the action menu. Depending on whether the sound is active
	 * or not, the mute icon and its title will change
	 */
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		try
		{
			if (this._soundEnabled)
			{
				menu.findItem(R.id.options_sound).setIcon(App.getContext().getResources().getDrawable(R.drawable.options_sound_off));
				menu.findItem(R.id.options_sound).setTitle(getString(R.string.options_sound_off));
			}
			else
			{
				menu.findItem(R.id.options_sound).setIcon(App.getContext().getResources().getDrawable(R.drawable.options_sound_on));
				menu.findItem(R.id.options_sound).setTitle(getString(R.string.options_sound_on));
			}
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[TitleScreenActivity.onPrepareOptionsMenu]", ex);
	    }
		
		return true;
	}
	
	/**
	 * Event related to the selection of one of the items of the action bar
	 */
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		try
		{
		    switch (item.getItemId()) 
		    {
			    case R.id.options_config:
			    	Intent intent = new Intent(this, ConfigActivity.class);
		    		startActivityForResult(intent, BLParameters.ACTIVITY_CODES.CONFIG);
		    		break;
			    case R.id.options_sound:
			    	this._soundEnabled = !this._soundEnabled;
			    	this.supportInvalidateOptionsMenu();
			    	StorageController.saveSoundActive(this._soundEnabled);
			        this.playVideo();
	    			break;
			    case R.id.options_summary:
			    	intent = new Intent(this, SummaryActivity.class);
			    	intent.putExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
			    	startActivity(intent);
			        break;
			    case R.id.options_export:
			    	intent = new Intent(this, FileSelectorActivity.class);
			    	intent.putExtra(BLParameters.PARAMETERS.IMPORT_MODE, false);
			    	startActivity(intent);
			        break;
			    case R.id.options_import:
			    	intent = new Intent(this, FileSelectorActivity.class);
			    	intent.putExtra(BLParameters.PARAMETERS.IMPORT_MODE, true);
			    	startActivity(intent);
			        break;
			    case R.id.options_photo_gallery:
			    	intent = new Intent(this, PhotoGalleryActivity.class);
			    	startActivityForResult(intent, BLParameters.ACTIVITY_CODES.PHOTO_GALLERY);
			        break;
		    }
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[TitleScreenActivity.onOptionsItemSelected]", ex);
	    }
		
		return true;
	}
	
	//------------------------------------------------------------------------- PRIVATE METHODS*/
	
	/**
	 * Method for loading and starting one of the videos selected on the config menu 
	 * (New Boot or Old Boot). The selection is stored on Shared Preferences.
	 */
	private void playVideo()
	{
		try
		{
			this._videoBoot.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" 
					+ (StorageController.loadOptions().getBoot() == Options.BootType.NEW ? R.raw.boot_new : R.raw.boot_old)));
			
	        this._videoBoot.requestFocus();
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[TitleScreenActivity.playVideo]", ex);
	    }
	}
}
