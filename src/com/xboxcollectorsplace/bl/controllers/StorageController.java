package com.xboxcollectorsplace.bl.controllers;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.bl.entities.Catalog;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.GamePhotoGallery;
import com.xboxcollectorsplace.bl.entities.Options;
import com.xboxcollectorsplace.bl.entities.Sort;
import com.xboxcollectorsplace.utils.XLog;

/**
 * Controller used to store and recover parameters from Shared Preferences
 */
public class StorageController 
{
	//------------------------------------------------------------------------- PUBLIC METHODS*/

	/**
     * Stores the selected options (Language, boot type and whether the genre or the year is shown
     * in Smartphones)
     *
     * @param options User selected options
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean saveOptions(Options options)
	{
		boolean result = false;
		SharedPreferences preferences;
		Editor editor;
				
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			editor.putString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_LANGUAGE, options.getLanguage().toString());
			editor.putString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_BOOT, options.getBoot().toString());
			editor.putString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_SHOW_GENRE, options.getShowGenre().toString());
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.saveOptions]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers the selected options (Language, boot type and whether the genre or the year is 
     * shown in Smartphones)
     *
     * @return User selected options
     */
	public synchronized static Options loadOptions()
	{
		Options options = new Options();
		SharedPreferences preferences;
		Options.Language language;
		Options.BootType boot;		
		Options.ShowGenre showGenre;		
		
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			
			String languageString = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_LANGUAGE, null);
			if (!TextUtils.isEmpty(languageString))
			{
				language = Options.Language.valueOf(languageString);
			}
			else
			{
				// If the user has not selected any language, the language code of the device
				// is checked to see if its supported by the App. Elseway the default language
				// is selected, English
				String currentLanguage = Locale.getDefault().getLanguage();
				
				if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.GERMAN_CODE))
				{
					language = Options.Language.GERMAN;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.SPANISH_CODE))
				{
					language = Options.Language.SPANISH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.BASQUE_CODE))
				{
					language = Options.Language.BASQUE;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.FRENCH_CODE))
				{
					language = Options.Language.FRENCH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.ITALIAN_CODE))
				{
					language = Options.Language.ITALIAN;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.POLISH_CODE))
				{
					language = Options.Language.POLISH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.JAPANESE_CODE))
				{
					language = Options.Language.JAPANESE;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.KOREAN_CODE))
				{
					language = Options.Language.KOREAN;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.CZECH_CODE))
				{
					language = Options.Language.CZECH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.RUSSIAN_CODE))
				{
					language = Options.Language.RUSSIAN;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.FINNISH_CODE))
				{
					language = Options.Language.FINNISH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.SWEDISH_CODE))
				{
					language = Options.Language.SWEDISH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.NORWEGIAN_CODE))
				{
					language = Options.Language.NORWEGIAN;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.DUTCH_CODE))
				{
					language = Options.Language.DUTCH;
				}
				else if (currentLanguage.equalsIgnoreCase(BLParameters.LANGUAGES.PORTUGUESE_CODE))
				{
					language = Options.Language.PORTUGUESE;
				}
				else
				{
					language = Options.Language.ENGLISH;
				}
			}
			
			String bootString = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_BOOT, null);
			if (!TextUtils.isEmpty(bootString))
			{
				boot = Options.BootType.valueOf(bootString);
			}
			else
			{
				boot = Options.BootType.OLD;
			}
			
			String showGenreString = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_SHOW_GENRE, null);
			if (!TextUtils.isEmpty(showGenreString))
			{
				showGenre = Options.ShowGenre.valueOf(showGenreString);
			}
			else
			{
				showGenre = Options.ShowGenre.YEAR;
			}
			
			options.setLanguage(language);
			options.setBoot(boot);
			options.setShowGenre(showGenre);
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadOptions]", ex);
		}	
			
		return options;
	}
	
	/**
     * Stores the user selected sorting of the collection/catalog
     *
     * @param sort User selected sorting
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean saveSorting(Sort sort)
	{
		boolean result = false;
		SharedPreferences preferences;
		Editor editor;
				
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			editor.putString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_LIST_SORT, sort.getSort().toString());
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.saveSorting]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers the user selected sorting of the collection/catalog
     *
     * @return User selected sorting
     */
	public synchronized static Sort loadSorting()
	{
		Sort sort = new Sort();
		SharedPreferences preferences;
		Sort.ListSort listSort;
		
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			
			String listOrderString = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_LIST_SORT, null);
			if (!TextUtils.isEmpty(listOrderString))
			{
				listSort = Sort.ListSort.valueOf(listOrderString);
			}
			else
			{
				listSort = Sort.ListSort.ASC_TITLE;
			}
			
			sort.setSort(listSort);
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadSorting]", ex);
		}	
			
		return sort;
	}
	
	/**
     * Stores the boolean indicating if the title screen should have sound
     *
     * @param soundActive If true, the title screen should be muted
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean saveSoundActive(boolean soundActive)
	{
		boolean result = false;
		SharedPreferences preferences;
		Editor editor;
				
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			editor.putBoolean(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_SOUND_ACTIVE, soundActive);
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.saveSoundActive]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers the boolean indicating if the title screen should have sound
     *
     * @return If true, the title screen should be muted
     */
	public synchronized static boolean loadSoundActive()
	{
		boolean soundActive = false;
		SharedPreferences preferences;
		
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			
			soundActive = preferences.getBoolean(BLParameters.SHARED_PREFERENCES.KEY_OPTIONS_SOUND_ACTIVE, false);
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadSoundActive]", ex);
		}	
			
		return soundActive;
	}
	
	/**
     * Stores the users game collection
     *
     * @param collection Game collection of the user
     * @param arcadeCollection Boolean indicating if the collection to store is the arcade or 
     * retail collection (true if Arcade)
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean saveCollection(Catalog collection, boolean arcadeCollection)
	{
		boolean result = false;
		SharedPreferences preferences;
		Editor editor;
		Gson gson = new Gson();
				
		try
		{	
			String catalogJSON = gson.toJson(collection);
			
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			
			if (arcadeCollection)
			{
				editor.putString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION_ARCADE, catalogJSON);
			}
			else
			{
				editor.putString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION, catalogJSON);
			}
			
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.saveCollection]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers the users game collection
     * 
     * @param arcadeCollection Boolean indicating if the collection to recover is the arcade or 
     * retail collection (true if Arcade)
     * @return Game collection of the user
     */
	public synchronized static Catalog loadCollection(boolean arcadeCollection)
	{
		Catalog collection = new Catalog();
		Gson gson = new Gson();
		SharedPreferences preferences;
		String collectionJSON;
		
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			
			if (arcadeCollection)
			{
				collectionJSON = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION_ARCADE, null);
			}
			else
			{
				collectionJSON = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION, null);
			}
			
			if (!TextUtils.isEmpty(collectionJSON))
			{
				collection = gson.fromJson(collectionJSON, Catalog.class);
			}
			else
			{
				collection = new Catalog();
				collection.setCatalog(new ArrayList<Game>());
			}
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadCollection]", ex);
		}	
			
		return collection;
	}
	
	/**
     * Deletes users game collection
     *
     * @param arcadeCollection Boolean indicating if the collection to delte is the arcade or 
     * retail collection (true if Arcade)
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean deleteCollection(boolean arcadeCollection)
	{
		boolean result = false;
		SharedPreferences preferences;
		Editor editor;
				
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			
			if (arcadeCollection)
			{
				editor.putString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION_ARCADE, null);
			}
			else
			{
				editor.putString(BLParameters.SHARED_PREFERENCES.KEY_COLLECTION, null);
			}
			
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.deleteCollection]", ex);
		}	
			
		return result;
	}
	
	/**
     * Adds a game to the users collection
     *
     * @param game Game to add
     * @param arcadeCollection Boolean indicating if the game is going to be added to the arcade
     * or retail collection (true if Arcade)
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean addGame(Game game, boolean arcadeCollection)
	{
		boolean result = false;
		Catalog catalog;
		ArrayList<Game> games;
		
		try
		{	
			catalog = loadCollection(arcadeCollection);
			games = catalog.getCatalog();
			games.add(game);
			catalog.setCatalog(games);
			result = saveCollection(catalog, arcadeCollection);
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.addGame]", ex);
		}	
			
		return result;
	}
	
	/**
     * Removes a game from the users collection
     *
     * @param gameID ID of the game to remove
     * @param arcadeCollection Boolean indicating if the game belongs to the arcade or
     * retail collection (true if Arcade)
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean removeGame(int gameID, boolean arcadeCollection)
	{
		boolean result = false;
		Catalog catalog;
		ArrayList<Game> games;
		
		try
		{	
			catalog = loadCollection(arcadeCollection);
			games = catalog.getCatalog();

			for (Game game : games)
			{
				if (game.getId() == gameID)
				{
					result = games.remove(game);
					break;
				}
			}
			
			catalog.setCatalog(games);
			result = saveCollection(catalog, arcadeCollection);
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.removeGame]", ex);
		}	
			
		return result;
	}
	
	/**
     * Edits a game of the users collection
     *
     * @param game Game to edit
     * @param arcadeCollection Boolean indicating if the game belongs to the arcade or
     * retail collection (true if Arcade)
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean editGame(Game editGame, boolean arcadeCollection)
	{
		boolean result = false;
		Catalog catalog;
		ArrayList<Game> games;
		
		try
		{	
			catalog = loadCollection(arcadeCollection);
			games = catalog.getCatalog();

			for (int i = 0; i < games.size(); i++)
			{
				Game game = games.get(i);
				
				if (game.getId() == editGame.getId())
				{
					games.set(i, editGame);
					result = true;
					break;
				}
			}
			
			catalog.setCatalog(games);
			result = saveCollection(catalog, arcadeCollection);
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.editGame]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers a game from the users collection
     *
     * @param gameID ID of the game to recover
     * @param arcadeCollection Boolean indicating if the game belongs to the arcade or
     * retail collection (true if Arcade)
     * @return Game recovered
     */
	public synchronized static Game loadGame(int gameID, boolean arcadeCollection)
	{
		Catalog catalog;
		ArrayList<Game> games;
		Game returnGame = null;
		
		try
		{	
			catalog = loadCollection(arcadeCollection);
			games = catalog.getCatalog();

			for (int i = 0; i < games.size(); i++)
			{
				Game game = games.get(i);
				
				if (game.getId() == gameID)
				{
					returnGame = game;
					break;
				}
			}		
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadGame]", ex);
		}	
			
		return returnGame;
	}
	
	/**
     * Checks if a game exists in the users collection
     *
     * @param gameID ID of the game to check
     * @param arcadeCollection Boolean indicating if the game belongs to the arcade or
     * retail collection (true if Arcade)
     * @return Boolean indicating if the game exists
     */
	public synchronized static boolean existsGame(int gameID, boolean arcadeCollection)
	{
		boolean exists = false;
		Catalog catalog;
		ArrayList<Game> games;
		
		try
		{	
			catalog = loadCollection(arcadeCollection);
			games = catalog.getCatalog();

			for (Game game : games)
			{
				if (game.getId() == gameID)
				{
					exists = true;
					break;
				}
			}
		}
		catch (Exception ex)
		{
			exists = false;
			XLog.e("[StorageController.existsGame]", ex);
		}	
			
		return exists;
	}
	
	/**
     * Stores the info of the photo gallery
     *
     * @param photoGallery Array with the info of the photo gallery
     * @return Boolean indicating the success of the operation
     */
	public synchronized static boolean savePhotoGallery(ArrayList<GamePhotoGallery> photoGallery)
	{
		boolean result = false;
		Gson gson = new Gson();
		SharedPreferences preferences;
		Editor editor;
				
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			editor = preferences.edit();
			editor.putString(BLParameters.SHARED_PREFERENCES.KEY_PHOTO_GALLERY, gson.toJson(photoGallery));
			result = editor.commit();
		}
		catch (Exception ex)
		{
			result = false;
			XLog.e("[StorageController.savePhotoGallery]", ex);
		}	
			
		return result;
	}
	
	/**
     * Recovers the info of the photo gallery
     * 
     * @return Array with the info of the photo gallery
     */
	public synchronized static ArrayList<GamePhotoGallery> loadPhotoGallery()
	{
		ArrayList<GamePhotoGallery> photoGallery = new ArrayList<GamePhotoGallery>();
		Gson gson = new Gson();
		SharedPreferences preferences;
		String photoGalleryJSON;
		
		try
		{	
			preferences = App.getContext().getSharedPreferences(BLParameters.SHARED_PREFERENCES.NAME, Context.MODE_PRIVATE);
			
			photoGalleryJSON = preferences.getString(BLParameters.SHARED_PREFERENCES.KEY_PHOTO_GALLERY, null);
			
			if (!TextUtils.isEmpty(photoGalleryJSON))
			{
				photoGallery = gson.fromJson(photoGalleryJSON, new TypeToken<ArrayList<GamePhotoGallery>>(){}.getType());
			}
			else
			{
				photoGallery = new ArrayList<GamePhotoGallery>();
			}
		}
		catch (Exception ex)
		{
			XLog.e("[StorageController.loadPhotoGallery]", ex);
		}	
			
		return photoGallery;
	}
}
