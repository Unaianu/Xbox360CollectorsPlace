package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity used to store the info of the links of each game
 */
public class Link implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = -4940699131233991743L;
	
	/*--------------------------------------------------------------- VARIABLES */
	
	private String type;
	private String site;
	private String url;
	
	/*--------------------------------------------------------------- CONSTRUCTORS */
	
	public Link()
	{
		super();
	}
	
	public Link(String type, String site, String url)
	{
		super();
		
		this.type = type;
		this.site = site;
		this.url = url;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public String getType() 
	{
		return type;
	}
	
	public String getSite() 
	{
		return site;
	}
	
	public String getUrl() 
	{
		return url;
	}	
	
	/*--------------------------------------------------------------- SETTERS */
	
	public void setType(String type) 
	{
		this.type = type;
	}
	
	public void setSite(String site) 
	{
		this.site = site;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
}
