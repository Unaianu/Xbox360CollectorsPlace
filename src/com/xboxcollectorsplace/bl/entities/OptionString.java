package com.xboxcollectorsplace.bl.entities;

/**
 * Entity used by the spinners to show a description (optionName) and store a code (optionKey) 
 */
public class OptionString
{
	//------------------------------------------------------------------------- VARIABLES*/
	
	private String optionName;
	private String optionKey;

	//------------------------------------------------------------------------- CONSTRUCTORS*/
	
	public OptionString(String optionName, String optionKey)
	{
		this.optionName = optionName;
		this.optionKey = optionKey;
	}
	
	//------------------------------------------------------------------------- GETTERS*/
	
	public String getOptionName()
	{
		return this.optionName;
	}
	
	//------------------------------------------------------------------------- SETTERS*/
	
	public String getOptionKey()
	{
		return this.optionKey;
	}
}