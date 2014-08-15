/**
 * Glasgow Caledonian University
 * Introduction to Mobile Device Programming M3G621212
 * Student Name: Mario Garcia
 * Student Mat.No: S1229875
 */

package com.mario.weatherlistingcoursework;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

//This Activity will display the addition or removal of a city 
public class CurrentCityListActivity extends Activity implements OnItemClickListener {
	
	public static String PACKAGE_NAME;
	public static Resources appResources;
	public static SharedPreferences appPreferences;
	
	public static final String LOG_TAG = "CurWeatherList Activity";

	ListView lvCurCity;
	SimpleAdapter adapter;
	
	//Handler needed to allow net thread to call the user interface thread
	public final Handler mainHandler = new Handler(); 
	public final Runnable changeLayout = new Runnable() { 
		public void run () { 
			
			OnCitiesDownloaded();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PACKAGE_NAME = getApplicationContext().getPackageName();
		appResources = getResources();
		appPreferences = getPreferences(MODE_PRIVATE);
		
		
	}
	
		protected void onResume() {
		super.onResume();
		
		refresh();
	}
	
		protected void OnCitiesDownloaded() {
			
			Log.d(LOG_TAG, "Looks like City info was downloaded successfully.");
			
			// Here we continue and build weather list from received information
			
			// Switching to layout with list
		    setContentView(R.layout.current_city_list);
			
		    // locate view from layout XML
			lvCurCity = (ListView) findViewById( R.id.lvCurCity);
			
			// Setup click actions
			lvCurCity.setOnItemClickListener(this);
			
			//Create items in the list
			fillCurCityList();
			
		}

	protected void fillCurCityList() {
			// Shorter label
		
		City[] cities = WeatherInfoDownloader.cities; 
		
		//Packing data into Structures
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(cities.length);
		
		for (int i=0; i < cities.length; i++)
			data.add( cities[i].getCurCityAsMap() );
		
		//Connecting everything to a simple Adapter
		String[] from = {City.ATTRIBUTE_CITYNAME, City.ATTRIBUTE_PUBDATE};
		
		int[] to = {R.id.tvCityName, R.id.tvCityDate};
		
		adapter = new SimpleAdapter(this, data, R.layout.lv_current_city_item, from, to);
		lvCurCity.setAdapter(adapter);
		
		}
	
	protected void refresh () {
		
		//ask user to be patient and displays "Loading...please wait"
		setContentView(R.layout.activity_loading_please_wait);
		
		//Start thread
		WeatherInfoDownloader downloader = new WeatherInfoDownloader(mainHandler, changeLayout);
		downloader.start();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
		//Prepare intent and launch forecastdayListActivity
		Intent intent = new Intent (this, ForecastDayListActivity.class);
		intent.putExtra(ForecastDayListActivity.INTENT_PARAM_CITYNUMBER, position);
		
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_current_city_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.citiesAddAction) {
			
			Intent intent = new Intent(this, CitiesChoosingActivity.class);
			startActivity(intent);
			
		} else if (item.getItemId() == R.id.refreshAction) {
			
			refresh();
		}
		return true;
	}
	
	

}
