package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity used to store the options selected by the user. These options are the language, the
 * boot type (video played in the title screen) and the attribute that indicates if the genre
 * or the year should be shown on the catalog list (only Smartphones)
 */
public class Options implements Serializable
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final long serialVersionUID = -4566367402169162820L;

	//------------------------------------------------------------------------- ENUMS*/
	
	public enum Language {ENGLISH, GERMAN, FRENCH, ITALIAN, BASQUE, POLISH, SPANISH, JAPANESE, DUTCH,
		NORWEGIAN, FINNISH, RUSSIAN, PORTUGUESE, CZECH, SWEDISH, KOREAN};
	public enum BootType {OLD, NEW};
	public enum ShowGenre {YEAR, GENRE};
	
	//------------------------------------------------------------------------- VARIABLES*/
	
	private Language language;
	private BootType boot;
	private ShowGenre showGenre;
		
	//------------------------------------------------------------------------- CONSTRUCTORS*/
	
	public Options()
	{
		super();
	}
	
	public Options(Language language, BootType boot, ShowGenre showGenre) 
	{
		super();
		this.language = language;
		this.boot = boot;
		this.showGenre = showGenre;
	}
	
	//------------------------------------------------------------------------- GETTERS*/
	
	public Language getLanguage()
	{
		return language;
	}
	
	public BootType getBoot() 
	{
		return boot;
	}
	
	public ShowGenre getShowGenre()
	{
		return showGenre;
	}
	
	//------------------------------------------------------------------------- SETTERS*/
	
	public void setLanguage(Language language)
	{
		this.language = language;
	}
	
	public void setBoot(BootType boot) 
	{
		this.boot = boot;
	}
	
	public void setShowGenre(ShowGenre showGenre)
	{
		this.showGenre = showGenre;
	}
}
