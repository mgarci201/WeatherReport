/**
 * Glasgow Caledonian University
 * Introduction to Mobile Device Programming M3G621212
 */

package com.mario.weatherlistingcoursework;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

//In this activity we will show 3 day forecast for city chosen by the user
public class ForecastDayListActivity extends Activity {
	
	public static final String LOG_TAG = "ForecastDayListActivity";
	public static final String INTENT_PARAM_CITYNUMBER = "cityNumber"; 
	
	protected int cityNumber;
	protected City city;
	protected ListView lvDaysForecast;
	protected SimpleAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast_list);
		
		//receive chosen city number
		cityNumber = getIntent().getIntExtra(INTENT_PARAM_CITYNUMBER, 0);
		Log.d(LOG_TAG, "cityNumber= " + cityNumber); 
		
		//locate view from layout xml
		lvDaysForecast= (ListView) findViewById (R.id.lvDaysForecast);
		
		//easy to remember (shorter label) 
		city = WeatherInfoDownloader.cities[ cityNumber ];
		
		//will pack the data into a whole structure 
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(city.forecast.length); 
		
		for (int i=0; i < city.forecast.length; i++)
			data.add( city.forecast[i].getContentsAsMap());
		
		//connect them all in simpleAdapter 
		String[] from = { City.ATTRIBUTE_DATE, City.ATTRIBUTE_WEATHERDESC, City.ATTRIBUTE_TEMPERATURE, City.ATTRIBUTE_WINDSPEED };
		
		int[] to = { R.id.tvDayDate, R.id.tvDayWeatherDesc, R.id.tvDayTemp, R.id.tvDayWindSpeed };
		
		adapter = new SimpleAdapter(this, data, R.layout.lv_day_forecast_item, from, to);
		lvDaysForecast.setAdapter(adapter);
		
		//set Title or the Activity
		setTitle (""+ city.name);
		
		}
	

}
