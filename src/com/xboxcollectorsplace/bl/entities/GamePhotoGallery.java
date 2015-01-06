package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Entity that contains the info of the photo gallery of each game
 */
public class GamePhotoGallery implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = 4421073528629914810L;
		
	/*--------------------------------------------------------------- VARIABLES */
	
	private String gameId;
	private String gameTitle;
	private ArrayList<GamePhoto> photos;

	/*--------------------------------------------------------------- CONSTRUCTOR */
	
	public GamePhotoGallery() 
	{
		super();
		this.photos = new ArrayList<GamePhoto>();
	}
	
	public GamePhotoGallery(String gameId, String gameTitle, ArrayList<GamePhoto> photos)
	{
		super();
		this.gameId = gameId;
		this.gameTitle = gameTitle;
		this.photos = photos;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public String getGameId() 
	{
		return gameId;
	}
	
	public String getGameTitle() 
	{
		return gameTitle;
	}
	
	public ArrayList<GamePhoto> getPhotos() 
	{
		return photos;
	}

	/*--------------------------------------------------------------- SETTERS */
	
	public void setGameId(String gameId)
	{
		this.gameId = gameId;
	}
	
	public void setTitle(String gameTitle)
	{
		this.gameTitle = gameTitle;
	}
	
	public void setPhotos(ArrayList<GamePhoto> photos)
	{
		this.photos = photos;
	}
}