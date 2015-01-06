package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;

/**
 * Entity used for the sorting of the catalog/collection
 */
public class Sort implements Serializable
{
	//------------------------------------------------------------------------- CONSTANTS*/
	
	private static final long serialVersionUID = 536346373230871639L;

	//------------------------------------------------------------------------- ENUMS*/
	
	public enum ListSort {ASC_YEAR, DESC_YEAR, ASC_TITLE, DESC_TITLE, ASC_ACHIEVEMENTS, DESC_ACHIEVEMENTS};
	
	//------------------------------------------------------------------------- VARIABLES*/
	
	private ListSort sort;
	
	//------------------------------------------------------------------------- CONSTRUCTORS*/
	
	public Sort()
	{
		super();
	}
	
	public Sort(ListSort sort) 
	{
		super();
		this.sort = sort;
	}
	
	//------------------------------------------------------------------------- GETTERS*/
	
	public ListSort getSort()
	{
		return sort;
	}
	
	//------------------------------------------------------------------------- SETTERS*/
	
	public void setSort(ListSort sort)
	{
		this.sort = sort;
	}	
}
