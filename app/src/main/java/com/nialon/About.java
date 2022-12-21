package com.nialon;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.shredzone.commons.suncalc.MoonPosition;

import java.util.Calendar;

public class About extends Activity {
	TextView textTitre;
	TextView textAuth;
	TextView textDate;
	TextView textDist;

	public void onCreate(Bundle savedInstanceState) {
		Button closeButton;

        // Pour enlever la barre de titre :
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		textTitre = findViewById(R.id.textTitre);
		textAuth = findViewById(R.id.textAuth);
		textDate = findViewById(R.id.textDate);
		textDist = findViewById(R.id.textDist);
		textTitre.setText(getString(R.string.app_name));
		textAuth.setText(getString(R.string.app_author));
		textDate.setText(getString(R.string.app_date));
		Calendar calendar = Calendar.getInstance();
		
		/* Format title */
        /*
        TextView title = (TextView) findViewById(android.R.id.title);
        title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.YELLOW);
        title.setTextSize(20);
        */
        
		String versionName = "";
	    PackageInfo packageInfo;
	    try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = "v " + packageInfo.versionName;
			MoonPosition.Parameters moonParam = MoonPosition.compute()
					.timezone("Europe/Paris")         // local time
					.on(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			MoonPosition moon = moonParam.execute();
			textDist.setText(String.format("%02.0f", moon.getDistance()) + " km");

		} catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView tv = findViewById(R.id.textVer);
        tv.setText(versionName);

        //Bouton OK, pas utilisé
        closeButton = this.findViewById(R.id.buttonOK);
        closeButton.setOnClickListener(v -> finish());
    }
}