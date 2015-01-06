package com.xboxcollectorsplace.utils;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import android.text.TextUtils;

import com.xboxcollectorsplace.bl.controllers.StorageController;
import com.xboxcollectorsplace.bl.entities.Game;
import com.xboxcollectorsplace.bl.entities.OptionString;
import com.xboxcollectorsplace.bl.entities.SelectionFile;
import com.xboxcollectorsplace.bl.entities.Sort;

/**
 * Class used to sort different type of entities
 */
public class XComparator implements Comparator<Object>
{
	//------------------------------------------------------------------------- CONSTANTS*/

	SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy/MM/dd");  

	//------------------------------------------------------------------------- PUBLIC METHODS*/

	public int compare(Game game1, Game game2) 
	{
		Sort.ListSort sort = StorageController.loadSorting().getSort();
		int result = 0;
		
		try
		{
			switch (sort)
			{
				case ASC_TITLE: 
					if (game1 == null || game1.getTitle() == null)
					{
						if (game2 == null || game2.getTitle() == null)
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || game2.getTitle() == null)
						{
							result = 1;
						}
						else
						{
							result = game1.getTitle().compareToIgnoreCase(game2.getTitle());
						}
					}
					break;
				case DESC_TITLE: 
					if (game1 == null || game1.getTitle() == null)
					{
						if (game2 == null || game2.getTitle() == null)
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || game2.getTitle() == null)
						{
							result = 1;
						}
						else
						{
							result = game2.getTitle().compareToIgnoreCase(game1.getTitle());
						}
					}
					break;
				case ASC_YEAR: 
					if (game1 == null || TextUtils.isEmpty(game1.getRelease()))
					{
						if (game2 == null || TextUtils.isEmpty(game2.getRelease()))
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || TextUtils.isEmpty(game2.getRelease()))
						{
							result = 1;
						}
						else
						{
							String release1 = game1.getRelease().length() > 4 ? game1.getRelease() : game1.getRelease() + "/12/31";
							String release2 = game2.getRelease().length() > 4 ? game2.getRelease() : game2.getRelease() + "/12/31";
							result = _dateFormat.parse(release1).compareTo(_dateFormat.parse(release2));
							
							if (result == 0)
							{
								if (game1 == null || game1.getTitle() == null)
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 0;
									}
									else
									{
										result = -1;
									}
								}
								else
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 1;
									}
									else
									{
										result = game1.getTitle().compareToIgnoreCase(game2.getTitle());
									}
								}
							}
						}
					}
					break;
				case DESC_YEAR:
					if (game1 == null || TextUtils.isEmpty(game1.getRelease()))
					{
						if (game2 == null || TextUtils.isEmpty(game2.getRelease()))
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || TextUtils.isEmpty(game2.getRelease()))
						{
							result = 1;
						}
						else
						{
							String release1 = game1.getRelease().length() > 4 ? game1.getRelease() : game1.getRelease() + "/12/31";
							String release2 = game2.getRelease().length() > 4 ? game2.getRelease() : game2.getRelease() + "/12/31";
							result = _dateFormat.parse(release2).compareTo(_dateFormat.parse(release1));
							
							if (result == 0)
							{
								if (game1 == null || game1.getTitle() == null)
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 0;
									}
									else
									{
										result = -1;
									}
								}
								else
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 1;
									}
									else
									{
										result = game1.getTitle().compareToIgnoreCase(game2.getTitle());
									}
								}
							}
						}
					}
					break;
				case ASC_ACHIEVEMENTS: 
					if (game1 == null || game1.getCurrentAchievements() == -1)
					{
						if (game2 == null || game2.getCurrentAchievements() == -1)
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || game2.getCurrentAchievements() == -1)
						{
							result = 1;
						}
						else
						{
							result = Integer.valueOf(game1.getCurrentAchievements()).compareTo(game2.getCurrentAchievements());
							
							if (result == 0)
							{
								if (game1 == null || game1.getTitle() == null)
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 0;
									}
									else
									{
										result = -1;
									}
								}
								else
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 1;
									}
									else
									{
										result = game1.getTitle().compareToIgnoreCase(game2.getTitle());
									}
								}
							}
						}
					}
					break;
				case DESC_ACHIEVEMENTS:
					if (game1 == null || game1.getCurrentAchievements() == -1)
					{
						if (game2 == null || game2.getCurrentAchievements() == -1)
						{
							result = 0;
						}
						else
						{
							result = -1;
						}
					}
					else
					{
						if (game2 == null || game2.getCurrentAchievements() == -1)
						{
							result = 1;
						}
						else
						{
							result = Integer.valueOf(game2.getCurrentAchievements()).compareTo(game1.getCurrentAchievements());
							
							if (result == 0)
							{
								if (game1 == null || game1.getTitle() == null)
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 0;
									}
									else
									{
										result = -1;
									}
								}
								else
								{
									if (game2 == null || game2.getTitle() == null)
									{
										result = 1;
									}
									else
									{
										result = game1.getTitle().compareToIgnoreCase(game2.getTitle());
									}
								}
							}
						}
					}
					break;
			}
		}
		catch (Exception ex)
		{
			XLog.e("[XComparator.compare] Exception: " + ex.getMessage());
		}

		return result;
	}
	
	public int compare(OptionString optionString1, OptionString optionString2) 
	{
		int result = 0;
		
		try
		{
			if (optionString1 == null || optionString1.getOptionName() == null)
			{
				if (optionString2 == null || optionString2.getOptionName() == null)
				{
					result = 0;
				}
				else
				{
					result = -1;
				}
			}
			else
			{
				if (optionString2 == null || optionString2.getOptionName() == null)
				{
					result = 1;
				}
				else
				{
					if (optionString1.getOptionKey() == Game.Genre.ALL.toString())
					{
						result = -1;
					}
					else if (optionString2.getOptionKey() == Game.Genre.ALL.toString())
					{
						result = 1;
					}
					else
					{
						result = optionString1.getOptionName().compareToIgnoreCase(optionString2.getOptionName());
					}
				}
			}
		}
		catch (Exception ex)
		{
			XLog.e("[XComparator.compare] Exception: " + ex.getMessage());
		}
		
		return result;
	}
	
	public int compare(SelectionFile sf1, SelectionFile sf2) 
	{
		int result = 0;
		
		try
		{
			if (sf1 == null || TextUtils.isEmpty(sf1.getName()))
			{
				if (sf2 == null || TextUtils.isEmpty(sf2.getName()))
				{
					result = 0;
				}
				else
				{
					result = -1;
				}
			}
			else
			{
				if (sf2 == null || TextUtils.isEmpty(sf2.getName()))
				{
					result = 1;
				}
				else
				{
					result = sf1.getName().compareToIgnoreCase(sf2.getName());
				}
			}
		}
		catch (Exception ex)
		{
			XLog.e("[XComparator.compare] Exception: " + ex.getMessage());
		}
		
		return result;
	}
	
	public int compare(String string1, String string2) 
	{
		int result = 0;
		
		try
		{
			if (string1 == null)
			{
				if (string2 == null)
				{
					result = 0;
				}
				else
				{
					result = -1;
				}
			}
			else
			{
				if (string2 == null)
				{
					result = 1;
				}
				else
				{
					result = string1.compareToIgnoreCase(string2);
				}
			}
		}
		catch (Exception ex)
		{
			XLog.e("[XComparator.compare] Exception: " + ex.getMessage());
		}
		
		return result;
	}
	
	public int compare(Object obj1, Object obj2) 
	{
		int result = 0;
		
		if (obj1 instanceof Game)
		{
			result = compare((Game)obj1, (Game)obj2);
		}
		else if (obj1 instanceof OptionString)
		{
			result = compare((OptionString)obj1, (OptionString)obj2);
		}
		else if (obj1 instanceof SelectionFile)
		{
			result = compare((SelectionFile)obj1, (SelectionFile)obj2);
		}
		else if (obj1 instanceof String)
		{
			result = compare((String)obj1, (String)obj2);
		}
		
		return result;
	}
}