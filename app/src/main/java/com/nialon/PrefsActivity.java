package com.nialon;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	   super.onCreate(savedInstanceState);
//	   super.getListView().setBackgroundColor(Color.BLACK);
	   //addPreferencesFromResource(R.xml.prefs);
	   getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
}