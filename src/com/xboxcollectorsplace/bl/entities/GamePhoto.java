package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity that contains the info of a photo
 */
public class GamePhoto implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = -5352570599654159227L;
		
	/*--------------------------------------------------------------- VARIABLES */
	
	private String title;
	private String path;
	private boolean imported;

	/*--------------------------------------------------------------- CONSTRUCTOR */
	
	public GamePhoto() 
	{
		super();
	}
	
	public GamePhoto(String title, String path, boolean imported)
	{
		super();
		this.title = title;
		this.path = path;
		this.imported = imported;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public String getTitle() 
	{
		return title;
	}
	
	public String getPath() 
	{
		return path;
	}
	
	public boolean getImported() 
	{
		return imported;
	}

	/*--------------------------------------------------------------- SETTERS */
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void setImported(boolean imported)
	{
		this.imported = imported;
	}
}
