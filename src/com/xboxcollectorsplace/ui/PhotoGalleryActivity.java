package com.xboxcollectorsplace.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.GamePhoto;
import com.xboxcollectorsplace.bl.entities.GamePhotoGallery;
import com.xboxcollectorsplace.bl.entities.OptionString;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Full screen activity. The first photo linked to the selected game is shown along with its title
 * (in case there is any photo). There are options to navigate through different photos, rotate 
 * the photo, edit its title, remove it, change the game between those with photos, take a new
 * photo with the camera, or link a new image selected from the device
 * 
 * NOTE: This class was not intended to be included yet, bugs and poor optimization
 * are to be expected
 */
public class PhotoGalleryActivity extends ActionBarActivity implements OnClickListener, OnItemSelectedListener
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final String SAVE_CURRENT_GAME = "SAVE_CURRENT_GAME";
	private static final String SAVE_CURRENT_GAME_TITLE = "SAVE_CURRENT_GAME_TITLE";
	private static final String SAVE_CURRENT_PATH = "SAVE_CURRENT_PATH";
	private static final String SAVE_NEW_PHOTO_PATH = "SAVE_NEW_PHOTO_PATH";
	private static final String SAVE_CURRENT_POSITION = "SAVE_CURRENT_POSITION";
	private static final String SAVE_RELOAD = "SAVE_RELOAD";
	private static final String SAVE_CURRENT_PHOTO_GALLERY = "SAVE_CURRENT_PHOTO_GALLERY";
	private static final String SAVE_IS_ARCADE = "SAVE_IS_ARCADE";
	
	//------------------------------------------------------------------------- VARIABLES*/
	
	private Spinner _spnGameSelection;
	private TextView _txtPosition;
	private TextView _txtPhotoTitle;
	private ImageView _imgPhoto;
	private ImageButton _btnNext;
	private ImageButton _btnPrevious;
	private ImageButton _btnEdit;
	private ImageButton _btnRemove;
	private ImageButton _btnRotateLeft;
	private ImageButton _btnRotateRight;
	private RelativeLayout _lytBackground;
	
	private String _currentGame = "";
	private String _currentGameTitle = "";
	private String _currentPath = "";
	private String _newPhotoPath = "";
	private int _currentPosition = 0;
	private boolean _reload = false;
	private boolean _isArcade = false;
	
	private GamePhotoGallery _currentGamePhotoGallery;
	private OptionAdapter _gameSelectionAdapter;
	private ProgressDialog _loadingScreen;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
    		this.setContentView(R.layout.activity_photo_gallery);
			
    		// The language is changed depending on what is stored in Shared Preferences (selected
	        // in the config menu by the user). This has to be done in case the language has been
    		// automatically changed from the camera activity
    		BLUtils.changeLanguage(StorageController.loadOptions().getLanguage());
    		
			ActionBar actionBar = this.getSupportActionBar();
	        	actionBar.setDisplayShowHomeEnabled(false);
	        	actionBar.setDisplayShowTitleEnabled(false);
	        	
        	this._spnGameSelection = (Spinner)this.findViewById(R.id.spnGameSelection);
		    this._imgPhoto = (ImageView)this.findViewById(R.id.imgPhoto);
		    this._txtPosition = (TextView)this.findViewById(R.id.txtPosition);
		    this._txtPhotoTitle = (TextView)this.findViewById(R.id.txtPhotoTitle);
		    this._btnNext = (ImageButton)this.findViewById(R.id.btnNextPhoto);
		    this._btnPrevious = (ImageButton)this.findViewById(R.id.btnPreviousPhoto);
		    this._btnEdit = (ImageButton)this.findViewById(R.id.btnEdit);
		    this._btnRemove = (ImageButton)this.findViewById(R.id.btnRemove);
		    this._btnRotateLeft = (ImageButton)this.findViewById(R.id.btnRotateLeft);
		    this._btnRotateRight = (ImageButton)this.findViewById(R.id.btnRotateRight);
		    this._lytBackground = (RelativeLayout)this.findViewById(R.id.lytBackground);
		    
		    this._btnNext.setOnClickListener(this);
		    this._btnPrevious.setOnClickListener(this);
		    this._btnEdit.setOnClickListener(this);
		    this._btnRemove.setOnClickListener(this);
			this._imgPhoto.setOnClickListener(this);
		    this._btnRotateLeft.setOnClickListener(this);
			this._btnRotateRight.setOnClickListener(this);  
		    
			((TextView)this.findViewById(R.id.txtGame)).setText(getString(R.string.photo_gallery_game));
			
			// The camera activity could have been destroyed the activity instance, in that case 
			// savedInstanceState wont be null and values will be taken from there. Also, the
			// photo taken will be processed if it has not been processed yet (_newPhotoPath has
			// a value if it has not been processed)
    		if (savedInstanceState != null) 
    		{
    			this._currentGame = savedInstanceState.getString(PhotoGalleryActivity.SAVE_CURRENT_GAME);
    			this._currentGameTitle = savedInstanceState.getString(PhotoGalleryActivity.SAVE_CURRENT_GAME_TITLE);
    			this._currentPath = savedInstanceState.getString(PhotoGalleryActivity.SAVE_CURRENT_PATH);
    			this._newPhotoPath = savedInstanceState.getString(PhotoGalleryActivity.SAVE_NEW_PHOTO_PATH);
    			this._currentPosition = savedInstanceState.getInt(PhotoGalleryActivity.SAVE_CURRENT_POSITION, 0);
    			this._reload = savedInstanceState.getBoolean(PhotoGalleryActivity.SAVE_RELOAD, false);
    			this._currentGamePhotoGallery = (GamePhotoGallery) savedInstanceState.getSerializable(PhotoGalleryActivity.SAVE_CURRENT_PHOTO_GALLERY);
    			this._isArcade = savedInstanceState.getBoolean(PhotoGalleryActivity.SAVE_IS_ARCADE, false);
    			
    			if (!TextUtils.isEmpty(this._newPhotoPath))
    			{
    				this.processPhotoTaken();
    			}
    		}
    		else
    		{
				if (this.getIntent() != null && this.getIntent().getExtras() != null
						&& this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.GAME)
						&& this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.GAME_TITLE))
			    {
			    	this._currentGame = this.getIntent().getStringExtra(BLParameters.PARAMETERS.GAME);
			    	this._currentGameTitle = this.getIntent().getStringExtra(BLParameters.PARAMETERS.GAME_TITLE);
			    }
				else
				{
					this._currentGame = BLParameters.PHOTO_GALLERY.GENERAL_FOLDER;
					this._currentGameTitle = BLParameters.PHOTO_GALLERY.GENERAL_FOLDER;
				}
				
				if (this.getIntent() != null && this.getIntent().getExtras() != null
						&& this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.IS_ARCADE))
			    {
					this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
			    }
				
				if (this.getIntent() != null && this.getIntent().getExtras() != null
						&& this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.CURRENT_POSITION))
			    {
					this._currentPosition = this.getIntent().getIntExtra(BLParameters.PARAMETERS.CURRENT_POSITION, 0);
			    }
    		}
		    
    		this.initializeGameGallery();
	    }
    	catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.onCreate]", ex);
		}
    }
    
    //------------------------------------------------------------------------- EVENTS*/

    /**
     * Called before destroying the instance, in order to save the parameters
     */
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putString(PhotoGalleryActivity.SAVE_CURRENT_GAME, this._currentGame);
		savedInstanceState.putString(PhotoGalleryActivity.SAVE_CURRENT_GAME_TITLE, this._currentGameTitle);
		savedInstanceState.putString(PhotoGalleryActivity.SAVE_CURRENT_PATH, this._currentPath);
		savedInstanceState.putString(PhotoGalleryActivity.SAVE_NEW_PHOTO_PATH, this._newPhotoPath);
		savedInstanceState.putInt(PhotoGalleryActivity.SAVE_CURRENT_POSITION, this._currentPosition);
		savedInstanceState.putBoolean(PhotoGalleryActivity.SAVE_RELOAD, this._reload);
		savedInstanceState.putSerializable(PhotoGalleryActivity.SAVE_CURRENT_PHOTO_GALLERY, this._currentGamePhotoGallery);
		savedInstanceState.putBoolean(PhotoGalleryActivity.SAVE_IS_ARCADE, this._isArcade);
	}
	
	/**
     * Called when restoring the instance
     */
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	/**
	 * Event related to the push of the back button. If the collection is to be reloaded (mainly
	 * because the language has been changed, or a new collection has been imported)
	 * it returns an OK and a true boolean to the caller activity
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) 
  	{
  	    if (keyCode == KeyEvent.KEYCODE_BACK) 
  	    {
  	    	Intent output = new Intent();
  	    	output.putExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, this._reload);
  	    	this.setResult(RESULT_OK, output);
  	    	this.finish();
  	    	
  	        return true;
  	    }
  	    
  	    return super.onKeyDown(keyCode, event);
  	}
	
	/**
	 * Event related to the creation of the action menu
	 */
	public boolean onCreateOptionsMenu(Menu menu)
    {
  		try
  		{
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.photo_gallery_menu, menu);
	    }
		catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.onCreateOptionsMenu]", ex);
		}
  		
        return true;
    }
    
	/**
	 * Event related to the selection of one of the items of the action bar
	 */
    public boolean onOptionsItemSelected(MenuItem item) 
	{
    	Intent output = new Intent();
    	
    	try
		{
		    switch (item.getItemId()) 
		    {
			    case R.id.options_take_photo:
			    	this.openCamera();
			    	break;
			    case R.id.options_select_photo:
			    	output = new Intent(this, FileSelectorActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IMAGE_SELECTION_MODE, true);
			    	startActivityForResult(output, BLParameters.ACTIVITY_CODES.IMAGE_SELECTION);
			    	break;
			    case R.id.options_summary:
			    	output = new Intent(this, SummaryActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
			    	startActivity(output);
			        break;
			    case R.id.options_export:
			    	output = new Intent(this, FileSelectorActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IMPORT_MODE, false);
			    	startActivity(output);
			        break;
			    case R.id.options_import:
			    	output = new Intent(this, FileSelectorActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IMPORT_MODE, true);
			    	startActivityForResult(output, BLParameters.ACTIVITY_CODES.IMPORT);
			        break;
			    case R.id.options_config:
		    		startActivityForResult(new Intent(this, ConfigActivity.class), BLParameters.ACTIVITY_CODES.CONFIG);
			        break;
		    }
		}
		catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.onOptionsItemSelected]", ex);
		}
    	
    	return true;
	}
    
    /**
     * Event related to the return from other activity.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	try
   	 	{
	    	if (requestCode == BLParameters.ACTIVITY_CODES.CAMERA && resultCode == Activity.RESULT_OK)
			{
	    		// The photo taken will be processed if it has not been processed yet 
	    		// (_newPhotoPath has a value if it has not been processed)
    			if (!TextUtils.isEmpty(this._newPhotoPath))
    			{
    				processPhotoTaken();
    			}
			}
	    	else if (requestCode == BLParameters.ACTIVITY_CODES.IMAGE_SELECTION && resultCode == Activity.RESULT_OK)
	    	{
	    		// An image has been selected. The image is added to the gallery, the changes
	    		// saved to Shared Preferences and the new photo displayed in screen
    			this._newPhotoPath = data.getStringExtra(BLParameters.PARAMETERS.SELECTED_PATH);
    		
    			if (!TextUtils.isEmpty(this._newPhotoPath))
    			{
    				GamePhoto photo = new GamePhoto(getString(R.string.photo_gallery_photo) + " " + String.valueOf(this._currentGamePhotoGallery.getPhotos().size() + 1), this._newPhotoPath, true);
    				this._currentGamePhotoGallery.getPhotos().add(photo);
    				
    				this._currentPosition = this._currentGamePhotoGallery.getPhotos().size() - 1;
    				this.saveChanges();
    				
    				this.enableDisableButtons();
    				this.loadCurrentPhoto();
    			}
	    	}
	    	else if (requestCode == BLParameters.ACTIVITY_CODES.EDIT_TITLE && resultCode == Activity.RESULT_OK)
	    	{
	    		// The photo title has been edited. The TextView must be redrawn and the changes
	    		// saved in Shared Preferences
	    		if (data != null)
				{
	    			String newTitle = data.getStringExtra(BLParameters.PARAMETERS.TITLE);
	    			this._txtPhotoTitle.setText(newTitle);
	    			this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).setTitle(newTitle);
	    			this.saveChanges();
	    		}
	    	}
	    	else if (requestCode == BLParameters.ACTIVITY_CODES.CONFIG && resultCode == Activity.RESULT_OK)
	    	{
	    		// Language has changed, texts must be redrawn
	    		((TextView)this.findViewById(R.id.txtGame)).setText(getString(R.string.photo_gallery_game));
	    		if (this._currentGamePhotoGallery.getPhotos() == null 
		        		|| !(this._currentGamePhotoGallery.getPhotos().size() > this._currentPosition))
		        {
	    			this._txtPhotoTitle.setText(getString(R.string.photo_gallery_no_photos));
		        }
	    		
	    		this.supportInvalidateOptionsMenu();
	    	}
	    	else if (requestCode == BLParameters.ACTIVITY_CODES.IMPORT && resultCode == Activity.RESULT_OK)
	    	{
	    		// A new collection has been imported, it must be reloaded
	    		this._reload = true;
	    	}
   	 	}
   	 	catch (Exception ex) 
	    {
   	 		XLog.e("[PhotoGalleryActivity.onActivityResult]", ex);
	    }
    }
    
    /**
     * Event related to the touch of one of the buttons
     */
	public void onClick(View view)
	{
		try
		{
			if (view.getId() == this._btnNext.getId())
			{
				// Next button clicked. The next photo is displayed in screen
				this._currentPosition++;
				
				this.enableDisableButtons();
				this.loadCurrentPhoto();
			}
			else if (view.getId() == this._btnPrevious.getId())
			{
				// Previous button clicked. The previous photo is displayed in screen
				this._currentPosition--;
				
				this.enableDisableButtons();
				this.loadCurrentPhoto();
			}
			else if (view.getId() == this._btnRemove.getId())
			{
				// Remove button clicked. A confirmation dialog is shown before deleting the photo
				this.showDeleteConfirmationDialog(this.getString(R.string.photo_gallery_delete_photo));
			}
			else if (view.getId() == this._btnEdit.getId())
			{
				// Title edit button clicked. Activity is called to edit the title
				Intent intent = new Intent(this, EditPhotoTitleActivity.class);
				intent.putExtra(BLParameters.PARAMETERS.TITLE, this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getTitle());
	    		startActivityForResult(intent, BLParameters.ACTIVITY_CODES.EDIT_TITLE);
			}
			else if (view.getId() == this._btnRotateLeft.getId())
			{
				// Rotate photo left is clicked. Asynchronous method is called to rotate the photo
				// 90 degrees to the left
				new RotatePhoto().execute(-90);
			}
			else if (view.getId() == this._btnRotateRight.getId())
			{
				// Rotate photo right is clicked. Asynchronous method is called to rotate the photo
				// 90 degrees to the right
				new RotatePhoto().execute(90);
			}
			else if (view.getId() == this._imgPhoto.getId())
			{
				// Photo is selected. If there is a photo displayed, the image viewer of the
				// device is called to display the image on full screen. Else, the device camera
				// is called
				if (this._currentGamePhotoGallery.getPhotos() != null 
		        		&& this._currentGamePhotoGallery.getPhotos().size() > this._currentPosition)
		        {
					File imageFile = new File(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getPath());
					
					if (imageFile.exists())
					{
						Intent intent = new Intent();
			 			intent.setAction(Intent.ACTION_VIEW);
			 			intent.setDataAndType(Uri.fromFile(imageFile), "image/*");
			 			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 			
						startActivity(intent);
					}
		        }
				else
				{
					this.openCamera();
				}
			}
		}
        catch (Exception ex)
        {
        	XLog.e("[PhotoGalleryActivity.onClick]", ex);
        }
	}
	
	/**
     * Event related to the selection of an item of the game spinner. When a game is selected,
     * the gallery of the selected game is loaded, and its first photo displayed
     */
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		try
		{
			String originalGame = this._currentGame;
			this._currentGame = this._gameSelectionAdapter.getItem(position).getOptionKey();
			this._currentGameTitle = this._gameSelectionAdapter.getItem(position).getOptionName();
			this._currentPath = Environment.getExternalStorageDirectory().getAbsolutePath()
	        		+ "/" + BLParameters.PHOTO_GALLERY.XBOX_FOLDER
	        		+ "/" + this._currentGame + "/";
			
			if (!originalGame.equals(this._currentGame))
			{
				this._currentPosition = 0;
			}
			
	    	File currentFolder = new File(this._currentPath);
	        if (!currentFolder.exists())
	        {
	        	currentFolder.mkdirs();
	        }
	        
	        ArrayList<GamePhotoGallery> photoGallery = StorageController.loadPhotoGallery();
	        
	        if (photoGallery != null && photoGallery.size() > 0)
	        {
	        	boolean currentGameFound = false;
	        	for (GamePhotoGallery gamePhotoGallery : photoGallery)
	        	{
	        		if (gamePhotoGallery != null)
	        		{
	        			if (gamePhotoGallery.getGameId().equals(this._currentGame))
		        		{
		        			this._currentGamePhotoGallery = gamePhotoGallery;
		        			currentGameFound = true;
		        			break;
		        		}
	        		}
	        	}
	        	
	        	if (!currentGameFound)
	        	{
	        		this._currentGamePhotoGallery = new GamePhotoGallery(this._currentGame, this._currentGameTitle, new ArrayList<GamePhoto>());
	        	}
	        }
	        else
	        {
	        	this._currentGamePhotoGallery = new GamePhotoGallery(this._currentGame, this._currentGameTitle, new ArrayList<GamePhoto>());
	        }
	        	        
	        this.enableDisableButtons();
	        this.loadCurrentPhoto();
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[PhotoGalleryActivity.onItemSelected]", ex);
	    }
	}

	public void onNothingSelected(AdapterView<?> parent) 
	{
	}
	
	//------------------------------------------------------------------------- PRIVATE METHODS*/

	/**
     * Initializes the gallery loading it from Shared Preferences, and searchs for the current
     * game inside it. If its not found, its temporarily added (it wont be saved in Shared
     * Preferences until at least one photo or image is added).
     */
    private void initializeGameGallery()
    {
    	try
    	{
    		ArrayList<GamePhotoGallery> photoGallery = StorageController.loadPhotoGallery();
	        ArrayList<OptionString> gameSelectionValues = new ArrayList<OptionString>();
			int gameSelectionInitialPosition = 0;
	        
	        if (photoGallery != null && photoGallery.size() > 0)
	        {
	        	boolean currentGameFound = false;
	        	for (GamePhotoGallery gamePhotoGallery : photoGallery)
	        	{
	        		if (gamePhotoGallery != null)
	        		{
	        			gameSelectionValues.add(new OptionString(gamePhotoGallery.getGameTitle(), gamePhotoGallery.getGameId()));
	        		
		        		if (gamePhotoGallery.getGameId().equals(this._currentGame))
		        		{
		        			this._currentGamePhotoGallery = gamePhotoGallery;
		        			currentGameFound = true;
		        			gameSelectionInitialPosition = gameSelectionValues.size() - 1;
		        		}
	        		}
	        	}
	        	
	        	if (!currentGameFound)
	        	{
	        		this._currentGamePhotoGallery = new GamePhotoGallery(this._currentGame, this._currentGameTitle, new ArrayList<GamePhoto>());
	        		gameSelectionValues.add(new OptionString(this._currentGameTitle, this._currentGame));
	        		gameSelectionInitialPosition = gameSelectionValues.size() - 1;
	        	}
	        }
	        else
	        {
	        	this._currentGamePhotoGallery = new GamePhotoGallery(this._currentGame, this._currentGameTitle, new ArrayList<GamePhoto>());
	        	gameSelectionValues.add(new OptionString(this._currentGameTitle, this._currentGame));
        		gameSelectionInitialPosition = gameSelectionValues.size() - 1;
	        }
	        
			this._gameSelectionAdapter = new OptionAdapter(this, R.layout.simple_spinner_item_custom, gameSelectionValues);
			this._gameSelectionAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnGameSelection.setAdapter(this._gameSelectionAdapter);
			this._spnGameSelection.setOnItemSelectedListener(this);
			this._spnGameSelection.setSelection(gameSelectionInitialPosition);
    	}
    	catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.initializeGameGallery]", ex);
		}
    }
    
    /**
     * Adds the photo taken with the camera to the gallery of the current game, naming it
     * "photo X", being X the number of photos of the gallery + 1. To avoid problems after
     * returning from the camera, the activity is reset
     */
    private void processPhotoTaken()
    {
    	try
    	{
    		GamePhoto photo = new GamePhoto(getString(R.string.photo_gallery_photo) + " " + String.valueOf(this._currentGamePhotoGallery.getPhotos().size() + 1), this._newPhotoPath, false);
			this._currentGamePhotoGallery.getPhotos().add(photo);
			
			this._currentPosition = this._currentGamePhotoGallery.getPhotos().size() - 1;
			
			this.saveChanges();
			Intent intent = new Intent(this, PhotoGalleryActivity.class);
			intent.putExtra(BLParameters.PARAMETERS.GAME, this._currentGame);
			intent.putExtra(BLParameters.PARAMETERS.GAME_TITLE, this._currentGameTitle);
  	    	intent.putExtra(BLParameters.PARAMETERS.CURRENT_POSITION, this._currentPosition);
  	    	startActivity(intent);
			this.finish();
    	}
    	catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.processPhoto]", ex);
		}
    }
    
    /**
     * Loads the current photo, displaying it in the ImageView and showing its title if exists.
     */
    private void loadCurrentPhoto()
    {
    	try
    	{
    		if (this._currentGamePhotoGallery.getPhotos() != null 
	        		&& this._currentGamePhotoGallery.getPhotos().size() > this._currentPosition)
	        {
    			this._txtPosition.setText((this._currentPosition + 1) + "/" + this._currentGamePhotoGallery.getPhotos().size());
    			this._txtPosition.setVisibility(TextView.VISIBLE);
	        	this._txtPhotoTitle.setText(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getTitle());
    			
    			if (new File(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getPath()).exists())
    			{
	    			Bitmap photoBitmap = BitmapFactory.decodeFile(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getPath());
	    			this._lytBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.view_border_black));
		        	this._imgPhoto.setImageBitmap(photoBitmap);
    			}
    			else
    			{
    				this._lytBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.view_border_grey));
    	        	this._imgPhoto.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_no_photo));
    			}
	        }
	        else
	        {
	        	this._txtPosition.setText("");
	        	this._txtPosition.setVisibility(TextView.GONE);
	        	this._txtPhotoTitle.setText(getString(R.string.photo_gallery_no_photos));
	        	this._lytBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.view_border_grey));
	        	this._imgPhoto.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_no_photo));
	        }
    	}
    	catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.loadCurrentPhoto]", ex);
		}
    }
    
    /**
     * Enables or disables the different buttons according to the following: 
     * 
     * Next button is enabled if there are more photos on the current games gallery after the
     * displayed one (if there is anyone)
     * Previous button is enabled if there are more photos on the current games gallery before
     * the displayed one (if there is anyone)
     * Edit and remove buttons are enabled if there is a photo being displayed
     * Rotate left and right buttons are enabled if there is a photo being displayed and it has
     * been taken with the camera, not selected from the device
     */
    private void enableDisableButtons()
	{
		try
		{
			if (this._currentGamePhotoGallery.getPhotos().size() == 0 || 
					this._currentGamePhotoGallery.getPhotos().size() == 1)
			{
				this._btnNext.setEnabled(false);
				this._btnPrevious.setEnabled(false);
			}
			else
			{
				if (this._currentPosition == 0)
				{
					this._btnPrevious.setEnabled(false);
				}
				else
				{
					this._btnPrevious.setEnabled(true);
				}
				
				if (this._currentPosition >= (this._currentGamePhotoGallery.getPhotos().size() - 1))
				{
					this._btnNext.setEnabled(false);
				}
				else
				{
					this._btnNext.setEnabled(true);
				}
			}
			
			if (this._currentGamePhotoGallery.getPhotos() != null 
					&& this._currentGamePhotoGallery.getPhotos().size() > this._currentPosition
	        		&& this._currentGamePhotoGallery.getPhotos().get(this._currentPosition) != null)
	        {
				this._btnEdit.setEnabled(true);
				this._btnRemove.setEnabled(true);
				
				if (new File(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getPath()).exists())
				{
					if (this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getImported())
					{
						this._btnRotateLeft.setEnabled(false);
						this._btnRotateRight.setEnabled(false);
					}
					else
					{
						this._btnRotateLeft.setEnabled(true);
						this._btnRotateRight.setEnabled(true);
					}
				}
				else
				{
					this._btnRotateLeft.setEnabled(false);
					this._btnRotateRight.setEnabled(false);
				}
	        }
			else
			{
				this._btnEdit.setEnabled(false);
				this._btnRemove.setEnabled(false);
				this._btnRotateLeft.setEnabled(false);
				this._btnRotateRight.setEnabled(false);
			}
		}
        catch (Exception ex)
        {
        	XLog.e("[PhotoGalleryActivity.enableDisableButtons]", ex);
        }
	}
    
    /**
     * Opens the device camera, indicating the path and name to save in
     */
    private void openCamera()
    {
   	 	try
   	 	{
   	 		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   	 		this._newPhotoPath = this._currentPath +
   	 			String.valueOf(Calendar.getInstance(Locale.getDefault()).getTimeInMillis()) +
   	 			BLParameters.PHOTO_GALLERY.IMAGE_FORMAT;
   	 			
   	 		File photoFile = new File(this._newPhotoPath);
   	 		Uri photoUri = Uri.fromFile(photoFile);
   	 		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
   	 		
   	 		this.startActivityForResult(cameraIntent, BLParameters.ACTIVITY_CODES.CAMERA);
   	 	}
   	 	catch (Exception ex) 
	    {
   	 		XLog.e("[PhotoGalleryActivity.openCamera]", ex);
	    }
    }
    
    /**
     * Stores the changes made to the gallery in Shared Preferences
     */
    private void saveChanges()
    {
    	try
   	 	{
    		ArrayList<GamePhotoGallery> photoGallery = StorageController.loadPhotoGallery();
	        
	        if (photoGallery.size() > 0)
	        {
	        	boolean currentGameFound = false;
	        	for (GamePhotoGallery gamePhotoGallery : photoGallery)
	        	{
	        		if (gamePhotoGallery != null && gamePhotoGallery.getGameId().equals(this._currentGame))
	        		{
	        			gamePhotoGallery.setGameId(this._currentGamePhotoGallery.getGameId());
	        			gamePhotoGallery.setPhotos(this._currentGamePhotoGallery.getPhotos());
	        			gamePhotoGallery.setTitle(this._currentGamePhotoGallery.getGameTitle());
	        			currentGameFound = true;
	        			break;
	        		}
	        	}
	        	
	        	if (!currentGameFound)
	        	{
	        		photoGallery.add(this._currentGamePhotoGallery);
	        	}
	        }
	        else
	        {
	        	photoGallery.add(this._currentGamePhotoGallery);
	        }
	        
	        StorageController.savePhotoGallery(photoGallery);
   	 	}
   	 	catch (Exception ex) 
	    {
   	 		XLog.e("[PhotoGalleryActivity.saveChanges]", ex);
	    }
    }
    
    /**
     * Shows or hides the loading screen, depending on the "show" parameter
     */
    private void showHideLoadingScreen(String loadingText, boolean show)
    {
    	try
    	{
    		if (show)
    		{
    			if (this._loadingScreen != null && this._loadingScreen.isShowing())
    			{
    				this._loadingScreen.dismiss();
    			}
    			
    			this._loadingScreen = ProgressDialog.show(this, null, loadingText, show);	
            	this._loadingScreen.show();
    		}
    		else
    		{
    			if (this._loadingScreen != null && this._loadingScreen.isShowing())
    			{
    				this._loadingScreen.dismiss();
    			}
    		}
    	}
		catch (Exception ex)
		{
			XLog.e("[PhotoGalleryActivity.showLoadingScreen]", ex);
		}
    }
    
    /**
     * Displays a confirmation dialog indicating that a photo is going to be deleted
     */
    private void showDeleteConfirmationDialog(String dialogText)
 	{
     	try
     	{
     		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
	     		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_custom_alert);
				
			TextView txtDialog = (TextView) dialog.findViewById(R.id.txtDialog);
				txtDialog.setText(dialogText);
				
			Button btnDialogPositive = (Button) dialog.findViewById(R.id.btnDialogPositive);
				btnDialogPositive.setText(getString(R.string.general_yes));
				btnDialogPositive.setOnClickListener(new OnClickListener() {
					public void onClick(View v) 
					{
						deletePhoto();
						dialog.dismiss();
					}
				});
				
			Button btnDialogNegative = (Button) dialog.findViewById(R.id.btnDialogNegative);
				btnDialogNegative.setText(getString(R.string.general_no));
				btnDialogNegative.setOnClickListener(new OnClickListener() {
					public void onClick(View v) 
					{
						dialog.dismiss();
					}
				});
			
			dialog.show();
 		}
 		catch (Exception ex)
 		{
 			XLog.e("[PhotoGalleryActivity.showDeleteConfirmationDialog]", ex);
 		}
 	}
    
    /**
     * Deletes a photo from the gallery. If its a photo taken with the camera, the photo is
     * removed from the device, else if its a image imported from the deviced, its not deleted,
     * instead the link is broken so that the image cannot be related to the gallery.
     * Also, if the current game has no more photos to display, it is removed from the gallery,
     * so that its not available when entering at the gallery from other game
     */
    private void deletePhoto()
 	{
    	try
     	{
    		// File is deleted only if its not imported and it still exists
	    	File photoRemove = new File(this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getPath());
			if (photoRemove.exists() && !this._currentGamePhotoGallery.getPhotos().get(this._currentPosition).getImported())
			{
				photoRemove.delete();
			}
			
			// Link is broken
			this._currentGamePhotoGallery.getPhotos().remove(this._currentPosition);
			
			if (!(this._currentGamePhotoGallery.getPhotos().size() > 0))
			{
				// If game has no more photos, it is removed from the gallery
				ArrayList<GamePhotoGallery> photoGallery = StorageController.loadPhotoGallery();
		        
		        if (photoGallery.size() > 0)
		        {
		        	for (GamePhotoGallery gamePhotoGallery : photoGallery)
		        	{
		        		if (gamePhotoGallery != null && gamePhotoGallery.getGameId().equals(this._currentGame))
		        		{
		        			photoGallery.remove(gamePhotoGallery);
			        		break;
		        		}
		        	}
		        }
		        
		        StorageController.savePhotoGallery(photoGallery);
			}
			else
			{
				this.saveChanges();
			}
			
			// If there are not more photos after the one removed but there are still more photos
			// before it, the previous one is displayed
			if (!(this._currentGamePhotoGallery.getPhotos().size() > this._currentPosition)
					&& this._currentPosition > 0)
			{
				this._currentPosition--;
			}
			
			this.enableDisableButtons();
			this.loadCurrentPhoto();
			
			BLUtils.showToast(getString(R.string.photo_gallery_photo_deleted), true);
 		}
 		catch (Exception ex)
 		{
 			XLog.e("[PhotoGalleryActivity.deletePhoto]", ex);
 		}
 	}
    
    //------------------------------------------------------------------------- INNER CLASSES*/
    
    /**
	 * Adapter used by the game spinner.
	 */
    private class OptionAdapter extends ArrayAdapter<OptionString>
    {
        private ArrayList<OptionString> values;

        public OptionAdapter(Context context, int textViewResourceId, ArrayList<OptionString> values) 
        {
            super(context, textViewResourceId, values);
            this.values = values;
        }

        public int getCount()
        {
        	return values.size();
        }

        public OptionString getItem(int position)
        {
        	return values.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	View view = super.getView(position, convertView, parent);
        	
            if (view instanceof TextView) 
            {
                ((TextView)view).setText(values.get(position).getOptionName());
            }
            
            return view;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) 
        {
        	 View row = super.getDropDownView(position, convertView, parent);
        	 
        	 if (row instanceof CheckedTextView)
        	 {
                 ((CheckedTextView)row).setText(values.get(position).getOptionName());
             }
        	 
             return row;
        }
    }
    
    /**
  	 * Asynchronous method used to rotate the photo by the degrees indicated in the parameter. 
  	 * The loading screen is displayed during the process, which requires more time depending on
  	 * the device
  	 */
    private class RotatePhoto extends AsyncTask<Integer, Void, Boolean>
	{				
  		@Override
		protected void onPreExecute()
		{			
			showHideLoadingScreen(getString(R.string.photo_gallery_rotating), true);
		}
		
		@Override
		protected Boolean doInBackground(Integer... parameters)
		{
			Boolean result = false;
			
			try
			{
				Matrix matrix = new Matrix();
				matrix.postRotate(parameters[0]);
	
				Bitmap photoBitmap = BitmapFactory.decodeFile(_currentGamePhotoGallery.getPhotos().get(_currentPosition).getPath());
				Bitmap photoRotated = Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmap.getWidth(), photoBitmap.getHeight(), matrix, true);
				result = photoRotated.compress(CompressFormat.PNG, 100, new FileOutputStream(new File(_currentGamePhotoGallery.getPhotos().get(_currentPosition).getPath())));
				
				photoBitmap.recycle();
				photoRotated.recycle();
			}
			catch (Exception ex)
			{
				XLog.e("[PhotoGalleryActivity.RotatePhoto.doInBackground]", ex);
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean succcess)
		{
			showHideLoadingScreen(null, false);
			loadCurrentPhoto();
		}
	}
}
