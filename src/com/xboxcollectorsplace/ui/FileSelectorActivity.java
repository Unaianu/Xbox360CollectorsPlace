package com.xboxcollectorsplace.ui;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.BLUtils;
import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Catalog;
import com.xboxcollectorsplace.bl.entities.SelectionFile;
import com.xboxcollectorsplace.utils.XComparator;
import com.xboxcollectorsplace.utils.XLog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Windowed activity, used for three different actions:
 * 
 * Exporting the current collection in a XML file to the selected path of the device
 * Importing the selected XML file and converting it into a collection, overwriting the current
 * users collection
 * Selecting an image file and returning its path to the Photo Gallery Activity, ir order to add 
 * the photo to the gallery
 */
public class FileSelectorActivity extends Activity implements OnClickListener, OnItemClickListener
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final String VALID_IMPORT_FORMAT = "xml";
	private static final String VALID_IMG_FORMAT = "jpg";
	private static final String VALID_IMG_FORMAT_2 = "jpeg";
	private static final String VALID_IMG_FORMAT_3 = "png";
	private static final String VALID_IMG_FORMAT_4 = "bmp";
	private static final String SEPARATOR = "#ARCADE_SEPARATOR#";
	private static final String EXPORT_FILE_NAME = "X360Collection";
	private static final String EXPORT_FILE_EXTENSION = ".xml";
	
	private static final String PERSISTENCY_CURRENT_PATH = "PERSISTENCY_CURRENT_PATH";
	
	//------------------------------------------------------------------------- ATTRIBUTES*/
	
	private TextView _txtTitle;
	private TextView _txtPath;
	private ListView _lstFiles;
	private Button _btnSelect;
	
	private File _currentPath;
	private FilesAdapter _adapter;
	private boolean _importMode;
	private boolean _imageSelectionMode;
	private int _fileQuantity;
	private String _selectedFilePath;
	
	//------------------------------------------------------------------------- ON CREATE*/
	
	public void onCreate(Bundle savedInstanceState)
    {
		try
		{
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.activity_file_selector);
	        
	        String initialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	        this._currentPath = new File(initialPath);
	        
	        if (this.getIntent() != null && this.getIntent().getExtras() != null)
	     	{
	        	this._importMode = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IMPORT_MODE, false);
	        	this._imageSelectionMode = this.getIntent().getBooleanExtra(BLParameters.PARAMETERS.IMAGE_SELECTION_MODE, false);
	     	}
	        
	        if (savedInstanceState != null)
			{
	        	this._currentPath = (File) savedInstanceState.getSerializable(FileSelectorActivity.PERSISTENCY_CURRENT_PATH);
	      	}
	        
	        this._txtTitle = (TextView)this.findViewById(R.id.txtFileSelectorTitle);
	        this._txtPath = (TextView)this.findViewById(R.id.txtPath);
	        this._lstFiles = (ListView)this.findViewById(R.id.lstFiles);
	        this._btnSelect = (Button)this.findViewById(R.id.btnSelect);
	     	
	        this._btnSelect.setOnClickListener(this);
	     	
			if (this._importMode || this._imageSelectionMode)
	    	{
	     		this._btnSelect.setEnabled(false);
	    	}
	     	
	     	if (this._importMode)
	     	{
	     		this._txtTitle.setText(this.getResources().getString(R.string.file_selector_import_title));
	     		this._btnSelect.setText(this.getResources().getString(R.string.file_selector_import));
	     	}
	     	else
	     	{
	     		if (this._imageSelectionMode)
	     		{
	     			this._txtTitle.setText(this.getResources().getString(R.string.file_selector_image_selection_title));
		     		this._btnSelect.setText(this.getResources().getString(R.string.file_selector_image_selection));
	     		}
	     		else
	     		{
		     		this._txtTitle.setText(this.getResources().getString(R.string.file_selector_export_title));
		     		this._btnSelect.setText(this.getResources().getString(R.string.file_selector_export));
	     		}
	     	}
	     	
	        this.fill(this._currentPath);
		}
		catch (Exception ex)
		{
			XLog.e("[FileSelectorActivity.onCreate]", ex);
		}
    }

	//------------------------------------------------------------------------- PRIVATE METHODS*/
    
	/** 
	 * Fills the file list. In order to do that, it reads the content of the current path,
	 * makes a list with all the folders inside it, another one with all the valid files, sorts
	 * them alphabetically and combine adding the ".." folder, used to navigate to the parent 
	 * folder
	 */
	private void fill(File currentFile)
    {
        File[] files = currentFile.listFiles();
        ArrayList<SelectionFile> folderList = new ArrayList<SelectionFile>();
        ArrayList<SelectionFile> importFileList = new ArrayList<SelectionFile>();
        ArrayList<SelectionFile> completeList = new ArrayList<SelectionFile>();
        
        this._txtPath.setText(this._currentPath.getPath().toString());
        
        try
        {
        	for (File file: files)
            {
        		if (file.isDirectory())
                {
        			// If its a folder, its added to the folder list
        			folderList.add(new SelectionFile(SelectionFile.Type.FOLDER, file.getName(), file.getAbsolutePath(), false));
                }
                else
                {
                	// If its not a folder, its added to the file list only if its a valid file
                	String format = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                	
                	if (this._importMode && format.equals(FileSelectorActivity.VALID_IMPORT_FORMAT))
                	{
                		importFileList.add(new SelectionFile(SelectionFile.Type.FILE, file.getName(), file.getAbsolutePath(), false));
                	}
                	
                	if (this._imageSelectionMode && 
                			(format.equals(FileSelectorActivity.VALID_IMG_FORMAT)
                			|| format.equals(FileSelectorActivity.VALID_IMG_FORMAT_2)
                			|| format.equals(FileSelectorActivity.VALID_IMG_FORMAT_3)
                			|| format.equals(FileSelectorActivity.VALID_IMG_FORMAT_4)))
                	{
                		importFileList.add(new SelectionFile(SelectionFile.Type.FILE, file.getName(), file.getAbsolutePath(), false));
                	}
                }
            }
        }
        catch (Exception ex)
		{
        	XLog.e("[FileSelectorActivity.fill]", ex);
		}
        
        // Lists are sorted alphabetically and folders are added to the combined list. Files are
        // only added if the action to perform is not exportation
        Collections.sort(folderList, new XComparator());
        Collections.sort(importFileList, new XComparator());
        completeList.addAll(folderList);
        if (this._importMode || this._imageSelectionMode)
        {
        	completeList.addAll(importFileList);
        }
        
        // ".." Folder is added to navigate to the parent folder, only if the current folder is
        // not the root
        if (!TextUtils.isEmpty(currentFile.getName()))
        {
        	completeList.add(0, new SelectionFile(SelectionFile.Type.FOLDER, "..", currentFile.getParent(), false));
        }
        
        this._fileQuantity = completeList.size();
        
        this._adapter = new FilesAdapter(FileSelectorActivity.this, R.layout.item_file_selector, completeList);
        this._lstFiles.setAdapter(this._adapter);
        this._lstFiles.setOnItemClickListener(this);
    }
	
	/**
	 * Imports a selected XML file, reads its content as a Json and tries to convert it 
	 * to a collection, storing it in Shared Preferences and overwriting the current users
	 * collection
	 */
	private void executeImport()
    {
		try
		{
			// File is read as a json
			Gson gson = new Gson();
			String collectionGson = BLUtils.readXML(this._selectedFilePath);
			
			if (!TextUtils.isEmpty(collectionGson))
			{
				// Checks if there are 2 collections (Retail and Arcade) divided by a separator
				String[] collections = collectionGson.split(FileSelectorActivity.SEPARATOR);
				
				if (collections != null && collections.length == 2 
						&& !TextUtils.isEmpty(collections[0])
						&& !TextUtils.isEmpty(collections[1]))
				{
					// Deletes the current collection and stores the new one
					StorageController.deleteCollection(false);
					StorageController.deleteCollection(true);
					
					Catalog retailCollection = gson.fromJson(collections[0], Catalog.class);
					StorageController.saveCollection(retailCollection, false);
					
					Catalog arcadeCollection = gson.fromJson(collections[1], Catalog.class);
					StorageController.saveCollection(arcadeCollection, true);
					
					BLUtils.showToast(getString(R.string.file_selector_imported), true);
					Intent output = new Intent();
					setResult(Activity.RESULT_OK, output);
					this.finish();
				}
				else
				{
					BLUtils.showToast(getString(R.string.file_selector_error_importing), true);
				}
			}
			else
			{
				BLUtils.showToast(getString(R.string.file_selector_error_importing), true);
			}
		}
		catch (Exception ex)
		{			
			BLUtils.showToast(getString(R.string.file_selector_error_importing), true);
			XLog.e("[FileSelectorActivity.executeImport]" , ex);
		}
    }
	
	//------------------------------------------------------------------------- EVENTS*/
	
	/** 
	 * Event related to the selection of the Select button. The code depends on the action to
	 * perform, import, export or image selection
	 */
	public void onClick(View view) 
	{
		try
		{
			if (view.getId() == this._btnSelect.getId())
			{
				Gson gson = new Gson();
				
				if (this._importMode)
				{
					// Import. Confirmation from the user is asked
					final Dialog dialog = new Dialog(this, R.style.DialogTheme);
			     		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.dialog_custom_alert);
						
					TextView txtDialog = (TextView) dialog.findViewById(R.id.txtDialog);
						txtDialog.setText(getString(R.string.file_selector_import_confirmation));
						
					Button btnDialogPositive = (Button) dialog.findViewById(R.id.btnDialogPositive);
						btnDialogPositive.setText(getString(R.string.general_yes));
						btnDialogPositive.setOnClickListener(new OnClickListener() {
							public void onClick(View v) 
							{
								dialog.dismiss();
								executeImport();
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
				else
				{
					if (this._imageSelectionMode)
					{
						// Image selection. The selected images path is returned to the caller
						// activity
						Intent output = new Intent();
						output.putExtra(BLParameters.PARAMETERS.SELECTED_PATH, this._selectedFilePath);
						setResult(RESULT_OK, output);
						this.finish();
					}
					else
					{
						// Export. The collection (Retail + Separator + Arcade) is converted to a 
						// Json and its stored on a new file in the selected path with the current
						// date in milliseconds as name.
						String collectionGson = gson.toJson(StorageController.loadCollection(false))
							+ FileSelectorActivity.SEPARATOR
							+ gson.toJson(StorageController.loadCollection(true));
						
						String filePath = this._currentPath.getAbsolutePath() + "/" + FileSelectorActivity.EXPORT_FILE_NAME
								+ String.valueOf(Calendar.getInstance(Locale.getDefault()).getTimeInMillis())
								+ FileSelectorActivity.EXPORT_FILE_EXTENSION;
						
						if (BLUtils.writeXML(collectionGson, filePath))
						{
							BLUtils.showToast(getString(R.string.file_selector_exported), true);
							this.finish();
						}
					}
				}
			}
		}
		catch (Exception ex)
		{			
			XLog.e("[FileSelectorActivity.onClick]" , ex);
		}
	}
	
	/** 
	 * Event related to the selection of a file. If the file is a folder, the path is changed to
	 * the folders path, and the list is redrawn to show the new files inside the folder. If
	 * the file isnt a folder, it is marked as selected (or deselected if it was previously
	 * selected) and all the other files are marked as deselected
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		SelectionFile file = this._adapter.getItem(position);

        if (file.getType().equals(SelectionFile.Type.FOLDER))
        {        	
        	this._adapter.notifyDataSetChanged();
        	
        	if (this._importMode || this._imageSelectionMode)
        	{
        		this._btnSelect.setEnabled(false);
        	}
        	
        	this._currentPath = new File(file.getPath());
            this.fill(this._currentPath);
        }
        else
        {
        	if (file.getSelected())
        	{
        		this._btnSelect.setEnabled(false);
        		
        		for (int i = 0; i < this._fileQuantity; i++)
    			{
        			this._adapter.getItem(i).setSelected(false);
        			view.setBackgroundColor(getResources().getColor(R.color.white));
    			}
        	}
        	else
        	{
        		this._btnSelect.setEnabled(true);
        		
        		for (int i = 0; i < this._fileQuantity; i++)
    			{
        			this._adapter.getItem(i).setSelected(false);
        			view.setBackgroundColor(getResources().getColor(R.color.white));
    			}
        		
        		file.setSelected(true);
        		this._selectedFilePath = file.getPath();
        		view.setBackgroundColor(getResources().getColor(R.color.lime_green));
        	}
        }
        
        this._adapter.notifyDataSetChanged();
	}
	
	//------------------------------------------------------------------------- INNER CLASSES*/
    
	/**
	 *  Adapter used to display the file list
	 */
	private class FilesAdapter extends ArrayAdapter<SelectionFile> implements Serializable
	{	
		private static final long serialVersionUID = -8661762495018203952L;
		
		private Context _context;
	    private int _id;
	    private List<SelectionFile> _items;
	 
	    public FilesAdapter(Context context, int id, List<SelectionFile> items) 
	    {
	        super(context, id, items);
	        this._context = context;
	        this._id = id;
	        this._items = items;
	    }
	    
	    @Override
		public SelectionFile getItem(int i)
	    {
	    	return this._items.get(i);
	    }

	    @Override
		public View getView(int position, View convertView, ViewGroup parent)
	    {
	    	View v = convertView;
	    	
	    	if (v == null)
	    	{
	    		LayoutInflater vi = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		v = vi.inflate(this._id, null);
	    	}	
	    	
	    	final SelectionFile file = this._items.get(position);
	    	
	    	if (file != null) 
	    	{
	    		if (file.getSelected())
	    		{
	    			v.setBackgroundColor(this._context.getResources().getColor(R.color.lime_green));
	    		}
	    		else
	    		{
	    			v.setBackgroundColor(this._context.getResources().getColor(R.color.white));
	    		}
	    		
	    		ImageView imgFileIcon = (ImageView) v.findViewById(R.id.imgFileIcon);
	    		TextView txtFileName = (TextView) v.findViewById(R.id.txtFileName);

	    		if (imgFileIcon != null)
	    		{
	    			if (file.getType().equals(SelectionFile.Type.FOLDER))
	    			{
	    				imgFileIcon.setImageDrawable(this._context.getResources().getDrawable(R.drawable.icon_folder));
	    			}
	    			else
	    			{
	    				if (_imageSelectionMode)
	    				{
	    					imgFileIcon.setImageDrawable(this._context.getResources().getDrawable(R.drawable.icon_photo));
	    				}
	    				else
	    				{
	    					imgFileIcon.setImageDrawable(this._context.getResources().getDrawable(R.drawable.icon_doc));
	    				}
	    			}
	    		}
	        	   
	    		if (txtFileName != null)
	    		{
	    			txtFileName.setText(file.getName());
	    		}
	    	}
	    	
	        return v;
	    }
	}
}
