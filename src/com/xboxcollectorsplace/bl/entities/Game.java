package com.xboxcollectorsplace.bl.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Entity used to store the details of each game
 */
public class Game implements Serializable
{
	/*--------------------------------------------------------------- CONSTANTS */
	
	private static final long serialVersionUID = 1039045339525438269L;
	
	/*--------------------------------------------------------------- ENUMS */
	
	public enum Status {COMPLETED, FINISHED, OWN, NOT_OWN} 
	public enum Genre {NONE, ALL, ACTION_ADVENTURE, FPS, TPS, ACTION, RPG, JRPG, TRPG, SPORTS, FIGHTING, SURVIVAL, RACING, ADVENTURE, OPEN_WORLD, RTS, TBS,
		STEALTH, PLATFORMER, HS, ARPG, BTU, STU, PUZZLE, TD, MUSICAL, CFS, SIM, HUNTING_FISHING, PARTY, CARDS_BOARD, POKER, MUSOU, VISUAL_NOVEL, PINBALL, FITNESS, MMORPG} 
		
	/*--------------------------------------------------------------- VARIABLES */
	
	private int id;
	private String title;
	private String altTitle;
	private String release;
	private int totalAchievements;
	private int currentAchievements;
	private int online;
	private int coop;
	private int splitScreen;
	private int systemLink;
	private int kinect;
	private Status status;
	private ArrayList<Genre> genre;
	private ArrayList<DLC> downloadedDLCs;
	private String notes;
	
	private Details details;
	
	/*--------------------------------------------------------------- CONSTRUCTORS */
	
	public Game() 
	{
		super();
		this.currentAchievements = 0;
		this.status = Status.NOT_OWN;
		this.online = 0;
		this.coop = 0;
		this.splitScreen = 0;
		this.systemLink = 0;
		this.genre = new ArrayList<Game.Genre>();
		this.downloadedDLCs = new ArrayList<DLC>();
		this.kinect = 0;
	}
	
	/*--------------------------------------------------------------- GETTERS */
	
	public int getId() 
	{
		return id;
	}

	public String getTitle() 
	{
		return title;
	}
	
	public String getAltTitle() 
	{
		return altTitle;
	}
	
	public String getRelease() 
	{
		return release;
	}

	public int getTotalAchievements() 
	{
		return totalAchievements;
	}

	public int getCurrentAchievements() 
	{
		return currentAchievements;
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
	
	public int getKinect() 
	{
		return kinect;
	}
	
	public Status getStatus() 
	{
		return status;
	}

	public ArrayList<Genre> getGenre() 
	{
		return genre;
	}
	
	public String getNotes() 
	{
		return notes;
	}
	
	public Details getDetails() 
	{
		return details;
	}
	
	public ArrayList<DLC> getDownloadedDLCs() 
	{
		return downloadedDLCs;
	}

	/*--------------------------------------------------------------- SETTERS */
	
	public void setId(int id)
	{
		this.id = id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setAltTitle(String altTitle)
	{
		this.altTitle = altTitle;
	}

	public void setRelease(String release)
	{
		this.release = release;
	}

	public void setTotalAchievements(int totalAchievements) 
	{
		this.totalAchievements = totalAchievements;
	}

	public void setCurrentAchievements(int currentAchievements)
	{
		this.currentAchievements = currentAchievements;
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
	
	public void setKinect(int kinect)
	{
		this.kinect = kinect;
	}

	public void setStatus(Status status) 
	{
		this.status = status;
	}

	public void setGenre(ArrayList<Genre> genre) 
	{
		this.genre = genre;
	}
	
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	
	public void setDetails(Details details)
	{
		this.details = details;
	}
	
	public void setDownloadedDLCs(ArrayList<DLC> downloadedDLCs) 
	{
		this.downloadedDLCs = downloadedDLCs;
	}
	
	/*--------------------------------------------------------------- INNER CLASSES */
	
	public class Details implements Serializable
	{
		private static final long serialVersionUID = 8848729394182141830L;
		
		private ArrayList<String> developer;
		private String country;
		private ArrayList<DLC> dlcs;
		private String synopsis;
		private ArrayList<Link> links;
		
		public Details()
		{
			super();
		}
		
		public ArrayList<String> getDeveloper()
		{
			return developer;
		}

		public String getCountry() 
		{
			return country;
		}

		public ArrayList<DLC> getDLCs() 
		{
			return dlcs;
		}

		public String getSynopsis()
		{
			return synopsis;
		}

		public ArrayList<Link> getLinks() 
		{
			return links;
		}
		
		public void setDeveloper(ArrayList<String> developer) 
		{
			this.developer = developer;
		}

		public void setEditions(String country) 
		{
			this.country = country;
		}		

		public void setDLCs(ArrayList<DLC> dlcs) 
		{
			this.dlcs = dlcs;
		}

		public void setSynopsis(String synopsis) 
		{
			this.synopsis = synopsis;
		}

		public void setLinks(ArrayList<Link> links) 
		{
			this.links = links;
		}
	}
}
