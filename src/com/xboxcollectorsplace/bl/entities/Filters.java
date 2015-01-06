package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

import com.xboxcollectorsplace.bl.entities.Game.Genre;

/**
 * Entity used to store the filters used on the catalog/collection
 */
public class Filters implements Serializable
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final long serialVersionUID = 3053807117118926224L;

	//------------------------------------------------------------------------- ENUMS*/
	
	public enum ListStatus {ALL, GAMES_OWN, GAMES_LEFT, GAMES_FINISHED, GAMES_COMPLETED, GAMES_NOT_FINISHED};
	public enum ListKinect {ALL, NOT_REQUIRED, USED, REQUIRED};
	
	//------------------------------------------------------------------------- VARIABLES*/
	
	private String title;
	private ListStatus status;
	private ListKinect kinect;
	private Genre genre1;
	private Genre genre2;
	private int year;
	private int online;
	private int coop;
	private int splitScreen;
	private int systemLink;
	
	//------------------------------------------------------------------------- CONSTRUCTORS*/
	
	public Filters()
	{
		super();
	}
	
	public Filters(ListStatus status) 
	{
		super();
		this.status = status;
	}
	
	//------------------------------------------------------------------------- GETTERS*/
		
	public ListStatus getStatus()
	{
		return status;
	}
	
	public ListKinect getKinect()
	{
		return kinect;
	}
	
	public String getTitle()
	{
		return title;
	}

	public Genre getGenre1()
	{
		return genre1;
	}

	public Genre getGenre2()
	{
		return genre2;
	}

	public int getYear()
	{
		return year;
	}

	public int getOnline() 
	{
		return online;
	}

	public int getCoop()
	{
		return coop;
	}

	public int getSplitScreen()
	{
		return splitScreen;
	}

	public int getSystemLink()
	{
		return systemLink;
	}
	
	//------------------------------------------------------------------------- SETTERS*/
		
	public void setStatus(ListStatus status)
	{
		this.status = status;
	}
	
	public void setKinect(ListKinect kinect)
	{
		this.kinect = kinect;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}

	public void setGenre1(Genre genre1)
	{
		this.genre1 = genre1;
	}

	public void setGenre2(Genre genre2) 
	{
		this.genre2 = genre2;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public void setOnline(int online) 
	{
		this.online = online;
	}

	public void setCoop(int coop) 
	{
		this.coop = coop;
	}

	public void setSplitScreen(int splitScreen) 
	{
		this.splitScreen = splitScreen;
	}

	public void setSystemLink(int systemLink) 
	{
		this.systemLink = systemLink;
	}
}
