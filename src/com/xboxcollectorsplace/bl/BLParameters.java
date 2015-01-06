package com.xboxcollectorsplace.bl;

/**
 * Class with static and final parameters used anywhere in the code
 */
public class BLParameters 
{
	public static class GENERAL_PARAMETERS
	{
		public static final String CODIFICATION_UTF_8 = "UTF-8";
		public static final String CODIFICATION_ISO_8859_1 = "iso-8859-1";
		
		public static final String CATALOG_RETAIL = "catalog.xml";
		public static final String CATALOG_ARCADE = "arcade_catalog.xml";
		
		public static final String ICON_PATH = ":drawable/icon_";
		public static final String ARCADE_ICON_PATH = ":drawable/icon_arcade_";
		public static final String COVER_PATH = ":drawable/cover_";
		public static final String ARCADE_COVER_PATH = ":drawable/cover_arcade_";
		//TODO: Only for Google Play. Used to indicate the path of the covers expansion file
		//public static final String COVER_PATH = "main.1.com.xboxcollectorsplace/cover_";
		//public static final String ARCADE_COVER_PATH = "main.1.com.xboxcollectorsplace/cover_arcade_";
		public static final String COVER_DLC_PATH = ":drawable/cover_dlc_";
		public static final String ARCADE_COVER_DLC_PATH = ":drawable/cover_arcade_dlc_";
		public static final String COVER_HORIZONTAL_DLC_PATH = ":drawable/cover_dlc_h_";
		public static final String ARCADE_COVER_HORIZONTAL_DLC_PATH = ":drawable/cover_arcade_dlc_h_";
		public static final String DETAILS_PATH = "details_";
		public static final String ARCADE_DETAILS_PATH = "arcade_details_";
		public static final String XML_FILE = ".xml";
		
		public static final int TRUNCATE_POSITION_GAME_SYNOPSIS = 525;
		public static final int TRUNCATE_POSITION_DLC_SYNOPSIS = 140;
	}
	
	public static class SHARED_PREFERENCES
	{
		public static final String NAME = "SharedPreferences_Xbox360";
		
		public static final String KEY_OPTIONS_LANGUAGE = "KEY_OPTIONS_LANGUAGE";	
		public static final String KEY_OPTIONS_BOOT = "KEY_OPTIONS_BOOT";
		public static final String KEY_OPTIONS_SHOW_GENRE = "KEY_OPTIONS_SHOW_GENRE";
		public static final String KEY_OPTIONS_SOUND_ACTIVE = "KEY_OPTIONS_SOUND_ACTIVE";
		public static final String KEY_OPTIONS_LIST_DISPLAY = "KEY_OPTIONS_LIST_DISPLAY";
		public static final String KEY_OPTIONS_LIST_SORT = "KEY_OPTIONS_LIST_SORT";
		public static final String KEY_OPTIONS_LIST_SHOW = "KEY_OPTIONS_LIST_SHOW";
		public static final String KEY_OPTIONS_PARTIAL_LANGUAGE = "KEY_OPTIONS_PARTIAL_LANGUAGE";	
		public static final String KEY_OPTIONS_PARTIAL_TRANSLATE_SYNOPSIS = "KEY_OPTIONS_PARTIAL_TRANSLATE_SYNOPSIS";	
		public static final String KEY_OPTIONS_PARTIAL_BOOT = "KEY_OPTIONS_PARTIAL_BOOT";
		public static final String KEY_COLLECTION = "KEY_COLLECTION";
		public static final String KEY_COLLECTION_ARCADE = "KEY_COLLECTION_ARCADE";
		public static final String KEY_PHOTO_GALLERY = "KEY_PHOTO_GALLERY";
	}
	
	public static class LANGUAGES
	{
		public static final String ENGLISH_CODE = "en";
		public static final String ENGLISH_DESCRIPTION = "English";
		
		public static final String GERMAN_CODE = "de";
		public static final String GERMAN_DESCRIPTION = "Deutsch";
		
		public static final String FRENCH_CODE = "fr";	
		public static final String FRENCH_DESCRIPTION = "Français";
		
		public static final String ITALIAN_CODE = "it";
		public static final String ITALIAN_DESCRIPTION = "Italiano";
		
		public static final String POLISH_CODE = "pl";
		public static final String POLISH_DESCRIPTION = "Polski";
		
		public static final String BASQUE_CODE = "eu";
		public static final String BASQUE_DESCRIPTION = "Euskara";
		
		public static final String SPANISH_CODE = "es";
		public static final String SPANISH_DESCRIPTION = "Español";

		public static final String JAPANESE_CODE = "ja";
		public static final String JAPANESE_DESCRIPTION = "Japanese";

		public static final String DUTCH_CODE = "nl";
		public static final String DUTCH_DESCRIPTION = "Nederlands";
		
		public static final String NORWEGIAN_CODE = "no";
		public static final String NORWEGIAN_DESCRIPTION = "Norsk";
		
		public static final String FINNISH_CODE = "fi";
		public static final String FINNISH_DESCRIPTION = "Suomi";
		
		public static final String RUSSIAN_CODE = "ru";
		public static final String RUSSIAN_DESCRIPTION = "Russian";
		
		public static final String PORTUGUESE_CODE = "pt";
		public static final String PORTUGUESE_DESCRIPTION = "Português";
		
		public static final String CZECH_CODE = "cs";
		public static final String CZECH_DESCRIPTION = "Ceský";
		
		public static final String SWEDISH_CODE = "sv";
		public static final String SWEDISH_DESCRIPTION = "Svenska";
		
		public static final String KOREAN_CODE = "ko";
		public static final String KOREAN_DESCRIPTION = "Korean";
	}
	
	public static class PARAMETERS
	{
		public static final String GAME = "GAME";
		public static final String IS_ARCADE = "IS_ARCADE";
		public static final String CURRENT_ACHIEVEMENTS = "CURRENT_ACHIEVEMENTS";
		public static final String TOTAL_ACHIEVEMENTS = "TOTAL_ACHIEVEMENTS";
		public static final String SYNOPSIS = "SYNOPSIS";
		public static final String FILTERS = "FILTERS";
		public static final String RELOAD_COLLECTION = "RELOAD_COLLECTION";
		public static final String SHOW_STATUS = "SHOW_STATUS";
		public static final String COMES_FROM_COLLECTION = "COMES_FROM_COLLECTION";
		public static final String TITLE = "TITLE";
		public static final String YEAR = "YEAR";
		public static final String IMPORT_MODE = "IMPORT_MODE";
		public static final String IMAGE_SELECTION_MODE = "IMAGE_SELECTION_MODE";
		public static final String GAME_TITLE = "GAME_TITLE";
		public static final String CURRENT_POSITION = "CURRENT_POSITION";
		public static final String SELECTED_PATH = "SELECTED_PATH";
	}
	
	public static class ACTIVITY_CODES
	{
		public static final int CONFIG = 0;
		public static final int FILTERS = 1;
		public static final int SUMMARY = 2;
		public static final int CATALOG = 3;
		public static final int GAME_DETAIL = 4;
		public static final int EDIT_ACHIEVEMENTS = 5;
		public static final int COLLECTION = 6;
		public static final int GOTO = 7;
		public static final int IMPORT = 8;
		public static final int CAMERA = 9;
		public static final int IMAGE_SELECTION = 10;
		public static final int EDIT_TITLE = 11;
		public static final int PHOTO_GALLERY = 12;
	}
	
	public static class LINKS
	{
		public static final String YOUTUBE = "YouTube";
		public static final String GAMEFAQS = "GameFAQs";
		public static final String TRUEACHIEVEMENTS = "TrueAchievements";
		public static final String XBOXACHIEVEMENTS = "XboxAchievements";
		public static final String METACRITIC = "Metacritic";
	}
	
	public static class PHOTO_GALLERY
	{
		public static final String XBOX_FOLDER = "Xbox360CollectorsPlace";
		public static final String GENERAL_FOLDER = "General";
		public static final String IMAGE_FORMAT = ".png";
	}
	
	public static class KINECT
	{
		public static final int NOT_USED = 0;
		public static final int OPTIONAL_FEATURES = 1;
		public static final int REQUIRED = 2;
	}
}
