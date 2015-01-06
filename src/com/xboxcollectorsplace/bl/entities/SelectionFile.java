package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity used in the file explorer. Stores the info of each file
 */
public class SelectionFile implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = -1099006376627944549L;

	/*--------------------------------------------------------------- ENUMS */
	
	public enum Type {FOLDER, FILE} 
	
	/*--------------------------------------------------------------- VARIABLES */
	
	private Type type;
	private String name;
	private String path;
	private boolean selected;
	
	/*--------------------------------------------------------------- CONSTRUCTORS */
	
	public SelectionFile(Type type, String name, String path, boolean selected) 
	{
		super();
		this.type = type;
		this.name = name;
		this.path = path;
		this.selected = selected;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public Type getType() 
	{
		return type;
	}

	public String getName() 
	{
		return name;
	}
	
	public String getPath() 
	{
		return path;
	}

	public boolean getSelected() 
	{
		return selected;
	}
	
	/*--------------------------------------------------------------- SETTERS */

	public void setType(Type type) 
	{
		this.type = type;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public void setPath(String path) 
	{
		this.path = path;
	}

	public void setSelected(boolean selected) 
	{
		this.selected = selected;
	}
}
