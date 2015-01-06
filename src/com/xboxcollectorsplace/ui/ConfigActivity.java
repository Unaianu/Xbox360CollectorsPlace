package com.xboxcollectorsplace.ui;

import java.util.ArrayList;

import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.OptionString;
import com.xboxcollectorsplace.bl.entities.Options;
import com.xboxcollectorsplace.bl.entities.Options.Language;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Windowed activity, called anywhere in the App and used to configurate the language, the boot
 * video shown in the title screen and the value shown in the catalog, genre or year (only for
 * Smartphones)
 */
public class ConfigActivity extends Activity implements OnClickListener, OnItemSelectedListener
{
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private TextView _txtConfigTitle;
	private TextView _txtLanguage;
	private TextView _txtConfigBoot;
	private TextView _txtShowGenre;
	private Spinner _spnLanguage;
	private Spinner _spnBoot;
	private Spinner _spnShowGenre;
	private Button _btnSave;
	
	private OptionAdapter _languageAdapter;
	private OptionAdapter _bootAdapter;
	private OptionAdapter _showGenreAdapter;
	
	private Language _originalLanguage;
	
	private boolean _changeLanguage = false;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
	        super.onCreate(savedInstanceState);	        
	        setContentView(R.layout.activity_config);
	        	 
	        this._txtConfigTitle = (TextView)this.findViewById(R.id.txtConfigTitle);
	        this._txtLanguage = (TextView)this.findViewById(R.id.txtLanguage);
			this._txtConfigBoot = (TextView)this.findViewById(R.id.txtConfigBoot);
			this._txtShowGenre = (TextView)this.findViewById(R.id.txtConfigShowGenre);
			this._spnLanguage = (Spinner)this.findViewById(R.id.spnLanguage);
			this._spnBoot = (Spinner)this.findViewById(R.id.spnBoot);
			this._spnShowGenre = (Spinner)this.findViewById(R.id.spnShowGenre);
			this._btnSave = (Button)this.findViewById(R.id.btnSave);
	        
			this._btnSave.setOnClickListener(this);
			this._spnLanguage.setOnItemSelectedListener(this);
			
			this.initializeSpinners();
			this.loadOptions();
	    }
    	catch (Exception ex)
		{
			XLog.e("[ConfigActivity.onCreate]", ex);
        }
    }
    
    //------------------------------------------------------------------------- PRIVATE METHODS*/
    
    /**
     * Initializes the spinner values and sets the adapters
     */
    private void initializeSpinners()
    {
    	try
    	{
	    	ArrayList<OptionString> languageList = new ArrayList<OptionString>();
				languageList.add(new OptionString(BLParameters.LANGUAGES.CZECH_DESCRIPTION, Options.Language.CZECH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.GERMAN_DESCRIPTION, Options.Language.GERMAN.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.ENGLISH_DESCRIPTION, Options.Language.ENGLISH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.SPANISH_DESCRIPTION, Options.Language.SPANISH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.BASQUE_DESCRIPTION, Options.Language.BASQUE.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.FRENCH_DESCRIPTION, Options.Language.FRENCH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.ITALIAN_DESCRIPTION, Options.Language.ITALIAN.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.JAPANESE_DESCRIPTION, Options.Language.JAPANESE.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.KOREAN_DESCRIPTION, Options.Language.KOREAN.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.DUTCH_DESCRIPTION, Options.Language.DUTCH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.NORWEGIAN_DESCRIPTION, Options.Language.NORWEGIAN.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.POLISH_DESCRIPTION, Options.Language.POLISH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.PORTUGUESE_DESCRIPTION, Options.Language.PORTUGUESE.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.RUSSIAN_DESCRIPTION, Options.Language.RUSSIAN.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.FINNISH_DESCRIPTION, Options.Language.FINNISH.toString()));
				languageList.add(new OptionString(BLParameters.LANGUAGES.SWEDISH_DESCRIPTION, Options.Language.SWEDISH.toString()));
				
			ArrayList<OptionString> bootList = new ArrayList<OptionString>();
				bootList.add(new OptionString(getString(R.string.config_intro_classic), Options.BootType.OLD.toString()));
				bootList.add(new OptionString(getString(R.string.config_intro_new), Options.BootType.NEW.toString()));
				
			ArrayList<OptionString> showGenreList = new ArrayList<OptionString>();
				showGenreList.add(new OptionString(getString(R.string.config_show_genre_year), Options.ShowGenre.YEAR.toString()));
				showGenreList.add(new OptionString(getString(R.string.config_show_genre_genre), Options.ShowGenre.GENRE.toString()));
				
			this._languageAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, languageList);
			this._languageAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnLanguage.setAdapter(this._languageAdapter);
			
			this._bootAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, bootList);
			this._bootAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnBoot.setAdapter(this._bootAdapter);
			
			this._showGenreAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, showGenreList);
			this._showGenreAdapter.setDropDownViewResource(R.layout.spinner_line_dropdown);
			this._spnShowGenre.setAdapter(this._showGenreAdapter);
    	}
    	catch (Exception ex)
		{
			XLog.e("[ConfigActivity.initializeSpinners]", ex);
        }
	}
    
    /**
     * Loads the previously selected options stored in Shared Preferences
     */
    private void loadOptions()
    {
    	try
    	{
	    	Options loadedOptions = StorageController.loadOptions();
	    	
	    	this._originalLanguage = loadedOptions.getLanguage();
	    	
	    	this._spnLanguage.setSelection(this._languageAdapter.getItemPosition(loadedOptions.getLanguage().toString()));
	    	this._spnBoot.setSelection(this._bootAdapter.getItemPosition(loadedOptions.getBoot().toString()));
			this._spnShowGenre.setSelection(this._showGenreAdapter.getItemPosition(loadedOptions.getShowGenre().toString()));
    	}
    	catch (Exception ex)
		{
			XLog.e("[ConfigActivity.loadOptions]", ex);
        }
    }
    
    /**
     * After changing the language, all the texts are redrawn and the spinners are refilled again
     * in order to show them in the correct language
     */
    private void changeTextLanguage()
    {
    	try
    	{
	    	this._txtConfigTitle.setText(this.getString(R.string.config_title));
	    	this._txtLanguage.setText(this.getString(R.string.config_language));
	    	this._txtConfigBoot.setText(this.getString(R.string.config_intro));
	    	this._txtShowGenre.setText(this.getString(R.string.config_show_genre));
	    	this._btnSave.setText(this.getString(R.string.config_button_save));
	    	
	    	Options options = StorageController.loadOptions();
				options.setLanguage(Options.Language.valueOf(this._languageAdapter.getItem(this._spnLanguage.getSelectedItemPosition()).getOptionKey()));
				options.setBoot(Options.BootType.valueOf(this._bootAdapter.getItem(this._spnBoot.getSelectedItemPosition()).getOptionKey()));
				
			this.initializeSpinners();
			
			this._spnLanguage.setSelection(this._languageAdapter.getItemPosition(options.getLanguage().toString()));
	    	this._spnBoot.setSelection(this._bootAdapter.getItemPosition(options.getBoot().toString()));
			this._spnShowGenre.setSelection(this._showGenreAdapter.getItemPosition(options.getShowGenre().toString()));
    	}
    	catch (Exception ex)
		{
			XLog.e("[ConfigActivity.changeTextLanguage]", ex);
        }
    }
    
    //------------------------------------------------------------------------- EVENTS*/
    
    /** 
     * Event related to the touch of the Save button. Stores the selected values for the options
     * and returns to the caller activity sending a OK. This means that the caller activitys texts
     * must be redrawn, as the language could have been changed
     */
    public void onClick(View view)
	{
		try
		{			
			if (view.getId() == this._btnSave.getId())
			{
				Options options = StorageController.loadOptions();
					options.setLanguage(Options.Language.valueOf(this._languageAdapter.getItem(this._spnLanguage.getSelectedItemPosition()).getOptionKey()));
					options.setBoot(Options.BootType.valueOf(this._bootAdapter.getItem(this._spnBoot.getSelectedItemPosition()).getOptionKey()));
					options.setShowGenre(Options.ShowGenre.valueOf(this._showGenreAdapter.getItem(this._spnShowGenre.getSelectedItemPosition()).getOptionKey()));
					
				StorageController.saveOptions(options);
				
				Intent output = new Intent();
				setResult(Activity.RESULT_OK, output);
				this.finish();
			}
		}
		catch (Exception ex) 
		{
			XLog.e("[ConfigActivity.onClick]", ex);
		}
	}    
    
    /**
     * Event related to the selection of an item of the language spinner. All the texts must be
     * redrawn using the new language. _changeLanguage prevents from entering in a loop, as this
     * event is always called when the language spinner is refilled after changing the language
     */
	public void onItemSelected(AdapterView<?> aView, View view, int position, long id) 
	{
		try
		{	
			if (aView.getId() == this._spnLanguage.getId())
			{				
				if (this._changeLanguage)
				{
					this._changeLanguage = false;
					
					BLUtils.changeLanguage(Language.valueOf(this._languageAdapter.getItem(position).getOptionKey()));
					this.changeTextLanguage();
				}
				else
				{
					this._changeLanguage = true;
				}
			}
		}
		catch (Exception ex) 
		{
			XLog.e("[ConfigActivity.onItemSelected]", ex);
		}
	}

	public void onNothingSelected(AdapterView<?> aView) 
	{
	}

	/**
	 * Event related to the push of the back button. It returns to the caller activity removing
	 * any change, this includes changing to the original language in which this activity
	 * was called 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)  
	{
		try
		{
		    if (keyCode == KeyEvent.KEYCODE_BACK)
		    {
		    	BLUtils.changeLanguage(this._originalLanguage);
				
				this.finish();
		    }
		}
		catch (Exception ex) 
		{
			XLog.e("[ConfigActivity.onKeyDown]", ex);
		}

	    return super.onKeyDown(keyCode, event);
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
