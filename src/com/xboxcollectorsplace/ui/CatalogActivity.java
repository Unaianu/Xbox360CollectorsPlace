package com.xboxcollectorsplace.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;
import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.entities.Catalog;
import com.xboxcollectorsplace.bl.entities.Filters;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.Game.Genre;
import com.xboxcollectorsplace.utils.XComparator;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Full screen activity, contains complete catalogs list of all games. It can be accessed to
 * display arcade or retail games. Similar to CollectionActivity
 */
public class CatalogActivity extends ActionBarActivity implements OnItemClickListener
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
		
	private ListView _lstCatalog;
	private TextView _txtEmptyCatalog;
	
	private Catalog _catalog;
	private Filters _filters;
	private ArrayList<Game> _filteredCatalog;
	private CatalogAdapter _catalogAdapter;

	private boolean _isArcade;
	private ProgressDialog _loadingScreen;
	
	private boolean _reloadCollection = false;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
			super.onCreate(savedInstanceState);
		    this.setContentView(R.layout.activity_catalog);
	     	 		
		    this._txtEmptyCatalog = (TextView)this.findViewById(R.id.txtEmptyCatalog);
 			this._lstCatalog = (ListView)this.findViewById(R.id.lstCatalog);
 			
 			ActionBar actionBar = this.getSupportActionBar();
        	actionBar.setDisplayShowHomeEnabled(false);
        	actionBar.setDisplayShowTitleEnabled(false);
        	  
        	this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
			this._lstCatalog.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			this._catalogAdapter = new CatalogAdapter(this.getBaseContext());
			this._lstCatalog.setAdapter(this._catalogAdapter);		
			
			this._lstCatalog.setOnItemClickListener(this);
			
			new LoadCatalog().execute(true, false);
	    }
    	catch (Exception ex)
		{
			XLog.e("[CatalogActivity.onCreate]", ex);
		}
    }
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/

    /**
     * Reads the asset with the complete catalog and converts it to a system readable Json. Then,
     * the json is converted to a Catalog entity used by the activity
     */
    private void readCatalog()
	{
    	try
		{
			InputStream inputStream;
			Gson gson = new Gson();
			String catalogJSON = null;
			
			if (this._isArcade)
    		{
				inputStream = App.getContext().getAssets().open(BLParameters.GENERAL_PARAMETERS.CATALOG_ARCADE);
    		}
			else
			{
				inputStream = App.getContext().getAssets().open(BLParameters.GENERAL_PARAMETERS.CATALOG_RETAIL);
			}
			 
			if (inputStream != null)
			{
				catalogJSON = BLUtils.inputStreamToString(inputStream, BLParameters.GENERAL_PARAMETERS.CODIFICATION_UTF_8).replace("&uuml;", "ü").replace("&ntilde", "ñ").replace("&eacute;", "é");
			}
		
			this._catalog = gson.fromJson(catalogJSON, Catalog.class);
		}
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.readCatalog]", ex);
		}
	}
    
    /**
     * Called after reading the catalog, adds all the games from the complete list to the
     * filtered list, and then removes all the games that dont meet the conditions set in
     * the filters
     */
    private void filterCatalog()
	{
    	try
    	{
	    	if (this._filteredCatalog == null)
	    	{
	    		this._filteredCatalog = new ArrayList<Game>();
	    	}
	    	else
	    	{
    			this._filteredCatalog.clear();
	    	}
	    	
	    	if (this._filters == null)
	    	{
	    		this._filteredCatalog.addAll(this._catalog.getCatalog());
	    	}
	    	else
	    	{
	    		// Each game is added at the end of this for. The end of the code is reached only
	    		// if all conditions are meet
	    		for (Game game : this._catalog.getCatalog())
		    	{
	    			// Special characters are removed and roman numbers are converted to common
	    			// numbers to make the search easier
	    			if (!TextUtils.isEmpty(this._filters.getTitle())
	    					&& !game.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(this._filters.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())
	    					&& (TextUtils.isEmpty(game.getAltTitle()) || !game.getAltTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase().contains(this._filters.getTitle().replace("'", "").replace("-", "").replace(":", "").replace(" xiii", " 13").replace(" xi", " 11").replace(" vi", " 6").replace(" iv", " 4").replace(" v", " 5").replace(" iii", " 3").replace(" ii", "2").toUpperCase())))
	    			{
	    				continue;
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
	    				
	    			this._filteredCatalog.add(game);
		    	}
	    	}
	    	
	    	Collections.sort(this._filteredCatalog, new XComparator());
    	}
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.filterCatalog]", ex);
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
    		if (this._filteredCatalog != null && this._filteredCatalog.size() > 0)
    		{
	    		for (Game game : this._filteredCatalog)
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
    			this._lstCatalog.setSelection(position);
    		}
    		else
    		{
    			BLUtils.showToast(getString(R.string.goto_nothing_found), true);
    		}
    	}
    	catch (Exception ex)
    	{
    		XLog.e("[CatalogActivity.gotoGame]", ex);
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
			XLog.e("[CatalogActivity.showLoadingScreen]", ex);
		}
    }
  	
  	/**
  	 * Called as an aftermath of the Load Catalog method. Refreshes the catalog games list
  	 */
  	private void afterLoadCatalog(boolean showToast)
  	{
  		try
  		{
	    	if (showToast)
  			{
	    		BLUtils.showToast(getString(R.string.catalog_games_recovered).replace("X", String.valueOf(this._filteredCatalog.size())), true);
  			}
  			this._catalogAdapter.notifyDataSetChanged();
	  		
	    	if (this._filteredCatalog.size() > 0)
			{
				this._txtEmptyCatalog.setVisibility(TextView.GONE);
				this._lstCatalog.setSelection(0);
			}
			else
			{
				this._txtEmptyCatalog.setVisibility(TextView.VISIBLE);
			}
	  	}
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.afterReloadCatalog]", ex);
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
				this._catalogAdapter.notifyDataSetChanged();
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
						new LoadCatalog().execute(false, true);
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.GAME_DETAIL)
					{
						// Changes in the collection, it must be reloaded when returning. Also,
						// language could have been changed
						_reloadCollection = true;
						this._catalogAdapter.notifyDataSetChanged();
						this.supportInvalidateOptionsMenu();
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.PHOTO_GALLERY)
					{
						// New collection (Import has been called from the photo gallery), 
						// it must be reloaded when returning. Also, language could have been 
						// changed
						if (data.getBooleanExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, false))
						{
							_reloadCollection = true;
						}
						this._catalogAdapter.notifyDataSetChanged();
						this.supportInvalidateOptionsMenu();
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.GOTO)
					{
						this.gotoGame(data.getStringExtra(BLParameters.PARAMETERS.TITLE), data.getStringExtra(BLParameters.PARAMETERS.YEAR));
					}
					else if (requestCode == BLParameters.ACTIVITY_CODES.IMPORT)
					{
						// New collection, it must be reloaded when returning
						this._reloadCollection = true;
					}
				}
				else
				{
					// Even if the result is not OK, the language could have been changed
					if (requestCode == BLParameters.ACTIVITY_CODES.GAME_DETAIL)
					{
						this._catalogAdapter.notifyDataSetChanged();
						this.supportInvalidateOptionsMenu();
					}
				}
			}
		}
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.onActivityResult]", ex);
		}
	}
  	
  	/**
	 * Event related to the push of the back button. If the collection is to be reloaded (mainly
	 * because the language has been changed, a new game has been added or a game has been deleted)
	 * it returns an OK and a true boolean to the caller activity
	 */
  	public boolean onKeyDown(int keyCode, KeyEvent event) 
  	{
  	    if (keyCode == KeyEvent.KEYCODE_BACK) 
  	    {
  	    	Intent output = new Intent();
  	    	output.putExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, this._reloadCollection);
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
	        inflater.inflate(R.menu.catalog_menu, menu);
	    }
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.onCreateOptionsMenu]", ex);
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
			    case R.id.options_filter_sort:
			    	output = new Intent(this, FilterSortActivity.class);
		    		if (this._filters != null)
			    	{
		    			output.putExtra(BLParameters.PARAMETERS.FILTERS, this._filters);
			    	}
		    		output.putExtra(BLParameters.PARAMETERS.SHOW_STATUS, false);
		    		output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, _isArcade);
		    		output.putExtra(BLParameters.PARAMETERS.COMES_FROM_COLLECTION, false);
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
		    		startActivityForResult(new Intent(this, ConfigActivity.class), BLParameters.ACTIVITY_CODES.FILTERS);
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
			XLog.e("[CatalogActivity.onOptionsItemSelected]", ex);
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
	  		if (parent.getId() == this._lstCatalog.getId())
	  		{
	  			Intent intent = new Intent(this, GameDetailActivity.class);
	  			intent.putExtra(BLParameters.PARAMETERS.GAME, this._catalogAdapter.getItem(position));
	  			intent.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
	  			this.startActivityForResult(intent, BLParameters.ACTIVITY_CODES.GAME_DETAIL);
	  		}
  		}
		catch (Exception ex)
		{
			XLog.e("[CatalogActivity.onItemClick]", ex);
		}
	}
  	
    //------------------------------------------------------------------------- INNER CLASSES*/
	
  	/**
	 *  Adapter used to display the catalog game list
	 */
  	private class CatalogAdapter extends BaseAdapter
  	{
  		private LayoutInflater _inflater;
  		
  		public CatalogAdapter(Context context)
  		{
  			this._inflater = LayoutInflater.from(context);
  		}
  		
  		public int getCount() 
  		{
  			int count = 0;
  			
  			if (_filteredCatalog != null)
  			{
  				count = _filteredCatalog.size();
  			}
  			
  			return count;
  		}

  		public Game getItem(int position) 
  		{
  			Game game = null;
  			
  			if (_filteredCatalog != null && position >= 0 && position < this.getCount())
  			{
  				game = _filteredCatalog.get(position);
  			}
  			
  			return game;
  		}

  		public long getItemId(int position) 
  		{
  			long id = -1;
  			
  			if (_filteredCatalog != null && position >= 0 && position < this.getCount())
  			{
  				id = _filteredCatalog.get(position).getId();
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
	  				convertView = this._inflater.inflate(R.layout.item_catalog_list, null);
	  				
	  				holder = new ViewHolder();
	              	holder.imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);
	              	holder.imgKinect = (ImageView)convertView.findViewById(R.id.imgKinect);
	              	holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
	              	holder.txtDescYear = (TextView)convertView.findViewById(R.id.txtDescYear);
	              	holder.txtYear = (TextView)convertView.findViewById(R.id.txtYear);
	              	holder.txtDescGenre = (TextView)convertView.findViewById(R.id.txtDescGenre);
	              	holder.txtGenre = (TextView)convertView.findViewById(R.id.txtGenre);
	              	
	              	convertView.setTag(holder);
	            }
		        else
		        {
		            holder = (ViewHolder)convertView.getTag();
		        }
	              
	            if (_filteredCatalog != null && position >= 0 && position < _filteredCatalog.size() && _filteredCatalog.get(position) != null)
	            {   
	            	Bitmap icon;
	            	if (!_isArcade)
	            	{
	            		icon = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ICON_PATH  + _filteredCatalog.get(position).getId() , null, null));
	            	}
	            	else
	            	{
	            		icon = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ARCADE_ICON_PATH  + _filteredCatalog.get(position).getId() , null, null));
	                }
	            	
	            	if (icon != null)
	            	{
	            		holder.imgIcon.setImageBitmap(icon);
	            	}
	            	else
	            	{
	            		holder.imgIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_unknown));
	            	}
	            	
	            	holder.txtTitle.setText(_filteredCatalog.get(position).getTitle());
	              	holder.txtDescYear.setText(getString(R.string.catalog_year) + ": ");
	              	holder.txtYear.setText(_filteredCatalog.get(position).getRelease().substring(0,4));
	              	holder.txtDescGenre.setText(getString(R.string.detail_genre) + ": ");
	              	
	              	String genres = "";
	              	for (Genre genre : _filteredCatalog.get(position).getGenre())
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
	              	
	              	if (_filteredCatalog.get(position).getKinect() == BLParameters.KINECT.REQUIRED)
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
  				XLog.e("[CatalogActivity.getView]", ex);
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
        }
  	}
  	
  	/**
  	 * Asynchronous method used to call the load catalog method. The loading screen is
  	 * displayed during the process, that requires a few seconds due to the big amount of data
  	 * to process
  	 */
  	private class LoadCatalog extends AsyncTask<Boolean, Void, Boolean>
	{		
  		boolean _showToast = false;
		
		@Override
		protected void onPreExecute()
		{			
			showHideLoadingScreen(getString(R.string.catalog_retrieving_catalog), true);
		}
		
		@Override
		protected Boolean doInBackground(Boolean... parameters)
		{
			try
			{
				boolean readCatalog = parameters[0];
				_showToast = parameters[1];
				
				if (readCatalog)
				{
					readCatalog(); 
				}
				
			    filterCatalog();
			}
			catch (Exception ex)
			{
				XLog.e("[CatalogActivity.LoadCatalog.doInBackground]", ex);
			}
		    
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			showHideLoadingScreen(null, false);
			afterLoadCatalog(_showToast);
		}
	}
}
