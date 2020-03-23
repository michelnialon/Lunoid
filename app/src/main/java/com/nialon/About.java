package com.nialon;
 
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
 
public class About extends Activity
{
	TextView textTitre;
	TextView textAuth;
	TextView textDate;

	public void onCreate(Bundle savedInstanceState) 
	{
		Button closeButton;

        // Pour enlever la barre de titre :
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		textTitre = findViewById(R.id.textTitre);
		textAuth = findViewById(R.id.textAuth);
		textDate =  findViewById(R.id.textDate);
		textTitre.setText(getString(R.string.app_name));
		textAuth.setText(getString(R.string.app_author));
		textDate.setText(getString(R.string.app_date));
		
		/* Format title */
        /*
        TextView title = (TextView) findViewById(android.R.id.title);
        title.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.YELLOW);
        title.setTextSize(20);
        */
        
		String versionName = "";
	    PackageInfo packageInfo;
	    try 
	    {
	        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	        versionName = "v " + packageInfo.versionName;
	    } catch (NameNotFoundException e) 
	    {
	        e.printStackTrace();
	    }
	    TextView tv = findViewById(R.id.textVer);
	    tv.setText(versionName);
	    
	    //Bouton OK, pas utilisé
	    closeButton = this.findViewById(R.id.buttonOK);
	    closeButton.setOnClickListener(new OnClickListener()
		  {
		    @Override
		    public void onClick(View v) 
		    {
		      finish();
		    }
		  });
	}
	
}