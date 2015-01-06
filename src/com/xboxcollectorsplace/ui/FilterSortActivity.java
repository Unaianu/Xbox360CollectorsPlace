package com.xboxcollectorsplace.ui;

import java.util.ArrayList;
import java.util.Collections;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Filters;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.OptionString;
import com.xboxcollectorsplace.bl.entities.Sort;
import com.xboxcollectorsplace.utils.XComparator;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Windowed activity, used to add or remove filters for the collection/catalog list, and changing
 * its sorting
 */
public class FilterSortActivity extends Activity implements OnClickListener, OnItemSelectedListener 
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private ScrollView _lytScroll;
	private Spinner _spnSort;
	private EditText _edtFilterTitle;
	private TextView _txtLabelFilterStatus;
	private Spinner _spnFilterStatus;
	private Spinner _spnFilterGenre1;
	private Spinner _spnFilterGenre2;
	private EditText _edtFilterYear;
	private EditText _edtFilterOnline;
	private EditText _edtFilterCoop;
	private EditText _edtFilterSplitScreen;
	private EditText _edtFilterSystemLink;
	private Spinner _spnFilterKinect;
	private Button _btnAccept;
	private Button _btnClear;
	
	private boolean _isArcade;
	private boolean _comesFromCollection;
	
	private OptionAdapter _sortAdapter;
	private OptionAdapter _filterStatusAdapter;
	private OptionAdapter _filterGenre1Adapter;
	private OptionAdapter _filterGenre2Adapter;
	private OptionAdapter _filterKinectAdapter;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_filter_sort);
	        
	        this._spnSort = (Spinner)this.findViewById(R.id.spnSort);
	        this._edtFilterTitle = (EditText)this.findViewById(R.id.edtFilterTitle);
	        this._txtLabelFilterStatus = (TextView)this.findViewById(R.id.txtLabelFilterStatus);
	        this._spnFilterStatus = (Spinner)this.findViewById(R.id.spnFilterStatus);
	        this._spnFilterGenre1 = (Spinner)this.findViewById(R.id.spnFilterGenre1);
	        this._spnFilterGenre2 = (Spinner)this.findViewById(R.id.spnFilterGenre2);
	        this._edtFilterYear = (EditText)this.findViewById(R.id.edtFilterYear);
	        this._edtFilterOnline = (EditText)this.findViewById(R.id.edtFilterOnline);
	        this._edtFilterCoop = (EditText)this.findViewById(R.id.edtFilterCoop);
	        this._edtFilterSplitScreen = (EditText)this.findViewById(R.id.edtFilterSplitScreen);
	        this._edtFilterSystemLink = (EditText)this.findViewById(R.id.edtFilterSystemLink);
	        this._spnFilterKinect = (Spinner)this.findViewById(R.id.spnFilterKinect);
	        this._btnAccept = (Button)this.findViewById(R.id.btnAccept);
	        this._btnClear = (Button)this.findViewById(R.id.btnClear);
		    
	        // Used to prevent the editTexts from getting the focus upon creation
	        this._lytScroll = (ScrollView)findViewById(R.id.lytScroll);
	        this._lytScroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
	        this._lytScroll.setFocusable(true);
	        this._lytScroll.setFocusableInTouchMode(true);
	        this._lytScroll.setOnTouchListener(new View.OnTouchListener() 
	        {
				public boolean onTouch(View v, MotionEvent event)
				{
	                v.requestFocusFromTouch();
	                return false;
				}
	        });
	        
	        this._btnAccept.setOnClickListener(this);
	        this._btnClear.setOnClickListener(this);
			this._spnFilterGenre1.setOnItemSelectedListener(this);
	        
			this._isArcade = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IS_ARCADE, false);
			this._comesFromCollection = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.COMES_FROM_COLLECTION, false);
			
			// Spinners are loaded and linked to the adapters
			ArrayList<OptionString> sortList = new ArrayList<OptionString>();
				sortList.add(new OptionString(getString(R.string.filter_sort_sort_year_asc), Sort.ListSort.ASC_YEAR.toString()));
				sortList.add(new OptionString(getString(R.string.filter_sort_sort_year_desc), Sort.ListSort.DESC_YEAR.toString()));
				sortList.add(new OptionString(getString(R.string.filter_sort_sort_title_asc), Sort.ListSort.ASC_TITLE.toString()));
				sortList.add(new OptionString(getString(R.string.filter_sort_sort_title_desc), Sort.ListSort.DESC_TITLE.toString()));
				if (!_comesFromCollection)
				{
					sortList.add(new OptionString(getString(R.string.filter_sort_sort_achievements_asc), Sort.ListSort.ASC_ACHIEVEMENTS.toString()));
					sortList.add(new OptionString(getString(R.string.filter_sort_sort_achievements_desc), Sort.ListSort.DESC_ACHIEVEMENTS.toString()));
				}
				
			ArrayList<OptionString> statusList = new ArrayList<OptionString>();
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_all), Filters.ListStatus.ALL.toString()));
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_games_left), Filters.ListStatus.GAMES_LEFT.toString()));
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_games_own), Filters.ListStatus.GAMES_OWN.toString()));
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_games_not_finished), Filters.ListStatus.GAMES_NOT_FINISHED.toString()));
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_games_finished), Filters.ListStatus.GAMES_FINISHED.toString()));
				statusList.add(new OptionString(getString(R.string.filter_sort_filter_status_games_completed), Filters.ListStatus.GAMES_COMPLETED.toString()));
				
			ArrayList<OptionString> genreList1 = new ArrayList<OptionString>();
				genreList1.add(new OptionString(getString(R.string.genre_all), Game.Genre.ALL.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_action_adventure), Game.Genre.ACTION_ADVENTURE.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_fps), Game.Genre.FPS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_tps), Game.Genre.TPS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_action), Game.Genre.ACTION.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_rpg), Game.Genre.RPG.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_arpg), Game.Genre.ARPG.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_jrpg), Game.Genre.JRPG.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_trpg), Game.Genre.TRPG.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_sports), Game.Genre.SPORTS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_fighting), Game.Genre.FIGHTING.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_survival), Game.Genre.SURVIVAL.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_racing), Game.Genre.RACING.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_musical), Game.Genre.MUSICAL.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_adventure), Game.Genre.ADVENTURE.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_stealth), Game.Genre.STEALTH.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_rts), Game.Genre.RTS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_tbs), Game.Genre.TBS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_platformer), Game.Genre.PLATFORMER.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_hs), Game.Genre.HS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_btu), Game.Genre.BTU.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_stu), Game.Genre.STU.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_puzzle), Game.Genre.MUSICAL.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_tower_defense), Game.Genre.TD.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_cfs), Game.Genre.CFS.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_sim), Game.Genre.SIM.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_hunting_fishing), Game.Genre.HUNTING_FISHING.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_party), Game.Genre.PARTY.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_cards_board), Game.Genre.CARDS_BOARD.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_poker), Game.Genre.POKER.toString()));
				genreList1.add(new OptionString(getString(R.string.genre_pinball), Game.Genre.PINBALL.toString()));
				if (!_isArcade)
				{
					genreList1.add(new OptionString(getString(R.string.genre_open_world), Game.Genre.OPEN_WORLD.toString()));
					genreList1.add(new OptionString(getString(R.string.genre_musou), Game.Genre.MUSOU.toString()));
					genreList1.add(new OptionString(getString(R.string.genre_visual_novel), Game.Genre.VISUAL_NOVEL.toString()));
					genreList1.add(new OptionString(getString(R.string.genre_fitness), Game.Genre.FITNESS.toString()));
					genreList1.add(new OptionString(getString(R.string.genre_mmorpg), Game.Genre.MMORPG.toString()));
				}
			
			Collections.sort(genreList1, new XComparator());
		   				
			ArrayList<OptionString> genreList2 = new ArrayList<OptionString>();
				genreList2.addAll(genreList1);
				genreList2.set(0, new OptionString(getString(R.string.genre_none), Game.Genre.NONE.toString()));
				
			ArrayList<OptionString> kinectList = new ArrayList<OptionString>();
				kinectList.add(new OptionString(getString(R.string.filter_sort_filter_kinect_all), Filters.ListKinect.ALL.toString()));
				kinectList.add(new OptionString(getString(R.string.filter_sort_filter_kinect_not_required), Filters.ListKinect.NOT_REQUIRED.toString()));
				kinectList.add(new OptionString(getString(R.string.filter_sort_filter_kinect_used), Filters.ListKinect.USED.toString()));
				kinectList.add(new OptionString(getString(R.string.filter_sort_filter_kinect_required), Filters.ListKinect.REQUIRED.toString()));
								
			this._sortAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, sortList);
			this._sortAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnSort.setAdapter(this._sortAdapter);
			
			this._filterStatusAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, statusList);
			this._filterStatusAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnFilterStatus.setAdapter(this._filterStatusAdapter);
			
			this._filterGenre1Adapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, genreList1);
			this._filterGenre1Adapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnFilterGenre1.setAdapter(this._filterGenre1Adapter);
						
			this._filterGenre2Adapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, genreList2);
			this._filterGenre2Adapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnFilterGenre2.setAdapter(this._filterGenre2Adapter);
			this._spnFilterGenre2.setEnabled(false);
			
			this._filterKinectAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, kinectList);
			this._filterKinectAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnFilterKinect.setAdapter(this._filterKinectAdapter);
			
			this.loadFilters();
			
			if (!this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.SHOW_STATUS, true))
			{
				this._txtLabelFilterStatus.setVisibility(View.GONE);
				this._spnFilterStatus.setVisibility(View.GONE);
			}
    	}
    	catch (Exception ex)
		{
			XLog.e("[FilterSortActivity.onCreate]", ex);
        }
    }
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/
    
    /**
     * Previously selected filters (and sorting) are loaded
     */
    private void loadFilters()
    {
    	try
    	{
    		this._spnSort.setSelection(this._sortAdapter.getItemPosition(StorageController.loadSorting().getSort().toString()));
    		
			Filters filters = ((Filters) this.getIntent().getSerializableExtra(BLParameters.PARAMETERS.FILTERS));
			
			if (filters != null)
			{
    			this._edtFilterTitle.setText(filters.getTitle());

    			if (filters.getStatus() != null)
    			{
    				this._spnFilterStatus.setSelection(this._filterStatusAdapter.getItemPosition(filters.getStatus().toString()));
    			}
    			
    			if (filters.getGenre1() != null)
    			{
    				this._spnFilterGenre1.setSelection(this._filterGenre1Adapter.getItemPosition(filters.getGenre1().toString()));
    			}
    			
    			if (filters.getGenre2() != null)
    			{
    				this._spnFilterGenre2.setSelection(this._filterGenre2Adapter.getItemPosition(filters.getGenre2().toString()));
    			}
    			
    			if (filters.getYear() != 0)
    			{
    				this._edtFilterYear.setText(String.valueOf(filters.getYear()));
    			}
    			
    			if (filters.getOnline() != 0)
    			{
    				this._edtFilterOnline.setText(String.valueOf(filters.getOnline()));
    			}
    			
    			if (filters.getCoop() != 0)
    			{
    				this._edtFilterCoop.setText(String.valueOf(filters.getCoop()));
    			}
    			
    			if (filters.getSplitScreen() != 0)
    			{
    				this._edtFilterSplitScreen.setText(String.valueOf(filters.getSplitScreen()));
    			}
    			
    			if (filters.getSystemLink() != 0)
    			{
    				this._edtFilterSystemLink.setText(String.valueOf(filters.getSystemLink()));
    			}
    			
    			if (filters.getKinect() != null)
    			{
    				this._spnFilterKinect.setSelection(this._filterKinectAdapter.getItemPosition(filters.getKinect().toString()));
    			}
			}
    	}
    	catch (Exception ex)
		{
			XLog.e("[FilterSortActivity.loadFilters]", ex);
        }
    }
    
    //------------------------------------------------------------------------- EVENTS*/
    
    /**
     * Event related to the touch of the Clear or Accept buttons
     */
    public void onClick(View view)
	{
		try
		{
			if (view.getId() == this._btnClear.getId())
			{
				// Clear touched, all the editText are cleared and the spinners reset
				this._edtFilterTitle.setText("");
				this._spnFilterStatus.setSelection(0);
				this._spnFilterGenre1.setSelection(0);
				this._spnFilterGenre2.setSelection(0);
				this._edtFilterYear.setText("");
				this._edtFilterOnline.setText("");
				this._edtFilterCoop.setText("");
				this._edtFilterSplitScreen.setText("");
				this._edtFilterSystemLink.setText("");
				this._spnFilterKinect.setSelection(0);
			}
			else if (view.getId() == this._btnAccept.getId())
			{
				// Accept touched, sorting is stored on Shared Preferences and all the selected
				// filters are returned to the caller activity
				StorageController.saveSorting(new Sort(Sort.ListSort.valueOf(this._sortAdapter.getItem(this._spnSort.getSelectedItemPosition()).getOptionKey())));
				
				Filters filters = new Filters();
				filters.setTitle(this._edtFilterTitle.getText().toString());

				if (this._spnFilterStatus.getSelectedItemPosition() != 0)
				{
					filters.setStatus(Filters.ListStatus.valueOf(this._filterStatusAdapter.getItem(this._spnFilterStatus.getSelectedItemPosition()).getOptionKey()));
				}
				
				if (this._spnFilterGenre1.getSelectedItemPosition() != 0)
				{
					filters.setGenre1(Game.Genre.valueOf(this._filterGenre1Adapter.getItem(this._spnFilterGenre1.getSelectedItemPosition()).getOptionKey()));
				}
				
				if (this._spnFilterGenre2.getSelectedItemPosition() != 0)
				{
					filters.setGenre2(Game.Genre.valueOf(this._filterGenre2Adapter.getItem(this._spnFilterGenre2.getSelectedItemPosition()).getOptionKey()));
				}
				
				if (!TextUtils.isEmpty(this._edtFilterYear.getText().toString()))
				{
					filters.setYear(Integer.valueOf(this._edtFilterYear.getText().toString()));
					
					if (filters.getYear() == 0)
					{
						filters.setYear(-1);
					}
				}
				
				if (!TextUtils.isEmpty(this._edtFilterOnline.getText().toString()))
				{
					filters.setOnline(Integer.valueOf(this._edtFilterOnline.getText().toString()));
				}
				
				if (!TextUtils.isEmpty(this._edtFilterCoop.getText().toString()))
				{
					filters.setCoop(Integer.valueOf(this._edtFilterCoop.getText().toString()));
				}
				
				if (!TextUtils.isEmpty(this._edtFilterSplitScreen.getText().toString()))
				{
					filters.setSplitScreen(Integer.valueOf(this._edtFilterSplitScreen.getText().toString()));
				}
				
				if (!TextUtils.isEmpty(this._edtFilterSystemLink.getText().toString()))
				{
					filters.setSystemLink(Integer.valueOf(this._edtFilterSystemLink.getText().toString()));
				}
				
				if (this._spnFilterKinect.getSelectedItemPosition() != 0)
				{
					filters.setKinect(Filters.ListKinect.valueOf(this._filterKinectAdapter.getItem(this._spnFilterKinect.getSelectedItemPosition()).getOptionKey()));
				}
				
				Intent output = new Intent();
				output.putExtra(BLParameters.PARAMETERS.FILTERS, filters);
				setResult(RESULT_OK, output);
				this.finish();
			}
		}
		catch (Exception ex) 
		{
			XLog.e("[FilterSortActivity.onClick]", ex);
		}
	}

    /**
     * Event related to the selection of a spinner item. Its used to enable or disable the second
     * genre spinner, depending if the first one has a value selected or not
     */
	public void onItemSelected(AdapterView<?> aView, View view, int position, long id)
	{
		try
		{			
			if (aView.getId() == this._spnFilterGenre1.getId())
			{
				if (this._spnFilterGenre1.getSelectedItemPosition() != 0)
				{
					this._spnFilterGenre2.setEnabled(true);
				}
				else
				{
					this._spnFilterGenre2.setEnabled(false);
					this._spnFilterGenre2.setSelection(0);
				}
			}
		}
		catch (Exception ex) 
		{
			XLog.e("[FilterSortActivity.onItemSelected]", ex);
		}
	}

	public void onNothingSelected(AdapterView<?> parent) 
	{
	} 
	
	//------------------------------------------------------------------------- INNER CLASSES*/
    
	/**
	 * Adapter used by the spinners. Description is shown (optionName) and value is returned upon
	 * selection (optionKey)
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
}
