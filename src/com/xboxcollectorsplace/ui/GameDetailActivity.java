package com.xboxcollectorsplace.ui;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.google.gson.Gson;
import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Catalog;
import com.xboxcollectorsplace.bl.entities.DLC;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.Link;
import com.xboxcollectorsplace.bl.entities.Game.Genre;
import com.xboxcollectorsplace.bl.entities.Game.Status;
import com.xboxcollectorsplace.bl.entities.OptionString;
import com.xboxcollectorsplace.utils.JustifiedTextView;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Full screen activity, contains all the details of the game selected in the caller activity
 */
public class GameDetailActivity extends ActionBarActivity implements OnClickListener
{	
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private ImageView _imgCover;
	private TextView _txtTitle;
	private TextView _txtRelease;
	private TextView _txtGenre;
	private TextView _txtDeveloper;
	private Spinner _spnStatus;
	private RelativeLayout _lytAchievements;
	private TextView _txtAchievements;
	private ImageButton _btnEditAchievements;
	private ProgressBar _progressAchievements;
	private TextView _txtOnline;
	private TextView _txtCoop;
	private TextView _txtSplitScreen;
	private TextView _txtSystemLink;
	private TextView _txtLabelKinect;
	private TextView _txtKinect;
	private TextView _txtNoDLCs;
	private JustifiedTextView _txtSynopsis;
	private LinearLayout _lytDLCs;
	private LinearLayout _lytLinks;
	private EditText _edtNotes;
	private ProgressBar _prgCover;
	
	private StatusAdapter _statusAdapter;
	
	private Game _game;
	private boolean _isArcade = false;
	private boolean _reloadCollection = false;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
			super.onCreate(savedInstanceState);
		    this.setContentView(R.layout.activity_game_detail);
	     	 
		    this._game = ((Game) this.getIntent().getSerializableExtra(BLParameters.PARAMETERS.GAME));
		    
			this._imgCover = (ImageView)this.findViewById(R.id.imgCover);
		    this._txtTitle = (TextView)this.findViewById(R.id.txtTitle);
		    this._txtRelease = (TextView)this.findViewById(R.id.txtRelease);
			this._txtGenre = (TextView)this.findViewById(R.id.txtGenre);
			this._txtDeveloper = (TextView)this.findViewById(R.id.txtDeveloper);
			this._spnStatus = (Spinner)this.findViewById(R.id.spnStatus);
			this._lytAchievements = (RelativeLayout)this.findViewById(R.id.lytAchievements);
			this._txtAchievements = (TextView)this.findViewById(R.id.txtAchievements);
			this._btnEditAchievements = (ImageButton)this.findViewById(R.id.btnEditAchievements);
			this._progressAchievements = (ProgressBar)this.findViewById(R.id.progressAchievements);
			this._txtOnline = (TextView)this.findViewById(R.id.txtOnline);
			this._txtCoop = (TextView)this.findViewById(R.id.txtCoop);
			this._txtSplitScreen = (TextView)this.findViewById(R.id.txtSplitScreen);
			this._txtSystemLink = (TextView)this.findViewById(R.id.txtSystemLink);
			this._txtSynopsis = (JustifiedTextView)this.findViewById(R.id.txtSynopsis);
			this._txtLabelKinect = (TextView)this.findViewById(R.id.txtLabelKinect);
		    this._txtKinect = (TextView)this.findViewById(R.id.txtKinect);
		    this._txtNoDLCs = (TextView)this.findViewById(R.id.txtNoDLCs);
		    this._lytDLCs = (LinearLayout)this.findViewById(R.id.lytDLCs);
		    this._lytLinks = (LinearLayout)this.findViewById(R.id.lytLinks);
		    this._edtNotes = (EditText)this.findViewById(R.id.edtNotes);
		    this._prgCover = (ProgressBar)this.findViewById(R.id.prgCover);
		    
		    ActionBar actionBar = this.getSupportActionBar();
	        	actionBar.setDisplayShowHomeEnabled(false);
	        	actionBar.setDisplayShowTitleEnabled(false);
        	
		    ArrayList<OptionString> statusList = new ArrayList<OptionString>();
		    	statusList.add(new OptionString(getString(R.string.catalog_status_not_own), Game.Status.NOT_OWN.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_own), Game.Status.OWN.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_finished), Game.Status.FINISHED.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_completed), Game.Status.COMPLETED.toString()));
			
		    this._statusAdapter = new StatusAdapter(this, R.layout.simple_spinner_item_custom, statusList);
				this._statusAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnStatus.setAdapter(this._statusAdapter);
			this._btnEditAchievements.setOnClickListener(this);
			
			if (this.getIntent().getExtras().containsKey(BLParameters.PARAMETERS.IS_ARCADE))
		    {
		    	this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
		    }
			
			Game gameLoaded = StorageController.loadGame(this._game.getId(), this._isArcade);
			if (gameLoaded != null)
			{
				this._game = gameLoaded;
			}
			
		    this.loadGameDetails();
		    this.supportInvalidateOptionsMenu();
	    	
		    this.loadBasicData();
			this.loadExtendedData();
			this.loadDLCs();
			this.loadLinks();
	    }
    	catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.onCreate]", ex);
		}
    }
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/
    
    /**
     * Loads the details of the current game from the detail assets
     */
    private void loadGameDetails()
    {
    	try
    	{
	    	String detailCatalogJSON = null;
		    Gson gson = new Gson();
		    DecimalFormat decimalFormat = new DecimalFormat("##");
		    
		    // Due to the high amount of games, they are divided in files of 100 games each one.
		    // To selected the appropriate file, the game ID is divided by 100, truncated, and 
		    // checked if the result number is a multiple of 100, to substract 1 to the result in 
		    // that case. For example, for game 535, the correct asset will be the 5th one, 
		    // (535 / 100 = 5,xx => 5), for the game 500 it will be the 4th asset (500 is multiple
		    // of 100, so => 500 -1 = 499 => 499 / 100 = 4,xx => 4). Its done this way because 
		    // games in assets go from 1 to 100
		    InputStream inputStream;
		    if (!_isArcade)
			{
		    	inputStream = App.getContext().getAssets().open(BLParameters.GENERAL_PARAMETERS.DETAILS_PATH + decimalFormat.format(this._game.getId() % 100 == 0 ? (this._game.getId() - 1) / 100 : this._game.getId() / 100) + BLParameters.GENERAL_PARAMETERS.XML_FILE);
			}
		    else
		    {
		    	inputStream = App.getContext().getAssets().open(BLParameters.GENERAL_PARAMETERS.ARCADE_DETAILS_PATH + decimalFormat.format(this._game.getId() % 100 == 0 ? (this._game.getId() - 1) / 100 : this._game.getId() / 100) + BLParameters.GENERAL_PARAMETERS.XML_FILE);
			}
		    
	    	if (inputStream != null)
			{
	    		detailCatalogJSON = BLUtils.inputStreamToString(inputStream, BLParameters.GENERAL_PARAMETERS.CODIFICATION_UTF_8);
			}
	    	
	    	Catalog detailCatalog = gson.fromJson(detailCatalogJSON, Catalog.class);
			for (Game game : detailCatalog.getCatalog())
	    	{
	    		if (game.getId() == this._game.getId())
	    		{
	    			this._game.setDetails(game.getDetails());
	    			break;
	    		}
	    	}
    	}
    	catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.loadGameDetails]", ex);
		}
    }
    
    /**
     * Displays the basic data of the game: title, release date, genre, developer and status
     */
    private void loadBasicData()
    {
    	try
    	{
    		new LoadCover().execute();
			
	    	this._txtTitle.setText(this._game.getTitle().toUpperCase(Locale.getDefault()));
	    	this._txtRelease.setText(BLUtils.translateDate(this._game.getRelease()));
	    	
			if (this._game.getGenre() != null && this._game.getGenre().size() > 0)
			{
				for (Genre genre : this._game.getGenre())
				{
					switch (genre)
					{
						case ACTION:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_action) + "\n");
							break;
						case ADVENTURE:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_adventure) + "\n");
							break;
						case ACTION_ADVENTURE:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_action_adventure) + "\n");
							break;
						case FPS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_fps) + "\n");
							break;
						case JRPG:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_jrpg) + "\n");
							break;
						case RACING:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_racing) + "\n");
							break;
						case RPG:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_rpg) + "\n");
							break;
						case RTS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_rts) + "\n");
							break;
						case SPORTS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_sports) + "\n");
							break;
						case SURVIVAL:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_survival) + "\n");
							break;
						case TPS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_tps) + "\n");
							break;
						case ARPG:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_arpg) + "\n");
							break;
						case TRPG:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_trpg) + "\n");
							break;
						case BTU:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_btu) + "\n");
							break;
						case CFS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_cfs) + "\n");
							break;
						case FIGHTING:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_fighting) + "\n");
							break;
						case HS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_hs) + "\n");
							break;
						case HUNTING_FISHING:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_hunting_fishing) + "\n");
							break;
						case TD:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_tower_defense) + "\n");
							break;
						case MUSICAL:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_musical) + "\n");
							break;
						case MUSOU:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_musou) + "\n");
							break;
						case OPEN_WORLD:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_open_world) + "\n");
							break;
						case PARTY:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_party) + "\n");
							break;
						case PLATFORMER:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_platformer) + "\n");
							break;
						case POKER:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_poker) + "\n");
							break;
						case PUZZLE:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_puzzle) + "\n");
							break;
						case TBS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_tbs) + "\n");
							break;
						case SIM:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_sim) + "\n");
							break;
						case STEALTH:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_stealth) + "\n");
							break;
						case CARDS_BOARD:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_cards_board) + "\n");
							break;
						case STU:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_stu) + "\n");
							break;
						case VISUAL_NOVEL:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_visual_novel) + "\n");
							break;
						case PINBALL:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_pinball) + "\n");
							break;
						case FITNESS:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_fitness) + "\n");
							break;
						case MMORPG:
							this._txtGenre.setText(this._txtGenre.getText().toString() + this.getString(R.string.genre_mmorpg) + "\n");
							break;
						default:
							break;
					}
				}

				this._txtGenre.setText(this._txtGenre.getText().toString().substring(0, this._txtGenre.getText().toString().length() - 1));
			}

			if (this._game.getDetails().getDeveloper() != null && this._game.getDetails().getDeveloper().size() > 0)
			{
				for (String developer : this._game.getDetails().getDeveloper())
				{
					this._txtDeveloper.setText(this._txtDeveloper.getText().toString() + developer + "\n");
				}
				
				this._txtDeveloper.setText(this._txtDeveloper.getText().toString().substring(0, this._txtDeveloper.getText().toString().length() - 1));
			}
			
			this._spnStatus.setSelection(this._statusAdapter.getItemPosition(this._game.getStatus().toString()));
    	}
    	catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.loadBasicData]", ex);
		}
    }

    /**
     * Displays the following data of the game: Total achievements, unlocked achievements (using
     * a progressbar), number of multiplayer players, use of kinect and synopsis (using the 
     * JustifiedTextView controller)
     */
    private void loadExtendedData()
    {
    	try
    	{
    		this._progressAchievements.setMax(this._game.getTotalAchievements());
			this._progressAchievements.setProgress(this._game.getCurrentAchievements());
		    this._txtAchievements.setText(this._game.getCurrentAchievements() + "/" + this._game.getTotalAchievements());   
		    this._lytAchievements.bringToFront();		    

		    this._txtOnline.setText(this._game.getOnline() > 0 ? (this._game.getOnline() >= 10000 ? this.getString(R.string.detail_multiplayer_mmo) : this._game.getOnline() + " " + this.getString(R.string.general_players)) : this.getString(R.string.general_no));
		    this._txtCoop.setText(this._game.getCoop() > 0 ? (this._game.getCoop() >= 10000 ? this.getString(R.string.detail_multiplayer_mmo) : this._game.getCoop() + " " + this.getString(R.string.general_players)) : this.getString(R.string.general_no));
		    this._txtSplitScreen.setText(this._game.getSplitScreen() > 0 ? this._game.getSplitScreen() + " " + this.getString(R.string.general_players) : this.getString(R.string.general_no));
		    this._txtSystemLink.setText(this._game.getSystemLink() > 0 ? this._game.getSystemLink() + " " + this.getString(R.string.general_players) : this.getString(R.string.general_no));
		    
		    switch(this._game.getKinect())
		    {
			    case BLParameters.KINECT.NOT_USED:
			    default:
			    	this._txtLabelKinect.setVisibility(View.GONE);
			    	this._txtKinect.setVisibility(View.GONE);
			    	break;
			    case BLParameters.KINECT.OPTIONAL_FEATURES:
			    	this._txtLabelKinect.setVisibility(View.VISIBLE);
			    	this._txtKinect.setVisibility(View.VISIBLE);
			    	this._txtKinect.setText(getString(R.string.detail_kinect_optional_features));
			    	break;
			    case BLParameters.KINECT.REQUIRED:
			    	this._txtLabelKinect.setVisibility(View.VISIBLE);
			    	this._txtKinect.setVisibility(View.VISIBLE);
			    	this._txtKinect.setText(getString(R.string.detail_kinect_required));
			    	break;
		    }
		    
		    this._txtSynopsis.setText(this._game.getDetails().getSynopsis(), BLParameters.GENERAL_PARAMETERS.TRUNCATE_POSITION_GAME_SYNOPSIS);
	    }
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.loadExtendedData]", ex);
		}
    }
    
    /**
     * Displays game DLCs using a dynamically generated list. For each one the following info is
     * displayed: Cover (can be vertical or horizontal, if it exists), name, DLC type, release
     * date, achievements, synopsis and a check indicating if it has been downloaded or not 
     * (checkable by the user)
     */
    private void loadDLCs()
    {
    	boolean isTablet = getResources().getBoolean(R.bool.isTablet);
	    
    	try
    	{    		
    		// Array indicating the downloaded DLCs. If the game is still not stored in Shared
    		// Preferences (its not in the collection) this will be empty
    		ArrayList<DLC> downloadedDLCS = this._game.getDownloadedDLCs();
			
    		if (this._game.getDetails().getDLCs() != null && this._game.getDetails().getDLCs().size() > 0)
 		    {    			
    			this._lytDLCs.setVisibility(LinearLayout.VISIBLE);
 		    	this._txtNoDLCs.setVisibility(LinearLayout.GONE);
 		    	
    			LayoutInflater layoutInflater = LayoutInflater.from(this);
    			
    			for (int i = 0; i < this._game.getDetails().getDLCs().size(); i++)
    			{
    				// DLC info is loaded from the game
    				final DLC dlc = this._game.getDetails().getDLCs().get(i);
    				
    				// Entry is initializated
    				LinearLayout lytDLC = (LinearLayout)layoutInflater.inflate(R.layout.item_dlc_list, null);
    				LinearLayout lytDLCCover = (LinearLayout)lytDLC.findViewById(R.id.lytDLCCover);
    				LinearLayout lytDLCHorizontal = (LinearLayout)lytDLC.findViewById(R.id.lytDLCHorizontal);
    				ImageView imgDLCHorizontal = (ImageView)lytDLC.findViewById(R.id.imgDLCHorizontal);
    				ImageView imgDLCCover = (ImageView)lytDLC.findViewById(R.id.imgDLCCover);
    				TextView txtDLCName = (TextView)lytDLC.findViewById(R.id.txtDLCName);
    				TextView txtDLCType = (TextView)lytDLC.findViewById(R.id.txtDLCType);
    				TextView txtDLCReleaseDate = (TextView)lytDLC.findViewById(R.id.txtDLCReleaseDate);
    				LinearLayout lytDLCAchievements = (LinearLayout)lytDLC.findViewById(R.id.lytDLCAchievements);
    				TextView txtDLCAchievements = (TextView)lytDLC.findViewById(R.id.txtDLCAchievements);
    				JustifiedTextView txtDLCSynopsis = (JustifiedTextView)lytDLC.findViewById(R.id.txtDLCSynopsis);
    				CheckBox chkDLCDownloaded = (CheckBox)lytDLC.findViewById(R.id.chkDLCDownloaded);
    				
    				// Name
    				txtDLCName.setText(dlc.getName());
    				
    				// DLC Type
    				switch (dlc.getType())
    				{
    					case 0:
    						txtDLCType.setText(getString(R.string.dlc_type_sp_add_on));
    						break;
    					case 1:
    						txtDLCType.setText(getString(R.string.dlc_type_mp_add_on));
    						break;
    					case 2:
    						txtDLCType.setText(getString(R.string.dlc_type_equipment));
    						break;
    					case 3:
    						txtDLCType.setText(getString(R.string.dlc_type_car_pack));
    						break;
    					case 4:
    						txtDLCType.setText(getString(R.string.dlc_type_minor_sp_add_on));
    						break;
    					case 5:
    						txtDLCType.setText(getString(R.string.dlc_type_sp_add_on_separately));
    						break;
    					case 6:
    						txtDLCType.setText(getString(R.string.dlc_type_mp_add_on_separately));
    						break;
    					case 7:
    					default:
    						txtDLCType.setText(getString(R.string.dlc_type_other));
    						break;
    					case 8:
    						txtDLCType.setText(getString(R.string.dlc_type_sp_mp_add_on));
    						break;
    					case 9:
    						txtDLCType.setText(getString(R.string.dlc_type_track_pack));
    						break;
    				}
    				
    				// Release Date
    				if (!TextUtils.isEmpty(dlc.getReleaseDate()))
    				{
    					txtDLCReleaseDate.setVisibility(View.VISIBLE);
    					txtDLCReleaseDate.setText(BLUtils.translateDate(dlc.getReleaseDate()));
    				}
    				else
    				{
    					txtDLCReleaseDate.setVisibility(View.GONE);
    				}
    				
    				// Achievements
    				if (dlc.getAchievements() > 0)
    				{
    					lytDLCAchievements.setVisibility(View.VISIBLE);
    					txtDLCAchievements.setText(String.valueOf(dlc.getAchievements()));
    				}
    				else
    				{
    					lytDLCAchievements.setVisibility(View.GONE);
    				}
    				
    				// Synopsis
    				if (!TextUtils.isEmpty(dlc.getSynopsis()))
    				{
    					txtDLCSynopsis.setVisibility(View.VISIBLE);
    					txtDLCSynopsis.setTextSize(isTablet ? 12 : 11);
    					txtDLCSynopsis.setText(dlc.getSynopsis(), BLParameters.GENERAL_PARAMETERS.TRUNCATE_POSITION_DLC_SYNOPSIS);
    				}
    				else
    				{
    					txtDLCSynopsis.setVisibility(View.GONE);
    				}
    				
    				// If the array is smaller than the actual number of DLCs, a entry will be
    				// added indicating that the current DLC is not downloaded. This is because
    				// if downloadedDLCs array doesnt exist, it is generated dinamically the 
    				// first time that the game is loaded 
    				if (downloadedDLCS.size() > i)
    				{
    					chkDLCDownloaded.setChecked(downloadedDLCS.get(i).getDownloaded());
    				}
    				else
    				{
    					downloadedDLCS.add(new DLC(dlc.getId(), false));
    				}
    				
    				Bitmap cover;
    				Bitmap imgHorizontal;
    				if (!_isArcade)
    				{
    					cover = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.COVER_DLC_PATH + this._game.getId() + "_" + dlc.getId() , null, null));
    					imgHorizontal = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.COVER_HORIZONTAL_DLC_PATH + this._game.getId() + "_" + dlc.getId() , null, null));
        			}
    				else
    				{
    					cover = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ARCADE_COVER_DLC_PATH + this._game.getId() + "_" + dlc.getId() , null, null));
    					imgHorizontal = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ARCADE_COVER_HORIZONTAL_DLC_PATH + this._game.getId() + "_" + dlc.getId() , null, null));
        			}
    				
    				// Vertical cover
    		    	if (cover != null)
    		    	{
    		    		lytDLCCover.setVisibility(ImageView.VISIBLE);
    		    		imgDLCCover.setImageBitmap(cover);
    		    	}
    		    	else
    		    	{
    		    		lytDLCCover.setVisibility(ImageView.GONE);
    		    	}
    		    	
    		    	// Horizontal cover
    		    	if (imgHorizontal != null)
    		    	{
    		    		lytDLCHorizontal.setVisibility(ImageView.VISIBLE);
    		    		imgDLCHorizontal.setImageBitmap(imgHorizontal);
    		    	}
    		    	else
    		    	{
    		    		lytDLCHorizontal.setVisibility(ImageView.GONE);
    		    	}
    				
    		    	// Listener for the check
    		    	chkDLCDownloaded.setOnCheckedChangeListener(new OnCheckedChangeListener() 
    				{
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
						{
							ArrayList<DLC> downloadedDLCSs = _game.getDownloadedDLCs();

							for (DLC donwloadedDLC : downloadedDLCSs)
							{
								if (donwloadedDLC.getId() == dlc.getId())
								{
									donwloadedDLC.setDownloaded(isChecked);
								}
							}
							
							_game.setDownloadedDLCs(downloadedDLCSs);
						}
					});
    		    	
    		    	// The DLC entry is added to the layout
    				this._lytDLCs.addView(lytDLC);
    				
    				// A separator is added to the layout in case there are more DLCs
    				if (i != this._game.getDetails().getDLCs().size() - 1)
    				{
	    				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
	 		    			layoutParams.setMargins(0, 5, 0, 5);
	 		    		LinearLayout linearLayout = new LinearLayout(App.getContext());
	 		    			linearLayout.setLayoutParams(layoutParams);
	 		    			linearLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
	 		    			
		    			this._lytDLCs.addView(linearLayout);
    				}
    			}
 		    }
 		    else
 		    {
 		    	this._lytDLCs.setVisibility(LinearLayout.GONE);
 		    	this._txtNoDLCs.setVisibility(LinearLayout.VISIBLE);
 		    }
	    }
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.loadDLCs]", ex);
		}
    }
    
    /**
     * Displays game links using a dynamically generated list. For each one the following info is
     * displayed: Web description, web name and web icon. Also, each entry has a clicklistener,
     * when clicked, a internet browser will be opened to access the link
     */
    private void loadLinks()
    {
    	boolean isTablet = getResources().getBoolean(R.bool.isTablet);
	    
    	try
    	{
    		if (this._game.getDetails().getLinks() != null && this._game.getDetails().getLinks().size() > 0)
 		    {
    			LayoutInflater layoutInflater = LayoutInflater.from(this);
    			
    			for (int i = 0; i < this._game.getDetails().getLinks().size(); i++)
    			{
    				// Link info is loaded from the game
    				final Link link = this._game.getDetails().getLinks().get(i);
    				    		
    				// Entry is initializated
    				LinearLayout lytLink = (LinearLayout)layoutInflater.inflate(R.layout.item_link_list, null);
    				TextView txtWebDesc = (TextView)lytLink.findViewById(R.id.txtWebDesc);
    				TextView txtWebName = (TextView)lytLink.findViewById(R.id.txtWebName);
    				ImageView imgIcon = (ImageView)lytLink.findViewById(R.id.imgIcon);
    				
    				// Web description and name
    				txtWebDesc.setText(link.getType());
					txtWebName.setText(link.getSite());
    				   
					// Web icon, depends on the site
					if (!TextUtils.isEmpty(link.getType()))
					{
						if (link.getSite().equals(BLParameters.LINKS.YOUTUBE))
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_yt));
						}
						else if (link.getSite().equals(BLParameters.LINKS.GAMEFAQS))
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_gf));
						}
						else if (link.getSite().equals(BLParameters.LINKS.TRUEACHIEVEMENTS))
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_ta));
						}
						else if (link.getSite().equals(BLParameters.LINKS.XBOXACHIEVEMENTS))
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_xa));
						}
						else if (link.getSite().equals(BLParameters.LINKS.METACRITIC))
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_mc));
						}
						else
						{
							imgIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_www));
						}
					}
    				
					// Listener of the link, it opens a browser when clicked
    				lytLink.setBackgroundDrawable(getResources().getDrawable(R.drawable.link_background));
    				lytLink.setOnClickListener(new OnClickListener()
    				{
						public void onClick(View view) 
						{
							Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
							startActivity(linkIntent);
						}
					});
    				
    				// The Link entry is added to the layout
    				this._lytLinks.addView(lytLink);
    				
    				// A separator is added to the layout in case there are more links
    				if (i != this._game.getDetails().getLinks().size() - 1)
    				{
	    					
	    				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
	 		    			layoutParams.setMargins(isTablet ? 25 : 15, 0, isTablet ? 15 : 10, 0);
	 		    		LinearLayout linearLayout = new LinearLayout(App.getContext());
	 		    			linearLayout.setLayoutParams(layoutParams);
	 		    			linearLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
	 		    			
		    			this._lytLinks.addView(linearLayout);
    				}
    			}
 		    }
    		else
 		    {
    			LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		    		layoutParams.setMargins(10, 5, 5, 0);
		    	TextView txtNoLinks = new TextView(App.getContext());
			    	txtNoLinks.setText(this.getString(R.string.detail_no_links));
			    	txtNoLinks.setTextSize(14);
			    	txtNoLinks.setTextColor(this.getResources().getColor(R.color.black));
			    	txtNoLinks.setLayoutParams(layoutParams);
			    	txtNoLinks.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			    				    	
 		    	this._lytLinks.addView(txtNoLinks);
 		    }
    		
    		this._edtNotes.setText(this._game.getNotes());
    	}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.loadExtras]", ex);
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
			XLog.e("[GameDetailActivity.showErrorDialog]", ex);
		}
	}
    
    /**
     * Displays a confirmation dialog indicating that the game is going to be deleted
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
						deleteGame();
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
			XLog.e("[GameDetailActivity.showConfirmationDialog]", ex);
		}
	}
    
    /**
     * Deletes the current game, removing it from the collection stored in Shared Preferences
     */
    private void deleteGame()
    {
    	try
    	{
    		boolean operationSuccess = false;
        	
	    	if (StorageController.existsGame(this._game.getId(), this._isArcade))
			{
	    		operationSuccess = StorageController.removeGame(this._game.getId(), this._isArcade);
			}
	    	
	    	if (operationSuccess)
	    	{
	    		BLUtils.showToast(this.getString(R.string.detail_remove_game).replace("X", this._game.getTitle()), true);
	    		setResult(RESULT_OK, new Intent());
	    		this.finish();
	    	}
	    	else
	    	{
	    		this.showErrorDialog(this.getString(R.string.general_error));
	    	}
    	}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.deleteGame]", ex);
		}
    }
    
    /**
     * Redraws every text of the activity in order for the language change to take effect
     */
    private void changeTextLanguage()
    {
    	try
    	{
    		this.supportInvalidateOptionsMenu();
    		this._txtGenre.setText("");
    		this._txtDeveloper.setText("");
    		this._lytDLCs.removeAllViews();
    		this._lytLinks.removeAllViews();
    		
    		ArrayList<OptionString> statusList = new ArrayList<OptionString>();
		    	statusList.add(new OptionString(getString(R.string.catalog_status_not_own), Game.Status.NOT_OWN.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_own), Game.Status.OWN.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_finished), Game.Status.FINISHED.toString()));
				statusList.add(new OptionString(getString(R.string.catalog_status_completed), Game.Status.COMPLETED.toString()));
			this._statusAdapter = new StatusAdapter(this, R.layout.simple_spinner_item_custom, statusList);
				this._statusAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnStatus.setAdapter(this._statusAdapter);
			
			((TextView)this.findViewById(R.id.txtReleaseDescription)).setText(this.getString(R.string.detail_release));
			((TextView)this.findViewById(R.id.txtGenreDescription)).setText(this.getString(R.string.detail_genre));
			((TextView)this.findViewById(R.id.txtDeveloperDescription)).setText(this.getString(R.string.detail_developer));
			((TextView)this.findViewById(R.id.txtStatusDescription)).setText(this.getString(R.string.detail_status));
			((TextView)this.findViewById(R.id.txtAchievementsDescription)).setText(this.getString(R.string.detail_achievements));
			((TextView)this.findViewById(R.id.txtMultiplayerDescription)).setText(this.getString(R.string.detail_multiplayer));
			((TextView)this.findViewById(R.id.txtOnlineDescription)).setText(this.getString(R.string.detail_multiplayer_online));
			((TextView)this.findViewById(R.id.txtCoopDescription)).setText(this.getString(R.string.detail_multiplayer_coop));
			((TextView)this.findViewById(R.id.txtSplitScreenDescription)).setText(this.getString(R.string.detail_multiplayer_split_screen));
			((TextView)this.findViewById(R.id.txtSystemLinkDescription)).setText(this.getString(R.string.detail_multiplayer_system_link));
			((TextView)this.findViewById(R.id.txtLabelKinect)).setText(this.getString(R.string.detail_kinect));
			((TextView)this.findViewById(R.id.txtSynopsisDescription)).setText(this.getString(R.string.detail_synopsis));
			((TextView)this.findViewById(R.id.txtDLCsDescription)).setText(this.getString(R.string.detail_dlcs));
			((TextView)this.findViewById(R.id.txtLinksDescription)).setText(this.getString(R.string.detail_links));
			((TextView)this.findViewById(R.id.txtNotesDescription)).setText(this.getString(R.string.detail_notes));
			this._txtNoDLCs.setText(this.getString(R.string.detail_no_dlc));
			this._edtNotes.setHint(getString(R.string.detail_notes_hint));
    	}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.changeTextLanguage]", ex);
		}
    }
    
    /**
     * Called after making an import, loads the new editable data of the current game, if there 
     * is any data at all. Non editable data is reloaded too in case the language has been changed
     * (possible when making the import at Photo Gallery Activity)
     */
    private void reloadAfterImport()
	{
		try
		{
			this._reloadCollection = true;
			Catalog collection = StorageController.loadCollection(this._isArcade);
			
			boolean found = false;
			if (collection != null && collection.getCatalog() != null 
					&& collection.getCatalog().size() > 0)
			{
				for (Game game : collection.getCatalog())
				{
					if (game.getId() == this._game.getId())
					{
						// If the game is in the new collection, the new editable data is loaded
						this._game.setCurrentAchievements(game.getCurrentAchievements());
						this._game.setStatus(game.getStatus());
						this._game.setDownloadedDLCs(game.getDownloadedDLCs());
						this._game.setNotes(game.getNotes());
						found = true;
						break;
					}
				}
			}
			
			// If the game is not in the new collection, editable data is reset
			if (!found)
			{
				this._game.setCurrentAchievements(0);
				this._game.setDownloadedDLCs(new ArrayList<DLC>());
				this._game.setNotes("");
				this._game.setStatus(Status.NOT_OWN);
			}

			// Its possible that the language has been changed too (when calling the method after
			// coming from Photo Gallery), we load all the data again
			this.supportInvalidateOptionsMenu();
    		this._txtGenre.setText("");
    		this._txtDeveloper.setText("");
    		this._lytDLCs.removeAllViews();
    		this._lytLinks.removeAllViews();
		    
			this.loadBasicData();
			this.loadExtendedData();
			this.loadDLCs();
			this.loadLinks();
		}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.reloadAfterImport]", ex);
		}
	}
    
    //------------------------------------------------------------------------- EVENTS*/
    
    /**
	 * Event related to the push of the back button. If the collection is to be reloaded (mainly
	 * because the language has been changed, a new game has been added or a game has been deleted)
	 * it returns an OK and a true boolean to the caller activity
	 */
    public boolean onKeyDown(int keyCode, KeyEvent event) 
  	{
  	    if (keyCode == KeyEvent.KEYCODE_BACK) 
  	    {
  	    	if (this._reloadCollection)
  	    	{
  	    		this.setResult(RESULT_OK, new Intent());
  	    	}
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
	        inflater.inflate(R.menu.game_detail_menu, menu);
	    }
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.onCreateOptionsMenu]", ex);
		}
  		
        return true;
    }
    
    /**
	 * Event related to the display of the action menu. Depending on whether the game is in the
	 * collection or not, remove icon will be displayed and OK icon title will change 
	 * (add or edit)
	 */
    public boolean onPrepareOptionsMenu(Menu menu)
	{
		try
		{
			if (StorageController.existsGame(this._game.getId(), this._isArcade))
			{
				menu.findItem(R.id.options_ok).setTitle(getString(R.string.options_save_changes));
				menu.findItem(R.id.options_remove).setVisible(true);
			}
			else
			{
				menu.findItem(R.id.options_ok).setTitle(getString(R.string.options_add_game));
				menu.findItem(R.id.options_remove).setVisible(false);
			}
		}
	    catch (Exception ex)
	    {
	    	XLog.e("[GameDetailActivity.onPrepareOptionsMenu]", ex);
	    }
		
		return true;
	}
    
    /**
	 * Event related to the selection of one of the items of the action bar
	 */
    public boolean onOptionsItemSelected(MenuItem item) 
	{
    	boolean operationSuccess = false;
    	String toastText = "";
    	Intent output = new Intent();
    	
    	try
		{
		    switch (item.getItemId()) 
		    {
			    case R.id.options_ok:
			    	// Editable data is saved and the game is added to the collection or edited
			    	// in case it existed
			    	this._game.setStatus(Game.Status.valueOf(this._statusAdapter.getItem(this._spnStatus.getSelectedItemPosition()).getOptionKey()));
		    		this._game.setNotes(this._edtNotes.getText().toString());
		    		this._game.setDetails(null);
		    		
		    		if (StorageController.existsGame(this._game.getId(), this._isArcade))
	    			{
			    		operationSuccess = StorageController.editGame(this._game, this._isArcade);
			    		toastText = this.getString(R.string.detail_edit_game);
	    			}
			    	else
			    	{
			    		operationSuccess = StorageController.addGame(this._game, this._isArcade);
			    		toastText = this.getString(R.string.detail_add_game);
			    	}
			    	
			    	if (operationSuccess)
			    	{
			    		BLUtils.showToast(toastText.replace("X", this._game.getTitle()), true);
			    		setResult(RESULT_OK, output);
			    		this.finish();
			    	}
			    	else
			    	{
			    		this.showErrorDialog(this.getString(R.string.general_error));
			    	}
			    	break;
			    case R.id.options_remove:
			    	this.showDeleteConfirmationDialog(this.getString(R.string.detail_remove_game_confirmation));
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
		    	case R.id.options_photo_gallery:
		    		output = new Intent(this, PhotoGalleryActivity.class);
			    	output.putExtra(BLParameters.PARAMETERS.GAME, (this._isArcade ? "A" : "R") + String.valueOf(this._game.getId()));
			    	output.putExtra(BLParameters.PARAMETERS.GAME_TITLE, this._game.getTitle());
			    	output.putExtra(BLParameters.PARAMETERS.IS_ARCADE, this._isArcade);
			    	startActivityForResult(output, BLParameters.ACTIVITY_CODES.PHOTO_GALLERY);
			        break;
		    }
		}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.onOptionsItemSelected]", ex);
		}
    	
    	return true;
	}

    /**
     * Event related to the touch of the Edit Achievements button.
     * Activity is called to edit the unlocked achievements
     */
	public void onClick(View view)
	{
		try
		{
			if (view.getId() == this._btnEditAchievements.getId())
			{
				Intent intent = new Intent(this, EditAchievementsActivity.class);
				intent.putExtra(BLParameters.PARAMETERS.CURRENT_ACHIEVEMENTS, this._game.getCurrentAchievements());
				intent.putExtra(BLParameters.PARAMETERS.TOTAL_ACHIEVEMENTS, this._game.getTotalAchievements());
	    		startActivityForResult(intent, BLParameters.ACTIVITY_CODES.EDIT_ACHIEVEMENTS);
			}
		}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.onClick]", ex);
		}
	}
	
	/**
     * Event related to the return from other activity.
     */
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		try
		{
			super.onActivityResult(requestCode, resultCode, data);
			
			if (requestCode == BLParameters.ACTIVITY_CODES.EDIT_ACHIEVEMENTS && resultCode == Activity.RESULT_OK)
			{	
				// New unlocked achievements must be displayed
				if (data != null)
				{
					int achievements = data.getIntExtra(BLParameters.PARAMETERS.CURRENT_ACHIEVEMENTS, 0);
					this._game.setCurrentAchievements(achievements);
					this._progressAchievements.setProgress(achievements);
					this._txtAchievements.setText(this._game.getCurrentAchievements() + "/" + this._game.getTotalAchievements());
				}
			}
			if (requestCode == BLParameters.ACTIVITY_CODES.CONFIG && resultCode == Activity.RESULT_OK)
			{
				// Language changed. Every text must be refreshed
				this.changeTextLanguage();

				this.loadBasicData();
				this.loadExtendedData();
				this.loadDLCs();
				this.loadLinks();
			}
			if (requestCode == BLParameters.ACTIVITY_CODES.PHOTO_GALLERY && resultCode == Activity.RESULT_OK)
			{
				// Language could have been changed. Every text must be refreshed
				this.changeTextLanguage();
				
				if (data.getBooleanExtra(BLParameters.PARAMETERS.RELOAD_COLLECTION, false))
				{
					// Import made, all editable data must be reloaded
					this.reloadAfterImport();
				}
				else
				{
					this.loadBasicData();
					this.loadExtendedData();
					this.loadDLCs();
					this.loadLinks();
				}
			}
			if (requestCode == BLParameters.ACTIVITY_CODES.IMPORT && resultCode == Activity.RESULT_OK)
			{
				this.reloadAfterImport();
			}
		}
		catch (Exception ex)
		{
			XLog.e("[GameDetailActivity.onActivityResult]", ex);
		}
	}
    
    //------------------------------------------------------------------------- INNER CLASSES*/
    
	/**
	 * Adapter used by the spinners. Description is shown (optionName) and value is returned upon
	 * selection (optionKey)
	 */
    private class StatusAdapter extends ArrayAdapter<OptionString>
    {
        private ArrayList<OptionString> values;

        public StatusAdapter(Context context, int textViewResourceId, ArrayList<OptionString> values) 
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
        
        public int getItemPosition(String key)
        {
        	int position = 0;
        	
        	if (!TextUtils.isEmpty(key))
        	{
	        	for (int i = 0; i < values.size(); i++)
	        	{
	        		if (key.equalsIgnoreCase(values.get(i).getOptionKey()))
	        		{
	        			position = i;
	        		}
	        	}
        	}
        	
        	return position;
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
  	 * Asynchronous method used to load the cover, made this way due to the high amount of time
  	 * it required to load the cover from the covers zip stored in the device (no longer used).
  	 * A loading icon is showed in the place of the cover during this process
  	 */
    private class LoadCover extends AsyncTask<Boolean, Void, Boolean>
	{		
    	Bitmap _cover;
    	
		@Override
		protected void onPreExecute()
		{	
			_prgCover.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Boolean... parameters)
		{
			try
			{
	    		if (!_isArcade)
		    	{
	    			//TODO: Only for Google Play. This code was for retrieving the cover from
	    			//the covers expansion zip
	    			/*ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(App.getContext(), 1, 0);
	        		InputStream fileStream = expansionFile.getInputStream(BLParameters.GENERAL_PARAMETERS.COVER_PATH + _game.getId() + ".jpg");
	        		_cover = BitmapFactory.decodeStream(fileStream);
	        		fileStream.close();*/
	    			_cover = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.COVER_PATH  + _game.getId() , null, null));
		    	}
		    	else
		    	{
		    		//TODO: Only for Google Play. This code was for retrieving the cover from
	    			//the covers expansion zip
		    		/*ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(App.getContext(), 1, 0);
	        		InputStream fileStream = expansionFile.getInputStream(BLParameters.GENERAL_PARAMETERS.ARCADE_COVER_PATH + _game.getId() + ".jpg");
	        		_cover = BitmapFactory.decodeStream(fileStream);
	        		fileStream.close();*/
		    		_cover = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(getPackageName() + BLParameters.GENERAL_PARAMETERS.ARCADE_COVER_PATH  + _game.getId() , null, null));
		    	}
			}
			catch (Exception ex)
			{
				XLog.e("[GameDetailActivity.LoadCover.doInBackground]", ex);
			}
		    
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			_prgCover.setVisibility(ProgressBar.GONE);
			
			if (_cover != null)
	    	{
	    		_imgCover.setImageBitmap(_cover);
	    	}
	    	else
	    	{
	    		_imgCover.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cover_unknown));
	    	}
		}
	}
}
