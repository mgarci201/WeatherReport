/**
 * Glasgow Caledonian University
 * Introduction to Mobile Device Programming M3G621212
 */

package com.mario.weatherlistingcoursework;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

//This Activity will allow the user to Add/Remove a city from the list, accessed through Menu -> Add/Remove Cities 
public class CitiesChoosingActivity extends Activity implements OnItemClickListener {
	
public static final String LOG_TAG = "CitiesChoosingActivity";
	
	protected ListView lvCitiesSelecter;
	protected ArrayAdapter<CharSequence> adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cities_choosing);
		
		//find view
		lvCitiesSelecter = (ListView) findViewById(R.id.lvCitiesSelecter);
		
		//In this snippet, I have modified the listView from my XML into multiple check list for add/removing city
		lvCitiesSelecter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		adapter = new ArrayAdapter<CharSequence> ( this, android.R.layout.simple_list_item_multiple_choice, WeatherInfoDownloader.cityIntegers );
		
		lvCitiesSelecter.setAdapter(adapter); 
		
		//setting up click actions
		lvCitiesSelecter.setOnItemClickListener(this);
		
		//setting some items check
		boolean isInPreferences;
		for (int i= 0; i < WeatherInfoDownloader.cityIntegers.length; i++) { 
			
			isInPreferences = CurrentCityListActivity.appPreferences.getBoolean( WeatherInfoDownloader.cityIntegers[i], false );
			lvCitiesSelecter.setItemChecked (i, isInPreferences);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		//put info into preferences
		Editor editor = CurrentCityListActivity.appPreferences.edit();
		
		//Mess API
		SparseBooleanArray sbArray = lvCitiesSelecter.getCheckedItemPositions();
		
		//Adding removing cities 
		editor.putBoolean( WeatherInfoDownloader.cityIntegers[position], sbArray.get (position) );
		editor.commit();
		
	}

}
