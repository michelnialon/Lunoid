package com.nialon;
 
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
 
public class About extends Activity
{
	TextView textTitre;
	TextView textVer;
	TextView textAuth;
	TextView textDate;
	private Button closeButton;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		textTitre = (TextView) findViewById(R.id.textTitre);
		textAuth = (TextView) findViewById(R.id.textAuth);
		textDate = (TextView) findViewById(R.id.textDate);
		textTitre.setText("LUNOID");
		textAuth.setText("Michel Nialon");
		textDate.setText("28/12/2017");
		
		// Pour enlever la barre de titre :
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		/** Format title */
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
	    TextView tv = (TextView) findViewById(R.id.textVer);
	    tv.setText(versionName);
	    
	    //Bouton OK, pas utilisé
	    closeButton = (Button)this.findViewById(R.id.buttonOK);
		  this.closeButton.setOnClickListener(new OnClickListener() 
		  {
		    @Override
		    public void onClick(View v) 
		    {
		      finish();
		    }
		  });
	}
	
}