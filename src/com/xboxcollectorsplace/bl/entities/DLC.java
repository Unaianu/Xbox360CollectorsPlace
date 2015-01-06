package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity that contains the info of a DLC (DownLoadable Content) of the games
 */
public class DLC implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */

	private static final long serialVersionUID = 3953279162991222214L;

	/*--------------------------------------------------------------- VARIABLES */
	
	private int id;
	private String name;
	private int type;
	private String releaseDate;
	private int achievements;
	private String synopsis;
	private boolean downloaded;
	
	/*--------------------------------------------------------------- CONSTRUCTORS */
	
	public DLC()
	{
		super();
		
		this.achievements = 0;
	}
	
	public DLC(int id, String name, int type, String releaseDate, int achievements, String synopsis, boolean downloaded)
	{
		super();
		
		this.id = id;
		this.type = type;
		this.name = name;
		this.releaseDate = releaseDate;
		this.achievements = achievements;
		this.synopsis = synopsis;
		this.downloaded = downloaded;
	}
	
	public DLC(int id, boolean downloaded)
	{
		super();
		
		this.id = id;
		this.downloaded = downloaded;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
	
	public int getType() 
	{
		return type;
	}

	public String getReleaseDate() 
	{
		return releaseDate;
	}

	public int getAchievements() 
	{
		return achievements;
	}
	
	public String getSynopsis() 
	{
		return synopsis;
	}

	public boolean getDownloaded() 
	{
		return downloaded;
	}
	
	/*--------------------------------------------------------------- SETTERS */
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}

	public void setType(int type) 
	{
		this.type = type;
	}
	
	public void setReleaseDate(String releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	public void setAchievements(int achievements) 
	{
		this.achievements = achievements;
	}
	
	public void setSynopsis(String synopsis) 
	{
		this.synopsis = synopsis;
	}

	public void setDownloaded(boolean downloaded) 
	{
		this.downloaded = downloaded;
	}
}
