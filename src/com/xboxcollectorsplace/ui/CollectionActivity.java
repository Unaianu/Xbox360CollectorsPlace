package com.xboxcollectorsplace.ui;

import java.util.ArrayList;
import java.util.Collections;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Catalog;
import com.xboxcollectorsplace.bl.entities.Filters;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.Game.Genre;
import com.xboxcollectorsplace.bl.entities.Options.ShowGenre;
import com.xboxcollectorsplace.utils.XComparator;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Full screen activity, contains users collection list of all games. It can be accessed to
 * display arcade or retail games. Similar to CatalogActivity
 */
public class CollectionActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener
{	
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private ListView _lstCollection;
	private TextView _txtEmptyCollection;
	
	private Catalog _collection;
	private Filters _filters;
	private ArrayList<Game> _filteredCollection;
	private CollectionAdapter _collectionAdapter;
	
	private boolean _isArcade;
	private boolean _isTablet;
	private boolean _showGenre;
	private ProgressDialog _loadingScreen;
		
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
			super.onCreate(savedInstanceState);
		    this.setContentView(R.layout.activity_collection);
	     	 		
		    this._txtEmptyCollection = (TextView)this.findViewById(R.id.txtEmptyCollection);
 			this._lstCollection = (ListView)this.findViewById(R.id.lstCollection);
 			
 			ActionBar actionBar = this.getSupportActionBar();
        	actionBar.setDisplayShowHomeEnabled(false);
        	actionBar.setDisplayShowTitleEnabled(false);
        	
        	this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
        	this._isTablet = getResources().getBoolean(R.bool.isTablet);
		    this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
        	
			this._collectionAdapter = new CollectionAdapter(this.getBaseContext());
			this._lstCollection.setAdapter(this._collectionAdapter);		
			this._lstCollection.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			this._lstCollection.setOnItemClickListener(this);
			this._lstCollection.setOnItemLongClickListener(this);
			
		    new LoadCollection().execute(true, true, false);
	    }
    	catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onCreate]", ex);
		}
    }
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/
    
    /**
     * Called after reading the collection, adds all the games from the complete list to the
     * filtered list, and then removes all the games that dont meet the conditions set in
     * the filters
     */
    private void filterCollection(boolean returnToFirstPosition)
	{
    	try
    	{
    		if (this._filteredCollection == null)
	    	{
	    		this._filteredCollection = new ArrayList<Game>();
	    	}
	    	else
	    	{
    			this._filteredCollection.clear();
	    	}
	    	
	    	if (this._filters == null)
	    	{
	    		this._filteredCollection.addAll(this._collection.getCatalog());
	    	}
	    	else
	    	{
	    		// Each game is added at the end of this for. The end of the code is reached only
	    		// if all conditions are meet
	    		for (Game game : this._collection.getCatalog())
		    	{
	    			// Special characters are removed and roman numbers are converted to common
	    			// numbers to make the search easier
	    			if (!TextUtils.isEmpty(this._filters.getTitle())
	    					&& !game.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(this._filters.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())
	    					&& (TextUtils.isEmpty(game.getAltTitle()) || !game.getAltTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(this._filters.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())))
	    			{	    				
	    				continue;
	    			}
	    			
	    			if (this._filters.getStatus() != null)
	    			{
	    				switch (this._filters.getStatus())
			    		{
	    					case ALL:
	    						break;
	    					case GAMES_LEFT:
				    			if (game.getStatus() != Game.Status.NOT_OWN)
				    			{
				    				continue;
				    			}
				    			break;
				    		case GAMES_OWN:
				    			if (game.getStatus() == Game.Status.NOT_OWN)
				    			{
				    				continue;
				    			}
				    			break;
				    		case GAMES_FINISHED:
				    			if (game.getStatus() != Game.Status.FINISHED && game.getStatus() != Game.Status.COMPLETED)
				    			{
				    				continue;
				    			}
				    			break;
				    		case GAMES_COMPLETED:
				    			if (game.getStatus() != Game.Status.COMPLETED)
				    			{
				    				continue;
				    			}
				    			break;
				    		case GAMES_NOT_FINISHED:
				    			if (game.getStatus() != Game.Status.OWN)
				    			{
				    				continue;
				    			}
				    			break;
			    		}
	    			}
	    			
	    			if (this._filters.getGenre1() != null && !game.getGenre().contains(this._filters.getGenre1()))
	    			{
	    				if (this._filters.getGenre2() == null)
	    				{
	    					continue;
	    				}
	    				else
	    				{
	    					if (!game.getGenre().contains(this._filters.getGenre2()))
							{
    							continue;
							}
	    				}
	    			}
	    			
	    			if (this._filters.getYear() != 0)
	    			{
	    				if (Integer.valueOf(game.getRelease().substring(0,4)) != this._filters.getYear())
						{
	    					continue;
						}
	    			}
	    			
	    			if (this._filters.getOnline() != 0)
	    			{
	    				if (game.getOnline() < this._filters.getOnline())
						{
	    					continue;
						}
	    			}
	    			
	    			if (this._filters.getCoop() != 0)
	    			{
	    				if (game.getCoop() < this._filters.getCoop())
						{
	    					continue;
						}
	    			}
	    			
	    			if (this._filters.getSplitScreen() != 0)
	    			{
	    				if (game.getSplitScreen() < this._filters.getSplitScreen())
						{
	    					continue;
						}
	    			}
	    			
	    			if (this._filters.getSystemLink() != 0)
	    			{
	    				if (game.getSystemLink() < this._filters.getSystemLink())
						{
	    					continue;
						}
	    			}
	    			
	    			if (this._filters.getKinect() != null)
	    			{
	    				switch (this._filters.getKinect())
			    		{
	    					case ALL:
	    						break;
	    					case NOT_REQUIRED:
				    			if (game.getKinect() == 2)
				    			{
				    				continue;
				    			}
				    			break;
				    		case USED:
				    			if (game.getKinect() == 0)
				    			{
				    				continue;
				    			}
				    			break;
				    		case REQUIRED:
				    			if (game.getKinect() != 2)
				    			{
				    				continue;
				    			}
				    			break;
			    		}
	    			}
	    				
	    			this._filteredCollection.add(game);
		    	}
	    	}
	    	
	    	Collections.sort(this._filteredCollection, new XComparator());
    	}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.filterCatalog]", ex);
		}
	}
    
    /**
     * Searchs for the first game that meets the requirements (title and/or year) and scrolls
     * the list to its position
     */
    private void gotoGame(String title, String year)
    {
    	boolean found = false;
    	int position = 0;
    	
    	try
    	{
    		if (this._filteredCollection != null && this._filteredCollection.size() > 0)
    		{
	    		for (Game game : this._filteredCollection)
		    	{
	    			if (!TextUtils.isEmpty(title))
	    			{
	    				// Special characters are removed and roman numbers are converted to common
		    			// numbers to make the search easier
		    			if ((game.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(title.replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())
		    					|| ((!TextUtils.isEmpty(game.getAltTitle()) && game.getAltTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(title.replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())))))
		    			{	    				
		    				if (!TextUtils.isEmpty(year))
							{
		    					int yearInt = Integer.valueOf(year);
		    					
		    					if (Integer.valueOf(game.getRelease().substring(0,4)) == yearInt)
								{
				    				found = true;
				    				break;
								}
							}
		    				else
		    				{
		    					found = true;
			    				break;
		    				}
		    			}
	    			}
	    			else
	    			{
	    				int yearInt = 0;
	    				if (!TextUtils.isEmpty(year))
						{
	    					yearInt = Integer.valueOf(year);
	    					
	    					if (Integer.valueOf(game.getRelease().substring(0,4)) == yearInt)
							{
		    					found = true;
			    				break;
							}
						}
	    			}
	    			
	    			position++;
		    	}
    		}
    		
    		if (found)
    		{
    			this._lstCollection.setSelection(position);
    		}
    		else
    		{
    			BLUtils.showToast(getString(R.string.goto_nothing_found), true);
    		}
    	}
    	catch (Exception ex)
    	{
    		XLog.e("[CollectionActivity.gotoGame]", ex);
    	}
    }
  	
    /**
     * Displays an error dialog
     */
  	private void showErrorDialog(String dialogText)
 	{
     	try
     	{
     		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
	     		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_custom_alert);
				
			TextView txtDialog = (TextView) dialog.findViewById(R.id.txtDialog);
				txtDialog.setText(dialogText);
				
			Button btnDialogPositive = (Button) dialog.findViewById(R.id.btnDialogPositive);
				btnDialogPositive.setText(getString(R.string.general_ok));
				btnDialogPositive.setOnClickListener(new OnClickListener() {
					public void onClick(View v) 
					{
						dialog.dismiss();
					}
				});
			
			Button btnDialogNegative = (Button) dialog.findViewById(R.id.btnDialogNegative);
				btnDialogNegative.setVisibility(Button.GONE);
			
			dialog.show();
     	}
     	catch (Exception ex)
 		{
 			XLog.e("[CollectionActivity.showErrorDialog]", ex);
 		}
 	}
     
  	/**
     * Displays a confirmation dialog indicating that a game is going to be deleted
     */
    private void showDeleteConfirmationDialog(String dialogText, final int position)
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
						deleteGame(position);
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
 			XLog.e("[CollectionActivity.showConfirmationDialog]", ex);
 		}
 	}
    
    /**
     * Displays a confirmation dialog indicating that the collection is going to be deleted
     */
    private void showClearConfirmationDialog(String dialogText)
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
						cleanCollection();
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
 			XLog.e("[CollectionActivity.showConfirmationDialog]", ex);
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
			XLog.e("[CollectionActivity.showLoadingScreen]", ex);
		}
    }
    
    /**
     * Deletes the game in the position indicated by the "position" parameter, removing it from
     * the collection stored in Shared Preferences. After this, the list is refreshed by calling
     * again the Load Collection method
     */
  	private void deleteGame(int position)
    {
    	try
    	{
    		boolean operationSuccess = false;
  	    	
    		// If the stored collection contains the game, its removed
  			if (StorageController.existsGame(this._filteredCollection.get(position).getId(), this._isArcade))
			{
	    		operationSuccess = StorageController.removeGame(this._filteredCollection.get(position).getId(), this._isArcade);
			}
	    	
  			// If the gmae is succesfully removed, the collection is reloaded and a toast is
  			// shown, else an error dialog is shown
	    	if (operationSuccess)
	    	{
	    		BLUtils.showToast(this.getString(R.string.detail_remove_game).replace("X", this._filteredCollection.get(position).getTitle()), true);
	    		new LoadCollection().execute(true, false, false);
	    	}
	    	else
	    	{
	    		this.showErrorDialog(this.getString(R.string.general_error));
	    	}
    	}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.deleteGame]", ex);
		}
    }
  	
  	/**
  	 * Cleans the entire collection removing it from Shared Preferences. After this, the list is 
  	 * refreshed by calling again the Load Collection method
  	 */
  	private void cleanCollection()
  	{
  		try
    	{
  			StorageController.deleteCollection(this._isArcade);
  			BLUtils.showToast(this.getString(R.string.catalog_collection_clean), true);
  			new LoadCollection().execute(true, false, false);
    	}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.cleanCollection]", ex);
		}
  	}
  	
  	/**
  	 * Called as an aftermath of the Load Collection method. Refreshes the collection games list
  	 */
  	private void afterLoadCollection(boolean returnToFirstPosition, boolean showToast)
  	{
  		try
  		{
  			if (showToast)
  			{
  				BLUtils.showToast(getString(R.string.catalog_games_recovered).replace("X", String.valueOf(this._filteredCollection.size())), true);
  			}
  			this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
        	this._collectionAdapter.notifyDataSetChanged();
	    	
	    	if (this._collection != null && this._collection.getCatalog() != null && this._collection.getCatalog().size() > 0)
			{
	    		if (this._filteredCollection != null && this._filteredCollection.size() > 0)
				{
	    			this._txtEmptyCollection.setVisibility(TextView.GONE);
	    			if (returnToFirstPosition)
	    			{
	    				this._lstCollection.setSelection(0);
	    			}
				}
	    		else
	    		{
	    			this._txtEmptyCollection.setText(this.getString(R.string.catalog_empty_filters));
	    			this._txtEmptyCollection.setVisibility(TextView.VISIBLE);
	    		}
			}
			else
			{
				this._txtEmptyCollection.setText(this.getString(R.string.catalog_empty_collection));
    			this._txtEmptyCollection.setVisibility(TextView.VISIBLE);
			}
	  	}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.afterLoadCollection]", ex);
		}
  	}
    
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

			if (requestCode == BLParameters.ACTIVITY_CODES.CONFIG)
			{
				// Both the list and the action bar must be refreshed in case the language has
				// changed
				this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
	        	this._collectionAdapter.notifyDataSetChanged();
				this.supportInvalidateOptionsMenu();
			}
			else
			{
				if (resultCode == Activity.RESULT_OK)
				{
					if (requestCode == BLParameters.ACTIVITY_CODES.FILTERS)
					{
						// Changes in the filters, list must be reloaded
						this._filters = (Filters) data.getSerializableExtra(BLParameters.PARAMETERS.FILTERS);
						new LoadCollection().execute(false, true, true);
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.CATALOG)
					{
						if (data.getBooleanExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, false))
						{
							// Changes in the collection, list must be reloaded
							new LoadCollection().execute(true, false, false);
						}
						else
						{
							// The language could have been changed
							this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
				        	this._collectionAdapter.notifyDataSetChanged();
							this.supportInvalidateOptionsMenu();
						}
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.PHOTO_GALLERY)
					{
						if (data.getBooleanExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, false))
						{
							// New collection (Import has been called from the photo gallery), 
							// list must be reloaded
							new LoadCollection().execute(true, false, false);
						}
						else
						{
							// The language could have been changed
							this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
				        	this._collectionAdapter.notifyDataSetChanged();
							this.supportInvalidateOptionsMenu();
						}
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.GAME_DETAIL)
					{
						// Changes in the collection, list must be reloaded
						new LoadCollection().execute(true, false, false);
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.GOTO)
					{
						this.gotoGame(data.getStringExtra(BLParameters.PARAMETERS.TITLE), data.getStringExtra(BLParameters.PARAMETERS.YEAR));
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.IMPORT)
					{
						// New collection, list must be reloaded
						new LoadCollection().execute(true, true, false);
					}
				}
				else
				{
					// Even if the result is not OK, the language could have been changed
					if (requestCode == BLParameters.ACTIVITY_CODES.CATALOG || requestCode == BLParameters.ACTIVITY_CODES.GAME_DETAIL)
					{
						this._showGenre = StorageController.loadOptions().getShowGenre() == ShowGenre.GENRE;
			        	this._collectionAdapter.notifyDataSetChanged();
						this.supportInvalidateOptionsMenu();
					}
				}
			}
		}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onActivityResult]", ex);
		}
	}
  	
	/**
	 * Event related to the creation of the action menu
	 */
  	public boolean onCreateOptionsMenu(Menu menu)
    {
  		try
  		{
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.collection_menu, menu);    
	    }
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onCreateOptionsMenu]", ex);
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
			Intent output;
			
		    switch (item.getItemId()) 
		    {
			    case R.id.options_add:
			    	output = new Intent(this, CatalogActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
		  			startActivityForResult(output, BLParameters.ACTIVITY_CODES.CATALOG);
			    	break;
			    case R.id.options_filter_sort:
			    	output = new Intent(this, FilterSortActivity.class);
			    	if (this._filters != null)
			    	{
		    			output.putExtra(BLParameters.PARAMETERS.FILTERS, this._filters);
			    	}
			    	output.putExtra(BLParameters.PARAMETERS.SHOW_STATUS, true);
			    	output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, _isArcade);
			    	output.putExtra(BLParameters.PARAMETERS.COMES_FROM_COLLECTION, true);
			    	startActivityForResult(output, BLParameters.ACTIVITY_CODES.FILTERS);
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
			    case R.id.options_clear:
		    		showClearConfirmationDialog(getString(R.string.catalog_clear_collection));
			        break;
			    case R.id.options_goto:
			    	startActivityForResult(new Intent(this, GoToActivity.class), BLParameters.ACTIVITY_CODES.GOTO);
			        break;
		    	case R.id.options_photo_gallery:
		    		output = new Intent(this, PhotoGalleryActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
			    	startActivityForResult(output, BLParameters.ACTIVITY_CODES.PHOTO_GALLERY);
			        break;
		    }
		}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onOptionsItemSelected]", ex);
		}
		
		return true;
	}
  	
  	/**
	 * Event related to the selection of one of the games of the list. Game Detail Activity
	 * is called indicating the game to be displayed and if its an arcade or a retail game
	 */
  	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
  		try
  		{
	  		if (parent.getId() == this._lstCollection.getId())
	  		{
	  			Intent intent = new Intent(this, GameDetailActivity.class);
	  			intent.putExtra(BLParameters.PARAMETERS.GAME, this._collectionAdapter.getItem(position));
	  			intent.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
	  			this.startActivityForResult(intent, BLParameters.ACTIVITY_CODES.GAME_DETAIL);
	  		}
  		}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onItemClick]", ex);
		}
	}
  	
  	/**
	 * Event related to the long click of one of the games of the list. It displays the
	 * confirmation dialog for removing the game
	 */
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
  	{
		try
  		{
	  		if (parent.getId() == this._lstCollection.getId())
	  		{
	  			showDeleteConfirmationDialog(this.getString(R.string.detail_remove_game_confirmation), position);
	  		}
  		}
		catch (Exception ex)
		{
			XLog.e("[CollectionActivity.onItemLongClick]", ex);
		}
		
		return true;
	}
  	  	
    //------------------------------------------------------------------------- INNER CLASSES*/
	
	/**
	 *  Adapter used to display the collection game list
	 */
  	private class CollectionAdapter extends BaseAdapter
  	{
  		private LayoutInflater _inflater;
  		
  		public CollectionAdapter(Context context)
  		{
  			this._inflater = LayoutInflater.from(context);
  		}
  		
  		public int getCount() 
  		{
  			int count = 0;
  			
  			if (_filteredCollection != null)
  			{
  				count = _filteredCollection.size();
  			}
  			
  			return count;
  		}

  		public Game getItem(int position) 
  		{
  			Game game = null;
  			
  			if (_filteredCollection != null && position >= 0 && position < this.getCount())
  			{
  				game = _filteredCollection.get(position);
  			}
  			
  			return game;
  		}

  		public long getItemId(int position) 
  		{
  			long id = -1;
  			
  			if (_filteredCollection != null && position >= 0 && position < this.getCount())
  			{
  				id = _filteredCollection.get(position).getId();
  			}
  			
  			return id;
  		}

  		public View getView(final int position, View convertView, ViewGroup parent) 
  		{
  			ViewHolder holder;
  			
  			try
  			{
	  			if (convertView == null)
	            {
	  				convertView = this._inflater.inflate(R.layout.item_collection_list, null);
	  				
	              	holder = new ViewHolder();
	              	holder.imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
	              	holder.imgKinect = (ImageView)convertView.findViewById(R.id.imgKinect);
	              	holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
	              	holder.txtDescYear = (TextView)convertView.findViewById(R.id.txtDescYear);
	              	holder.txtYear = (TextView)convertView.findViewById(R.id.txtYear);
	              	holder.txtDescGenre = (TextView)convertView.findViewById(R.id.txtDescGenre);
	              	holder.txtGenre = (TextView)convertView.findViewById(R.id.txtGenre);
	              	holder.txtStatus = (TextView)convertView.findViewById(R.id.txtStatus);
	              	holder.txtDescAchievements = (TextView)convertView.findViewById(R.id.txtDescAchievements);
	              	holder.txtAchievements = (TextView)convertView.findViewById(R.id.txtAchievements);
	              	holder.lytStatus = (View)convertView.findViewById(R.id.lytStatus);
	              	
	              	convertView.setTag(holder);
	            }
		        else
		        {
		            holder = (ViewHolder)convertView.getTag();
		        }
              
	            if (_filteredCollection != null && position >= 0 && position < _filteredCollection.size() && _filteredCollection.get(position) != null)
	            {   
	            	Bitmap icon;
	            	if (!_isArcade)
	            	{
	            		icon = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ICON_PATH + _filteredCollection.get(position).getId() , null, null));
	            	}
	            	else
	            	{
	            		icon = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ARCADE_ICON_PATH + _filteredCollection.get(position).getId() , null, null));
	                }
	            	
	            	if (icon != null)
	            	{
	            		holder.imgIcon.setImageBitmap(icon);
	            	}
	            	else
	            	{
	            		holder.imgIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_unknown));
	            	}
	            	
	            	holder.txtTitle.setText(_filteredCollection.get(position).getTitle());
	              	holder.txtDescYear.setText(getString(R.string.catalog_year) + ": ");
	              	holder.txtYear.setText(_filteredCollection.get(position).getRelease().substring(0,4));
	              	
	              	holder.txtDescGenre.setText(getString(R.string.detail_genre) + ": ");
	              	
	              	String genres = "";
	              	for (Genre genre : _filteredCollection.get(position).getGenre())
	              	{
	              		switch (genre)
						{
							case ACTION:
								genres = genres + getString(R.string.genre_action) + ", ";
								break;
							case ADVENTURE:
								genres = genres + getString(R.string.genre_adventure) + ", ";
								break;
							case ACTION_ADVENTURE:
								genres = genres + getString(R.string.genre_action_adventure) + ", ";
								break;
							case FPS:
								genres = genres + getString(R.string.genre_fps) + ", ";
								break;
							case JRPG:
								genres = genres + getString(R.string.genre_jrpg) + ", ";
								break;
							case RACING:
								genres = genres + getString(R.string.genre_racing) + ", ";
								break;
							case RPG:
								genres = genres + getString(R.string.genre_rpg) + ", ";
								break;
							case RTS:
								genres = genres + getString(R.string.genre_rts) + ", ";
								break;
							case SPORTS:
								genres = genres + getString(R.string.genre_sports) + ", ";
								break;
							case SURVIVAL:
								genres = genres + getString(R.string.genre_survival) + ", ";
								break;
							case TPS:
								genres = genres + getString(R.string.genre_tps) + ", ";
								break;
							case ARPG:
								genres = genres + getString(R.string.genre_arpg) + ", ";
								break;
							case TRPG:
								genres = genres + getString(R.string.genre_trpg) + ", ";
								break;
							case BTU:
								genres = genres + getString(R.string.genre_btu) + ", ";
								break;
							case CFS:
								genres = genres + getString(R.string.genre_cfs) + ", ";
								break;
							case FIGHTING:
								genres = genres + getString(R.string.genre_fighting) + ", ";
								break;
							case HS:
								genres = genres + getString(R.string.genre_hs) + ", ";
								break;
							case HUNTING_FISHING:
								genres = genres + getString(R.string.genre_hunting_fishing) + ", ";
								break;
							case TD:
								genres = genres + getString(R.string.genre_tower_defense) + ", ";
								break;
							case MUSICAL:
								genres = genres + getString(R.string.genre_musical) + ", ";
								break;
							case MUSOU:
								genres = genres + getString(R.string.genre_musou) + ", ";
								break;
							case OPEN_WORLD:
								genres = genres + getString(R.string.genre_open_world) + ", ";
								break;
							case PARTY:
								genres = genres + getString(R.string.genre_party) + ", ";
								break;
							case PLATFORMER:
								genres = genres + getString(R.string.genre_platformer) + ", ";
								break;
							case POKER:
								genres = genres + getString(R.string.genre_poker) + ", ";
								break;
							case PUZZLE:
								genres = genres + getString(R.string.genre_puzzle) + ", ";
								break;
							case TBS:
								genres = genres + getString(R.string.genre_tbs) + ", ";
								break;
							case SIM:
								genres = genres + getString(R.string.genre_sim) + ", ";
								break;
							case STEALTH:
								genres = genres + getString(R.string.genre_stealth) + ", ";
								break;
							case CARDS_BOARD:
								genres = genres + getString(R.string.genre_cards_board) + ", ";
								break;
							case STU:
								genres = genres + getString(R.string.genre_stu) + ", ";
								break;
							case VISUAL_NOVEL:
								genres = genres + getString(R.string.genre_visual_novel) + ", ";
								break;
							case PINBALL:
								genres = genres + getString(R.string.genre_pinball) + ", ";
								break;
							case FITNESS:
								genres = genres + getString(R.string.genre_fitness) + ", ";
								break;
							case MMORPG:
								genres = genres + getString(R.string.genre_mmorpg) + ", ";
								break;
							default:
								genres = genres + ", ";
								break;
						}
	              	}
	              	holder.txtGenre.setText(genres.substring(0, genres.length() - 2));
	              	
	              	// Due to the smaller size of the smartphones, year and genre cannot be shown
	              	// at same time, so the user selects which one of them to show
	              	if (!_isTablet && _showGenre)
	    		    {
	    		    	holder.txtYear.setText(genres.substring(0, genres.length() - 2));
	    		    }
	              		              	
	              	holder.txtDescAchievements.setText(getString(R.string.catalog_achievements) + ": ");
	      			holder.txtAchievements.setText(_filteredCollection.get(position).getCurrentAchievements() + "/" + _filteredCollection.get(position).getTotalAchievements());
	            
	      			// Color indicates the status of the game. TxtStatus is not currently used
	      			switch (_filteredCollection.get(position).getStatus())
	              	{
	              		case COMPLETED:
	              			holder.txtStatus.setText(getString(R.string.catalog_status_completed));
	              			holder.txtStatus.setTextColor(getResources().getColor(R.color.deep_sky_blue));
	              			holder.lytStatus.setBackgroundColor(getResources().getColor(R.color.deep_sky_blue));
	              			break;
	              		case FINISHED:
	              			holder.txtStatus.setText(getString(R.string.catalog_status_finished));
	              			holder.txtStatus.setTextColor(getResources().getColor(R.color.forest_green));
	              			holder.lytStatus.setBackgroundColor(getResources().getColor(R.color.forest_green));
	              			break;
	              		case OWN:
	              			holder.txtStatus.setText(getString(R.string.catalog_status_own));
	              			holder.txtStatus.setTextColor(getResources().getColor(R.color.black));
	              			holder.lytStatus.setBackgroundColor(getResources().getColor(R.color.grey));
	              			break;
	              		case NOT_OWN:
	              			holder.txtStatus.setText(getString(R.string.catalog_status_not_own));
	              			holder.txtStatus.setTextColor(getResources().getColor(R.color.red));
	              			holder.lytStatus.setBackgroundColor(getResources().getColor(R.color.red));
	              			break;
	              	}
	      			
	      			if (_filteredCollection.get(position).getKinect() == BLParameters.KINECT.REQUIRED)
	      			{
	      				holder.imgKinect.setVisibility(View.VISIBLE);
	      			}
	      			else
	      			{
	      				holder.imgKinect.setVisibility(View.GONE);
	      			}
	            }
	            else 
	            {
	              	convertView = null;
	            }
  			}
			catch (Exception ex)
			{
				XLog.e("[CollectionActivity.getView]", ex);
			}
              
  			return convertView;
  		}
  		
      	private class ViewHolder
      	{   
      		ImageView imgIcon;
      		ImageView imgKinect;
            TextView txtTitle;
            TextView txtDescYear;
            TextView txtYear;
            TextView txtDescGenre;
            TextView txtGenre;
            TextView txtStatus;
            TextView txtDescAchievements;
            TextView txtAchievements;
            View lytStatus;
        }
  	}
  	
  	/**
  	 * Asynchronous method used to call the load collection method. The loading screen is
  	 * displayed during the process, that requires a few seconds due to the big amount of data
  	 * to process
  	 */
  	private class LoadCollection extends AsyncTask<Boolean, Void, Boolean>
	{				
  		boolean _showToast = false;
		
  		@Override
		protected void onPreExecute()
		{			
			showHideLoadingScreen(getString(R.string.catalog_retrieving_collection), true);
		}
		
		@Override
		protected Boolean doInBackground(Boolean... parameters)
		{
			boolean returnToFirstposition = true;
			
			try
			{
				boolean readCollection = parameters[0];
				returnToFirstposition = parameters[1];
				_showToast = parameters[2];
				
				if (readCollection)
				{
					_collection = StorageController.loadCollection(_isArcade);
				}
				
				filterCollection(true);
			}
			catch (Exception ex)
			{
				XLog.e("[CollectionActivity.LoadCollection.doInBackground]", ex);
			}
		    
			return returnToFirstposition;
		}
		
		@Override
		protected void onPostExecute(Boolean returnToFirstposition)
		{
			showHideLoadingScreen(null, false);
			afterLoadCollection(returnToFirstposition, _showToast);
		}
	}
}
