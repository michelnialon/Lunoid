package com.nialon;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrefsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	   super.onCreate(savedInstanceState);
//	   super.getListView().setBackgroundColor(Color.BLACK);
	   addPreferencesFromResource(R.xml.prefs);
	}
}
