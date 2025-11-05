package com.nialon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Locale;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Infos extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		LinearLayout lmain;
		// Pour enlever la barre de titre :
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.infos);

		setContentView(R.layout.aide);
		WebView webView = findViewById(R.id.wvAide);
		lmain = findViewById(R.id.linearLayoutMain6);
		ViewCompat.setOnApplyWindowInsetsListener(lmain, (v, windowInsets) -> {
			Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
			ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			mlp.topMargin = insets.top;
			v.setLayoutParams(mlp);
			return WindowInsetsCompat.CONSUMED;
		});
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