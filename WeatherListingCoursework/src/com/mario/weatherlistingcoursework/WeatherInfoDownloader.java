/**
 * Glasgow Caledonian University
 * Introduction to Mobile Device Programming M3G621212
 * Student Name: Mario Garcia
 * Student Mat.No: S1229875
 */

package com.mario.weatherlistingcoursework;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Handler;
import android.util.Log;

//This class will connect to the BBC weather online RSS feeds 
//gets weather info as RSS/XML, parses RSS/XML and returns city weather information

//Reference for my Sax parsing Tutorials:
//Website: http://www.codeproject.com/Articles/334859/Parsing-XML-in-Android-with-SAX 
//Website: http://www.technotalkative.com/android-sax-parsing-example/ 
//Blog: http://android-pro.blogspot.co.uk/2011/07/parsing-xml-wit-sax-parser.html
//Website for the time converter: http://stackoverflow.com/questions/2705548/parse-rss-pubdate-to-date-object-in-java
public class WeatherInfoDownloader extends Thread {
	
	public static final String LOG_TAG = "WeatherInfoDownloader thread";//for debugging purposes on the Logcat
	public static final String REQUEST_URL = "http://open.live.bbc.co.uk/weather/feeds/en/"; 
	
	//Value integers for cities 10 in total as I have added 2 more Belfast and London
	public static String[] cityIntegers = { "2640006", "2648579","2657832", "2650752", "2650225", "2641419", "2649169", "2635199", "2655984", "2643741" };
	
	public static String[] cityNameParams = null;
	public static City[] cities = null;
	
	protected Handler returnHandler;
	protected Runnable returnRunnable;

	public WeatherInfoDownloader(Handler returnHandler, Runnable returnRunnable) {
		// Runnables 
		
		super();
		this.returnHandler = returnHandler;
		this.returnRunnable = returnRunnable;
	}
	
	protected static String sanitize(String s) {
		
		return s.replaceAll(Pattern.quote(" "), "+");
	}
	
	protected void getCityNameParams() {
		
		int tmpsize = 0;
		String[] tmp = new String[ cityIntegers.length ];
		
		for (int i = 0; i < cityIntegers.length; ++i) {
			
			// Is this city checked?
			
			if ( CurrentCityListActivity.appPreferences.getBoolean( cityIntegers[i], false ) ) 
				tmp[ tmpsize++ ] = cityIntegers[i];
		}
		
		cityNameParams = new String[ tmpsize ];
		for (int i = 0; i < tmpsize; ++i)
			cityNameParams[i] = tmp[i];

	}
	
	public void run() {
		Log.d(LOG_TAG, "Starting WeatherInfoDownloader thread!");
		
		// step 1: get city list into cityNameParams
		getCityNameParams();
		Log.d(LOG_TAG, "Getting cities list: <SharedPreferences> got " + cityNameParams.length + " cities.");
		
		// step 2: get weather info for every city
		cities = new City[ cityNameParams.length ];
		for (int i = 0; i < cityNameParams.length; ++i)
			cities[i] = XMLParser.downloadCityWeatherInfo( sanitize( cityNameParams[i] ) );

		// step 3: notify UI thread that we're done
		returnHandler.post(returnRunnable);
	}

}

class XMLParser extends DefaultHandler {
	
	public static final String LOG_TAG = "SAX XML Parser";
	String thisElement = "";
	
	protected boolean stateCurCity = false;
	protected boolean stateDayForecast = false; 
	protected int currentDayNumber = 0;
	protected City city = new City();
	
	

	public static City downloadCityWeatherInfo(
			String cityNameParam) {
		
		City city = null;
		
		//This will make an HTTP request (download the RSS feed) and the code should run on separate thread
		try { 
			
			// The generic URL for a BBC RSS feed is (http://open.live.bbc.co.uk/weather/feeds/en/location_id/3dayforecast.rss)
			HttpURLConnection conn = (HttpURLConnection) ( new URL(WeatherInfoDownloader.REQUEST_URL + cityNameParam + "/3dayforecast.rss" ) ).openConnection();
			
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLParser myXMLParser = new XMLParser(); 
		    	parser.parse( conn.getInputStream(), myXMLParser );
		    	
		    	city = myXMLParser.getResult();
		    }
		    
		} catch(IOException e) {
			
			e.printStackTrace();
			
		} catch(SAXException e) {
			
			e.printStackTrace();
			
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} 
		
		return city;
	} 
	
	public City getResult () {
		return city;
	}
	
	public void startDocument() throws SAXException { 
		
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if ( qName.equals("image") ) { 
			
			stateCurCity = true;
			
		}else if ( qName.equals("item") ) { 
			
			if ( currentDayNumber < City.FORECAST_LENGTH )
				stateDayForecast = true;
		}
		
		thisElement = qName;
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if ( qName.equals("image") ) { 
			
			stateCurCity = false;
		
		} else if ( qName.equals("item") ) {
			stateDayForecast = false;
			++currentDayNumber;
			
		}
			
			thisElement = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	
		if (stateCurCity) { 
			
			//read current city 
			if ( thisElement.equals("title") ) { 
				city.cityTitle = new String(ch, start, length);
				
			} else if ( thisElement.equals("pubDate") ) { 
				city.pubDate = new String(ch, start, length);
				
			} 					
		} else if ( stateDayForecast ) { 
			
			//read the 3 day forecast 
			if ( thisElement.equals("title") ) { 
				city.forecast[currentDayNumber].dateTitle = new String(ch, start, length);
				
			} else if ( thisElement.equals("description") ) { 
				city.forecast[currentDayNumber].tempDescription = new String(ch, start, length);
				
			} else if ( thisElement.equals("pubDate") ) { 
				city.pubDate = new String(ch, start, length);
				
				//This snippet will convert the usual pubDate format to current : day, dd/mm/year..Much simple for the UI display
				try { 
					Date date = ( new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z")).parse(new String (ch, start, length) );
					city.pubDate = new SimpleDateFormat("EEEE, dd.MM.yyyy").format(date);
				
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if ( thisElement.equals("title") ) { 
			city.name = new String(ch, start, length);
		}

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

}
