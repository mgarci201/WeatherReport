/**
 * Glasgow Caledonian University
 * Introduction to Mobile Device Programming M3G621212
 * Student Name: Mario Garcia
 * Student Mat.No: S1229875
 */

package com.mario.weatherlistingcoursework;

import java.util.HashMap;
import java.util.Map;

//This class will get all the attributes necessary for the City and 3 day forecast
public class City {
		
		public static final String ATTRIBUTE_DATE = "dateTitle"; //item node
		public static final String ATTRIBUTE_PUBDATE = "pubDate";//image node
		public static final String ATTRIBUTE_CITYNAME = "cityTitle"; //image node
		public static final String ATTRIBUTE_WEATHERDESC = "weatherTitle"; //item node
		public static final String ATTRIBUTE_TEMPERATURE = "tempDescription"; //item node
		public static final String ATTRIBUTE_WINDSPEED = "windDescription"; //item node
		
		//Fields
		public String name; //I used this to display the City for 3 day forecast in the title above
		
		//City and pubDate
		public String cityTitle; //From image node
		public String pubDate; //Image node
		
		//Forecast for 3 days
		public static final int FORECAST_LENGTH = 3;
		
		public Day[] forecast;
		
		public class Day { 
			
			public String dateTitle; //item node
			public String weatherTitle; //item node
			public String tempDescription; //item node
			
			public Map<String, Object> getContentsAsMap() { 
				
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put(ATTRIBUTE_DATE, dateTitle);
				map.put(ATTRIBUTE_WEATHERDESC, weatherTitle);
				map.put(ATTRIBUTE_TEMPERATURE, "Details: " + tempDescription);
//				map.put(ATTRIBUTE_WINDSPEED, "Wind: " + description + " ");
				
				return map;
				
			}

		}
		
		//Method for Gettting City and Date 
		public Map<String, Object> getCurCityAsMap() { 
			
			Map<String, Object> map = new HashMap<String, Object> ();
			
			map.put(ATTRIBUTE_CITYNAME, cityTitle);
			map.put(ATTRIBUTE_PUBDATE, "As of: " + pubDate);
			
			return map;
		}
		//Our constructor
		public City () {
			
			//Initialize Forecast Array
			forecast = new Day [ FORECAST_LENGTH];
			for (int i=0; i < FORECAST_LENGTH; ++i)
				forecast[i] = new Day();
			
		}
	

}
