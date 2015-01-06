package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Entity used to Store the collection/catalog
 */
public class Catalog implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = 6605598678482270963L;
		
	/*--------------------------------------------------------------- VARIABLES */
	
	private ArrayList<Game> catalog;

	/*--------------------------------------------------------------- CONSTRUCTOR */
	
	public Catalog() 
	{
		super();
	}
	
	public Catalog(ArrayList<Game> catalog)
	{
		super();
		this.catalog = catalog;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public ArrayList<Game> getCatalog() 
	{
		return catalog;
	}

	/*--------------------------------------------------------------- SETTERS */
	
	public void setCatalog(ArrayList<Game> catalog)
	{
		this.catalog = catalog;
	}
}
