package com.nialon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.util.Locale;

public class Infos extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		// Pour enlever la barre de titre :
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.infos);
		setContentView(R.layout.aide);
		WebView webView = findViewById(R.id.wvAide);
		Log.d("locale", Locale.getDefault().getISO3Language());
		switch (Locale.getDefault().getISO3Language()) {
			case "eng":
				webView.loadUrl("file:///android_asset/aide_en.html");
				break;
			case "spa":
				webView.loadUrl("file:///android_asset/aide_es.html");
				break;
			case "ita":
				webView.loadUrl("file:///android_asset/aide_fr.html");
				break;
			case "deu":
				webView.loadUrl("file:///android_asset/aide_de.html");
				break;
			default:
				webView.loadUrl("file:///android_asset/aide_fr.html");
				break;
		}
	}
}